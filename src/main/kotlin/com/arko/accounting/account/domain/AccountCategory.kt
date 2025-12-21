package com.arko.accounting.account.domain

enum class AccountCategory {

    // -------------------- REVENUE --------------------
    PRODUCT_SALES,        // Core product revenue
    SERVICE_REVENUE,      // Services, repairs, add-ons
    OTHER_REVENUE,        // Interest, refunds recovered

    // -------------------- COST OF GOODS SOLD (COGS) --------------------
    RAW_MATERIALS,        // Leather, hardware
    MANUFACTURING_COST,  // Labor, factory
    PACKAGING_COST,      // Boxes, labels
    SHIPPING_COST,       // Courier costs

    // -------------------- OPERATING EXPENSES --------------------
    MARKETING,            // Ads, influencers, campaigns
    ADVERTISING,          // Specifically for ad spend
    PLATFORM_FEES,        // Shopify, Etsy, payment gateway
    TECHNOLOGY,           // Hosting, domain, software
    OPERATIONS,           // Office, utilities
    OFFICE_SUPPLIES,      // Stationery, small equipment
    ADMINISTRATION,       // Legal, accounting, license
    PROFESSIONAL_SERVICES, // Consultants, legal, accounting
    SALARY,               // Staff salaries
    RENT,                 // Office/warehouse rent
    UTILITIES,            // Electricity, water, internet
    DEPRECIATION,         // Equipment depreciation
    INTEREST_EXPENSE,     // Loan interest
    INSURANCE,            // Business insurance
    TAXES,                // General taxes (non-payroll)

    // -------------------- ASSETS --------------------
    CASH,
    BANK,
    INVENTORY,
    EQUIPMENT,
    PREPAID_EXPENSE,
    ACCOUNTS_RECEIVABLE,

    // -------------------- LIABILITIES --------------------
    PAYABLE,
    TAX_PAYABLE,
    LOAN,
    ACCRUED_EXPENSE,
    ACCOUNTS_PAYABLE,
    UNEARNED_REVENUE,
    CREDIT_CARD,

    // -------------------- EQUITY --------------------
    OWNER_CAPITAL,
    OWNER_DRAWINGS,
    RETAINED_EARNINGS,
    DIVIDENDS,

    OTHER
}
