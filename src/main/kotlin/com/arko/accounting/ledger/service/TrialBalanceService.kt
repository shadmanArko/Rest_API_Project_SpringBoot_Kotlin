package com.arko.accounting.ledger.service

import com.arko.accounting.ledger.dto.TrialBalanceDto
import java.util.UUID

interface TrialBalanceService {
    fun generate(companyId: UUID): TrialBalanceDto
}
