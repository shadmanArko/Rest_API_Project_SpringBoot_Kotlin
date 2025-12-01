package com.arko.accounting.account.dto

import com.arko.accounting.account.domain.AccountType

data class AccountUpdateRequest(
    val name: String,
    val description: String?,
    val isActive: Boolean
)
