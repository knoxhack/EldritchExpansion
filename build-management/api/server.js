const express = require('express');
const cors = require('cors');
const bodyParser = require('body-parser');
const { exec } = require('child_process');
const WebSocket = require('ws');
const path = require('path');
const fs = require('fs');
const axios = require('axios');

// Create Express app
const app = express();
const PORT = 5001;

// No need for a complex middleware - Express handles paths natively

// Enable CORS
app.use(cors());

// Parse JSON bodies
app.use(bodyParser.json());

// Store build data in memory
let builds = [];
let runningBuild = null;
let buildId = 1;

// Version management
let currentVersion = {
  major: 0,
  minor: 1,
  patch: 0,
  build: 1,
  lastRelease: null
};

// Create WebSocket server that will be upgraded from HTTP server
const wss = new WebSocket.Server({ noServer: true });

// Handle WebSocket connections
wss.on('connection', (ws) => {
  console.log('Client connected to WebSocket');
  
  // Send current build status if there's a running build
  if (runningBuild) {
    ws.send(JSON.stringify({
      type: 'BUILD_STARTED',
      build: runningBuild
    }));
  }
  
  ws.on('close', () => {
    console.log('Client disconnected from WebSocket');
  });
});

// Broadcast message to all connected clients
const broadcast = (data) => {
  wss.clients.forEach(client => {
    if (client.readyState === WebSocket.OPEN) {
      client.send(JSON.stringify(data));
    }
  });
};

// API endpoints

// Get all builds
app.get('/api/builds', (req, res) => {
  res.json(builds);
});

// Get a specific build
app.get('/api/builds/:id', (req, res) => {
  const buildId = parseInt(req.params.id);
  const build = builds.find(b => b.id === buildId);
  
  if (!build) {
    return res.status(404).json({ error: 'Build not found' });
  }
  
  res.json(build);
});

// Get build logs
app.get('/api/builds/:id/logs', (req, res) => {
  const buildId = parseInt(req.params.id);
  const build = builds.find(b => b.id === buildId);
  
  if (!build) {
    return res.status(404).json({ error: 'Build not found' });
  }
  
  res.json(build.logs || []);
});

// Start a build
app.post('/api/builds/:id/start', (req, res) => {
  const buildId = parseInt(req.params.id);
  const build = builds.find(b => b.id === buildId);
  
  if (!build) {
    return res.status(404).json({ error: 'Build not found' });
  }
  
  if (runningBuild) {
    return res.status(400).json({ error: 'A build is already running' });
  }
  
  // Set build as running
  build.status = 'running';
  build.startTime = new Date().toISOString();
  build.logs = [
    { type: 'info', message: `Starting build ${build.name} (${build.id})`, timestamp: new Date().toISOString() }
  ];
  
  runningBuild = {
    id: build.id,
    name: build.name,
    status: 'running',
    startTime: build.startTime,
    task: 'build'
  };
  
  // Broadcast build started event
  broadcast({
    type: 'BUILD_STARTED',
    build: runningBuild
  });
  
  // Execute the Gradle build process
  const buildProcess = exec('cd ../.. && ./gradlew build', (error, stdout, stderr) => {
    build.endTime = new Date().toISOString();
    build.duration = new Date(build.endTime) - new Date(build.startTime);
    
    if (error) {
      build.status = 'failed';
      build.logs.push(
        { type: 'error', message: `Build failed: ${error.message}`, timestamp: new Date().toISOString() },
        { type: 'error', message: stderr, timestamp: new Date().toISOString() }
      );
      
      broadcast({
        type: 'BUILD_FAILED',
        error: error.message,
        buildId: build.id
      });
    } else {
      build.status = 'success';
      build.logs.push(
        { type: 'success', message: 'Build completed successfully', timestamp: new Date().toISOString() }
      );
      
      broadcast({
        type: 'BUILD_COMPLETED',
        buildId: build.id
      });
      
      // Try to find the build artifacts
      try {
        const jarFile = `eldritch-expansion-${build.version || '0.1.0'}.jar`;
        build.jarFile = jarFile;
      } catch (err) {
        console.error('Error finding build artifacts:', err);
      }
    }
    
    runningBuild = null;
  });
  
  // Stream the build output
  buildProcess.stdout.on('data', (data) => {
    const lines = data.toString().split('\n').filter(line => line.trim() !== '');
    
    lines.forEach(line => {
      let logType = 'info';
      
      if (line.includes('ERROR') || line.includes('error') || line.includes('Exception')) {
        logType = 'error';
      } else if (line.includes('WARNING') || line.includes('warning') || line.includes('deprecated')) {
        logType = 'warning';
      } else if (line.includes('SUCCESS') || line.includes('success') || line.includes('BUILD SUCCESSFUL')) {
        logType = 'success';
      }
      
      const logEntry = {
        type: logType,
        message: line,
        timestamp: new Date().toISOString()
      };
      
      build.logs.push(logEntry);
      
      broadcast({
        type: 'BUILD_PROGRESS',
        logType,
        message: line,
        buildId: build.id
      });
    });
  });
  
  buildProcess.stderr.on('data', (data) => {
    const lines = data.toString().split('\n').filter(line => line.trim() !== '');
    
    lines.forEach(line => {
      const logEntry = {
        type: 'error',
        message: line,
        timestamp: new Date().toISOString()
      };
      
      build.logs.push(logEntry);
      
      broadcast({
        type: 'BUILD_PROGRESS',
        logType: 'error',
        message: line,
        buildId: build.id
      });
    });
  });
  
  res.json({ message: 'Build started', buildId: build.id });
});

