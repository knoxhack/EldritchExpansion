import React, { useState, useEffect } from 'react';
import { Card, Button, Form, Row, Col, ProgressBar, Badge, Modal, Spinner, Alert } from 'react-bootstrap';
import axios from 'axios';

const BuildDashboard = ({ 
  startBuild, 
  stopBuild, 
  createCheckpoint, 
  buildProgress,
  buildStatus,
  currentVersion 
}) => {
  const [selectedTask, setSelectedTask] = useState('build');
  const [showReleaseModal, setShowReleaseModal] = useState(false);
  const [releaseNotes, setReleaseNotes] = useState('');
  const [isPrerelease, setIsPrerelease] = useState(false);
  const [isCreatingRelease, setIsCreatingRelease] = useState(false);
  const [releaseSuccess, setReleaseSuccess] = useState(false);
  const [releaseError, setReleaseError] = useState(null);
  const [versionInfo, setVersionInfo] = useState({
    major: 0,
    minor: 1,
    patch: 0,
    build: 1,
    versionString: '0.1.0',
    fullVersion: '0.1.0-build.1',
    lastRelease: null
  });
  
  // Fetch current version on mount
  useEffect(() => {
    const fetchVersion = async () => {
      try {
        const response = await axios.get('http://localhost:5001/api/version');
        setVersionInfo(response.data);
      } catch (error) {
        console.error('Error fetching version:', error);
      }
    };
    
    fetchVersion();
  }, []);
  
  // Handle GitHub release 
  const handleCreateGitHubRelease = () => {
    setReleaseNotes(`# Release Notes for version ${versionInfo.versionString}

## New Features
- 

## Bug Fixes
- 

## Improvements
- 

## Modules Updated
- Core
- VoidAlchemy
`);
    setIsPrerelease(false);
    setReleaseSuccess(false);
    setReleaseError(null);
    setShowReleaseModal(true);
  };
  
  // Submit GitHub release
  const submitGitHubRelease = async () => {
    setIsCreatingRelease(true);
    setReleaseError(null);
    
    try {
      // Assuming the latest successful build has ID 1 for this demo
      const buildId = 1;
      
      const response = await axios.post('http://localhost:5001/api/github/release', {
        buildId,
        releaseNotes,
        isPrerelease
      });
      
      // Update version info from the response
      if (response.data && response.data.release) {
        const release = response.data.release;
        setVersionInfo(prev => ({
          ...prev,
          versionString: release.version,
          fullVersion: release.fullVersion,
          lastRelease: {
            date: release.releaseDate,
            version: release.fullVersion,
            url: release.githubReleaseUrl
          }
        }));
      }
      
      setReleaseSuccess(true);
      
      // Close the modal after a delay to show success message
      setTimeout(() => {
        setShowReleaseModal(false);
      }, 2000);
    } catch (error) {
      console.error('Error creating GitHub release:', error);
      setReleaseError(error.response?.data?.error || 'Failed to create GitHub release');
    } finally {
      setIsCreatingRelease(false);
    }
  };
  
  return (
    <div>
      <h2 className="mb-4">Build Status Dashboard</h2>
      
      {/* Build status card */}
      <Card className="mb-4">
        <Card.Body>
          <h4>Build Status: {buildStatus}</h4>
          <ProgressBar 
            now={buildProgress} 
            label={`${buildProgress}%`} 
            variant="primary" 
            animated={buildStatus === 'Building'} 
            className="mb-3" 
          />
        </Card.Body>
      </Card>
      
      {/* Module status card */}
      <Card className="mb-4">
        <Card.Body>
          <h4>Module Status</h4>
          
          <div className="module-list">
            {['Core', 'Power', 'Machinery', 'Biotech', 'Construction', 'Robotics', 'Space'].map(module => (
              <Row key={module} className="py-2 module-row">
                <Col>
                  <span className="module-name">{module}</span>
                </Col>
                <Col>
                  <ProgressBar now={75} className="module-progress" />
                </Col>
                <Col xs={1}>
                  <Badge 
                    bg="secondary" 
                    className="module-status-badge"
                    style={{ borderRadius: '50%', width: '30px', height: '30px', display: 'flex', alignItems: 'center', justifyContent: 'center' }}
                  />
                </Col>
              </Row>
            ))}
          </div>
        </Card.Body>
      </Card>
      
      {/* Build actions card */}
      <Card>
        <Card.Body>
          <Form.Group className="mb-3">
            <Form.Select 
              value={selectedTask}
              onChange={(e) => setSelectedTask(e.target.value)}
            >
              <option value="build">build</option>
              <option value="clean">clean</option>
              <option value="buildDependents">buildDependents</option>
              <option value="javadoc">javadoc</option>
              <option value="runClient">runClient</option>
              <option value="runServer">runServer</option>
            </Form.Select>
          </Form.Group>
          
          <div className="d-grid gap-2">
            <Button 
              variant="primary" 
              onClick={() => startBuild(selectedTask)}
              disabled={buildStatus === 'Building'}
              className="mb-2"
            >
              Start Build
            </Button>
            
            <Button 
              variant="danger" 
              onClick={stopBuild}
              disabled={buildStatus !== 'Building'}
              className="mb-2"
            >
              Stop Build
            </Button>
            
            <Button 
              variant="info" 
              onClick={createCheckpoint}
              className="mb-2"
            >
              Create Checkpoint
            </Button>
            
            <Button 
              variant="success" 
              onClick={handleCreateGitHubRelease}
            >
              Create GitHub Release
            </Button>
          </div>
        </Card.Body>
      </Card>
      
      {/* Version information card */}
      <Card className="mt-4">
        <Card.Body>
          <h4 className="text-primary">Version Information</h4>
          <hr />
          <div>
            <p className="mb-1"><strong>Current Version:</strong> {versionInfo.fullVersion}</p>
            <p className="mb-1">
              <strong>Last Release:</strong> {versionInfo.lastRelease ? (
                <a href={versionInfo.lastRelease.url} target="_blank" rel="noopener noreferrer">
                  {versionInfo.lastRelease.version} ({new Date(versionInfo.lastRelease.date).toLocaleDateString()})
                </a>
              ) : 'None'}
            </p>
            <div className="p-3 mt-3 bg-light rounded">
              <p className="mb-0">Release Modules: All core and extension modules with categorized changelogs</p>
            </div>
          </div>
        </Card.Body>
      </Card>

      {/* GitHub Release Modal */}
      <Modal show={showReleaseModal} onHide={() => !isCreatingRelease && setShowReleaseModal(false)} size="lg">
        <Modal.Header closeButton={!isCreatingRelease}>
          <Modal.Title>Create GitHub Release</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          {releaseSuccess ? (
            <div className="text-center p-4">
              <h4 className="text-success mb-3">Release Created Successfully!</h4>
              <p className="mb-0">
                Version <strong>{versionInfo.fullVersion}</strong> has been released to GitHub.
              </p>
              <p className="mt-2">
                <a href={versionInfo.lastRelease?.url} target="_blank" rel="noopener noreferrer">
                  View Release on GitHub
                </a>
              </p>
            </div>
          ) : (
            <>
              <p className="mb-3">
                Creating a new GitHub release from the latest successful build.
                This will automatically increment the build number.
              </p>
              
              <Form.Group className="mb-3">
                <Form.Label>Version Information</Form.Label>
                <div className="p-3 bg-light rounded">
                  <p className="mb-1">
                    <strong>Current Version:</strong> {versionInfo.versionString}
                  </p>
                  <p className="mb-1">
                    <strong>Build Number:</strong> {versionInfo.build}
                  </p>
                  <p className="mb-0">
                    <strong>New Release Version:</strong> {versionInfo.versionString}-build.{versionInfo.build + 1}
                  </p>
                </div>
              </Form.Group>
              
              <Form.Group className="mb-3">
                <Form.Label>Release Notes</Form.Label>
                <Form.Control 
                  as="textarea" 
                  rows={10} 
                  value={releaseNotes}
                  onChange={(e) => setReleaseNotes(e.target.value)}
                  disabled={isCreatingRelease}
                />
                <Form.Text muted>
                  Markdown formatting is supported
                </Form.Text>
              </Form.Group>
              
              <Form.Group className="mb-3">
                <Form.Check
                  type="checkbox"
                  id="prerelease-checkbox"
                  label="Mark as pre-release"
                  checked={isPrerelease}
                  onChange={(e) => setIsPrerelease(e.target.checked)}
                  disabled={isCreatingRelease}
                />
              </Form.Group>
              
              {releaseError && (
                <Alert variant="danger">
                  {releaseError}
                </Alert>
              )}
            </>
          )}
        </Modal.Body>
        <Modal.Footer>
          {!releaseSuccess && (
            <>
              <Button 
                variant="secondary" 
                onClick={() => setShowReleaseModal(false)}
                disabled={isCreatingRelease}
              >
                Cancel
              </Button>
              <Button 
                variant="primary" 
                onClick={submitGitHubRelease}
                disabled={isCreatingRelease || !releaseNotes.trim()}
              >
                {isCreatingRelease ? (
                  <>
                    <Spinner 
                      as="span"
                      animation="border"
                      size="sm"
                      role="status"
                      aria-hidden="true"
                      className="me-2"
                    />
                    Creating Release...
                  </>
                ) : 'Create GitHub Release'}
              </Button>
            </>
          )}
          {releaseSuccess && (
            <Button 
              variant="primary" 
              onClick={() => setShowReleaseModal(false)}
            >
              Close
            </Button>
          )}
        </Modal.Footer>
      </Modal>
    </div>
  );
};

export default BuildDashboard;