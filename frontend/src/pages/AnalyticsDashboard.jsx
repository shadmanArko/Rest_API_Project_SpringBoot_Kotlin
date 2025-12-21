import React, { useEffect, useState, useCallback } from 'react';
import {
    getCampaigns,
    getFinancials,
    getPlatforms,
    getPlatformTrend,
    getKpis,
    triggerSimulatorEvent
} from '../services/analyticsApi';
import {
    BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer,
    AreaChart, Area, PieChart, Pie, Cell, LineChart, Line
} from 'recharts';
import '../styles/Dashboard.css';

const TABS = [
    { id: 'overview', label: 'Overview', icon: '📊' },
    { id: 'financials', label: 'Financials', icon: '💰' },
    { id: 'campaigns', label: 'Campaigns', icon: '🎯' },
    { id: 'platforms', label: 'Platforms', icon: '🌐' },
    { id: 'growth', label: 'Growth & KPIs', icon: '🚀' }
];

const COLORS = ['#6366f1', '#10b981', '#f59e0b', '#ef4444', '#8b5cf6', '#06b6d4'];

const AnalyticsDashboard = () => {
    const [activeTab, setActiveTab] = useState('overview');
    const [data, setData] = useState({
        overview: null,
        financials: [],
        campaigns: [],
        platforms: [],
        kpis: null,
        growth: null
    });
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [lastRefresh, setLastRefresh] = useState(new Date());

    // Simulator State
    const [showSimulator, setShowSimulator] = useState(false);
    const [simulating, setSimulating] = useState(false);

    const fetchData = useCallback(async () => {
        setLoading(true);
        try {
            const now = new Date();
            const from = new Date(now.getTime() - 30 * 24 * 60 * 60 * 1000).toISOString();
            const to = now.toISOString();

            const results = await Promise.allSettled([
                getFinancials(from, to, 'daily'),
                getCampaigns(from, to),
                getPlatforms(from, to),
                getKpis(from, to)
            ]);

            const [fin, camp, plat, kpi] = results.map(r => r.status === 'fulfilled' ? r.value : null);

            // Calculate Overview from campaigns
            const overview = camp ? camp.reduce((acc, c) => ({
                spend: acc.spend + c.spend,
                revenue: acc.revenue + c.revenue,
                impressions: acc.impressions + c.impressions,
                clicks: acc.clicks + c.clicks,
                orders: acc.orders + c.orders
            }), { spend: 0, revenue: 0, impressions: 0, clicks: 0, orders: 0 }) : null;

            setData({
                overview,
                financials: fin || [],
                campaigns: camp || [],
                platforms: plat || [],
                kpis: kpi
            });
            setLastRefresh(new Date());
        } catch (err) {
            console.error("Fetch Error:", err);
            setError("Failed to sync with Analytics Engine.");
        } finally {
            setLoading(false);
        }
    }, []);

    useEffect(() => {
        fetchData();
        const interval = setInterval(fetchData, 60000); // Auto-refresh every min
        return () => clearInterval(interval);
    }, [fetchData]);

    const handleSimulate = async (type, payload) => {
        setSimulating(true);
        try {
            // Map 'source' to 'platform' and add timestamp for the backend EventDto
            const eventData = {
                eventType: type,
                platform: payload.source || 'web',
                timestamp: new Date().toISOString().split('.')[0], // Format: yyyy-MM-ddTHH:mm:ss
                ...payload
            };
            await triggerSimulatorEvent(eventData);
            await fetchData();
            alert(`✅ ${type.toUpperCase()} simulated! Data synced via Postgres fallback.`);
        } catch (e) {
            alert("Simulation Failed: " + e.message);
        } finally {
            setSimulating(false);
        }
    };

    if (loading && !data.overview) {
        return (
            <div className="dashboard-loading">
                <div className="loader-spinner"></div>
                <p>Establishing Secure Connection...</p>
            </div>
        );
    }

    const renderOverview = () => (
        <div className="tab-content">
            <div className="kpi-grid">
                <div className="kpi-card premium">
                    <div className="kpi-icon">💸</div>
                    <div className="kpi-content">
                        <label>Total Ad Spend</label>
                        <div className="kpi-value">${data.overview?.spend.toLocaleString()}</div>
                        <span className="kpi-trend positive">↑ 12% vs last month</span>
                    </div>
                </div>
                <div className="kpi-card premium">
                    <div className="kpi-icon">💰</div>
                    <div className="kpi-content">
                        <label>Gross Revenue</label>
                        <div className="kpi-value">${data.overview?.revenue.toLocaleString()}</div>
                        <span className="kpi-trend positive">↑ 8.4% volume</span>
                    </div>
                </div>
                <div className="kpi-card premium">
                    <div className="kpi-icon">📈</div>
                    <div className="kpi-content">
                        <label>ROAS</label>
                        <div className="kpi-value">{(data.overview?.revenue / (data.overview?.spend || 1)).toFixed(2)}x</div>
                        <span className="kpi-trend neutral">Efficiency stable</span>
                    </div>
                </div>
                <div className="kpi-card premium">
                    <div className="kpi-icon">🛒</div>
                    <div className="kpi-content">
                        <label>Orders</label>
                        <div className="kpi-value">{data.overview?.orders}</div>
                        <span className="kpi-trend positive">New peaks reached</span>
                    </div>
                </div>
            </div>

            <div className="charts-grid">
                <div className="chart-card">
                    <div className="chart-header"><h3>Revenue vs Spend Trend</h3></div>
                    <ResponsiveContainer width="100%" height={350}>
                        <AreaChart data={data.financials}>
                            <defs>
                                <linearGradient id="colorRev" x1="0" y1="0" x2="0" y2="1">
                                    <stop offset="5%" stopColor="#10b981" stopOpacity={0.3} />
                                    <stop offset="95%" stopColor="#10b981" stopOpacity={0} />
                                </linearGradient>
                            </defs>
                            <CartesianGrid strokeDasharray="3 3" vertical={false} stroke="rgba(255,255,255,0.05)" />
                            <XAxis dataKey="date" stroke="#64748b" tick={{ fontSize: 12 }} />
                            <YAxis stroke="#64748b" tick={{ fontSize: 12 }} tickFormatter={v => `$${v}`} />
                            <Tooltip contentStyle={{ background: '#1e293b', border: '1px solid #334155' }} />
                            <Area type="monotone" dataKey="revenue" stroke="#10b981" fillOpacity={1} fill="url(#colorRev)" strokeWidth={3} />
                            <Area type="monotone" dataKey="spend" stroke="#6366f1" fill="transparent" strokeWidth={2} strokeDasharray="5 5" />
                        </AreaChart>
                    </ResponsiveContainer>
                </div>
                <div className="chart-card">
                    <div className="chart-header"><h3>Platform Dist.</h3></div>
                    <ResponsiveContainer width="100%" height={300}>
                        <PieChart>
                            <Pie data={data.platforms} innerRadius={60} outerRadius={80} paddingAngle={5} dataKey="spend" nameKey="platform">
                                {data.platforms.map((entry, index) => (
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
    );

    const renderCampaigns = () => (
        <div className="tab-content">
            <div className="chart-card" style={{ marginBottom: '2rem' }}>
                <div className="chart-header"><h3>Campaign Performance Comparison</h3></div>
                <ResponsiveContainer width="100%" height={300}>
                    <BarChart data={data.campaigns}>
                        <CartesianGrid strokeDasharray="3 3" vertical={false} stroke="rgba(255,255,255,0.05)" />
                        <XAxis dataKey="campaignId" stroke="#64748b" />
                        <YAxis stroke="#64748b" />
                        <Tooltip contentStyle={{ background: '#1e293b', border: 'none' }} />
                        <Bar dataKey="revenue" fill="#10b981" radius={[4, 4, 0, 0]} />
                        <Bar dataKey="spend" fill="#6366f1" radius={[4, 4, 0, 0]} />
                    </BarChart>
                </ResponsiveContainer>
            </div>
            <div className="recent-activity">
                <table className="premium-table">
                    <thead>
                        <tr>
                            <th>Campaign</th>
                            <th>Platform</th>
                            <th>Spend</th>
                            <th>Revenue</th>
                            <th>ROAS</th>
                            <th>Status</th>
                        </tr>
                    </thead>
                    <tbody>
                        {data.campaigns.map(c => (
                            <tr key={c.campaignId}>
                                <td>{c.campaignId}</td>
                                <td><span className="badge" style={{ background: 'rgba(99,102,241,0.1)', color: '#818cf8', padding: '2px 8px', borderRadius: '4px' }}>{c.source}</span></td>
                                <td>${c.spend.toLocaleString()}</td>
                                <td style={{ color: '#10b981', fontWeight: 600 }}>${c.revenue.toLocaleString()}</td>
                                <td>{c.roas.toFixed(2)}x</td>
                                <td><span style={{ color: '#10b981' }}>● Active</span></td>
                            </tr>
                        ))}
                    </tbody>
                </table>
            </div>
        </div>
    );

    const renderPlatforms = () => (
        <div className="tab-content">
            <div className="kpi-grid">
                {data.platforms.map((p, i) => (
                    <div className="kpi-card premium" key={p.platform}>
                        <div className="kpi-icon" style={{ color: COLORS[i % COLORS.length] }}>◈</div>
                        <div className="kpi-content">
                            <label>{p.platform}</label>
                            <div className="kpi-value">${p.revenue.toLocaleString()}</div>
                            <span className="kpi-trend">{(p.share * 100).toFixed(1)}% market share</span>
                        </div>
                    </div>
                ))}
            </div>
            {/* Trend Chart for Platforms could go here */}
        </div>
    );

    const renderGrowth = () => (
        <div className="tab-content">
            <div className="kpi-grid">
                {data.kpis?.metrics.map(m => (
                    <div className="kpi-card premium" key={m.label}>
                        <div className="kpi-content">
                            <label>{m.label}</label>
                            <div className="kpi-value">{m.current.toLocaleString()}</div>
                            <span className={`kpi-trend ${m.changePercent >= 0 ? 'positive' : 'negative'}`}>
                                {m.changePercent >= 0 ? '↑' : '↓'} {Math.abs(m.changePercent)}% vs LY
                            </span>
                        </div>
                    </div>
                ))}
            </div>
            <div className="charts-grid" style={{ marginTop: '2rem' }}>
                <div className="chart-card">
                    <h3>Unit Economics</h3>
                    <div style={{ padding: '2rem', display: 'flex', justifyContent: 'space-around' }}>
                        <div style={{ textAlign: 'center' }}>
                            <div style={{ fontSize: '2.5rem', fontWeight: 800, color: '#10b981' }}>${data.kpis?.ltv}</div>
                            <label style={{ color: '#64748b' }}>Customer LTV</label>
                        </div>
                        <div style={{ textAlign: 'center' }}>
                            <div style={{ fontSize: '2.5rem', fontWeight: 800, color: '#ef4444' }}>${data.kpis?.cac}</div>
                            <label style={{ color: '#64748b' }}>Avg. CAC</label>
                        </div>
                        <div style={{ textAlign: 'center' }}>
                            <div style={{ fontSize: '2.5rem', fontWeight: 800, color: '#6366f1' }}>3.3x</div>
                            <label style={{ color: '#64748b' }}>LTV/CAC Ratio</label>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );

    return (
        <div className="dashboard-container analytics-theme">
            <header className="dashboard-header">
                <div>
                    <h1>Executive Insights</h1>
                    <p className="subtitle">Last Synced: {lastRefresh.toLocaleTimeString()}</p>
                </div>
                <div className="header-actions">
                    <button className="refresh-btn" onClick={fetchData}>↻ Refresh</button>
                    <span className="live-indicator">● Active Stream</span>
                </div>
            </header>

            <nav className="dashboard-tabs">
                {TABS.map(tab => (
                    <button
                        key={tab.id}
                        className={`tab-btn ${activeTab === tab.id ? 'active' : ''}`}
                        onClick={() => setActiveTab(tab.id)}
                    >
                        <span>{tab.icon}</span> {tab.label}
                    </button>
                ))}
            </nav>

            {error && <div className="error-banner">{error}</div>}

            {activeTab === 'overview' && renderOverview()}
            {activeTab === 'campaigns' && renderCampaigns()}
            {activeTab === 'platforms' && renderPlatforms()}
            {activeTab === 'growth' && renderGrowth()}
            {activeTab === 'financials' && renderOverview() /* Reuse or expand */}

            {/* Simulator Toggle */}
            <button
                className="simulator-toggle-btn"
                onClick={() => setShowSimulator(true)}
            >
                ⚡ Sim
            </button>

            {/* Sidebar Simulator (Simplified for this view) */}
            <div className={`simulator-panel ${showSimulator ? 'open' : ''}`}>
                <div className="simulator-header">
                    <h3>Traffic Simulator</h3>
                    <button onClick={() => setShowSimulator(false)}>×</button>
                </div>
                <div className="simulator-body">
                    <p>Trigger live events to see real-time chart updates.</p>
                    <div className="sim-buttons">
                        <button
                            disabled={simulating}
                            onClick={() => handleSimulate('impression', { campaignId: 'CAMP_001', source: 'Google Ads' })}
                        >
                            {simulating ? 'Processing...' : 'Bulk Views'}
                        </button>
                        <button
                            disabled={simulating}
                            onClick={() => handleSimulate('order', { campaignId: 'CAMP_001', source: 'Google Ads', amount: 99.0 })}
                        >
                            {simulating ? 'Processing...' : 'New Sale'}
                        </button>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default AnalyticsDashboard;
