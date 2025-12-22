import { useState, useEffect } from 'react';
import { fetchOverview, fetchDailyFinancials, fetchMonthlyPnL, fetchCampaignMetrics } from '../services/analyticsApi';

export const useAnalytics = ({ from, to }) => {
    const [overview, setOverview] = useState(null);
    const [daily, setDaily] = useState([]);
    const [monthly, setMonthly] = useState([]);
    const [campaigns, setCampaigns] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    const fetchAll = async () => {
        try {
            setLoading(true);
            const [overviewData, dailyData, monthlyData, campaignsData] = await Promise.all([
                fetchOverview(from, to),
                fetchDailyFinancials(from, to),
                fetchMonthlyPnL(from, to),
                fetchCampaignMetrics(from, to)
            ]);
            setOverview(overviewData);
            setDaily(dailyData);
            setMonthly(monthlyData);
            setCampaigns(campaignsData);
        } catch (err) {
            console.error(err);
            setError(err);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => { fetchAll(); }, [from, to]);

    return { overview, daily, monthly, campaigns, loading, error, refetch: fetchAll };
};
