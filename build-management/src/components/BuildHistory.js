import React, { useState, useEffect } from 'react';
import { Card, Table, Badge, Button, Spinner, Alert } from 'react-bootstrap';
import buildService from '../services/api';

const BuildHistory = () => {
  const [builds, setBuilds] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  
  // Load builds on component mount
  useEffect(() => {
    loadBuilds();
  }, []);
  
  // Load builds from API
  const loadBuilds = async () => {
    setLoading(true);
    setError(null);
    
    try {
      const data = await buildService.getAllBuilds();
      setBuilds(data);
    } catch (error) {
      console.error('Error loading builds:', error);
      setError('Failed to load build history. Please try again.');
    } finally {
      setLoading(false);
    }
  };
  
  // Format date for display
  const formatDate = (dateString) => {
    if (!dateString) return 'N/A';
    const date = new Date(dateString);
    return date.toLocaleString();
  };
  
  // Get badge color based on build status
  const getStatusBadge = (status) => {
    switch (status) {
      case 'success':
        return <Badge bg="success">Success</Badge>;
      case 'failed':
        return <Badge bg="danger">Failed</Badge>;
      case 'running':
        return <Badge bg="primary">Running</Badge>;
      case 'queued':
        return <Badge bg="warning" text="dark">Queued</Badge>;
      case 'aborted':
        return <Badge bg="secondary">Aborted</Badge>;
      default:
        return <Badge bg="info">Unknown</Badge>;
    }
  };
  
  return (
    <Card className="shadow-sm mb-4">
      <Card.Header className="bg-dark text-light">
        <div className="d-flex justify-content-between align-items-center">
          <h5 className="mb-0">Build History</h5>
          <Button 
            variant="outline-light" 
            size="sm" 
            onClick={loadBuilds} 
            disabled={loading}
          >
            {loading ? (
              <>
                <Spinner animation="border" size="sm" className="me-1" />
                Loading...
              </>
            ) : 'Refresh'}
          </Button>
        </div>
      </Card.Header>
      
      <Card.Body>
        {error && (
          <Alert variant="danger" className="mb-3">
            {error}
          </Alert>
        )}
        
        {loading ? (
          <div className="text-center p-3">
            <Spinner animation="border" />
            <p className="mt-2">Loading build history...</p>
          </div>
        ) : builds.length === 0 ? (
          <Alert variant="info">
            No builds found. Run a Gradle task to create a build.
          </Alert>
        ) : (
          <Table striped hover responsive>
            <thead>
              <tr>
                <th>ID</th>
                <th>Name</th>
                <th>Status</th>
                <th>Started</th>
                <th>Completed</th>
                <th>Duration</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {builds.map(build => (
                <tr key={build.id}>
                  <td>{build.id}</td>
                  <td>{build.name}</td>
                  <td>{getStatusBadge(build.status)}</td>
                  <td>{formatDate(build.startTime)}</td>
                  <td>{formatDate(build.endTime)}</td>
                  <td>
                    {build.duration 
                      ? `${Math.round(build.duration / 1000)}s` 
                      : build.status === 'running' 
                        ? 'In progress' 
                        : 'N/A'}
                  </td>
                  <td>
                    <Button 
                      variant="outline-info" 
                      size="sm" 
                      className="me-1"
                      onClick={() => {
                        // Handle viewing build logs
                      }}
                    >
                      Logs
                    </Button>
                    {build.status === 'running' && (
                      <Button 
                        variant="outline-danger" 
                        size="sm"
                        onClick={() => {
                          // Handle aborting build
                        }}
                      >
                        Abort
                      </Button>
                    )}
                  </td>
                </tr>
              ))}
            </tbody>
          </Table>
        )}
      </Card.Body>
    </Card>
  );
};

export default BuildHistory;