import React, { useState, useEffect } from 'react';
import { Plus, Trash2, Save } from 'lucide-react';
import { api } from '../services/api';
import '../styles/JournalEntry.css';

const JournalEntry = () => {
    const [accounts, setAccounts] = useState([]);
    const [entries, setEntries] = useState([]);
    const [view, setView] = useState('list'); // 'list' or 'form'
    const [editingId, setEditingId] = useState(null);
    const [loading, setLoading] = useState(false);
    const [formData, setFormData] = useState({
        date: new Date().toISOString().split('T')[0],
        reference: '',
        description: '',
        lines: [
            { accountId: '', description: '', debit: '', credit: '' },
            { accountId: '', description: '', debit: '', credit: '' }
        ]
    });

    const companyId = '550e8400-e29b-41d4-a716-446655440000'; // Example UUID

    useEffect(() => {
        fetchAccounts();
        fetchHistory();
    }, []);

    const fetchHistory = async () => {
        try {
            const data = await api.getJournalEntries(companyId);
            setEntries(data);
        } catch (error) {
            console.error('Failed to fetch history', error);
        }
    };

    const fetchAccounts = async () => {
        try {
            const data = await api.getAccounts();
            setAccounts(data.sort((a, b) => a.code.localeCompare(b.code)));
        } catch (error) {
            console.error('Failed to fetch accounts', error);
        }
    };

    const handleHeadChange = (e) => {
        const { name, value } = e.target;
        setFormData(prev => ({ ...prev, [name]: value }));
    };

    const handleLineChange = (index, field, value) => {
        const newLines = [...formData.lines];
        newLines[index][field] = value;

        // Auto-clear opposite field if debit/credit is entered
        if (field === 'debit' && value) newLines[index].credit = '';
        if (field === 'credit' && value) newLines[index].debit = '';

        setFormData(prev => ({ ...prev, lines: newLines }));
    };

    const addLine = () => {
        setFormData(prev => ({
            ...prev,
            lines: [...prev.lines, { accountId: '', description: '', debit: '', credit: '' }]
        }));
    };

    const removeLine = (index) => {
        if (formData.lines.length <= 2) return;
        setFormData(prev => ({
            ...prev,
            lines: prev.lines.filter((_, i) => i !== index)
        }));
    };

    const calculateTotals = () => {
        return formData.lines.reduce((acc, line) => ({
            debit: acc.debit + (parseFloat(line.debit) || 0),
            credit: acc.credit + (parseFloat(line.credit) || 0)
        }), { debit: 0, credit: 0 });
    };

    const totals = calculateTotals();
    const isBalanced = Math.abs(totals.debit - totals.credit) < 0.01;

    const handleEdit = (entry) => {
        setEditingId(entry.id);
        setFormData({
            date: entry.date,
            reference: entry.reference || '',
            description: entry.description || '',
            lines: entry.lines.map(l => ({
                accountId: l.accountId,
                description: l.description || '',
                debit: l.debit.toString(),
                credit: l.credit.toString()
            }))
        });
        setView('form');
    };

    const resetForm = () => {
        setFormData({
            date: new Date().toISOString().split('T')[0],
            reference: '',
            description: '',
            lines: [
                { accountId: '', description: '', debit: '', credit: '' },
                { accountId: '', description: '', debit: '', credit: '' }
            ]
        });
        setEditingId(null);
        setView('list');
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        if (!isBalanced) {
            alert('Journal entry must be balanced!');
            return;
        }

        setLoading(true);
        try {
            const payload = {
                ...formData,
                lines: formData.lines.map(line => ({
                    ...line,
                    debit: parseFloat(line.debit) || 0,
                    credit: parseFloat(line.credit) || 0
                }))
            };

            if (editingId) {
                await api.updateJournalEntry(editingId, payload, companyId);
                alert('Journal entry updated successfully!');
            } else {
                await api.createJournalEntry(payload, companyId);
                alert('Journal entry posted successfully!');
            }

            resetForm();
            fetchHistory();
        } catch (error) {
            console.error('Failed to save journal entry', error);
            alert('Failed to save journal entry');
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="journal-page">
            <div className="page-header">
                <h2>{view === 'list' ? 'Journal Entries' : (editingId ? 'Edit Journal Entry' : 'New Journal Entry')}</h2>
                <div className="header-actions">
                    {view === 'list' ? (
                        <button className="btn-primary" onClick={() => { resetForm(); setView('form'); }}>
                            <Plus size={20} />
                            New Entry
                        </button>
                    ) : (
                        <div className="form-actions">
                            <button className="btn-text" onClick={() => setView('list')} style={{ marginRight: '1rem' }}>
                                Cancel
                            </button>
                            <button
                                className="btn-primary"
                                onClick={handleSubmit}
                                disabled={loading || !isBalanced || totals.debit === 0}
                            >
                                <Save size={20} />
                                {loading ? 'Saving...' : (editingId ? 'Save Changes' : 'Post Entry')}
                            </button>
                        </div>
                    )}
                </div>
            </div>

            {view === 'list' ? (
                <div className="history-container card">
                    <table className="history-table">
                        <thead>
                            <tr>
                                <th>Date</th>
                                <th>Reference</th>
                                <th>Description</th>
                                <th className="text-right">Amount</th>
                                <th className="text-center">Action</th>
                            </tr>
                        </thead>
                        <tbody>
                            {entries.map(entry => {
                                const total = entry.lines.reduce((sum, l) => sum + l.debit, 0);
                                return (
                                    <tr key={entry.id}>
                                        <td>{entry.date}</td>
                                        <td>{entry.reference || '-'}</td>
                                        <td>{entry.description}</td>
                                        <td className="text-right font-bold">{total.toFixed(2)}</td>
                                        <td className="text-center">
                                            <button
                                                className="btn-text sm"
                                                onClick={() => handleEdit(entry)}
                                            >
                                                Edit
                                            </button>
                                        </td>
                                    </tr>
                                );
                            })}
                            {entries.length === 0 && (
                                <tr>
                                    <td colSpan="5" className="text-center muted" style={{ padding: '2rem' }}>
                                        No journal entries found.
                                    </td>
                                </tr>
                            )}
                        </tbody>
                    </table>
                </div>
            ) : (
                <div className="journal-form card">
                    {/* Header Fields */}
                    <div className="form-row">
                        <div className="form-group">
                            <label>Date</label>
                            <input
                                type="date"
                                name="date"
                                value={formData.date}
                                onChange={handleHeadChange}
                                required
                            />
                        </div>
                        <div className="form-group">
                            <label>Reference</label>
                            <input
                                type="text"
                                name="reference"
                                value={formData.reference}
                                onChange={handleHeadChange}
                                placeholder="e.g. INV-001"
                            />
                        </div>
                        <div className="form-group flex-2">
                            <label>Description</label>
                            <input
                                type="text"
                                name="description"
                                value={formData.description}
                                onChange={handleHeadChange}
                                placeholder="Brief description of the transaction"
                            />
                        </div>
                    </div>

                    {/* Lines Table */}
                    <div className="lines-container">
                        <table className="lines-table">
                            <thead>
                                <tr>
                                    <th style={{ width: '30%' }}>Account</th>
                                    <th style={{ width: '30%' }}>Description</th>
                                    <th style={{ width: '15%' }}>Debit</th>
                                    <th style={{ width: '15%' }}>Credit</th>
                                    <th style={{ width: '5%' }}></th>
                                </tr>
                            </thead>
                            <tbody>
                                {formData.lines.map((line, index) => (
                                    <tr key={index}>
                                        <td>
                                            <select
                                                value={line.accountId}
                                                onChange={(e) => handleLineChange(index, 'accountId', e.target.value)}
                                                className="line-input"
                                            >
                                                <option value="">Select Account</option>
                                                {accounts.map(acc => (
                                                    <option key={acc.id} value={acc.id}>
                                                        {acc.code} - {acc.name}
                                                    </option>
                                                ))}
                                            </select>
                                        </td>
                                        <td>
                                            <input
                                                type="text"
                                                value={line.description}
                                                onChange={(e) => handleLineChange(index, 'description', e.target.value)}
                                                className="line-input"
                                                placeholder="Line description"
                                            />
                                        </td>
                                        <td>
                                            <input
                                                type="number"
                                                value={line.debit}
                                                onChange={(e) => handleLineChange(index, 'debit', e.target.value)}
                                                className="line-input text-right"
                                                placeholder="0.00"
                                                step="0.01"
                                            />
                                        </td>
                                        <td>
                                            <input
                                                type="number"
                                                value={line.credit}
                                                onChange={(e) => handleLineChange(index, 'credit', e.target.value)}
                                                className="line-input text-right"
                                                placeholder="0.00"
                                                step="0.01"
                                            />
                                        </td>
                                        <td className="text-center">
                                            <button
                                                className="icon-btn delete"
                                                onClick={() => removeLine(index)}
                                                disabled={formData.lines.length <= 2}
                                            >
                                                <Trash2 size={18} />
                                            </button>
                                        </td>
                                    </tr>
                                ))}
                            </tbody>
                            <tfoot>
                                <tr>
                                    <td colSpan="2">
                                        <button className="btn-text" onClick={addLine}>
                                            <Plus size={18} /> Add Line
                                        </button>
                                    </td>
                                    <td className="text-right font-bold">
                                        {totals.debit.toFixed(2)}
                                    </td>
                                    <td className="text-right font-bold">
                                        {totals.credit.toFixed(2)}
                                    </td>
                                    <td></td>
                                </tr>
                                {!isBalanced && (
                                    <tr>
                                        <td colSpan="5" className="error-message text-center">
                                            Entry is unbalanced by {Math.abs(totals.debit - totals.credit).toFixed(2)}
                                        </td>
                                    </tr>
                                )}
                            </tfoot>
                        </table>
                    </div>
                </div>
            )}
        </div>
    );
};

export default JournalEntry;
