import axios from 'axios';

const ANALYTICS_BASE_URL = 'http://localhost:8085/api/analytics';

const api = axios.create({
    baseURL: ANALYTICS_BASE_URL,
    headers: { 'Content-Type': 'application/json' }
});

export const fetchOverview = async (from, to, platform) => {
    const params = { from, to };
    if (platform) params.platform = platform;
    const { data } = await api.get('/overview', { params });
    return data;
};

export const fetchCampaignMetrics = async (from, to) => {
    const { data } = await api.get('/campaigns/overview', { params: { from, to } });
    return data;
};

export const fetchDailyFinancials = async (from, to) => {
    const { data } = await api.get('/financials/daily', { params: { from, to } });
    return data;
};

export const fetchMonthlyPnL = async (from, to) => {
    const { data } = await api.get('/financials/pnl/monthly', { params: { from, to } });
    return data;
};

export const triggerSimulatorEvent = async (event) => {
    const { data } = await api.post('/events', event);
    return data;
};
