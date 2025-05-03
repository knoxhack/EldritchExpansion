import React, { useState, useEffect } from 'react';
import { Card, ListGroup, Button, Spinner, Badge, Form, InputGroup, Alert } from 'react-bootstrap';
import buildService from '../services/api';

const GradleTasks = () => {
  const [tasks, setTasks] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [running, setRunning] = useState(false);
  const [selectedTask, setSelectedTask] = useState(null);
  const [taskOutput, setTaskOutput] = useState(null);
  const [filter, setFilter] = useState('');
  const [customTask, setCustomTask] = useState('');
  
  // Load tasks on component mount
  useEffect(() => {
    loadTasks();
  }, []);
  
  // Load tasks from API
  const loadTasks = async () => {
    setLoading(true);
    setError(null);
    
    try {
      const data = await buildService.getGradleTasks();
      setTasks(data);
    } catch (error) {
      console.error('Error loading Gradle tasks:', error);
      setError('Failed to load Gradle tasks. Please try again.');
    } finally {
      setLoading(false);
    }
  };
  
  // Run selected Gradle task
  const runTask = async (taskName) => {
    if (running) return;
    
    setRunning(true);
    setError(null);
    setTaskOutput(null);
    setSelectedTask(taskName);
    
    try {
      const result = await buildService.runGradleTask(taskName);
      setTaskOutput(result.output);
    } catch (error) {
      console.error(`Error running Gradle task ${taskName}:`, error);
      setError(`Failed to run "${taskName}". Please check the logs.`);
    } finally {
      setRunning(false);
    }
  };
  
  // Filter tasks based on search input
  const filteredTasks = tasks.filter(task => 
    task.name.toLowerCase().includes(filter.toLowerCase()) || 
    (task.description && task.description.toLowerCase().includes(filter.toLowerCase()))
  );
  
  // Run custom task
  const handleRunCustomTask = () => {
    if (!customTask.trim()) return;
    runTask(customTask.trim());
  };
  
  // Group tasks by category
  const groupedTasks = filteredTasks.reduce((acc, task) => {
    const category = task.name.includes(':') ? task.name.split(':')[0] : 'Other';
    if (!acc[category]) {
      acc[category] = [];
    }
    acc[category].push(task);
    return acc;
  }, {});
  
  return (
    <Card className="shadow-sm mb-4">
      <Card.Header className="bg-dark text-light">
        <div className="d-flex justify-content-between align-items-center">
          <h5 className="mb-0">Gradle Tasks</h5>
          <Button 
            variant="outline-light" 
            size="sm" 
            onClick={loadTasks} 
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
        
        {/* Search filter */}
        <Form.Group className="mb-3">
          <InputGroup>
            <Form.Control
              type="text"
              placeholder="Filter tasks..."
              value={filter}
              onChange={(e) => setFilter(e.target.value)}
            />
            <Button 
              variant="outline-secondary"
              onClick={() => setFilter('')}
            >
              Clear
            </Button>
          </InputGroup>
        </Form.Group>
        
        {/* Custom task input */}
        <Form.Group className="mb-3">
          <InputGroup>
            <Form.Control
              type="text"
              placeholder="Run custom Gradle task (e.g., 'clean build')"
              value={customTask}
              onChange={(e) => setCustomTask(e.target.value)}
              disabled={running}
            />
            <Button 
              variant="primary"
              onClick={handleRunCustomTask}
              disabled={!customTask.trim() || running}
            >
              Run
            </Button>
          </InputGroup>
          <Form.Text className="text-muted">
            You can run multiple tasks by separating them with spaces
          </Form.Text>
        </Form.Group>
        
        {/* Task output */}
        {taskOutput && (
          <div className="mb-3">
            <h6>
              Output for: <Badge bg="primary">{selectedTask}</Badge>
            </h6>
            <pre className="p-2 bg-light border rounded" style={{ maxHeight: '200px', overflow: 'auto' }}>
              {taskOutput}
            </pre>
          </div>
        )}
        
        {/* Tasks list */}
        {loading ? (
          <div className="text-center p-3">
            <Spinner animation="border" />
            <p className="mt-2">Loading Gradle tasks...</p>
          </div>
        ) : filteredTasks.length === 0 ? (
          <Alert variant="info">
            No tasks found. {filter && 'Try adjusting your filter.'}
          </Alert>
        ) : (
          Object.entries(groupedTasks).map(([category, categoryTasks]) => (
            <div key={category} className="mb-3">
              <h6 className="border-bottom pb-2">{category}</h6>
              <ListGroup variant="flush">
                {categoryTasks.map(task => (
                  <ListGroup.Item key={task.name} className="d-flex justify-content-between align-items-center">
                    <div>
                      <div className="fw-bold">{task.name}</div>
                      <small className="text-muted">{task.description || 'No description available'}</small>
                    </div>
                    <Button 
                      variant="outline-primary" 
                      size="sm"
                      onClick={() => runTask(task.name)}
                      disabled={running}
                    >
                      {running && selectedTask === task.name ? (
                        <>
                          <Spinner animation="border" size="sm" className="me-1" />
                          Running...
                        </>
                      ) : 'Run'}
                    </Button>
                  </ListGroup.Item>
                ))}
              </ListGroup>
            </div>
          ))
        )}
      </Card.Body>
    </Card>
  );
};

export default GradleTasks;