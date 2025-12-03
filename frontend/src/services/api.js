const API_URL = import.meta.env.VITE_API_URL || 'http://localhost:8085';

export const api = {
  // Accounts
  getAccounts: async () => {
    const res = await fetch(`${API_URL}/api/accounts`);
    return res.json();
  },
  createAccount: async (account) => {
    const res = await fetch(`${API_URL}/api/accounts`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(account),
    });
    return res.json();
  },

  // Journal Entries
  createJournalEntry: async (entry, companyId) => {
    const res = await fetch(`${API_URL}/journal-entries`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'X-Company-ID': companyId,
      },
      body: JSON.stringify(entry),
    });
    return res.json();
  },

  // Ledger
  getLedgerEntries: async (accountId) => {
    const res = await fetch(`${API_URL}/api/ledger/account/${accountId}`);
    return res.json();
  },
  getBalances: async () => {
    const res = await fetch(`${API_URL}/api/ledger/balances`);
    return res.json();
  },

  // Reports
  getBalanceSheet: async (companyId) => {
    const res = await fetch(`${API_URL}/api/reports/balance-sheet`, {
      headers: { 'X-Company-ID': companyId },
    });
    return res.json();
  },
  getIncomeStatement: async (companyId) => {
    const res = await fetch(`${API_URL}/api/reports/income-statement`, {
      headers: { 'X-Company-ID': companyId },
    });
    return res.json();
  },
  getCashFlow: async (companyId) => {
    const res = await fetch(`${API_URL}/api/reports/cash-flow`, {
      headers: { 'X-Company-ID': companyId },
    });
    return res.json();
  },
};
