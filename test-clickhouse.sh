#!/bin/bash

# Test ClickHouse Analytics Pipeline
echo "Testing ClickHouse Analytics Pipeline..."
echo ""

# Check if services are running
echo "1. Checking Docker services..."
docker ps --format "table {{.Names}}\t{{.Status}}" | grep -E "NAME|kafka|clickhouse"
echo ""

# Post an analytics event
echo "2. Posting analytics event to http://localhost:8085/api/analytics/events..."
curl -X POST http://localhost:8085/api/analytics/events \
  -H "Content-Type: application/json" \
  -d '{
  "eventId": "550e8400-e29b-41d4-a716-446655440000",
  "platform": "meta",
  "accountId": "act_123",
  "campaignId": "cmp_456",  
  "adId": "ad_789",
  "eventType": "spend",
  "timestamp": "2025-12-10T14:00:00Z",
  "amount": 25.50,
  "currency": "USD",
  "payload": {
    "impressions": 1000,
    "clicks": 50
  }
}'
echo ""
echo ""

# Wait a moment for processing
echo "3. Waiting 2 seconds for Kafka processing..."
sleep 2
echo ""

# Check ClickHouse
echo "4. Querying ClickHouse..."
docker exec clickhouse clickhouse-client --query "SELECT * FROM analytics.agg_campaign_hourly FORMAT Pretty"
echo ""

# Check PostgreSQL raw_events
echo "5. Checking PostgreSQL raw_events (last 5)..."
docker exec clickhouse clickhouse-client --query "SELECT count() as total_events FROM analytics.agg_campaign_hourly"
