import axios from 'axios';

// Analytics Service runs on port 8085 (same as the main Accounting API)
const ANALYTICS_BASE_URL = 'http://localhost:8085/api/analytics';

const analyticsApi = axios.create({
    baseURL: ANALYTICS_BASE_URL,
    headers: {
        'Content-Type': 'application/json',
    },
});

export const getCampaigns = async (from, to) => {
    try {
        const response = await analyticsApi.get('/campaigns', { params: { from, to } });
        return response.data;
    } catch (error) {
        console.error('Error fetching all campaigns:', error);
        throw error;
    }
};

export const getFinancials = async (from, to, type = 'daily') => {
    try {
        const response = await analyticsApi.get(`/financials/${type}`, { params: { from, to } });
        return response.data;
    } catch (error) {
        console.error(`Error fetching ${type} financials:`, error);
        throw error;
    }
};

export const getPlatforms = async (from, to) => {
    try {
        const response = await analyticsApi.get('/platforms', { params: { from, to } });
        return response.data;
    } catch (error) {
        console.error('Error fetching platforms:', error);
        throw error;
    }
};

export const getPlatformTrend = async (platform, from, to) => {
    try {
        const response = await analyticsApi.get(`/platforms/${platform}/trend`, { params: { from, to } });
        return response.data;
    } catch (error) {
        console.error(`Error fetching trend for ${platform}:`, error);
        throw error;
    }
};

export const getKpis = async (from, to) => {
    try {
        const response = await analyticsApi.get('/kpis', { params: { from, to } });
        return response.data;
    } catch (error) {
        console.error('Error fetching KPIs:', error);
        throw error;
    }
};

export const getGrowth = async () => {
    try {
        const response = await analyticsApi.get('/growth');
        return response.data;
    } catch (error) {
        console.error('Error fetching growth metrics:', error);
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
