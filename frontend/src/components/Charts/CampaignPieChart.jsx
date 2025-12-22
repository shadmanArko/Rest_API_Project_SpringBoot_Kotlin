import React from 'react';
import { PieChart, Pie, Cell, Tooltip, Legend, ResponsiveContainer } from 'recharts';

const COLORS = ['#6366f1', '#10b981', '#f59e0b', '#ef4444', '#8b5cf6'];

const CampaignPieChart = ({ campaigns }) => {
    if (!campaigns || campaigns.length === 0) return <p>No campaign data</p>;

    return (
        <ResponsiveContainer width="100%" height={300}>
            <PieChart>
                <Pie data={campaigns} dataKey="cost" nameKey="campaignId" cx="50%" cy="50%" innerRadius={60} outerRadius={100}>
                    {campaigns.map((entry, index) => <Cell key={index} fill={COLORS[index % COLORS.length]} />)}
                </Pie>
                <Tooltip />
                <Legend />
            </PieChart>
        </ResponsiveContainer>
    );
};

export default CampaignPieChart;
