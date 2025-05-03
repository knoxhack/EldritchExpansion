import React, { useState, useEffect } from 'react';
import { Container, Row, Col, Card, Button, Form, Alert, Table, Badge, Nav, Navbar } from 'react-bootstrap';
import axios from 'axios';

function App() {
  const [builds, setBuilds] = useState([
    {
      id: 1,
      version: "0.1.0",
      name: "Initial Development",
      date: "2025-05-03",
      status: "In Progress",
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
    setBuilds(builds.map(build => 
      build.id === id ? { ...build, status: newStatus } : build
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

  return (
    <div className="App">
      <Navbar bg="dark" variant="dark" expand="lg" className="mb-4">
        <Container>
          <Navbar.Brand>Eldritch Expansion Build Management</Navbar.Brand>
          <Navbar.Toggle aria-controls="basic-navbar-nav" />
          <Navbar.Collapse id="basic-navbar-nav">
            <Nav className="me-auto">
              <Nav.Link onClick={() => setActiveTab('builds')} active={activeTab === 'builds'}>Builds</Nav.Link>
              <Nav.Link onClick={() => setActiveTab('create')} active={activeTab === 'create'}>Create Build</Nav.Link>
              <Nav.Link onClick={() => setActiveTab('modules')} active={activeTab === 'modules'}>Modules</Nav.Link>
            </Nav>
          </Navbar.Collapse>
        </Container>
      </Navbar>

      <Container>
        {activeTab === 'builds' && (
          <>
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
                  <tr key={build.id}>
                    <td>{build.version}</td>
                    <td>{build.name}</td>
                    <td>{build.date}</td>
                    <td>{getStatusBadge(build.status)}</td>
                    <td>{build.modules.length} modules</td>
                    <td>
                      <Button size="sm" variant="primary" className="me-2" onClick={() => setActiveTab(`details-${build.id}`)}>
                        Details
                      </Button>
                      <Button 
                        size="sm" 
                        variant={build.status === 'In Progress' ? "success" : "primary"} 
                        onClick={() => updateBuildStatus(build.id, build.status === 'In Progress' ? 'Completed' : 'In Progress')}
                        disabled={build.status === 'Completed' || build.status === 'Failed'}
                      >
                        {build.status === 'In Progress' ? "Mark Complete" : "Start Build"}
                      </Button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </Table>
          </>
        )}

        {activeTab === 'create' && (
          <Card>
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
          <>
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
                    <tr key={module}>
                      <td>{module}</td>
                      <td>
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
                      <td>{firstAddedIn}</td>
                      <td>{lastUpdatedIn}</td>
                    </tr>
                  );
                })}
              </tbody>
            </Table>
          </>
        )}

        {activeTab.startsWith('details-') && (() => {
          const buildId = parseInt(activeTab.split('-')[1]);
          const build = builds.find(b => b.id === buildId);
          
          if (!build) return <Alert variant="danger">Build not found</Alert>;
          
          return (
            <>
              <div className="d-flex justify-content-between align-items-center mb-4">
                <h2>Build Details: {build.version} - {build.name}</h2>
                <Button variant="outline-secondary" onClick={() => setActiveTab('builds')}>Back to Builds</Button>
              </div>
              <Row>
                <Col md={6}>
                  <Card className="mb-4">
                    <Card.Header>Build Information</Card.Header>
                    <Card.Body>
                      <p><strong>Version:</strong> {build.version}</p>
                      <p><strong>Name:</strong> {build.name}</p>
                      <p><strong>Date:</strong> {build.date}</p>
                      <p><strong>Status:</strong> {getStatusBadge(build.status)}</p>
                      {build.status !== 'Completed' && build.status !== 'Failed' && (
                        <div className="mt-3">
                          <Button 
                            variant={build.status === 'In Progress' ? "success" : "primary"} 
                            onClick={() => updateBuildStatus(build.id, build.status === 'In Progress' ? 'Completed' : 'In Progress')}
                          >
                            {build.status === 'In Progress' ? "Mark Complete" : "Start Build"}
                          </Button>
                          {build.status === 'In Progress' && (
                            <Button 
                              variant="danger" 
                              className="ms-2"
                              onClick={() => updateBuildStatus(build.id, 'Failed')}
                            >
                              Mark Failed
                            </Button>
                          )}
                        </div>
                      )}
                    </Card.Body>
                  </Card>
                </Col>
                <Col md={6}>
                  <Card className="mb-4">
                    <Card.Header>Modules ({build.modules.length})</Card.Header>
                    <Card.Body>
                      <div className="d-flex flex-wrap gap-2">
                        {build.modules.map(module => (
                          <Badge key={module} bg="info" className="p-2">{module}</Badge>
                        ))}
                      </div>
                    </Card.Body>
                  </Card>
                </Col>
              </Row>
              <Card>
                <Card.Header>Changes</Card.Header>
                <Card.Body>
                  <ul className="list-group">
                    {build.changes.map((change, index) => (
                      <li key={index} className="list-group-item">{change}</li>
                    ))}
                  </ul>
                </Card.Body>
              </Card>
            </>
          );
        })()}
      </Container>
    </div>
  );
}

export default App;