// Abort a build
app.post('/api/builds/:id/abort', (req, res) => {
  const buildId = parseInt(req.params.id);
  const build = builds.find(b => b.id === buildId);
  
  if (!build) {
    return res.status(404).json({ error: 'Build not found' });
  }
  
  if (!runningBuild || runningBuild.id !== build.id) {
    return res.status(400).json({ error: 'This build is not currently running' });
  }
  
  // Execute the command to kill the Gradle process
  exec('pkill -f gradlew', (error) => {
    // Update build status
    build.status = 'aborted';
    build.endTime = new Date().toISOString();
    build.duration = new Date(build.endTime) - new Date(build.startTime);
    
    build.logs.push(
      { type: 'warning', message: 'Build manually aborted by user', timestamp: new Date().toISOString() }
    );
    
    broadcast({
      type: 'BUILD_ABORTED',
      buildId: build.id
    });
    
    runningBuild = null;
    
    res.json({ message: 'Build aborted', buildId: build.id });
  });
});

// Get Gradle tasks
app.get('/api/gradle/tasks', (req, res) => {
  exec('cd ../.. && ./gradlew tasks --all', (error, stdout, stderr) => {
    if (error) {
      return res.status(500).json({ error: 'Failed to get Gradle tasks', details: error.message });
    }
    
    // Parse the tasks output
    const tasks = [];
    const lines = stdout.split('\n');
    let currentGroup = '';
    
    for (const line of lines) {
      // Skip empty lines
      if (!line.trim()) continue;
      
      // Task group headers
      if (line.match(/^-+$/) && lines[lines.indexOf(line) - 1]) {
        currentGroup = lines[lines.indexOf(line) - 1].trim();
        continue;
      }
      
      // Task entries
      const taskMatch = line.match(/^(\w+)\s+-\s+(.+)$/);
      if (taskMatch) {
        tasks.push({
          name: taskMatch[1],
          description: taskMatch[2],
          group: currentGroup
        });
      }
    }
    
    res.json(tasks);
  });
});

