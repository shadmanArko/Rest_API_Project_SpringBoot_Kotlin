import React from 'react';

const CampaignTable = ({ campaigns }) => {
    if (!campaigns || campaigns.length === 0) return <p>No campaign data</p>;

    return (
        <table className="campaign-table">
            <thead>
                <tr>
                    <th>Campaign</th>
                    <th>Cost</th>
                    <th>Revenue</th>
                    <th>ROAS</th>
                </tr>
            </thead>
            <tbody>
                {campaigns.map(c => {
                    const roi = c.cost > 0 ? (c.revenue / c.cost).toFixed(2) : '0.00';
                    return (
                        <tr key={c.campaignId}>
                            <td>{c.campaignId}</td>
                            <td>${c.cost.toLocaleString()}</td>
                            <td>${c.revenue.toLocaleString()}</td>
                            <td>{roi}x</td>
                        </tr>
                    );
                })}
            </tbody>
        </table>
    );
};

export default CampaignTable;
