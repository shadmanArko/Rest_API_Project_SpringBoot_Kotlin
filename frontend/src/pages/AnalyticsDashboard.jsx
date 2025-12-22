import React, { useState } from 'react';
import { useAnalytics } from '../hooks/useAnalytics';
import KPISection from '../components/KPISection';
import RevenueSpendChart from '../components/Charts/RevenueSpendChart';
import DailyTrendsChart from '../components/Charts/DailyTrendsChart';
import CampaignPieChart from '../components/Charts/CampaignPieChart';
import CampaignTable from '../components/CampaignTable';
import SimulatorPanel from '../components/SimulatorPanel';
import { subDays, formatISO } from 'date-fns';

const AnalyticsDashboard = () => {
    const [from, setFrom] = useState(formatISO(subDays(new Date(), 30), { representation: 'date' }));
    const [to, setTo] = useState(formatISO(new Date(), { representation: 'date' }));
    const { overview, daily, monthly, campaigns, loading, error } = useAnalytics({ from, to });

    if (loading) return <div className="loader">Loading Analytics...</div>;
    if (error) return <div className="error">Failed to load analytics.</div>;

    return (
        <div className="dashboard">
            <KPISection overview={overview} />
            <RevenueSpendChart campaigns={campaigns} />
            <DailyTrendsChart daily={daily} />
            <CampaignPieChart campaigns={campaigns} />
            <CampaignTable campaigns={campaigns} />
            <SimulatorPanel />
        </div>
    );
};

export default AnalyticsDashboard;
