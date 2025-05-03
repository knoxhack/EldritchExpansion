const express = require('express');
const { exec, spawn } = require('child_process');
const path = require('path');
const cors = require('cors');
const bodyParser = require('body-parser');
const WebSocket = require('ws');
const http = require('http');
const fs = require('fs');

// Create Express app
const app = express();
const port = process.env.PORT || 5001;

// Create HTTP server
const server = http.createServer(app);

// Create WebSocket server
const wss = new WebSocket.Server({ server });

// Apply middleware
app.use(cors());
app.use(bodyParser.json());

// Active builds
const activeBuilds = {};
const buildLogs = {};

// For storing build statuses
const BUILDS_FILE = path.join(__dirname, 'builds.json');

// Load saved builds data
let builds = [];
try {
  if (fs.existsSync(BUILDS_FILE)) {
    builds = JSON.parse(fs.readFileSync(BUILDS_FILE, 'utf8'));
  }
} catch (error) {
  console.error('Error loading builds file:', error);
}

// Save builds data
const saveBuilds = () => {
  fs.writeFileSync(BUILDS_FILE, JSON.stringify(builds, null, 2));
};

// WebSocket connection handler
wss.on('connection', (ws) => {
  console.log('Client connected to WebSocket');
  
  // Send active builds data to newly connected client
  ws.send(JSON.stringify({ type: 'activeBuilds', data: activeBuilds }));
  
  ws.on('close', () => {
    console.log('Client disconnected from WebSocket');
  });
});

// Broadcast build updates to all clients
const broadcastBuildUpdate = (buildId, data) => {
  wss.clients.forEach(client => {
    if (client.readyState === WebSocket.OPEN) {
      client.send(JSON.stringify({
        type: 'buildUpdate',
        buildId,
        data
      }));
    }
  });
};

// Helper to find root directory
const findProjectRoot = () => {
  // In production, this would be more sophisticated
  // For now, we'll assume the main project is one directory up
  return path.resolve(__dirname, '../../');
};

// API Routes
// Get all builds
app.get('/api/builds', (req, res) => {
  res.json(builds);
});

// Get a specific build
app.get('/api/builds/:id', (req, res) => {
  const build = builds.find(b => b.id === parseInt(req.params.id));
  if (!build) {
    return res.status(404).json({ error: 'Build not found' });
  }
  res.json(build);
});

// Create a new build
app.post('/api/builds', (req, res) => {
  const newBuild = {
    ...req.body,
    id: builds.length > 0 ? Math.max(...builds.map(b => b.id)) + 1 : 1,
    createdAt: new Date().toISOString()
  };
  builds.push(newBuild);
  saveBuilds();
  res.status(201).json(newBuild);
});

// Update a build
app.put('/api/builds/:id', (req, res) => {
  const buildId = parseInt(req.params.id);
  const buildIndex = builds.findIndex(b => b.id === buildId);
  
  if (buildIndex === -1) {
    return res.status(404).json({ error: 'Build not found' });
  }
  
  builds[buildIndex] = {
    ...builds[buildIndex],
    ...req.body,
    updatedAt: new Date().toISOString()
  };
  
  saveBuilds();
  res.json(builds[buildIndex]);
});

// Delete a build
app.delete('/api/builds/:id', (req, res) => {
  const buildId = parseInt(req.params.id);
  const buildIndex = builds.findIndex(b => b.id === buildId);
  
  if (buildIndex === -1) {
    return res.status(404).json({ error: 'Build not found' });
  }
  
  const deletedBuild = builds.splice(buildIndex, 1)[0];
  saveBuilds();
  res.json(deletedBuild);
});

// Start a Gradle build
app.post('/api/builds/:id/start', (req, res) => {
  const buildId = parseInt(req.params.id);
  const build = builds.find(b => b.id === buildId);
  
  if (!build) {
    return res.status(404).json({ error: 'Build not found' });
  }
  
  // Check if build is already running
  if (activeBuilds[buildId]) {
    return res.status(400).json({ error: 'Build already in progress' });
  }
  
  // Initialize build logs
  buildLogs[buildId] = [];
  
  // Find project root
  const projectRoot = findProjectRoot();
  
  // Update build status
  const updatedBuild = {
    ...build,
    status: 'In Progress',
    previousStatus: build.status,
    buildStartTime: new Date().toISOString()
  };
  
  builds = builds.map(b => b.id === buildId ? updatedBuild : b);
  saveBuilds();
  
  // Create a new build process
  try {
    console.log(`Starting Gradle build for build ID ${buildId}`);
    
    // Determine Gradle command based on platform
    const isWindows = process.platform === 'win32';
    const gradleCmd = isWindows ? 'gradlew.bat' : './gradlew';
    
    // Create log function
    const logBuildOutput = (data, type = 'info') => {
      const logEntry = {
        type,
        message: data.toString().trim(),
        timestamp: new Date().toISOString()
      };
      
      buildLogs[buildId].push(logEntry);
      
      // Broadcast update
      broadcastBuildUpdate(buildId, {
        log: logEntry,
        status: 'running',
        progress: calculateProgress(data.toString())
      });
    };
    
    // Spawn Gradle process
    const gradleProcess = spawn(gradleCmd, ['build'], {
      cwd: projectRoot,
      shell: true
    });
    
    // Track process
    activeBuilds[buildId] = {
      process: gradleProcess,
      startTime: new Date().toISOString(),
      build: updatedBuild
    };
    
    // Handle stdout
    gradleProcess.stdout.on('data', (data) => {
      logBuildOutput(data, 'info');
    });
    
    // Handle stderr
    gradleProcess.stderr.on('data', (data) => {
      logBuildOutput(data, 'error');
    });
    
    // Handle process completion
    gradleProcess.on('close', (code) => {
      console.log(`Gradle process exited with code ${code}`);
      
      // Build is complete - update status
      completeBuild(buildId, code === 0);
    });
    
    res.status(200).json({
      message: 'Build started successfully',
      buildId,
      status: 'In Progress'
    });
    
  } catch (error) {
    console.error('Error starting Gradle build:', error);
    
    // Revert build status
    builds = builds.map(b => b.id === buildId ? {
      ...b,
      status: b.previousStatus || 'Failed',
      previousStatus: null,
      error: error.message
    } : b);
    saveBuilds();
    
    res.status(500).json({ error: 'Failed to start build process' });
  }
});

