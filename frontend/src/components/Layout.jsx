import React from 'react';
import { Link, useLocation } from 'react-router-dom';
import { LayoutDashboard, BookOpen, FileText, PieChart, PlusCircle, BarChart2 } from 'lucide-react';
import '../styles/Layout.css';

const Layout = ({ children }) => {
    const location = useLocation();

    const navItems = [
        { path: '/', label: 'Dashboard', icon: LayoutDashboard },
        { path: '/analytics', label: 'Analytics', icon: BarChart2 },
        { path: '/accounts', label: 'Chart of Accounts', icon: BookOpen },
        { path: '/journal', label: 'New Journal Entry', icon: PlusCircle },
        { path: '/ledger', label: 'General Ledger', icon: FileText },
        { path: '/reports', label: 'Financial Reports', icon: PieChart },
    ];

    return (
        <div className="layout-container">
            {/* Sidebar */}
            <div className="sidebar">
                <div className="sidebar-header">
                    <h1 className="brand">
                        <div className="brand-icon">A</div>
                        Accounting
                    </h1>
                </div>

                <nav className="sidebar-nav">
                    {navItems.map((item) => {
                        const Icon = item.icon;
                        const isActive = location.pathname === item.path;

                        return (
                            <Link
                                key={item.path}
                                to={item.path}
                                className={`nav-item ${isActive ? 'active' : ''}`}
                            >
                                <Icon size={20} />
                                {item.label}
                            </Link>
                        );
                    })}
                </nav>

                <div className="sidebar-footer">
                    <div className="user-profile">
                        <div className="user-avatar"></div>
                        <div className="user-info">
                            <p className="user-name">User</p>
                            <p className="user-role">Admin</p>
                        </div>
                    </div>
                </div>
            </div>

            {/* Main Content */}
            <div className="main-content">
                <header className="top-header">
                    <h2 className="page-title">
                        {navItems.find(i => i.path === location.pathname)?.label || 'Dashboard'}
                    </h2>
                </header>
                <main className="page-content">
                    {children}
                </main>
            </div>
        </div>
    );
};

export default Layout;
