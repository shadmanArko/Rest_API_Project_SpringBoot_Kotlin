package com.arko.accounting.account.dto

import com.arko.accounting.account.domain.AccountType
import java.util.UUID

data class AccountResponse(
    val id: UUID,
    val code: String,
    val name: String,
    val type: AccountType,
    val isParent: Boolean,
    val parentId: UUID?,
    val isActive: Boolean,
    val description: String?
)
