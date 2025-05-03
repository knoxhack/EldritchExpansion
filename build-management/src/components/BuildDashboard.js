import React, { useState } from 'react';
import { Card, Button, Form, Row, Col, ProgressBar, Badge } from 'react-bootstrap';

const BuildDashboard = ({ 
  startBuild, 
  stopBuild, 
  createCheckpoint, 
  createGitHubRelease,
  buildProgress,
  buildStatus,
  currentVersion 
}) => {
  const [selectedTask, setSelectedTask] = useState('build');
  
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
              onClick={createGitHubRelease}
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
            <p className="mb-1"><strong>Current Version:</strong> {currentVersion || '0.1.0'}</p>
            <p className="mb-1"><strong>Last Release:</strong> None</p>
            <div className="p-3 mt-3 bg-light rounded">
              <p className="mb-0">Release Modules: All core and extension modules with categorized changelogs</p>
            </div>
          </div>
        </Card.Body>
      </Card>
    </div>
  );
};

export default BuildDashboard;