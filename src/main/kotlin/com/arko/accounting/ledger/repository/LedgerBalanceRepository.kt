package com.arko.accounting.ledger.repository

import com.arko.accounting.ledger.domain.LedgerBalance
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface LedgerBalanceRepository : JpaRepository<LedgerBalance, UUID>
