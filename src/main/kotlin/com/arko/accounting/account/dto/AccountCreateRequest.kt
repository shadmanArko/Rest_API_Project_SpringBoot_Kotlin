package com.arko.accounting.account.dto

import com.arko.accounting.account.domain.AccountType
import java.util.UUID

data class AccountCreateRequest(
    val code: String,
    val name: String,
    val type: AccountType,
    val isParent: Boolean = false,
    val parentId: UUID? = null,
    val description: String? = null
)