// Run a Gradle task
app.post('/api/gradle/run', (req, res) => {
  const { task } = req.body;
  
  if (!task) {
    return res.status(400).json({ error: 'Task name is required' });
  }
  
  if (runningBuild) {
    return res.status(400).json({ error: 'A build is already running' });
  }
  
  // Create a new build record
  const build = {
    id: buildId++,
    name: `Task: ${task}`,
    status: 'running',
    startTime: new Date().toISOString(),
    task,
    logs: [
      { type: 'info', message: `Starting Gradle task: ${task}`, timestamp: new Date().toISOString() }
    ]
  };
  
  builds.push(build);
  
  runningBuild = {
    id: build.id,
    name: build.name,
    status: 'running',
    startTime: build.startTime,
    task
  };
  
  // Broadcast build started event
  broadcast({
    type: 'BUILD_STARTED',
    build: runningBuild
  });
  
  // Execute the Gradle task
  const buildProcess = exec(`cd ../.. && ./gradlew ${task}`, (error, stdout, stderr) => {
    build.endTime = new Date().toISOString();
    build.duration = new Date(build.endTime) - new Date(build.startTime);
    
    if (error) {
      build.status = 'failed';
      build.logs.push(
        { type: 'error', message: `Task failed: ${error.message}`, timestamp: new Date().toISOString() },
        { type: 'error', message: stderr, timestamp: new Date().toISOString() }
      );
      
      broadcast({
        type: 'BUILD_FAILED',
        error: error.message,
        buildId: build.id
      });
    } else {
      build.status = 'success';
      build.logs.push(
        { type: 'success', message: 'Task completed successfully', timestamp: new Date().toISOString() }
      );
      
      broadcast({
        type: 'BUILD_COMPLETED',
        buildId: build.id
      });
    }
    
    runningBuild = null;
  });
  
  // Stream the task output
  buildProcess.stdout.on('data', (data) => {
    const lines = data.toString().split('\n').filter(line => line.trim() !== '');
    
    lines.forEach(line => {
      let logType = 'info';
      
      if (line.includes('ERROR') || line.includes('error') || line.includes('Exception')) {
        logType = 'error';
      } else if (line.includes('WARNING') || line.includes('warning') || line.includes('deprecated')) {
        logType = 'warning';
      } else if (line.includes('SUCCESS') || line.includes('success') || line.includes('BUILD SUCCESSFUL')) {
        logType = 'success';
      }
      
      const logEntry = {
        type: logType,
        message: line,
        timestamp: new Date().toISOString()
      };
      
      build.logs.push(logEntry);
      
      broadcast({
        type: 'BUILD_PROGRESS',
        logType,
        message: line,
        buildId: build.id,
        progress: calculateProgress(line, task)
      });
    });
  });
  
  buildProcess.stderr.on('data', (data) => {
    const lines = data.toString().split('\n').filter(line => line.trim() !== '');
    
    lines.forEach(line => {
      const logEntry = {
        type: 'error',
        message: line,
        timestamp: new Date().toISOString()
      };
      
      build.logs.push(logEntry);
      
      broadcast({
        type: 'BUILD_PROGRESS',
        logType: 'error',
        message: line,
        buildId: build.id
      });
    });
  });
  
  res.json({ 
    message: 'Task started', 
    buildId: build.id,
    output: `Starting Gradle task: ${task}` 
  });
});

// Helper function to estimate build progress based on output
function calculateProgress(line, task) {
  // This is a simple heuristic, a real implementation would use more sophisticated tracking
  if (line.includes('BUILD SUCCESSFUL')) {
    return 100;
  } else if (line.includes('Finished')) {
    return 90;
  } else if (line.includes('Executing')) {
    return 50;
  } else if (line.includes('Starting')) {
    return 10;
  }
  return null; // No progress update
}

// Get current version
app.get('/api/version', (req, res) => {
  const versionString = `${currentVersion.major}.${currentVersion.minor}.${currentVersion.patch}`;
  res.json({
    ...currentVersion,
    versionString,
    fullVersion: `${versionString}-build.${currentVersion.build}`
  });
});

// Update version manually
app.post('/api/version/update', (req, res) => {
  const { major, minor, patch, build } = req.body;
  
  if (major !== undefined) currentVersion.major = parseInt(major);
  if (minor !== undefined) currentVersion.minor = parseInt(minor);
  if (patch !== undefined) currentVersion.patch = parseInt(patch);
  if (build !== undefined) currentVersion.build = parseInt(build);
  
  const versionString = `${currentVersion.major}.${currentVersion.minor}.${currentVersion.patch}`;
  
  broadcast({
    type: 'VERSION_UPDATED',
    version: {
      ...currentVersion,
      versionString,
      fullVersion: `${versionString}-build.${currentVersion.build}`
    }
  });
  
  res.json({
    message: 'Version updated successfully',
    version: {
      ...currentVersion,
      versionString,
      fullVersion: `${versionString}-build.${currentVersion.build}`
    }
  });
});

// Bump version automatically
app.post('/api/version/bump', (req, res) => {
  const { type } = req.body;
  
  switch (type) {
    case 'major':
      currentVersion.major += 1;
      currentVersion.minor = 0;
      currentVersion.patch = 0;
      break;
    case 'minor':
      currentVersion.minor += 1;
      currentVersion.patch = 0;
      break;
    case 'patch':
      currentVersion.patch += 1;
      break;
    case 'build':
    default:
      currentVersion.build += 1;
      break;
  }
  
  const versionString = `${currentVersion.major}.${currentVersion.minor}.${currentVersion.patch}`;
  
  broadcast({
    type: 'VERSION_UPDATED',
    version: {
      ...currentVersion,
      versionString,
      fullVersion: `${versionString}-build.${currentVersion.build}`
    }
  });
  
  res.json({
    message: `Version bumped (${type}) successfully`,
    version: {
      ...currentVersion,
      versionString,
      fullVersion: `${versionString}-build.${currentVersion.build}`
    }
  });
});

