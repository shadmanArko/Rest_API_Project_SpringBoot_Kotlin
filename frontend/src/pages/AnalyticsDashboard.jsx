import React, { useEffect, useState } from 'react';
import { getOverviewMetrics, getCampaignMetrics, triggerSimulatorEvent } from '../services/analyticsApi';
import {
    BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer,
    AreaChart, Area, PieChart, Pie, Cell
} from 'recharts';
import '../styles/Dashboard.css';

const AnalyticsDashboard = () => {
    const [overview, setOverview] = useState(null);
    const [campaigns, setCampaigns] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    // Simulator State
    const [showSimulator, setShowSimulator] = useState(false);
    const [simSource, setSimSource] = useState('Google Ads');
    const [simCampaignId, setSimCampaignId] = useState('CAMP_001');
    const [simulating, setSimulating] = useState(false);

    const fetchData = async () => {
        try {
            const [overviewData, campaignsData] = await Promise.all([
                getOverviewMetrics(),
                getCampaignMetrics()
            ]);
            setOverview(overviewData);
            setCampaigns(campaignsData);
        } catch (err) {
            console.error("Analytics Fetch Error:", err);
            setError('Failed to connect to Analytics Engine. Please ensure the service is running.');
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        // Initial Fetch with artificial delay
        const initialLoad = async () => {
            await new Promise(resolve => setTimeout(resolve, 600));
            fetchData();
        };
        initialLoad();
    }, []);

    const handleSimulate = async (type) => {
        setSimulating(true);
        try {
            let amount = null;
            let count = 1;

            if (type === 'impression') count = 100; // Bulk add
            if (type === 'click') count = 5;       // Bulk add
            if (type === 'spend') amount = 50.00;
            if (type === 'order') amount = 120.00;

            // Send multiple requests for bulk actions if needed, or loop in backend (simplified here: loop in frontend)
            const promises = [];
            for (let i = 0; i < count; i++) {
                promises.push(triggerSimulatorEvent({
                    eventType: type,
                    source: simSource,
                    campaignId: simCampaignId,
                    amount: amount
                }));
            }

            await Promise.all(promises);
            // Refresh Data
            await fetchData();
        } catch (e) {
            alert("Simulation Failed: " + e.message);
        } finally {
            setSimulating(false);
        }
    };

    // Custom Tooltip for Charts
    const CustomTooltip = ({ active, payload, label }) => {
        if (active && payload && payload.length) {
            return (
                <div className="custom-tooltip" style={{
                    backgroundColor: '#1e293b',
                    padding: '12px',
                    borderRadius: '8px',
                    border: '1px solid rgba(255,255,255,0.1)'
                }}>
                    <p className="label" style={{ color: '#fff', fontWeight: 600, marginBottom: '4px' }}>{label}</p>
                    {payload.map((entry, index) => (
                        <p key={index} style={{ color: entry.color, fontSize: '0.9rem' }}>
                            {entry.name}: ${entry.value.toLocaleString()}
                        </p>
                    ))}
                </div>
            );
        }
        return null;
    };

    if (loading) {
        return (
            <div className="dashboard-loading">
                <div className="loader-spinner"></div>
                <p>Syncing with Analytics Engine...</p>
            </div>
        );
    }

    if (error) {
        return (
            <div className="dashboard-error">
                <div className="error-icon">⚠️</div>
                <h2>Connection Failed</h2>
                <p>{error}</p>
                <button onClick={() => window.location.reload()} className="retry-btn">
                    Retry Connection
                </button>
            </div>
        );
    }

    const COLORS = ['#6366f1', '#10b981', '#f59e0b', '#ef4444', '#8b5cf6'];

    return (
        <div className="dashboard-container analytics-theme">
            <header className="dashboard-header">
                <div>
                    <h1>Marketing Performance</h1>
                    <p className="subtitle">Real-time insights across all campaigns</p>
                </div>
                <div className="header-actions">
                    <span className="live-indicator">● Live Data</span>
                </div>
            </header>

            {/* KPI Grid */}
            <div className="kpi-grid">
                <div className="kpi-card premium">
                    <div className="kpi-icon spend-icon">💸</div>
                    <div className="kpi-content">
                        <h3>Total Spend</h3>
                        <p className="kpi-value">${overview?.totalSpend?.toLocaleString(undefined, { minimumFractionDigits: 2, maximumFractionDigits: 2 })}</p>
                        <span className="kpi-trend negative">invested</span>
                    </div>
                </div>

                <div className="kpi-card premium">
                    <div className="kpi-icon revenue-icon">💰</div>
                    <div className="kpi-content">
                        <h3>Total Revenue</h3>
                        <p className="kpi-value">${overview?.totalRevenue?.toLocaleString(undefined, { minimumFractionDigits: 2, maximumFractionDigits: 2 })}</p>
                        <span className="kpi-trend positive">gross volume</span>
                    </div>
                </div>

                <div className="kpi-card premium">
                    <div className="kpi-icon roas-icon">📈</div>
                    <div className="kpi-content">
                        <h3>ROAS</h3>
                        <p className="kpi-value highlight">{overview?.roas?.toFixed(2)}x</p>
                        <span className="kpi-trend neutral">return on ad spend</span>
                    </div>
                </div>

                <div className="kpi-card premium">
                    <div className="kpi-icon clicks-icon">👆</div>
                    <div className="kpi-content">
                        <h3>Conversions</h3>
                        <p className="kpi-value">{overview?.impressions?.toLocaleString()}</p>
                        <span className="kpi-sub-value">Impressions</span>
                    </div>
                </div>
            </div>

            {/* Charts Section */}
            <div className="charts-grid">
                {/* Main Performance Chart */}
                <div className="chart-card main-chart">
                    <div className="chart-header">
                        <h3>Campaign Performance</h3>
                        <div className="chart-legend">
                            <span className="legend-item"><span className="dot revenue-dot"></span>Revenue</span>
                            <span className="legend-item"><span className="dot spend-dot"></span>Spend</span>
                        </div>
                    </div>
                    <div className="chart-wrapper">
                        <ResponsiveContainer width="100%" height={350}>
                            <BarChart data={campaigns} barGap={4} margin={{ top: 20, right: 30, left: 0, bottom: 5 }}>
                                <CartesianGrid strokeDasharray="3 3" vertical={false} stroke="rgba(255,255,255,0.05)" />
                                <XAxis
                                    dataKey="source"
                                    stroke="#94a3b8"
                                    tick={{ fontSize: 12 }}
                                    axisLine={false}
                                    tickLine={false}
                                />
                                <YAxis
                                    stroke="#94a3b8"
                                    tick={{ fontSize: 12 }}
                                    axisLine={false}
                                    tickLine={false}
                                    tickFormatter={(value) => `$${value}`}
                                />
                                <Tooltip content={<CustomTooltip />} cursor={{ fill: 'rgba(255,255,255,0.03)' }} />
                                <Bar dataKey="revenue" name="Revenue" fill="#10b981" radius={[4, 4, 0, 0]} barSize={20} />
                                <Bar dataKey="spend" name="Spend" fill="#6366f1" radius={[4, 4, 0, 0]} barSize={20} />
                            </BarChart>
                        </ResponsiveContainer>
                    </div>
                </div>

                {/* Efficiency Chart */}
                <div className="chart-card side-chart">
                    <h3>Budget Allocation</h3>
                    <div className="chart-wrapper">
                        <ResponsiveContainer width="100%" height={300}>
                            <PieChart>
                                <Pie
                                    data={campaigns}
                                    cx="50%"
                                    cy="50%"
                                    innerRadius={60}
                                    outerRadius={80}
                                    paddingAngle={5}
                                    dataKey="spend"
                                >
                                    {campaigns.map((entry, index) => (
                                        <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />
                                    ))}
                                </Pie>
                                <Tooltip />
                                <Legend />
                            </PieChart>
                        </ResponsiveContainer>
                    </div>
                </div>
            </div>

            {/* Detailed Table */}
            <div className="recent-activity">
                <h2>Campaign Details (Phase 2 Data)</h2>
                <div className="table-responsive">
                    <table className="premium-table">
                        <thead>
                            <tr>
                                <th>Source / Campaign</th>
                                <th>Impressions</th>
                                <th>Clicks</th>
                                <th>Spend</th>
                                <th>Revenue</th>
                                <th>ROAS</th>
                            </tr>
                        </thead>
                        <tbody>
                            {campaigns.map((camp) => {
                                const roas = camp.spend > 0 ? (camp.revenue / camp.spend).toFixed(2) : '0.00';
                                return (
                                    <tr key={`${camp.source}-${camp.campaignId}`}>
                                        <td>
                                            <div className="campaign-info">
                                                <span className="source-badge">{camp.source}</span>
                                                <span className="campaign-id">{camp.campaignId}</span>
                                            </div>
                                        </td>
                                        <td>{camp.impressions.toLocaleString()}</td>
                                        <td>{camp.clicks.toLocaleString()}</td>
                                        <td className="font-mono">${camp.spend.toLocaleString(undefined, { minimumFractionDigits: 2 })}</td>
                                        <td className="font-mono text-green">${camp.revenue.toLocaleString(undefined, { minimumFractionDigits: 2 })}</td>
                                        <td>
                                            <span className={`roas-badge ${parseFloat(roas) >= 2 ? 'good' : 'bad'}`}>
                                                {roas}x
                                            </span>
                                        </td>
                                    </tr>
                                );
                            })}
                        </tbody>
                    </table>
                </div>
            </div>
            {/* Simulator Sidebar */}
            <div className={`simulator-panel ${showSimulator ? 'open' : ''}`}>
                <div className="simulator-header">
                    <h3>⚡ Traffic Simulator</h3>
                    <button className="close-btn" onClick={() => setShowSimulator(false)}>×</button>
                </div>

                <div className="simulator-content">
                    <div className="form-group">
                        <label>Campaign Source</label>
                        <select
                            value={simSource}
                            onChange={(e) => setSimSource(e.target.value)}
                            className="sim-input"
                        >
                            <option value="Google Ads">Google Ads</option>
                            <option value="Facebook">Facebook</option>
                            <option value="TikTok">TikTok</option>
                            <option value="LinkedIn">LinkedIn</option>
                            <option value="Email">Email Blast</option>
                        </select>
                    </div>

                    <div className="form-group">
                        <label>Campaign ID</label>
                        <input
                            type="text"
                            value={simCampaignId}
                            onChange={(e) => setSimCampaignId(e.target.value)}
                            className="sim-input"
                            placeholder="e.g. SUMMER_SALE_2024"
                        />
                    </div>

                    <div className="sim-actions-grid">
                        <button
                            className="sim-action-btn impression"
                            onClick={() => handleSimulate('impression')}
                            disabled={simulating}
                        >
                            <div className="icon">👀</div>
                            <span>+100 Views</span>
                        </button>

                        <button
                            className="sim-action-btn click"
                            onClick={() => handleSimulate('click')}
                            disabled={simulating}
                        >
                            <div className="icon">👆</div>
                            <span>+5 Clicks</span>
                        </button>

                        <button
                            className="sim-action-btn spend"
                            onClick={() => handleSimulate('spend')}
                            disabled={simulating}
                        >
                            <div className="icon">💸</div>
                            <span>+$50 Spend</span>
                        </button>

                        <button
                            className="sim-action-btn order"
                            onClick={() => handleSimulate('order')}
                            disabled={simulating}
                        >
                            <div className="icon">🛒</div>
                            <span>+$120 Rev</span>
                        </button>
                    </div>

                    {simulating && (
                        <div className="sim-status">
                            <div className="mini-loader"></div> Sending Event...
                        </div>
                    )}
                </div>
            </div>

            <button
                className="simulator-toggle-btn"
                onClick={() => setShowSimulator(true)}
                title="Open Simulator"
            >
                ⚡ Sim
            </button>
        </div>
    );
};

export default AnalyticsDashboard;
