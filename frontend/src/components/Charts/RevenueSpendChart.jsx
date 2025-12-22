import React from 'react';
import { BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer } from 'recharts';

const RevenueSpendChart = ({ campaigns }) => {
    if (!campaigns || campaigns.length === 0) return <p>No campaign data</p>;

    return (
        <ResponsiveContainer width="100%" height={350}>
            <BarChart data={campaigns} margin={{ top: 20, right: 30, left: 0, bottom: 5 }}>
                <CartesianGrid strokeDasharray="3 3" vertical={false} />
                <XAxis dataKey="campaignId" />
                <YAxis />
                <Tooltip />
                <Bar dataKey="revenue" fill="#10b981" />
                <Bar dataKey="cost" fill="#6366f1" />
            </BarChart>
        </ResponsiveContainer>
    );
};

export default RevenueSpendChart;