// Function to complete a build
const completeBuild = (buildId, success) => {
  // Get the build
  const build = builds.find(b => b.id === buildId);
  if (!build) return;
  
  // Calculate build duration
  const startTime = new Date(build.buildStartTime);
  const endTime = new Date();
  const duration = endTime - startTime;
  
  // Update build status
  const newStatus = success ? 'Completed' : 'Failed';
  const updatedBuild = {
    ...build,
    status: newStatus,
    previousStatus: null,
    buildEndTime: endTime.toISOString(),
    buildDuration: duration,
    buildSuccess: success,
    jarFile: success ? `eldritch-expansion-${build.version}.jar` : null
  };
  
  // Save to builds array
  builds = builds.map(b => b.id === buildId ? updatedBuild : b);
  saveBuilds();
  
  // Broadcast completion
  broadcastBuildUpdate(buildId, {
    status: newStatus,
    progress: success ? 100 : 0,
    complete: true,
    success,
    log: {
      type: success ? 'success' : 'error',
      message: `Build ${success ? 'completed successfully' : 'failed'}`,
      timestamp: new Date().toISOString()
    }
  });
  
  // Cleanup
  delete activeBuilds[buildId];
};

// Function to calculate progress percentage from Gradle output
const calculateProgress = (output) => {
  // In a real implementation, this would parse Gradle output
  // to determine progress. For now, we'll use a simple heuristic
  
  if (output.includes('Building')) return 10;
  if (output.includes('Compiling')) return 30;
  if (output.includes('Executing')) return 50;
  if (output.includes('Generating')) return 70;
  if (output.includes('Finalizing')) return 90;
  
  return -1; // No progress update
};

// Abort a running build
app.post('/api/builds/:id/abort', (req, res) => {
  const buildId = parseInt(req.params.id);
  
  if (!activeBuilds[buildId]) {
    return res.status(400).json({ error: 'No active build found with this ID' });
  }
  
  try {
    // Kill the process
    activeBuilds[buildId].process.kill();
    
    // Log the abort
    const logEntry = {
      type: 'warning',
      message: 'Build manually aborted by user',
      timestamp: new Date().toISOString()
    };
    
    buildLogs[buildId].push(logEntry);
    
    // Complete the build as failed
    completeBuild(buildId, false);
    
    res.status(200).json({ message: 'Build aborted successfully' });
  } catch (error) {
    console.error('Error aborting build:', error);
    res.status(500).json({ error: 'Failed to abort build' });
  }
});

// Get build logs
app.get('/api/builds/:id/logs', (req, res) => {
  const buildId = parseInt(req.params.id);
  
  if (!buildLogs[buildId]) {
    return res.status(404).json({ error: 'No logs found for this build' });
  }
  
  res.json(buildLogs[buildId]);
});

// List available Gradle tasks
app.get('/api/gradle/tasks', (req, res) => {
  const projectRoot = findProjectRoot();
  
  exec('./gradlew tasks --all', { cwd: projectRoot }, (error, stdout, stderr) => {
    if (error) {
      console.error('Error listing Gradle tasks:', error);
      return res.status(500).json({ error: 'Failed to list Gradle tasks' });
    }
    
    // Parse output to extract task names and descriptions
    const tasks = [];
    const lines = stdout.split('\n');
    let inTaskSection = false;
    
    for (const line of lines) {
      if (line.includes('----')) {
        inTaskSection = true;
        continue;
      }
      
      if (inTaskSection && line.trim() && !line.includes('All tasks runnable from root project')) {
        const match = line.match(/^([a-zA-Z0-9:]+)\s+-\s+(.*)$/);
        if (match) {
          tasks.push({
            name: match[1].trim(),
            description: match[2].trim()
          });
        }
      }
    }
    
    res.json(tasks);
  });
});

// Run a specific Gradle task
app.post('/api/gradle/run', (req, res) => {
  const { task } = req.body;
  if (!task) {
    return res.status(400).json({ error: 'No task specified' });
  }
  
  const projectRoot = findProjectRoot();
  
  exec(`./gradlew ${task}`, { cwd: projectRoot }, (error, stdout, stderr) => {
    if (error) {
      console.error(`Error running Gradle task ${task}:`, error);
      return res.status(500).json({ error: `Failed to run task: ${error.message}` });
    }
    
    res.json({
      task,
      success: true,
      output: stdout
    });
  });
});

// Start the server
server.listen(port, () => {
  console.log(`Build Management API server listening on port ${port}`);
});