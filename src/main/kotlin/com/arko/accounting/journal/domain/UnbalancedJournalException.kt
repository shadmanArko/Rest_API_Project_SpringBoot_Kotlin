package com.arko.accounting.journal.domain

class UnbalancedJournalException :
    RuntimeException("Journal entry is not balanced (total debit != total credit)")
