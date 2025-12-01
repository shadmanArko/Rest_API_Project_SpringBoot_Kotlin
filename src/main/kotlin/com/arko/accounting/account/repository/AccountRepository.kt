package com.arko.accounting.account.repository

import com.arko.accounting.account.domain.Account
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface AccountRepository : JpaRepository<Account, UUID> {
    fun existsByCode(code: String): Boolean
    fun existsByParentId(parentId: UUID): Boolean
}
