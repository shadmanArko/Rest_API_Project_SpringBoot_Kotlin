import React from 'react';
import { AreaChart, Area, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer } from 'recharts';

const DailyTrendsChart = ({ daily }) => {
    if (!daily || daily.length === 0) return <p>No daily data</p>;

    return (
        <ResponsiveContainer width="100%" height={300}>
            <AreaChart data={daily}>
                <CartesianGrid strokeDasharray="3 3" />
                <XAxis dataKey="date" />
                <YAxis />
                <Tooltip />
                <Area type="monotone" dataKey="revenue" stroke="#10b981" fill="#d1fae5" />
                <Area type="monotone" dataKey="expense" stroke="#ef4444" fill="#fee2e2" />
            </AreaChart>
        </ResponsiveContainer>
    );
};

export default DailyTrendsChart;
