package com.arko.accounting.journal.exception

class UnbalancedJournalException :
    RuntimeException("Journal entry is not balanced (total debit != total credit)")
