import React, { useState, useEffect } from 'react';
import { api } from '../services/api';
import '../styles/Reports.css';

const Reports = () => {
    const [activeTab, setActiveTab] = useState('balance-sheet');
    const [reportData, setReportData] = useState(null);
    const [loading, setLoading] = useState(false);

    // Hardcoded company ID for now
    const companyId = '550e8400-e29b-41d4-a716-446655440000';

    useEffect(() => {
        fetchReport(activeTab);
    }, [activeTab]);

    const fetchReport = async (type) => {
        setLoading(true);
        try {
            let data;
            if (type === 'balance-sheet') {
                data = await api.getBalanceSheet(companyId);
            } else if (type === 'income-statement') {
                data = await api.getIncomeStatement(companyId);
            } else if (type === 'cash-flow') {
                data = await api.getCashFlow(companyId);
            }
            setReportData(data);
        } catch (error) {
            console.error('Failed to fetch report', error);
        } finally {
            setLoading(false);
        }
    };

    const formatCurrency = (amount) => {
        return new Intl.NumberFormat('en-US', {
            style: 'currency',
            currency: 'USD'
        }).format(amount);
    };

    const renderBalanceSheet = () => {
        if (!reportData) return null;
        const { assets, liabilities, equity, isBalanced } = reportData;

        return (
            <div className="report-content">
                <div className="report-header text-center">
                    <h3>Balance Sheet</h3>
                    <p className="text-muted">As of {new Date().toLocaleDateString()}</p>
                </div>

                <div className="report-grid">
                    <div className="report-section card">
                        <h4>Assets</h4>
                        <table className="report-table">
                            <tbody>
                                {assets?.items?.map((item, i) => (
                                    <tr key={i}>
                                        <td>{item.accountName || item.name}</td>
                                        <td className="text-right">{formatCurrency(item.amount)}</td>
                                    </tr>
                                ))}
                            </tbody>
                            <tfoot>
                                <tr>
                                    <th>Total Assets</th>
                                    <th className="text-right">{formatCurrency(assets?.total || 0)}</th>
                                </tr>
                            </tfoot>
                        </table>
                    </div>

                    <div className="report-section card">
                        <h4>Liabilities & Equity</h4>

                        <div className="subsection">
                            <h5>Liabilities</h5>
                            <table className="report-table">
                                <tbody>
                                    {liabilities?.items?.map((item, i) => (
                                        <tr key={i}>
                                            <td>{item.accountName || item.name}</td>
                                            <td className="text-right">{formatCurrency(item.amount)}</td>
                                        </tr>
                                    ))}
                                </tbody>
                                <tfoot>
                                    <tr>
                                        <th>Total Liabilities</th>
                                        <th className="text-right">{formatCurrency(liabilities?.total || 0)}</th>
                                    </tr>
                                </tfoot>
                            </table>
                        </div>

                        <div className="subsection mt-4">
                            <h5>Equity</h5>
                            <table className="report-table">
                                <tbody>
                                    {equity?.items?.map((item, i) => (
                                        <tr key={i}>
                                            <td>{item.accountName || item.name}</td>
                                            <td className="text-right">{formatCurrency(item.amount)}</td>
                                        </tr>
                                    ))}
                                </tbody>
                                <tfoot>
                                    <tr>
                                        <th>Total Equity</th>
                                        <th className="text-right">{formatCurrency(equity?.total || 0)}</th>
                                    </tr>
                                </tfoot>
                            </table>
                        </div>

                        <div className="total-row mt-4 pt-4 border-t">
                            <div className="flex justify-between font-bold">
                                <span>Total Liabilities & Equity</span>
                                <span>{formatCurrency((liabilities?.total || 0) + (equity?.total || 0))}</span>
                            </div>
                        </div>
                    </div>
                </div>

                <div className={`balance-status ${isBalanced ? 'success' : 'error'}`}>
                    {isBalanced ? 'Balance Sheet is Balanced' : 'Balance Sheet is NOT Balanced'}
                </div>
            </div>
        );
    };

    const renderIncomeStatement = () => {
        if (!reportData) return null;
        const { revenue, expenses, netIncome } = reportData;

        return (
            <div className="report-content max-w-3xl mx-auto card">
                <div className="report-header text-center">
                    <h3>Income Statement</h3>
                    <p className="text-muted">For the period ending {new Date().toLocaleDateString()}</p>
                </div>

                <div className="report-section">
                    <h4>Revenue</h4>
                    <table className="report-table">
                        <tbody>
                            {revenue?.items?.map((item, i) => (
                                <tr key={i}>
                                    <td>{item.accountName || item.name}</td>
                                    <td className="text-right">{formatCurrency(item.amount)}</td>
                                </tr>
                            ))}
                        </tbody>
                        <tfoot>
                            <tr>
                                <th>Total Revenue</th>
                                <th className="text-right">{formatCurrency(revenue?.total || 0)}</th>
                            </tr>
                        </tfoot>
                    </table>
                </div>

                <div className="report-section mt-6">
                    <h4>Expenses</h4>
                    <table className="report-table">
                        <tbody>
                            {expenses?.items?.map((item, i) => (
                                <tr key={i}>
                                    <td>{item.accountName || item.name}</td>
                                    <td className="text-right">{formatCurrency(item.amount)}</td>
                                </tr>
                            ))}
                        </tbody>
                        <tfoot>
                            <tr>
                                <th>Total Expenses</th>
                                <th className="text-right">{formatCurrency(expenses?.total || 0)}</th>
                            </tr>
                        </tfoot>
                    </table>
                </div>

                <div className="net-income-row mt-6 pt-4 border-t-2">
                    <div className="flex justify-between text-xl font-bold">
                        <span>Net Income</span>
                        <span className={netIncome >= 0 ? 'text-green-600' : 'text-red-600'}>
                            {formatCurrency(netIncome || 0)}
                        </span>
                    </div>
                </div>
            </div>
        );
    };

    const renderCashFlow = () => {
        if (!reportData) return null;
        const { operating, investing, financing, netCashFlow } = reportData;

        return (
            <div className="report-content max-w-3xl mx-auto card">
                <div className="report-header text-center">
                    <h3>Cash Flow Statement</h3>
                    <p className="text-muted">For the period ending {new Date().toLocaleDateString()}</p>
                </div>

                <table className="report-table">
                    <tbody>
                        <tr>
                            <td className="font-bold">Operating Activities</td>
                            <td className="text-right">{formatCurrency(operating?.amount || 0)}</td>
                        </tr>
                        <tr>
                            <td className="font-bold">Investing Activities</td>
                            <td className="text-right">{formatCurrency(investing?.amount || 0)}</td>
                        </tr>
                        <tr>
                            <td className="font-bold">Financing Activities</td>
                            <td className="text-right">{formatCurrency(financing?.amount || 0)}</td>
                        </tr>
                    </tbody>
                    <tfoot>
                        <tr className="total-row">
                            <th>Net Cash Flow</th>
                            <th className="text-right">{formatCurrency(netCashFlow || 0)}</th>
                        </tr>
                    </tfoot>
                </table>
            </div>
        );
    };

    return (
        <div className="reports-page">
            <div className="tabs-container">
                <button
                    className={`tab-btn ${activeTab === 'balance-sheet' ? 'active' : ''}`}
                    onClick={() => setActiveTab('balance-sheet')}
                >
                    Balance Sheet
                </button>
                <button
                    className={`tab-btn ${activeTab === 'income-statement' ? 'active' : ''}`}
                    onClick={() => setActiveTab('income-statement')}
                >
                    Income Statement
                </button>
                <button
                    className={`tab-btn ${activeTab === 'cash-flow' ? 'active' : ''}`}
                    onClick={() => setActiveTab('cash-flow')}
                >
                    Cash Flow
                </button>
            </div>

            <div className="report-display">
                {loading ? (
                    <div className="text-center py-8">Loading report...</div>
                ) : (
                    <>
                        {activeTab === 'balance-sheet' && renderBalanceSheet()}
                        {activeTab === 'income-statement' && renderIncomeStatement()}
                        {activeTab === 'cash-flow' && renderCashFlow()}
                    </>
                )}
            </div>
        </div>
    );
};

export default Reports;
