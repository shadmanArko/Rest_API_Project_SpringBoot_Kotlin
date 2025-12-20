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
    PLATFORM_FEES,        // Shopify, Etsy, payment gateway
    TECHNOLOGY,           // Hosting, domain, software
    OPERATIONS,           // Office, utilities
    ADMINISTRATION,       // Legal, accounting, license
    SALARY,               // Staff salaries
    RENT,                 // Office/warehouse rent

    // -------------------- ASSETS --------------------
    CASH,
    BANK,
    INVENTORY,
    EQUIPMENT,
    PREPAID_EXPENSE,

    // -------------------- LIABILITIES --------------------
    PAYABLE,
    TAX_PAYABLE,
    LOAN,

    // -------------------- EQUITY --------------------
    OWNER_CAPITAL,
    RETAINED_EARNINGS,



    OTHER
}
