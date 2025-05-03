import React, { useState, useEffect, useRef } from 'react';
import { Card, Button, Form } from 'react-bootstrap';

const BuildOutput = ({ logs, filter, setFilter, clearOutput }) => {
  const [autoScroll, setAutoScroll] = useState(true);
  const outputRef = useRef(null);

  // Auto-scroll to bottom when new logs arrive
  useEffect(() => {
    if (autoScroll && outputRef.current) {
      outputRef.current.scrollTop = outputRef.current.scrollHeight;
    }
  }, [logs, autoScroll]);

  const getLogClass = (type) => {
    switch (type) {
      case 'error':
        return 'text-danger';
      case 'warning':
        return 'text-warning';
      case 'success':
        return 'text-success';
      default:
        return 'text-info';
    }
  };

  const filteredLogs = filter === 'all' 
    ? logs 
    : logs.filter(log => log.type === filter);

  return (
    <div>
      <h2>Build Output</h2>
      
      <Card className="mb-3">
        <Card.Body className="d-flex align-items-center justify-content-between">
          <Button 
            variant="outline-secondary" 
            onClick={clearOutput}
          >
            Clear Output
          </Button>
          
          <div className="d-flex align-items-center">
            <span className="me-2">Filter:</span>
            <Form.Select 
              value={filter} 
              onChange={(e) => setFilter(e.target.value)}
              style={{ width: '150px' }}
            >
              <option value="all">All</option>
              <option value="info">Info</option>
              <option value="warning">Warnings</option>
              <option value="error">Errors</option>
              <option value="success">Success</option>
            </Form.Select>
          </div>
        </Card.Body>
      </Card>
      
      <div className="d-flex align-items-center mb-2">
        <Form.Check 
          type="checkbox"
          id="auto-scroll-check"
          label="Auto-scroll"
          checked={autoScroll}
          onChange={() => setAutoScroll(!autoScroll)}
        />
      </div>
      
      <Card>
        <Card.Body 
          ref={outputRef}
          style={{ 
            height: '400px', 
            overflowY: 'auto', 
            backgroundColor: '#1E1E1E', 
            color: '#E0E0E0',
            fontFamily: 'monospace',
            fontSize: '0.85rem',
            padding: '15px'
          }}
        >
          {filteredLogs.length === 0 ? (
            <div className="text-muted">No logs to display</div>
          ) : (
            filteredLogs.map((log, index) => (
              <div key={index} className={getLogClass(log.type)}>
                <span className="text-secondary">[{new Date(log.timestamp).toLocaleTimeString()}]</span> {log.message}
              </div>
            ))
          )}
        </Card.Body>
      </Card>
    </div>
  );
};

export default BuildOutput;