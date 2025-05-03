import React, { useState, useEffect } from 'react';
import { Container, Row, Col, Card, Button, Form, Alert, Table, Badge, Nav, Navbar, Spinner, Modal, OverlayTrigger, Tooltip, ProgressBar, InputGroup } from 'react-bootstrap';
import axios from 'axios';
import './App.css';

// Import custom components
import GradleTasks from './components/GradleTasks';
import BuildHistory from './components/BuildHistory';
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
    },
    {
      id: 2,
      version: "0.2.0",
      name: "Module Expansion",
      date: "2025-05-10",
      status: "Planned",
      previousStatus: null,
      modules: [
        "VoidAlchemy", 
        "ObsidianForgemaster", 
        "EldritchArtifacts", 
        "VoidCorruption", 
        "EldritchArcana",
        "ObsidianConstructs",
        "EldritchDimensions"
      ],
      changes: [
        "Add EldritchDimensions module",
        "Implement dimension generation system",
        "Add first Eldritch dimension world type"
      ]
    },
    {
      id: 3,
      version: "0.3.0",
      name: "Entity System",
      date: "2025-05-17",
      status: "Planned",
      previousStatus: null,
      modules: [
        "VoidAlchemy", 
        "ObsidianForgemaster", 
        "EldritchArtifacts", 
        "VoidCorruption", 
        "EldritchArcana",
        "ObsidianConstructs",
        "EldritchDimensions",
        "EldritchBestiary"
      ],
      changes: [
        "Add EldritchBestiary module",
        "Implement first three eldritch creatures",
        "Add spawn mechanics and loot tables"
      ]
    }
  ]);

  const [activeTab, setActiveTab] = useState('builds');
  const [newBuild, setNewBuild] = useState({
    version: "",
    name: "",
    date: new Date().toISOString().split('T')[0],
    status: "Planned",
    modules: [],
    changes: []
  });
  const [changeInput, setChangeInput] = useState("");
  const [allModules, setAllModules] = useState([
    "VoidAlchemy", 
    "ObsidianForgemaster", 
    "EldritchArtifacts", 
    "VoidCorruption", 
    "EldritchArcana",
    "ObsidianConstructs",
    "EldritchDimensions",
    "VoidTech",
    "EldritchBestiary",
    "VoidCultists"
  ]);
  
  // GitHub release functionality
  const [showReleaseModal, setShowReleaseModal] = useState(false);
  const [currentBuild, setCurrentBuild] = useState(null);
  const [releaseNotes, setReleaseNotes] = useState('');
  const [isPrerelease, setIsPrerelease] = useState(false);
  const [releaseCreating, setReleaseCreating] = useState(false);
  const [releaseSuccess, setReleaseSuccess] = useState(false);
  const [releaseError, setReleaseError] = useState(null);
  
  // Download functionality
  const [downloadStats, setDownloadStats] = useState({});
  const [showDownloadModal, setShowDownloadModal] = useState(false);
  const [downloadOptions, setDownloadOptions] = useState({
    minecraftVersion: '1.21.5',
    includeSourceCode: false,
    includeDevTools: false
  });
  
  // Build process management
  const [buildInProgress, setBuildInProgress] = useState(false);
  const [currentBuildProcess, setCurrentBuildProcess] = useState(null);
  const [buildProgress, setBuildProgress] = useState(0);
  const [buildOutput, setBuildOutput] = useState([]);
  const [showBuildModal, setShowBuildModal] = useState(false);
  const [buildError, setBuildError] = useState(null);
  
  // Versioning management
  const [showVersionModal, setShowVersionModal] = useState(false);
  const [versionData, setVersionData] = useState({
    major: 0,
    minor: 0,
    patch: 0,
    type: 'release', // release, beta, alpha
    buildNumber: 1,
    customSuffix: ''
  });
  
  // Build output and logs
  const [showBuildOutputModal, setShowBuildOutputModal] = useState(false);
  const [logFilter, setLogFilter] = useState('all'); // all, error, warning, info
  
  // Initialize WebSocket connection
  useEffect(() => {
    const socket = initWebSocket();
    
    const removeMessageHandler = onWebSocketMessage((data) => {
      console.log('WebSocket message received:', data);
      
      // Handle different message types
      if (data.type === 'BUILD_STARTED') {
        // Handle build started
        const newBuild = {
          id: data.build.id,
          name: data.build.name,
          status: 'running',
          startTime: data.build.startTime,
          task: data.build.task
        };
        // Update build state as needed
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
        setBuildProgress(data.progress || 0);
      } else if (data.type === 'BUILD_COMPLETED') {
        // Handle build completed
        setBuildInProgress(false);
        setBuildProgress(100);
        // Update build status
      } else if (data.type === 'BUILD_FAILED') {
        // Handle build failed
        setBuildInProgress(false);
        setBuildError(data.error || 'Build failed');
      }
    });
    
    // Clean up WebSocket connection on component unmount
    return () => {
      removeMessageHandler();
      if (socket) socket.close();
    };
  }, []);

  const handleAddChange = () => {
    if (changeInput.trim() !== "") {
      setNewBuild({
        ...newBuild,
        changes: [...newBuild.changes, changeInput.trim()]
      });
      setChangeInput("");
    }
  };

  const toggleModule = (module) => {
    if (newBuild.modules.includes(module)) {
      setNewBuild({
        ...newBuild,
        modules: newBuild.modules.filter(m => m !== module)
      });
    } else {
      setNewBuild({
        ...newBuild,
        modules: [...newBuild.modules, module]
      });
    }
  };

  const handleCreateBuild = () => {
    if (newBuild.version && newBuild.name) {
      const newBuildWithId = {
        ...newBuild,
        id: builds.length > 0 ? Math.max(...builds.map(b => b.id)) + 1 : 1
      };
      setBuilds([...builds, newBuildWithId]);
      setNewBuild({
        version: "",
        name: "",
        date: new Date().toISOString().split('T')[0],
        status: "Planned",
        modules: [],
        changes: []
      });
      setActiveTab('builds');
    }
  };

  const updateBuildStatus = (id, newStatus) => {
    setBuilds(builds.map(build => {
      // If status is changing to Completed, generate a JAR file path
      const jarFile = newStatus === 'Completed' ? 
        `eldritch-expansion-${build.version}.jar` : build.jarFile;
        
      return build.id === id ? { 
        ...build, 
        previousStatus: build.status, 
        status: newStatus,
        jarFile: jarFile
      } : build;
    }));
  };
  
  const undoStatusChange = (id) => {
    setBuilds(builds.map(build => 
      build.id === id && build.previousStatus ? { 
        ...build, 
        status: build.previousStatus,
        previousStatus: null 
      } : build
    ));
  };

  const getStatusBadge = (status) => {
    switch(status) {
      case 'Completed':
        return <Badge bg="success">Completed</Badge>;
      case 'In Progress':
        return <Badge bg="primary">In Progress</Badge>;
      case 'Failed':
        return <Badge bg="danger">Failed</Badge>;
      default:
        return <Badge bg="secondary">Planned</Badge>;
    }
  };
  
  // Download handling functions
  const handleDownload = (build) => {
    // Track download stats
    setDownloadStats(prev => ({
      ...prev,
      [build.id]: (prev[build.id] || 0) + 1
    }));
    
    // In a real application, this would communicate with a backend server
    // to generate or retrieve the actual JAR file
    
    // For now, we'll simulate a download by opening the download modal
    setCurrentBuild(build);
    setShowDownloadModal(true);
  };
  
  const generateDownloadLink = (build, options) => {
    const { minecraftVersion, includeSourceCode, includeDevTools } = options;
    const baseUrl = `/downloads/`;
    const versionSegment = build.version;
    const mcVersionSegment = minecraftVersion;
    const extras = [];
    
    if (includeSourceCode) extras.push('src');
    if (includeDevTools) extras.push('dev');
    
    const extrasSegment = extras.length > 0 ? `-${extras.join('-')}` : '';
    
    return `${baseUrl}eldritch-expansion-${versionSegment}-mc${mcVersionSegment}${extrasSegment}.jar`;
  };
  
  const handleDownloadWithOptions = () => {
    if (!currentBuild) return;
    
    // Generate the download link with the selected options
    const downloadLink = generateDownloadLink(currentBuild, downloadOptions);
    
    // In a real app, this would trigger the actual download
    // but for demo purposes, we'll just close the modal
    setShowDownloadModal(false);
    
    // Open the download in a new tab to simulate the download
    window.open(downloadLink, '_blank');
  };
  
  // GitHub release functions
  const handleCreateRelease = (build) => {
    setCurrentBuild(build);
    // Auto-populate release notes with the build changes
    setReleaseNotes(build.changes.map(change => `- ${change}`).join('\n'));
    setShowReleaseModal(true);
  };
  
  const submitGitHubRelease = async () => {
    if (!currentBuild) return;
    
    setReleaseCreating(true);
    setReleaseError(null);
    
    try {
      // In a real application, this would use the GitHub API
      // to create an actual release, using a backend endpoint
      // that securely stores the GitHub token
      
      // Simulate API call
      await new Promise(resolve => setTimeout(resolve, 1500));
      
      // Update the build to indicate it has a GitHub release
      setBuilds(builds.map(build => 
        build.id === currentBuild.id 
          ? { ...build, gitHubReleaseUrl: `https://github.com/yourusername/eldritch-expansion/releases/tag/v${build.version}` } 
          : build
      ));
      
      setReleaseSuccess(true);
      
      // Close the modal after a delay
      setTimeout(() => {
        setShowReleaseModal(false);
        setReleaseCreating(false);
        setReleaseSuccess(false);
      }, 2000);
      
    } catch (error) {
      console.error("Error creating GitHub release:", error);
      setReleaseError("Failed to create GitHub release. Please try again.");
      setReleaseCreating(false);
    }
  };
  
  // Build process functions
  const startBuild = (build) => {
    if (buildInProgress) return;
    
    setCurrentBuild(build);
    setBuildInProgress(true);
    setBuildProgress(0);
    setBuildOutput([
      { type: 'info', message: `Starting build for ${build.version}...`, timestamp: new Date().toISOString() }
    ]);
    setShowBuildModal(true);
    setBuildError(null);
    setCurrentBuildProcess({
      buildId: build.id,
      startTime: new Date().toISOString(),
      step: 'initializing'
    });
    
    // Simulate a build process
    simulateBuildProcess(build);
  };
  
  const simulateBuildProcess = (build) => {
    const steps = [
      { name: 'initializing', duration: 1000, progress: 5 },
      { name: 'compiling', duration: 3000, progress: 30 },
      { name: 'packaging', duration: 2000, progress: 60 },
      { name: 'testing', duration: 2000, progress: 80 },
      { name: 'finalizing', duration: 1000, progress: 95 }
    ];
    
    let currentStepIndex = 0;
    
    const processStep = () => {
      if (currentStepIndex >= steps.length) {
        // Build completed
        completeBuild(build, true);
        return;
      }
      
      const step = steps[currentStepIndex];
      setCurrentBuildProcess(prev => ({ ...prev, step: step.name }));
      setBuildProgress(step.progress);
      
      // Add log entries for this step
      setBuildOutput(prev => [
        ...prev,
        { 
          type: 'info', 
          message: `Step: ${step.name}`, 
          timestamp: new Date().toISOString() 
        }
      ]);
      
      // Add some random detailed logs
      if (step.name === 'compiling') {
        setTimeout(() => {
          setBuildOutput(prev => [
            ...prev,
            { 
              type: 'info', 
              message: `Compiling module: VoidAlchemy`, 
              timestamp: new Date().toISOString() 
            },
            { 
              type: 'info', 
              message: `Compiling module: ObsidianForgemaster`, 
              timestamp: new Date().toISOString() 
            }
          ]);
        }, 500);
        
        // Add a warning to make it interesting
        setTimeout(() => {
          setBuildOutput(prev => [
            ...prev,
            { 
              type: 'warning', 
              message: `Warning: Deprecated method used in VoidAlchemyFluids.java:122`, 
              timestamp: new Date().toISOString() 
            }
          ]);
        }, 1500);
      }
      
      if (step.name === 'testing') {
        setTimeout(() => {
          setBuildOutput(prev => [
            ...prev,
            { 
              type: 'info', 
              message: `Running tests: 24 tests completed`, 
              timestamp: new Date().toISOString() 
            }
          ]);
        }, 1000);
      }
      
      // Move to next step after delay
      setTimeout(() => {
        currentStepIndex++;
        processStep();
      }, step.duration);
    };
    
    processStep();
  };
  
  const abortBuild = () => {
    if (!buildInProgress) return;
    
    setBuildOutput(prev => [
      ...prev,
      { 
        type: 'error', 
        message: 'Build manually aborted by user', 
        timestamp: new Date().toISOString() 
      }
    ]);
    
    // Simulate cleanup
    setTimeout(() => {
      setBuildOutput(prev => [
        ...prev,
        { 
          type: 'info', 
          message: 'Cleaning up resources...', 
          timestamp: new Date().toISOString() 
        }
      ]);
      
      setTimeout(() => {
        completeBuild(currentBuild, false);
      }, 1000);
    }, 500);
  };
  
  const completeBuild = (build, success) => {
    setBuildInProgress(false);
    setBuildProgress(success ? 100 : 0);
    
    const newStatus = success ? 'Completed' : 'Failed';
    
    // Add final log message
    setBuildOutput(prev => [
      ...prev,
      { 
        type: success ? 'success' : 'error', 
        message: `Build ${success ? 'completed successfully' : 'failed'}`, 
        timestamp: new Date().toISOString() 
      }
    ]);
    
    // Update build status
    updateBuildStatus(build.id, newStatus);
    
    // In a real app, you would also generate build artifacts here
    if (success) {
      setTimeout(() => {
        const jarFile = `eldritch-expansion-${build.version}.jar`;
        setBuilds(builds.map(b => 
          b.id === build.id 
            ? { 
                ...b, 
                jarFile,
                lastBuildTime: new Date().toISOString(),
                buildLogs: buildOutput
              } 
            : b
        ));
      }, 1000);
    }
  };
  
  // Versioning functions
  const openVersioningModal = (build) => {
    setCurrentBuild(build);
    
    // Parse the current version into components
    const versionRegex = /^(\d+)\.(\d+)\.(\d+)(?:-(alpha|beta|rc)\.(\d+))?(?:-(.+))?$/;
    const matches = build.version.match(versionRegex) || [0, 0, 0];
    
    setVersionData({
      major: parseInt(matches[1] || 0),
      minor: parseInt(matches[2] || 0),
      patch: parseInt(matches[3] || 0),
      type: matches[4] || 'release',
      buildNumber: parseInt(matches[5] || 1),
      customSuffix: matches[6] || ''
    });
    
    setShowVersionModal(true);
  };
  
  const generateVersionString = () => {
    const { major, minor, patch, type, buildNumber, customSuffix } = versionData;
    
    let version = `${major}.${minor}.${patch}`;
    
    if (type !== 'release') {
      version += `-${type}.${buildNumber}`;
    }
    
    if (customSuffix) {
      version += `-${customSuffix}`;
    }
    
    return version;
  };
  
  const applyVersionChange = (incrementType) => {
    // Create a copy of the version data
    const newVersionData = { ...versionData };
    
    // Increment the specified part
    switch (incrementType) {
      case 'major':
        newVersionData.major += 1;
        newVersionData.minor = 0;
        newVersionData.patch = 0;
        break;
      case 'minor':
        newVersionData.minor += 1;
        newVersionData.patch = 0;
        break;
      case 'patch':
        newVersionData.patch += 1;
        break;
      case 'build':
        newVersionData.buildNumber += 1;
        break;
      default:
        break;
    }
    
    setVersionData(newVersionData);
  };
  
  const saveVersionChanges = () => {
    if (!currentBuild) return;
    
    const newVersion = generateVersionString();
    
    // Update the build with the new version
    setBuilds(builds.map(build => 
      build.id === currentBuild.id 
        ? { ...build, version: newVersion, versionUpdated: new Date().toISOString() } 
        : build
    ));
    
    setShowVersionModal(false);
  };
  
  // Build output functions
  const openBuildOutput = (build) => {
    setCurrentBuild(build);
    setBuildOutput(build.buildLogs || []);
    setShowBuildOutputModal(true);
  };
  
  const getFilteredBuildOutput = () => {
    if (logFilter === 'all') return buildOutput;
    
    return buildOutput.filter(log => log.type === logFilter);
  };

  return (
    <div className="App">
      <Navbar bg="dark" variant="dark" expand="lg" className="mb-4 animate__animated animate__fadeIn">
        <Container>
          <Navbar.Brand className="animate__animated animate__pulse animate__infinite">
            <span className="me-2">⚡</span>
            Eldritch Expansion Build Management
          </Navbar.Brand>
          <Navbar.Toggle aria-controls="basic-navbar-nav" />
          <Navbar.Collapse id="basic-navbar-nav">
            <Nav className="me-auto">
              <Nav.Link onClick={() => setActiveTab('builds')} active={activeTab === 'builds'} className="animate__animated animate__fadeIn">Builds</Nav.Link>
              <Nav.Link onClick={() => setActiveTab('create')} active={activeTab === 'create'} className="animate__animated animate__fadeIn animate__delay-1s">Create Build</Nav.Link>
              <Nav.Link onClick={() => setActiveTab('gradle')} active={activeTab === 'gradle'} className="animate__animated animate__fadeIn animate__delay-2s">Gradle Tasks</Nav.Link>
              <Nav.Link onClick={() => setActiveTab('history')} active={activeTab === 'history'} className="animate__animated animate__fadeIn animate__delay-3s">Build History</Nav.Link>
              <Nav.Link onClick={() => setActiveTab('modules')} active={activeTab === 'modules'} className="animate__animated animate__fadeIn animate__delay-4s">Modules</Nav.Link>
            </Nav>
          </Navbar.Collapse>
        </Container>
      </Navbar>

      <Container>
        {activeTab === 'builds' && (
          <div className="animate__animated animate__fadeIn">
            <h2 className="mb-4">Build History</h2>
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
                  <tr key={build.id} className="build-row animate__animated animate__fadeIn">
                    <td data-label="Version">{build.version}</td>
                    <td data-label="Name">{build.name}</td>
                    <td data-label="Date">{build.date}</td>
                    <td data-label="Status">{getStatusBadge(build.status)}</td>
                    <td data-label="Modules">{build.modules.length} modules</td>
                    <td data-label="Actions">
                      <div className="d-flex">
                        <Button size="sm" variant="primary" className="me-2" onClick={() => setActiveTab(`details-${build.id}`)}>
                          Details
                        </Button>
                        
                        {build.previousStatus && (
                          <Button 
                            size="sm" 
                            variant="warning" 
                            className="me-2"
                            onClick={() => undoStatusChange(build.id)}
                            title="Undo the last status change"
                          >
                            Undo
                          </Button>
                        )}
                        
                        {build.status === 'In Progress' ? (
                          <Button 
                            size="sm" 
                            variant="success"
                            onClick={() => updateBuildStatus(build.id, 'Completed')}
                            className="me-2"
                          >
                            Mark Complete
                          </Button>
                        ) : build.status !== 'Completed' && build.status !== 'Failed' ? (
                          <Button 
                            size="sm" 
                            variant="primary"
                            onClick={() => startBuild(build)}
                            className="me-2"
                          >
                            Start Build
                          </Button>
                        ) : null}
                        
                        <Button
                          size="sm"
                          variant="outline-primary"
                          onClick={() => openVersioningModal(build)}
                          className="me-2"
                          title="Manage version"
                        >
                          Version
                        </Button>
                        
                        {build.status === 'Completed' && build.jarFile && (
                          <Button
                            size="sm"
                            variant="info"
                            className="download-btn me-2"
                            onClick={() => handleDownload(build)}
                            title={`Download ${build.jarFile}`}
                          >
                            <span className="download-icon">⬇️</span> Download
                          </Button>
                        )}
                        
                        {build.status === 'Completed' && !build.gitHubReleaseUrl && (
                          <Button
                            size="sm"
                            variant="secondary"
                            onClick={() => handleCreateRelease(build)}
                            title="Create GitHub Release"
                          >
                            <span className="me-1">🚀</span> GitHub Release
                          </Button>
                        )}
                        
                        {build.status === 'Completed' && build.gitHubReleaseUrl && (
                          <Button
                            size="sm"
                            variant="outline-secondary"
                            as="a"
                            href={build.gitHubReleaseUrl}
                            target="_blank"
                            rel="noopener noreferrer"
                            title="View GitHub Release"
                          >
                            <span className="me-1">🔗</span> GitHub
                          </Button>
                        )}
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </Table>
          </div>
        )}

        {activeTab === 'create' && (
          <Card className="animate__animated animate__fadeIn">
            <Card.Header as="h5">Create New Build</Card.Header>
            <Card.Body>
              <Form>
                <Row className="mb-3">
                  <Col md={6}>
                    <Form.Group>
                      <Form.Label>Version</Form.Label>
                      <Form.Control
                        type="text"
                        placeholder="e.g., 0.4.0"
                        value={newBuild.version}
                        onChange={e => setNewBuild({...newBuild, version: e.target.value})}
                      />
                    </Form.Group>
                  </Col>
                  <Col md={6}>
                    <Form.Group>
                      <Form.Label>Name</Form.Label>
                      <Form.Control
                        type="text"
                        placeholder="e.g., Feature Release"
                        value={newBuild.name}
                        onChange={e => setNewBuild({...newBuild, name: e.target.value})}
                      />
                    </Form.Group>
                  </Col>
                </Row>

                <Row className="mb-3">
                  <Col md={6}>
                    <Form.Group>
                      <Form.Label>Target Date</Form.Label>
                      <Form.Control
                        type="date"
                        value={newBuild.date}
                        onChange={e => setNewBuild({...newBuild, date: e.target.value})}
                      />
                    </Form.Group>
                  </Col>
                  <Col md={6}>
                    <Form.Group>
                      <Form.Label>Status</Form.Label>
                      <Form.Select
                        value={newBuild.status}
                        onChange={e => setNewBuild({...newBuild, status: e.target.value})}
                      >
                        <option>Planned</option>
                        <option>In Progress</option>
                        <option>Completed</option>
                        <option>Failed</option>
                      </Form.Select>
                    </Form.Group>
                  </Col>
                </Row>

                <Form.Group className="mb-3">
                  <Form.Label>Modules</Form.Label>
                  <div className="d-flex flex-wrap gap-2">
                    {allModules.map(module => (
                      <Button
                        key={module}
                        variant={newBuild.modules.includes(module) ? "primary" : "outline-primary"}
                        size="sm"
                        onClick={() => toggleModule(module)}
                      >
                        {module}
                      </Button>
                    ))}
                  </div>
                </Form.Group>

                <Form.Group className="mb-3">
                  <Form.Label>Changes</Form.Label>
                  <div className="d-flex mb-2">
                    <Form.Control
                      type="text"
                      placeholder="Add a change..."
                      value={changeInput}
                      onChange={e => setChangeInput(e.target.value)}
                      onKeyPress={e => {
                        if (e.key === 'Enter') {
                          e.preventDefault();
                          handleAddChange();
                        }
                      }}
                    />
                    <Button variant="outline-primary" className="ms-2" onClick={handleAddChange}>Add</Button>
                  </div>
                  <ul className="list-group">
                    {newBuild.changes.map((change, index) => (
                      <li key={index} className="list-group-item d-flex justify-content-between align-items-center">
                        {change}
                        <Button 
                          variant="outline-danger" 
                          size="sm"
                          onClick={() => setNewBuild({
                            ...newBuild,
                            changes: newBuild.changes.filter((_, i) => i !== index)
                          })}
                        >
                          Remove
                        </Button>
                      </li>
                    ))}
                  </ul>
                </Form.Group>

                <Button variant="success" onClick={handleCreateBuild}>
                  Create Build
                </Button>
              </Form>
            </Card.Body>
          </Card>
        )}

        {activeTab === 'modules' && (
          <div className="animate__animated animate__fadeIn">
            <h2 className="mb-4">Module Status</h2>
            <Table striped bordered hover>
              <thead>
                <tr>
                  <th>Module Name</th>
                  <th>Implementation Status</th>
                  <th>First Added in Version</th>
                  <th>Last Updated in Version</th>
                </tr>
              </thead>
              <tbody>
                {allModules.map(module => {
                  const moduleBuildAdditions = builds
                    .filter(build => build.modules.includes(module))
                    .sort((a, b) => a.id - b.id);
                  
                  const firstAddedIn = moduleBuildAdditions.length > 0 
                    ? moduleBuildAdditions[0].version 
                    : 'Not implemented';
                  
                  const lastUpdatedIn = moduleBuildAdditions.length > 0 
                    ? moduleBuildAdditions[moduleBuildAdditions.length - 1].version 
                    : 'N/A';
                  
                  const implementationStatus = moduleBuildAdditions.length > 0 
                    ? builds.find(b => b.id === moduleBuildAdditions[0].id).status 
                    : 'Not Started';

                  return (
                    <tr key={module} className="animate__animated animate__fadeIn">
                      <td data-label="Module">{module}</td>
                      <td data-label="Status">
                        {implementationStatus === 'Completed' ? (
                          <Badge bg="success">Implemented</Badge>
                        ) : implementationStatus === 'In Progress' ? (
                          <Badge bg="primary">In Development</Badge>
                        ) : implementationStatus === 'Not Started' ? (
                          <Badge bg="secondary">Not Started</Badge>
                        ) : (
                          <Badge bg="warning">Planned</Badge>
                        )}
                      </td>
                      <td data-label="First Added">{firstAddedIn}</td>
                      <td data-label="Last Updated">{lastUpdatedIn}</td>
                    </tr>
                  );
                })}
              </tbody>
            </Table>
          </div>
        )}

        {activeTab.startsWith('details-') && (() => {
          const buildId = parseInt(activeTab.split('-')[1]);
          const build = builds.find(b => b.id === buildId);
          
          if (!build) return <Alert variant="danger">Build not found</Alert>;
          
          return (
            <div className="animate__animated animate__fadeIn">
              <div className="d-flex justify-content-between align-items-center mb-4">
                <h2>Build Details: {build.version} - {build.name}</h2>
                <Button variant="outline-secondary" onClick={() => setActiveTab('builds')}>Back to Builds</Button>
              </div>
              <Row>
                <Col md={6}>
                  <Card className="mb-4 animate__animated animate__fadeInLeft animate__delay-1s">
                    <Card.Header>Build Information</Card.Header>
                    <Card.Body>
                      <p><strong>Version:</strong> {build.version}</p>
                      <p><strong>Name:</strong> {build.name}</p>
                      <p><strong>Date:</strong> {build.date}</p>
                      <p><strong>Status:</strong> {getStatusBadge(build.status)}</p>
                      <div className="mt-3">
                        {build.previousStatus && (
                          <Button 
                            variant="warning" 
                            className="me-2"
                            onClick={() => undoStatusChange(build.id)}
                          >
                            Undo Status Change
                          </Button>
                        )}
                        
                        {build.status !== 'Completed' && build.status !== 'Failed' && (
                          <>
                            <Button 
                              variant={build.status === 'In Progress' ? "success" : "primary"} 
                              className="me-2"
                              onClick={() => updateBuildStatus(build.id, build.status === 'In Progress' ? 'Completed' : 'In Progress')}
                            >
                              {build.status === 'In Progress' ? "Mark Complete" : "Start Build"}
                            </Button>
                            {build.status === 'In Progress' && (
                              <Button 
                                variant="danger"
                                onClick={() => updateBuildStatus(build.id, 'Failed')}
                              >
                                Mark Failed
                              </Button>
                            )}
                          </>
                        )}
                      </div>
                      
                      {build.lastBuildTime && (
                        <div className="mt-4 animate__animated animate__fadeIn">
                          <h5>Build Information</h5>
                          <p className="mb-2">Last build: {new Date(build.lastBuildTime).toLocaleString()}</p>
                          <Button 
                            variant="outline-secondary" 
                            onClick={() => openBuildOutput(build)}
                            className="mb-3"
                          >
                            <span className="me-1">📋</span> View Build Logs
                          </Button>
                        </div>
                      )}
                      
                      {build.status === 'Completed' && build.jarFile && (
                        <div className="jar-download-container mt-4 animate__animated animate__fadeIn">
                          <div>
                            <h5>Build JAR</h5>
                            <p className="mb-2">Your build is complete! Download the JAR file to install the mod:</p>
                            <div className="d-flex flex-wrap gap-2">
                              <Button 
                                variant="info" 
                                className="download-btn"
                                onClick={() => handleDownload(build)}
                                title={`Download ${build.jarFile}`}
                              >
                                <span className="download-icon">⬇️</span> Download {build.jarFile}
                              </Button>
                              
                              {!build.gitHubReleaseUrl && (
                                <Button
                                  variant="secondary"
                                  onClick={() => handleCreateRelease(build)}
                                  title="Create GitHub Release"
                                >
                                  <span className="me-1">🚀</span> Create GitHub Release
                                </Button>
                              )}
                              
                              {build.gitHubReleaseUrl && (
                                <Button
                                  variant="outline-secondary"
                                  as="a"
                                  href={build.gitHubReleaseUrl}
                                  target="_blank"
                                  rel="noopener noreferrer"
                                  title="View GitHub Release"
                                >
                                  <span className="me-1">🔗</span> View on GitHub
                                </Button>
                              )}
                            </div>
                            
                            {downloadStats[build.id] && (
                              <p className="mt-2 text-muted">
                                <small>Downloaded {downloadStats[build.id]} {downloadStats[build.id] === 1 ? 'time' : 'times'}</small>
                              </p>
                            )}
                          </div>
                        </div>
                      )}
                    </Card.Body>
                  </Card>
                </Col>
                <Col md={6}>
                  <Card className="mb-4 animate__animated animate__fadeInRight animate__delay-1s">
                    <Card.Header>Modules ({build.modules.length})</Card.Header>
                    <Card.Body>
                      <div className="d-flex flex-wrap gap-2">
                        {build.modules.map((module, idx) => (
                          <Badge 
                            key={module} 
                            bg="info" 
                            className="p-2 animate__animated animate__fadeIn"
                            style={{animationDelay: `${0.2 * idx}s`}}
                          >
                            {module}
                          </Badge>
                        ))}
                      </div>
                    </Card.Body>
                  </Card>
                </Col>
              </Row>
              <Card className="animate__animated animate__fadeInUp animate__delay-2s">
                <Card.Header>Changes</Card.Header>
                <Card.Body>
                  <ul className="list-group">
                    {build.changes.map((change, index) => (
                      <li 
                        key={index} 
                        className="list-group-item animate__animated animate__fadeInUp"
                        style={{animationDelay: `${0.3 + (index * 0.1)}s`}}
                      >
                        {change}
                      </li>
                    ))}
                  </ul>
                </Card.Body>
              </Card>
            </div>
          );
        })()}

        {activeTab === 'gradle' && (
          <div className="animate__animated animate__fadeIn">
            <h2 className="mb-4">Gradle Task Management</h2>
            <p className="mb-4">
              Run Gradle tasks directly from the build management interface. 
              This allows you to execute builds, tests, and other tasks without using the command line.
            </p>
            <div id="gradle-tasks-container">
              <GradleTasks />
            </div>
          </div>
        )}

        {activeTab === 'history' && (
          <div className="animate__animated animate__fadeIn">
            <h2 className="mb-4">Build Execution History</h2>
            <p className="mb-4">
              View the history of all executed Gradle builds and their results. 
              This includes build status, timestamps, and detailed logs for debugging.
            </p>
            <div id="build-history-container">
              <BuildHistory />
            </div>
          </div>
        )}
      </Container>
      
      {/* Build Process Modal */}
      <Modal show={showBuildModal} onHide={() => !buildInProgress && setShowBuildModal(false)} size="lg" centered backdrop="static">
        <Modal.Header closeButton={!buildInProgress}>
          <Modal.Title>
            {buildInProgress ? `Building ${currentBuild?.version}` : 'Build Complete'}
          </Modal.Title>
        </Modal.Header>
        <Modal.Body>
          <div className="mb-3">
            <h5>Build Progress</h5>
            <ProgressBar 
              now={buildProgress} 
              label={`${buildProgress}%`} 
              animated={buildInProgress}
              variant={buildInProgress ? "primary" : buildProgress === 100 ? "success" : "danger"}
            />
          </div>
          
          {currentBuildProcess && (
            <div className="mb-3">
              <h5>Current Step: {currentBuildProcess.step}</h5>
              <p>Started: {new Date(currentBuildProcess.startTime).toLocaleString()}</p>
            </div>
          )}
          
          <div className="build-output">
            <h5>Build Output</h5>
            <div className="output-filter mb-2">
              <div className="btn-group">
                <Button
                  size="sm"
                  variant={logFilter === 'all' ? "primary" : "outline-primary"}
                  onClick={() => setLogFilter('all')}
                >
                  All
                </Button>
                <Button
                  size="sm"
                  variant={logFilter === 'error' ? "danger" : "outline-danger"}
                  onClick={() => setLogFilter('error')}
                >
                  Errors
                </Button>
                <Button
                  size="sm"
                  variant={logFilter === 'warning' ? "warning" : "outline-warning"}
                  onClick={() => setLogFilter('warning')}
                >
                  Warnings
                </Button>
                <Button
                  size="sm"
                  variant={logFilter === 'info' ? "info" : "outline-info"}
                  onClick={() => setLogFilter('info')}
                >
                  Info
                </Button>
              </div>
            </div>
            <div className="output-console">
              {getFilteredBuildOutput().map((log, index) => (
                <div key={index} className={`log-entry log-${log.type}`}>
                  <span className="log-time">[{new Date(log.timestamp).toLocaleTimeString()}]</span>
                  <span className={`log-badge badge bg-${log.type === 'info' ? 'info' : log.type === 'warning' ? 'warning' : log.type === 'error' ? 'danger' : 'success'}`}>
                    {log.type.toUpperCase()}
                  </span>
                  <span className="log-message">{log.message}</span>
                </div>
              ))}
            </div>
          </div>
          
          {buildError && (
            <Alert variant="danger" className="mt-3">
              {buildError}
            </Alert>
          )}
        </Modal.Body>
        <Modal.Footer>
          {buildInProgress ? (
            <Button variant="danger" onClick={abortBuild}>
              Abort Build
            </Button>
          ) : (
            <Button variant="secondary" onClick={() => setShowBuildModal(false)}>
              Close
            </Button>
          )}
        </Modal.Footer>
      </Modal>
      
      {/* Version Control Modal */}
      <Modal show={showVersionModal} onHide={() => setShowVersionModal(false)} centered>
        <Modal.Header closeButton>
          <Modal.Title>Version Control</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          <p>Current version: <strong>{currentBuild?.version}</strong></p>
          
          <h5 className="mb-3">Quick Version Updates</h5>
          <div className="d-flex gap-2 mb-4">
            <Button variant="outline-primary" onClick={() => applyVersionChange('major')}>
              Major ↑
            </Button>
            <Button variant="outline-primary" onClick={() => applyVersionChange('minor')}>
              Minor ↑
            </Button>
            <Button variant="outline-primary" onClick={() => applyVersionChange('patch')}>
              Patch ↑
            </Button>
          </div>
          
          <h5 className="mb-3">Custom Version</h5>
          <Form>
            <Row className="mb-3">
              <Col xs={4}>
                <Form.Group>
                  <Form.Label>Major</Form.Label>
                  <Form.Control
                    type="number"
                    min="0"
                    value={versionData.major}
                    onChange={e => setVersionData({...versionData, major: parseInt(e.target.value)})}
                  />
                </Form.Group>
              </Col>
              <Col xs={4}>
                <Form.Group>
                  <Form.Label>Minor</Form.Label>
                  <Form.Control
                    type="number"
                    min="0"
                    value={versionData.minor}
                    onChange={e => setVersionData({...versionData, minor: parseInt(e.target.value)})}
                  />
                </Form.Group>
              </Col>
              <Col xs={4}>
                <Form.Group>
                  <Form.Label>Patch</Form.Label>
                  <Form.Control
                    type="number"
                    min="0"
                    value={versionData.patch}
                    onChange={e => setVersionData({...versionData, patch: parseInt(e.target.value)})}
                  />
                </Form.Group>
              </Col>
            </Row>
            
            <Row className="mb-3">
              <Col md={6}>
                <Form.Group>
                  <Form.Label>Type</Form.Label>
                  <Form.Select
                    value={versionData.type}
                    onChange={e => setVersionData({...versionData, type: e.target.value})}
                  >
                    <option value="release">Release</option>
                    <option value="beta">Beta</option>
                    <option value="alpha">Alpha</option>
                    <option value="rc">Release Candidate</option>
                  </Form.Select>
                </Form.Group>
              </Col>
              <Col md={6}>
                <Form.Group>
                  <Form.Label>Build Number</Form.Label>
                  <Form.Control
                    type="number"
                    min="1"
                    value={versionData.buildNumber}
                    onChange={e => setVersionData({...versionData, buildNumber: parseInt(e.target.value)})}
                    disabled={versionData.type === 'release'}
                  />
                </Form.Group>
              </Col>
            </Row>
            
            <Form.Group className="mb-3">
              <Form.Label>Custom Suffix (optional)</Form.Label>
              <Form.Control
                type="text"
                placeholder="e.g., snapshot, hotfix"
                value={versionData.customSuffix}
                onChange={e => setVersionData({...versionData, customSuffix: e.target.value})}
              />
            </Form.Group>
          </Form>
          
          <div className="bg-light p-3 rounded">
            <h6>Preview</h6>
            <code>{generateVersionString()}</code>
          </div>
        </Modal.Body>
        <Modal.Footer>
          <Button variant="secondary" onClick={() => setShowVersionModal(false)}>
            Cancel
          </Button>
          <Button variant="primary" onClick={saveVersionChanges}>
            Save Version
          </Button>
        </Modal.Footer>
      </Modal>
      
      {/* Build Output Modal */}
      <Modal show={showBuildOutputModal} onHide={() => setShowBuildOutputModal(false)} size="lg" centered>
        <Modal.Header closeButton>
          <Modal.Title>Build Logs: {currentBuild?.version}</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          <div className="output-filter mb-3">
            <div className="btn-group">
              <Button
                size="sm"
                variant={logFilter === 'all' ? "primary" : "outline-primary"}
                onClick={() => setLogFilter('all')}
              >
                All
              </Button>
              <Button
                size="sm"
                variant={logFilter === 'error' ? "danger" : "outline-danger"}
                onClick={() => setLogFilter('error')}
              >
                Errors
              </Button>
              <Button
                size="sm"
                variant={logFilter === 'warning' ? "warning" : "outline-warning"}
                onClick={() => setLogFilter('warning')}
              >
                Warnings
              </Button>
              <Button
                size="sm"
                variant={logFilter === 'info' ? "info" : "outline-info"}
                onClick={() => setLogFilter('info')}
              >
                Info
              </Button>
              <Button
                size="sm"
                variant={logFilter === 'success' ? "success" : "outline-success"}
                onClick={() => setLogFilter('success')}
              >
                Success
              </Button>
            </div>
          </div>
          <div className="output-console-full">
            {getFilteredBuildOutput().map((log, index) => (
              <div key={index} className={`log-entry log-${log.type}`}>
                <span className="log-time">[{new Date(log.timestamp).toLocaleTimeString()}]</span>
                <span className={`log-badge badge bg-${log.type === 'info' ? 'info' : log.type === 'warning' ? 'warning' : log.type === 'error' ? 'danger' : 'success'}`}>
                  {log.type.toUpperCase()}
                </span>
                <span className="log-message">{log.message}</span>
              </div>
            ))}
          </div>
        </Modal.Body>
        <Modal.Footer>
          <Button variant="secondary" onClick={() => setShowBuildOutputModal(false)}>
            Close
          </Button>
        </Modal.Footer>
      </Modal>
      
      {/* Download Options Modal */}
      <Modal show={showDownloadModal} onHide={() => setShowDownloadModal(false)} centered>
        <Modal.Header closeButton>
          <Modal.Title>Download Options</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          <p>You're downloading <strong>{currentBuild?.jarFile}</strong></p>
          
          <Form>
            <Form.Group className="mb-3">
              <Form.Label>Minecraft Version</Form.Label>
              <Form.Select
                value={downloadOptions.minecraftVersion}
                onChange={e => setDownloadOptions({...downloadOptions, minecraftVersion: e.target.value})}
              >
                <option value="1.21.5">1.21.5 (Latest)</option>
                <option value="1.20.1">1.20.1</option>
                <option value="1.19.4">1.19.4</option>
              </Form.Select>
            </Form.Group>
            
            <Form.Group className="mb-3">
              <Form.Check
                type="checkbox"
                label="Include source code"
                checked={downloadOptions.includeSourceCode}
                onChange={e => setDownloadOptions({...downloadOptions, includeSourceCode: e.target.checked})}
              />
            </Form.Group>
            
            <Form.Group className="mb-3">
              <Form.Check
                type="checkbox"
                label="Include development tools"
                checked={downloadOptions.includeDevTools}
                onChange={e => setDownloadOptions({...downloadOptions, includeDevTools: e.target.checked})}
              />
            </Form.Group>
          </Form>
        </Modal.Body>
        <Modal.Footer>
          <Button variant="secondary" onClick={() => setShowDownloadModal(false)}>
            Cancel
          </Button>
          <Button variant="primary" onClick={handleDownloadWithOptions}>
            Download
          </Button>
        </Modal.Footer>
      </Modal>
      
      {/* GitHub Release Modal */}
      <Modal show={showReleaseModal} onHide={() => !releaseCreating && setShowReleaseModal(false)} centered backdrop={releaseCreating ? "static" : true}>
        <Modal.Header closeButton={!releaseCreating}>
          <Modal.Title>Create GitHub Release</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          {releaseSuccess ? (
            <Alert variant="success">
              Successfully created GitHub release!
            </Alert>
          ) : (
            <>
              <p>You're creating a GitHub release for <strong>v{currentBuild?.version}</strong></p>
              
              <Form>
                <Form.Group className="mb-3">
                  <Form.Label>Release Notes</Form.Label>
                  <Form.Control
                    as="textarea"
                    rows={6}
                    value={releaseNotes}
                    onChange={e => setReleaseNotes(e.target.value)}
                  />
                </Form.Group>
                
                <Form.Group className="mb-3">
                  <Form.Check
                    type="checkbox"
                    label="This is a pre-release"
                    checked={isPrerelease}
                    onChange={e => setIsPrerelease(e.target.checked)}
                  />
                </Form.Group>
              </Form>
              
              {releaseError && (
                <Alert variant="danger" className="mt-3">
                  {releaseError}
                </Alert>
              )}
            </>
          )}
        </Modal.Body>
        <Modal.Footer>
          {!releaseSuccess && (
            <>
              <Button variant="secondary" onClick={() => setShowReleaseModal(false)} disabled={releaseCreating}>
                Cancel
              </Button>
              <Button 
                variant="primary" 
                onClick={submitGitHubRelease} 
                disabled={releaseCreating || !releaseNotes.trim()}
              >
                {releaseCreating ? (
                  <>
                    <Spinner as="span" size="sm" animation="border" role="status" className="me-2" />
                    Creating...
                  </>
                ) : (
                  "Create Release"
                )}
              </Button>
            </>
          )}
        </Modal.Footer>
      </Modal>
    </div>
  );
}

export default App;