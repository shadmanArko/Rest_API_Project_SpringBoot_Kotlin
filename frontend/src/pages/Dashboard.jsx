import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { TrendingUp, TrendingDown, DollarSign, Plus, FileText } from 'lucide-react';
import { api } from '../services/api';
import '../styles/Dashboard.css';

const Dashboard = () => {
    const [metrics, setMetrics] = useState({
        assets: 0,
        liabilities: 0,
        equity: 0,
        revenue: 0,
        expenses: 0
    });
    const [loading, setLoading] = useState(true);

    // Hardcoded company ID
    const companyId = '550e8400-e29b-41d4-a716-446655440000';

    useEffect(() => {
        fetchMetrics();
    }, []);

    const fetchMetrics = async () => {
        try {
            const [balanceSheet, incomeStatement] = await Promise.all([
                api.getBalanceSheet(companyId),
                api.getIncomeStatement(companyId)
            ]);

            setMetrics({
                assets: balanceSheet?.assets?.total || 0,
                liabilities: balanceSheet?.liabilities?.total || 0,
                equity: balanceSheet?.equity?.total || 0,
                revenue: incomeStatement?.revenue?.total || 0,
                expenses: incomeStatement?.expenses?.total || 0
            });
        } catch (error) {
            console.error('Failed to fetch dashboard metrics', error);
        } finally {
            setLoading(false);
        }
    };

    const formatCurrency = (amount) => {
        return new Intl.NumberFormat('en-US', {
            style: 'currency',
            currency: 'USD',
            maximumFractionDigits: 0
        }).format(amount);
    };

    return (
        <div className="dashboard-page">
            <div className="welcome-section">
                <h2>Financial Overview</h2>
                <p className="text-muted">Here's what's happening with your finances today.</p>
            </div>

            <div className="metrics-grid">
                <div className="metric-card">
                    <div className="metric-icon bg-green-100 text-green-600">
                        <TrendingUp size={24} />
                    </div>
                    <div className="metric-content">
                        <p className="metric-label">Total Assets</p>
                        <h3 className="metric-value">{loading ? '...' : formatCurrency(metrics.assets)}</h3>
                    </div>
                </div>

                <div className="metric-card">
                    <div className="metric-icon bg-red-100 text-red-600">
                        <TrendingDown size={24} />
                    </div>
                    <div className="metric-content">
                        <p className="metric-label">Total Liabilities</p>
                        <h3 className="metric-value">{loading ? '...' : formatCurrency(metrics.liabilities)}</h3>
                    </div>
                </div>

                <div className="metric-card">
                    <div className="metric-icon bg-blue-100 text-blue-600">
                        <DollarSign size={24} />
                    </div>
                    <div className="metric-content">
                        <p className="metric-label">Net Income</p>
                        <h3 className="metric-value">
                            {loading ? '...' : formatCurrency(metrics.revenue - metrics.expenses)}
                        </h3>
                    </div>
                </div>
            </div>

            <div className="dashboard-actions">
                <h3>Quick Actions</h3>
                <div className="actions-grid">
                    <Link to="/journal" className="action-card">
                        <div className="action-icon">
                            <Plus size={24} />
                        </div>
                        <h4>New Journal Entry</h4>
                        <p>Post a new transaction to the general ledger</p>
                    </Link>

                    <Link to="/accounts" className="action-card">
                        <div className="action-icon">
                            <FileText size={24} />
                        </div>
                        <h4>Manage Accounts</h4>
                        <p>View chart of accounts or create new ones</p>
                    </Link>
                </div>
            </div>
        </div>
    );
};

export default Dashboard;
