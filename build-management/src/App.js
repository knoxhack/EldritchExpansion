import React, { useState, useEffect } from 'react';
import { Container, Row, Col, Card, Button, Form, Alert, Table, Badge, Nav, Navbar } from 'react-bootstrap';
import './App.css';

// Import custom components
import GradleTasks from './components/GradleTasks';
import BuildHistory from './components/BuildHistory';
import BuildDashboard from './components/BuildDashboard';
import BuildOutput from './components/BuildOutput';
import { initWebSocket, onWebSocketMessage } from './services/api';

function App() {
  const [builds, setBuilds] = useState([
    {
      id: 1,
      version: "0.1.0",
      name: "Initial Development",
      date: "2025-05-03",
      status: "In Progress",
      previousStatus: null,
      jarFile: null,
      modules: [
        "VoidAlchemy", 
        "ObsidianForgemaster", 
        "EldritchArtifacts", 
        "VoidCorruption", 
        "EldritchArcana",
        "ObsidianConstructs"
      ],
      changes: [
        "Created initial module structure",
        "Fixed ResourceLocation usage for NeoForge 1.21.5",
        "Implemented base module registration system",
        "Added placeholder content for modules"
      ]
    }
  ]);

  // Active tab state
  const [activeTab, setActiveTab] = useState('dashboard');
  
  // Build process management
  const [buildInProgress, setBuildInProgress] = useState(false);
  const [buildProgress, setBuildProgress] = useState(0);
  const [buildOutput, setBuildOutput] = useState([]);
  const [logFilter, setLogFilter] = useState('all');
  
  // Initialize WebSocket connection
  useEffect(() => {
    const socket = initWebSocket();
    
    const removeMessageHandler = onWebSocketMessage((data) => {
      console.log('WebSocket message received:', data);
      
      // Handle different message types
      if (data.type === 'BUILD_STARTED') {
        setBuildInProgress(true);
        setBuildProgress(10);
        setBuildOutput(prev => [
          ...prev,
          { 
            type: 'info', 
            message: `Build started: ${data.build?.name || 'Unknown build'}`, 
            timestamp: new Date().toISOString() 
          }
        ]);
      } else if (data.type === 'BUILD_PROGRESS') {
        // Handle build progress updates
        setBuildOutput(prev => [
          ...prev,
          { 
            type: data.logType || 'info', 
            message: data.message, 
            timestamp: new Date().toISOString() 
          }
        ]);
        if (data.progress) {
          setBuildProgress(data.progress);
        }
      } else if (data.type === 'BUILD_COMPLETED') {
        // Handle build completed
        setBuildInProgress(false);
        setBuildProgress(100);
        setBuildOutput(prev => [
          ...prev,
          { 
            type: 'success', 
            message: 'Build completed successfully', 
            timestamp: new Date().toISOString() 
          }
        ]);
      } else if (data.type === 'BUILD_FAILED') {
        // Handle build failed
        setBuildInProgress(false);
        setBuildOutput(prev => [
          ...prev,
          { 
            type: 'error', 
            message: `Build failed: ${data.error || 'Unknown error'}`, 
            timestamp: new Date().toISOString() 
          }
        ]);
      }
    });
    
    // Add some initial dummy build output for demonstration
    setBuildOutput([
      { 
        type: 'info', 
        message: 'Build system initialized', 
        timestamp: new Date().toISOString() 
      },
      { 
        type: 'info', 
        message: 'Gradle version: 8.5', 
        timestamp: new Date().toISOString() 
      },
      { 
        type: 'info', 
        message: 'Java version: 21.0.1', 
        timestamp: new Date().toISOString() 
      }
    ]);
    
    // Clean up WebSocket connection on component unmount
    return () => {
      removeMessageHandler();
      if (socket) socket.close();
    };
  }, []);

  // Start a build process
  const startBuild = (task) => {
    if (buildInProgress) return;
    
    setBuildInProgress(true);
    setBuildProgress(0);
    setBuildOutput(prev => [
      ...prev,
      { 
        type: 'info', 
        message: `Starting build task: ${task}`, 
        timestamp: new Date().toISOString() 
      }
    ]);
    
    // Simulate build progress
    const steps = [
      { progress: 10, delay: 500, message: 'Initializing build environment' },
      { progress: 25, delay: 1000, message: 'Compiling core modules' },
      { progress: 40, delay: 1500, message: 'Compiling mod modules' },
      { progress: 60, delay: 2000, message: 'Running unit tests' },
      { progress: 75, delay: 1200, message: 'Creating documentation' },
      { progress: 90, delay: 800, message: 'Packaging JAR file' },
      { progress: 100, delay: 500, message: 'Build completed successfully' }
    ];
    
    // Simulate step-by-step progress
    let currentStep = 0;
    
    const progressTimer = setInterval(() => {
      if (currentStep < steps.length) {
        const step = steps[currentStep];
        
        setBuildProgress(step.progress);
        setBuildOutput(prev => [
          ...prev,
          { 
            type: 'info', 
            message: step.message, 
            timestamp: new Date().toISOString() 
          }
        ]);
        
        currentStep++;
      } else {
        clearInterval(progressTimer);
        setBuildInProgress(false);
      }
    }, 1500);  // Each step has a delay between them
  };
  
  // Stop a running build
  const stopBuild = () => {
    if (!buildInProgress) return;
    
    setBuildOutput(prev => [
      ...prev,
      { 
        type: 'warning', 
        message: 'Build manually stopped by user', 
        timestamp: new Date().toISOString() 
      }
    ]);
    
    setBuildInProgress(false);
  };
  
  // Create a checkpoint
  const createCheckpoint = () => {
    setBuildOutput(prev => [
      ...prev,
      { 
        type: 'info', 
        message: 'Creating checkpoint...', 
        timestamp: new Date().toISOString() 
      }
    ]);
    
    setTimeout(() => {
      setBuildOutput(prev => [
        ...prev,
        { 
          type: 'success', 
          message: 'Checkpoint created successfully: eldritch-expansion-0.1.0-checkpoint-1', 
          timestamp: new Date().toISOString() 
        }
      ]);
    }, 1000);
  };
  
  // Create GitHub release
  const createGitHubRelease = () => {
    setBuildOutput(prev => [
      ...prev,
      { 
        type: 'info', 
        message: 'Preparing GitHub release...', 
        timestamp: new Date().toISOString() 
      }
    ]);
    
    setTimeout(() => {
      setBuildOutput(prev => [
        ...prev,
        { 
          type: 'success', 
          message: 'GitHub release created successfully: v0.1.0', 
          timestamp: new Date().toISOString() 
        }
      ]);
    }, 1500);
  };
  
  // Clear build output
  const clearBuildOutput = () => {
    setBuildOutput([]);
  };

  return (
    <div className="App">
      <Navbar bg="dark" variant="dark" expand="lg" className="mb-4">
        <Container>
          <Navbar.Brand>
            <span className="me-2">⚡</span>
            Eldritch Expansion Build Management
          </Navbar.Brand>
          <Navbar.Toggle aria-controls="basic-navbar-nav" />
          <Navbar.Collapse id="basic-navbar-nav">
            <Nav className="me-auto">
              <Nav.Link onClick={() => setActiveTab('dashboard')} active={activeTab === 'dashboard'}>Dashboard</Nav.Link>
              <Nav.Link onClick={() => setActiveTab('builds')} active={activeTab === 'builds'}>Builds</Nav.Link>
              <Nav.Link onClick={() => setActiveTab('history')} active={activeTab === 'history'}>History</Nav.Link>
              <Nav.Link onClick={() => setActiveTab('gradle')} active={activeTab === 'gradle'}>Gradle Tasks</Nav.Link>
            </Nav>
          </Navbar.Collapse>
        </Container>
      </Navbar>

      <Container>
        {activeTab === 'dashboard' && (
          <div>
            <Row>
              <Col lg={8}>
                <BuildDashboard 
                  startBuild={startBuild}
                  stopBuild={stopBuild}
                  createCheckpoint={createCheckpoint}
                  buildProgress={buildProgress}
                  buildStatus={buildInProgress ? 'Building' : 'Ready'}
                  currentVersion="0.1.0"
                />
              </Col>
              <Col lg={4}>
                <BuildOutput 
                  logs={buildOutput}
                  filter={logFilter}
                  setFilter={setLogFilter}
                  clearOutput={clearBuildOutput}
                />
              </Col>
            </Row>
          </div>
        )}
        
        {activeTab === 'builds' && (
          <div>
            <h2 className="mb-4">Builds</h2>
            <Table striped bordered hover>
              <thead>
                <tr>
                  <th>Version</th>
                  <th>Name</th>
                  <th>Date</th>
                  <th>Status</th>
                  <th>Modules</th>
                  <th>Actions</th>
                </tr>
              </thead>
              <tbody>
                {builds.map(build => (
                  <tr key={build.id}>
                    <td>{build.version}</td>
                    <td>{build.name}</td>
                    <td>{build.date}</td>
                    <td>
                      <Badge bg={build.status === 'In Progress' ? 'primary' : 'secondary'}>
                        {build.status}
                      </Badge>
                    </td>
                    <td>{build.modules.length} modules</td>
                    <td>
                      <Button size="sm" variant="primary" className="me-2">Details</Button>
                      <Button size="sm" variant="dark" className="me-2">Build</Button>
                      <Button size="sm" variant="success">Download</Button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </Table>
          </div>
        )}
        
        {activeTab === 'history' && (
          <div>
            <BuildHistory />
          </div>
        )}
        
        {activeTab === 'gradle' && (
          <div>
            <GradleTasks />
          </div>
        )}
      </Container>
    </div>
  );
}

export default App;