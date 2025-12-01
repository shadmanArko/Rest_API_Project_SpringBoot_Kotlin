package com.arko.accounting.ledger.domain

import jakarta.persistence.*
import java.math.BigDecimal
import java.util.UUID

@Entity
@Table(name = "ledger_balances")
data class LedgerBalance(

    @Id
    @Column(name = "account_id")
    val accountId: UUID = UUID.randomUUID(),

    @Column(nullable = false)
    val debit: BigDecimal = BigDecimal.ZERO,

    @Column(nullable = false)
    val credit: BigDecimal = BigDecimal.ZERO
)