// Create a GitHub release
app.post('/api/github/release', async (req, res) => {
  const { buildId, releaseNotes, isPrerelease } = req.body;
  
  // Validate request
  if (!buildId) {
    return res.status(400).json({ error: 'Build ID is required' });
  }
  
  const build = builds.find(b => b.id === parseInt(buildId));
  
  if (!build) {
    return res.status(404).json({ error: 'Build not found' });
  }
  
  if (build.status !== 'success') {
    return res.status(400).json({ error: 'Cannot create a release from a build that was not successful' });
  }
  
  try {
    // In a real implementation, this would call the GitHub API
    // For this demo, we'll simulate the GitHub release
    
    // Bump the version
    currentVersion.build += 1;
    const versionString = `${currentVersion.major}.${currentVersion.minor}.${currentVersion.patch}`;
    const fullVersion = `${versionString}-build.${currentVersion.build}`;
    
    // Create a timestamp for the release
    const releaseDate = new Date().toISOString();
    
    // Create a simulated GitHub release URL
    const githubReleaseUrl = `https://github.com/yourusername/eldritch-expansion/releases/tag/v${versionString}`;
    
    // Generate module-specific JAR files for the release
    const moduleJars = [
      {
        name: "Eldritch Core",
        jarFile: `eldritch-expansion-core-${fullVersion}.jar`,
        description: "Core module required by all other modules"
      }
    ];
    
    // For each additional module in the build, create a separate JAR
    if (build.modules && build.modules.length > 0) {
      build.modules.forEach(moduleName => {
        if (moduleName !== "Eldritch Core") {
          const moduleId = moduleName.toLowerCase().replace(/\s+/g, '-');
          moduleJars.push({
            name: moduleName,
            jarFile: `eldritch-expansion-${moduleId}-${fullVersion}.jar`,
            description: `${moduleName} module for Eldritch Expansion. Requires Eldritch Core.`
          });
        }
      });
    }
    
    // Update the build record with release info
    build.version = versionString;
    build.fullVersion = fullVersion;
    build.gitHubReleaseUrl = githubReleaseUrl;
    build.releaseNotes = releaseNotes;
    build.releaseDate = releaseDate;
    build.isPrerelease = isPrerelease || false;
    build.moduleJars = moduleJars;
    
    // In a real implementation, we would also upload the JAR file to the GitHub release
    
    // Update the current version's last release info
    currentVersion.lastRelease = {
      date: releaseDate,
      version: fullVersion,
      url: githubReleaseUrl,
      buildId: build.id
    };
    
    // Broadcast the release
    broadcast({
      type: 'RELEASE_CREATED',
      release: {
        buildId: build.id,
        version: versionString,
        fullVersion,
        githubReleaseUrl,
        releaseDate,
        isPrerelease: build.isPrerelease,
        moduleJars: moduleJars
      }
    });
    
    res.json({
      message: 'GitHub release created successfully',
      release: {
        buildId: build.id,
        version: versionString,
        fullVersion,
        githubReleaseUrl,
        releaseDate,
        isPrerelease: build.isPrerelease,
        moduleJars: moduleJars
      }
    });
  } catch (error) {
    console.error('Error creating GitHub release:', error);
    res.status(500).json({ error: 'Failed to create GitHub release', details: error.message });
  }
});

// Update build.gradle with the new version
app.post('/api/gradle/update-version', (req, res) => {
  const { version } = req.body;
  
  if (!version) {
    return res.status(400).json({ error: 'Version is required' });
  }
  
  try {
    // In a real implementation, this would modify the build.gradle file
    // For demo purposes, we'll just pretend it worked
    
    res.json({
      message: `Updated build.gradle with version ${version}`,
      version
    });
  } catch (error) {
    console.error('Error updating build.gradle:', error);
    res.status(500).json({ error: 'Failed to update build.gradle', details: error.message });
  }
});

// Start the server
// This is development mode, so we don't need to serve static files from the build directory
// The React dev server is serving those files directly

// No middleware needed for API proxying

// In development mode, React dev server handles these routes
// These routes are here just for documentation
app.get('/', (req, res) => {
  res.json({ message: 'API server is running' });
});

// Setup a route to handle WebSocket connections
app.get('/ws', (req, res) => {
  res.send('WebSocket endpoint');
});

// Start HTTP server
const server = app.listen(PORT, () => {
  console.log(`API server running on port ${PORT}`);
});

// Handle WebSocket upgrade requests
server.on('upgrade', (request, socket, head) => {
  // Simple pathname extraction without using URL constructor
  const pathname = request.url ? request.url.split('?')[0] : '';
  
  if (pathname === '/ws') {
    wss.handleUpgrade(request, socket, head, (ws) => {
      wss.emit('connection', ws, request);
    });
  } else {
    socket.destroy();
  }
});