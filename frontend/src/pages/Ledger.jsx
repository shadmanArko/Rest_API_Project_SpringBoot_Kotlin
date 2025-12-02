import React, { useState, useEffect } from 'react';
import { api } from '../services/api';
import '../styles/Ledger.css';

const Ledger = () => {
    const [accounts, setAccounts] = useState([]);
    const [selectedAccount, setSelectedAccount] = useState('');
    const [entries, setEntries] = useState([]);
    const [loading, setLoading] = useState(false);

    useEffect(() => {
        fetchAccounts();
    }, []);

    useEffect(() => {
        if (selectedAccount) {
            fetchLedger(selectedAccount);
        } else {
            setEntries([]);
        }
    }, [selectedAccount]);

    const fetchAccounts = async () => {
        try {
            const data = await api.getAccounts();
            setAccounts(data.sort((a, b) => a.code.localeCompare(b.code)));
        } catch (error) {
            console.error('Failed to fetch accounts', error);
        }
    };

    const fetchLedger = async (accountId) => {
        setLoading(true);
        try {
            const data = await api.getLedgerEntries(accountId);
            setEntries(data);
        } catch (error) {
            console.error('Failed to fetch ledger', error);
        } finally {
            setLoading(false);
        }
    };

    // Calculate running balance
    let runningBalance = 0;
    const entriesWithBalance = entries.map(entry => {
        const debit = parseFloat(entry.debit) || 0;
        const credit = parseFloat(entry.credit) || 0;
        // For simplicity, assuming Asset/Expense: Debit +, Credit -
        // Liability/Equity/Revenue: Credit +, Debit -
        // But for general ledger view, usually Debit is + and Credit is - for net calculation
        // Or we just show Debit/Credit columns and let user interpret.
        // Let's just show Debit/Credit columns.
        return { ...entry, debit, credit };
    });

    return (
        <div className="ledger-page">
            <div className="ledger-header card">
                <div className="form-group" style={{ marginBottom: 0 }}>
                    <label>Select Account to View Ledger</label>
                    <select
                        value={selectedAccount}
                        onChange={(e) => setSelectedAccount(e.target.value)}
                        className="account-select"
                    >
                        <option value="">-- Select Account --</option>
                        {accounts.map(acc => (
                            <option key={acc.id} value={acc.id}>
                                {acc.code} - {acc.name} ({acc.type})
                            </option>
                        ))}
                    </select>
                </div>
            </div>

            <div className="ledger-content card">
                <h3 className="section-title">
                    {selectedAccount
                        ? `Ledger for ${accounts.find(a => a.id === selectedAccount)?.name || 'Account'}`
                        : 'Select an account to view transactions'
                    }
                </h3>

                <div className="table-responsive">
                    <table className="ledger-table">
                        <thead>
                            <tr>
                                <th>Date</th>
                                <th>Journal Ref</th>
                                <th>Description</th>
                                <th className="text-right">Debit</th>
                                <th className="text-right">Credit</th>
                            </tr>
                        </thead>
                        <tbody>
                            {loading ? (
                                <tr><td colSpan="5" className="text-center">Loading...</td></tr>
                            ) : entries.length === 0 ? (
                                <tr><td colSpan="5" className="text-center text-muted">No entries found</td></tr>
                            ) : (
                                entriesWithBalance.map((entry, index) => (
                                    <tr key={index}>
                                        <td>{new Date(entry.createdAt).toLocaleDateString()}</td>
                                        <td>
                                            <span className="ref-link">{entry.journalEntryId.substring(0, 8)}...</span>
                                        </td>
                                        <td>-</td> {/* Description not currently in LedgerEntry, would need join */}
                                        <td className="text-right font-mono">
                                            {entry.debit > 0 ? entry.debit.toFixed(2) : '-'}
                                        </td>
                                        <td className="text-right font-mono">
                                            {entry.credit > 0 ? entry.credit.toFixed(2) : '-'}
                                        </td>
                                    </tr>
                                ))
                            )}
                        </tbody>
                        {entries.length > 0 && (
                            <tfoot>
                                <tr>
                                    <td colSpan="3" className="text-right font-bold">Totals</td>
                                    <td className="text-right font-bold">
                                        {entriesWithBalance.reduce((sum, e) => sum + e.debit, 0).toFixed(2)}
                                    </td>
                                    <td className="text-right font-bold">
                                        {entriesWithBalance.reduce((sum, e) => sum + e.credit, 0).toFixed(2)}
                                    </td>
                                </tr>
                            </tfoot>
                        )}
                    </table>
                </div>
            </div>
        </div>
    );
};

export default Ledger;
