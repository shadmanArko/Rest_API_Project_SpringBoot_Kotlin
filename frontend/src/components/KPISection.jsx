import React from 'react';

const KPISection = ({ overview }) => {
    if (!overview) return null;

    const { totalRevenue, totalSpend, roas, impressions, orders } = overview;

    return (
        <div className="kpi-grid">
            <div className="kpi-card">
                <h3>Total Spend</h3>
                <p>${totalSpend?.toLocaleString(undefined, { minimumFractionDigits: 2 })}</p>
            </div>
            <div className="kpi-card">
                <h3>Total Revenue</h3>
                <p>${totalRevenue?.toLocaleString(undefined, { minimumFractionDigits: 2 })}</p>
            </div>
            <div className="kpi-card">
                <h3>ROAS</h3>
                <p>{roas?.toFixed(2)}x</p>
            </div>
            <div className="kpi-card">
                <h3>Impressions</h3>
                <p>{impressions?.toLocaleString()}</p>
            </div>
            <div className="kpi-card">
                <h3>Orders</h3>
                <p>{orders?.toLocaleString()}</p>
            </div>
        </div>
    );
};

export default KPISection;
