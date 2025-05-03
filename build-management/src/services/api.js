import axios from 'axios';

const API_URL = 'http://localhost:5001/api';

// WebSocket connection
let socket = null;
let messageHandlers = [];

// Initialize WebSocket connection
export const initWebSocket = () => {
  if (socket) {
    socket.close();
  }
  
  socket = new WebSocket('ws://localhost:5002');
  
  socket.onopen = () => {
    console.log('WebSocket connection established');
  };
  
  socket.onmessage = (event) => {
    try {
      const data = JSON.parse(event.data);
      messageHandlers.forEach(handler => handler(data));
    } catch (error) {
      console.error('Error parsing WebSocket message:', error);
    }
  };
  
  socket.onerror = (error) => {
    console.error('WebSocket error:', error);
  };
  
  socket.onclose = () => {
    console.log('WebSocket connection closed');
    // Attempt to reconnect after a delay
    setTimeout(() => {
      initWebSocket();
    }, 5000);
  };
  
  return socket;
};

// Register a message handler
export const onWebSocketMessage = (handler) => {
  messageHandlers.push(handler);
  return () => {
    messageHandlers = messageHandlers.filter(h => h !== handler);
  };
};

// Build API service
const buildService = {
  // Get all builds
  getAllBuilds: async () => {
    try {
      const response = await axios.get(`${API_URL}/builds`);
      return response.data;
    } catch (error) {
      console.error('Error fetching builds:', error);
      throw error;
    }
  },
  
  // Get a specific build
  getBuild: async (buildId) => {
    try {
      const response = await axios.get(`${API_URL}/builds/${buildId}`);
      return response.data;
    } catch (error) {
      console.error(`Error fetching build ${buildId}:`, error);
      throw error;
    }
  },
  
  // Create a new build
  createBuild: async (buildData) => {
    try {
      const response = await axios.post(`${API_URL}/builds`, buildData);
      return response.data;
    } catch (error) {
      console.error('Error creating build:', error);
      throw error;
    }
  },
  
  // Update a build
  updateBuild: async (buildId, buildData) => {
    try {
      const response = await axios.put(`${API_URL}/builds/${buildId}`, buildData);
      return response.data;
    } catch (error) {
      console.error(`Error updating build ${buildId}:`, error);
      throw error;
    }
  },
  
  // Delete a build
  deleteBuild: async (buildId) => {
    try {
      const response = await axios.delete(`${API_URL}/builds/${buildId}`);
      return response.data;
    } catch (error) {
      console.error(`Error deleting build ${buildId}:`, error);
      throw error;
    }
  },
  
  // Start a build
  startBuild: async (buildId) => {
    try {
      const response = await axios.post(`${API_URL}/builds/${buildId}/start`);
      return response.data;
    } catch (error) {
      console.error(`Error starting build ${buildId}:`, error);
      throw error;
    }
  },
  
  // Abort a build
  abortBuild: async (buildId) => {
    try {
      const response = await axios.post(`${API_URL}/builds/${buildId}/abort`);
      return response.data;
    } catch (error) {
      console.error(`Error aborting build ${buildId}:`, error);
      throw error;
    }
  },
  
  // Get build logs
  getBuildLogs: async (buildId) => {
    try {
      const response = await axios.get(`${API_URL}/builds/${buildId}/logs`);
      return response.data;
    } catch (error) {
      console.error(`Error fetching logs for build ${buildId}:`, error);
      throw error;
    }
  },
  
  // Get Gradle tasks
  getGradleTasks: async () => {
    try {
      const response = await axios.get(`${API_URL}/gradle/tasks`);
      return response.data;
    } catch (error) {
      console.error('Error fetching Gradle tasks:', error);
      throw error;
    }
  },
  
  // Run a Gradle task
  runGradleTask: async (task) => {
    try {
      const response = await axios.post(`${API_URL}/gradle/run`, { task });
      return response.data;
    } catch (error) {
      console.error(`Error running Gradle task ${task}:`, error);
      throw error;
    }
  }
};

export default buildService;