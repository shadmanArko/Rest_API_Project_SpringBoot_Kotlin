package com.arko.analytics.exception

import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(code = org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE)
class AnalyticsDisabledException : RuntimeException(
    "Analytics querying is disabled (ClickHouse not enabled)"
)