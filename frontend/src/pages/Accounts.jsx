import React, { useState, useEffect } from 'react';
import { Plus, Search } from 'lucide-react';
import { api } from '../services/api';
import '../styles/Accounts.css';

const Accounts = () => {
    const [accounts, setAccounts] = useState([]);
    const [loading, setLoading] = useState(true);
    const [showModal, setShowModal] = useState(false);
    const [formData, setFormData] = useState({
        code: '',
        name: '',
        type: 'ASSET',
        description: '',
        isParent: false,
        parentId: null
    });

    useEffect(() => {
        fetchAccounts();
    }, []);

    const fetchAccounts = async () => {
        try {
            const data = await api.getAccounts();
            // Normalize data (backend returns 'parent' instead of 'isParent' and 'active' instead of 'isActive')
            const normalizedData = data.map(acc => ({
                ...acc,
                isParent: acc.parent !== undefined ? acc.parent : acc.isParent,
                isActive: acc.active !== undefined ? acc.active : acc.isActive
            }));
            // Sort by code
            setAccounts(normalizedData.sort((a, b) => a.code.localeCompare(b.code)));
        } catch (error) {
            console.error('Failed to fetch accounts', error);
        } finally {
            setLoading(false);
        }
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            await api.createAccount(formData);
            setShowModal(false);
            setFormData({ code: '', name: '', type: 'ASSET', description: '', isParent: false, parentId: null });
            fetchAccounts();
        } catch (error) {
            console.error('Failed to create account', error);
            alert('Failed to create account');
        }
    };

    const handleChange = (e) => {
        const { name, value, type, checked } = e.target;
        setFormData(prev => ({
            ...prev,
            [name]: type === 'checkbox' ? checked : value
        }));
    };

    return (
        <div className="accounts-page">
            <div className="actions-bar">
                <div className="search-box">
                    <Search size={20} className="search-icon" />
                    <input type="text" placeholder="Search accounts..." className="search-input" />
                </div>
                <button className="btn-primary" onClick={() => setShowModal(true)}>
                    <Plus size={20} />
                    New Account
                </button>
            </div>

            <div className="table-container">
                <table className="data-table">
                    <thead>
                        <tr>
                            <th>Code</th>
                            <th>Name</th>
                            <th>Type</th>
                            <th>Description</th>
                            <th>Status</th>
                        </tr>
                    </thead>
                    <tbody>
                        {loading ? (
                            <tr><td colSpan="5" className="text-center">Loading...</td></tr>
                        ) : accounts.length === 0 ? (
                            <tr><td colSpan="5" className="text-center">No accounts found</td></tr>
                        ) : (
                            accounts.map(account => (
                                <tr key={account.id}>
                                    <td className="font-mono">{account.code}</td>
                                    <td className="font-medium">{account.name}</td>
                                    <td>
                                        <span className={`badge badge-${account.type.toLowerCase()}`}>
                                            {account.type}
                                        </span>
                                    </td>
                                    <td className="text-muted">{account.description || '-'}</td>
                                    <td>
                                        <span className={`status-dot ${account.isActive ? 'active' : 'inactive'}`}></span>
                                        {account.isActive ? 'Active' : 'Inactive'}
                                    </td>
                                </tr>
                            ))
                        )}
                    </tbody>
                </table>
            </div>

            {/* Modal */}
            {showModal && (
                <div className="modal-overlay">
                    <div className="modal">
                        <div className="modal-header">
                            <h3>Create New Account</h3>
                            <button className="close-btn" onClick={() => setShowModal(false)}>&times;</button>
                        </div>
                        <form onSubmit={handleSubmit}>
                            <div className="modal-body">
                                <div className="form-group">
                                    <label>Account Code</label>
                                    <input
                                        type="text"
                                        name="code"
                                        value={formData.code}
                                        onChange={handleChange}
                                        required
                                        placeholder="e.g. 1000"
                                    />
                                </div>
                                <div className="form-group">
                                    <label>Account Name</label>
                                    <input
                                        type="text"
                                        name="name"
                                        value={formData.name}
                                        onChange={handleChange}
                                        required
                                        placeholder="e.g. Cash"
                                    />
                                </div>
                                <div className="form-group">
                                    <label>Type</label>
                                    <select name="type" value={formData.type} onChange={handleChange}>
                                        <option value="ASSET">Asset</option>
                                        <option value="LIABILITY">Liability</option>
                                        <option value="EQUITY">Equity</option>
                                        <option value="REVENUE">Revenue</option>
                                        <option value="EXPENSE">Expense</option>
                                    </select>
                                </div>

                                <div className="form-group checkbox-group">
                                    <label className="flex items-center gap-2" style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                                        <input
                                            type="checkbox"
                                            name="isParent"
                                            checked={formData.isParent}
                                            onChange={handleChange}
                                        />
                                        Is Parent Account?
                                    </label>
                                    <small className="text-muted" style={{ display: 'block', marginTop: '0.25rem' }}>
                                        Parent accounts can have child accounts but cannot have transactions.
                                    </small>
                                </div>

                                {!formData.isParent && (
                                    <div className="form-group">
                                        <label>Parent Account</label>
                                        <select
                                            name="parentId"
                                            value={formData.parentId || ''}
                                            onChange={handleChange}
                                            required={!formData.isParent}
                                        >
                                            <option value="">-- Select Parent --</option>
                                            {accounts
                                                .filter(acc => acc.isParent && acc.type === formData.type)
                                                .map(acc => (
                                                    <option key={acc.id} value={acc.id}>
                                                        {acc.code} - {acc.name}
                                                    </option>
                                                ))
                                            }
                                        </select>
                                    </div>
                                )}

                                <div className="form-group">
                                    <label>Description</label>
                                    <textarea
                                        name="description"
                                        value={formData.description}
                                        onChange={handleChange}
                                        rows="3"
                                    ></textarea>
                                </div>
                            </div>
                            <div className="modal-footer">
                                <button type="button" className="btn-secondary" onClick={() => setShowModal(false)}>Cancel</button>
                                <button type="submit" className="btn-primary">Create Account</button>
                            </div>
                        </form>
                    </div>
                </div>
            )}
        </div>
    );
};

export default Accounts;
