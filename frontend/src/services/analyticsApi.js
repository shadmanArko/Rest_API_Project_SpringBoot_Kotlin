import axios from 'axios';

// Analytics Service runs on port 8081, different from the main Accounting API (8085)
const ANALYTICS_BASE_URL = 'http://localhost:8081/api/analytics';

const analyticsApi = axios.create({
    baseURL: ANALYTICS_BASE_URL,
    headers: {
        'Content-Type': 'application/json',
    },
});

export const getOverviewMetrics = async () => {
    try {
        const response = await analyticsApi.get('/overview');
        return response.data;
    } catch (error) {
        console.error('Error fetching analytics overview:', error);
        throw error;
    }
};

export const getCampaignMetrics = async () => {
    try {
        const response = await analyticsApi.get('/campaigns');
        return response.data;
    } catch (error) {
        console.error('Error fetching campaign metrics:', error);
        throw error;
    }
};

export const triggerSimulatorEvent = async (eventData) => {
    try {
        const response = await analyticsApi.post('/events', eventData);
        return response.data;
    } catch (error) {
        console.error('Error triggering simulator event:', error);
        throw error;
    }
};

export default analyticsApi;
