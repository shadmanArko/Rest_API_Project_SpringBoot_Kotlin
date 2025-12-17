package com.arko.analytics.service

import com.arko.accounting.analytics.events.AccountingEvent
import org.springframework.stereotype.Service
import javax.sql.DataSource

@Service
@org.springframework.boot.autoconfigure.condition.ConditionalOnProperty(name = ["app.clickhouse.enabled"], havingValue = "true", matchIfMissing = false)
class AccountingAnalyticsIngestService(
    @org.springframework.beans.factory.annotation.Qualifier("clickHouseDataSource")
    private val clickHouseJdbc: DataSource
) {

    fun ingest(event: AccountingEvent) {
        clickHouseJdbc.connection.use { conn ->
            conn.prepareStatement(
                "INSERT INTO accounting_events (event_date, company_id, account_code, account_type, amount, source) VALUES (?, ?, ?, ?, ?, ?)"
            ).apply {
                setDate(1, java.sql.Date.valueOf(event.eventDate))
                setString(2, event.companyId.toString())
                setString(3, event.accountCode)
                setString(4, event.accountType)
                setBigDecimal(5, event.amount)
                setString(6, event.source)
                execute()
            }
        }
    }
}
