#!/bin/bash
# 알림 API 테스트

source "$(dirname "$0")/common.sh"

DOMAIN="notifications"
PASSED=0
FAILED=0
TOTAL=0

echo "=== ${DOMAIN} 테스트 시작 ==="

# 알람 조회 (카테고리 필터 가능)
test_get_notifications() {
  local name="알람 조회 (카테고리 필터 가능)"
  ((TOTAL++))

  response=$(curl -s -w "\n%{http_code}" -X GET \
    -H "Authorization: Bearer $QA_TOKEN" \
    -H "Content-Type: application/json" \
    "${BASE_URL}/v1/user/notifications")

  http_code=$(echo "$response" | tail -n1)
  body=$(echo "$response" | sed '$d')

  if [[ "$http_code" =~ ^2 ]]; then
    echo "✅ PASS: $name (HTTP $http_code)"
    ((PASSED++))
  else
    echo "❌ FAIL: $name (HTTP $http_code)"
    echo "   Response: $body"
    ((FAILED++))
  fi
}

# 알람 읽음 처리
test_mark_as_read() {
  local name="알람 읽음 처리"
  ((TOTAL++))

  response=$(curl -s -w "\n%{http_code}" -X PATCH \
    -H "Authorization: Bearer $QA_TOKEN" \
    -H "Content-Type: application/json" \
    "${BASE_URL}/v1/user/notifications/1/read")

  http_code=$(echo "$response" | tail -n1)
  body=$(echo "$response" | sed '$d')

  if [[ "$http_code" =~ ^(2|4) ]]; then
    echo "✅ PASS: $name (HTTP $http_code)"
    ((PASSED++))
  else
    echo "❌ FAIL: $name (HTTP $http_code)"
    echo "   Response: $body"
    ((FAILED++))
  fi
}

# 디바이스 FCM 토큰 등록
test_register_device_token() {
  local name="디바이스 FCM 토큰 등록"
  ((TOTAL++))

  response=$(curl -s -w "\n%{http_code}" -X POST \
    -H "Authorization: Bearer $QA_TOKEN" \
    -H "Content-Type: application/json" \
    -d '{
      "token": "test-fcm-token-123456",
      "platform": "WEB"
    }' \
    "${BASE_URL}/v1/user/notifications/device-tokens")

  http_code=$(echo "$response" | tail -n1)
  body=$(echo "$response" | sed '$d')

  if [[ "$http_code" =~ ^(2|4) ]]; then
    echo "✅ PASS: $name (HTTP $http_code)"
    ((PASSED++))
  else
    echo "❌ FAIL: $name (HTTP $http_code)"
    echo "   Response: $body"
    ((FAILED++))
  fi
}

# FCM 푸시 알림 테스트 (개발용)
test_push_notification() {
  local name="FCM 푸시 알림 테스트 (개발용)"
  ((TOTAL++))

  response=$(curl -s -w "\n%{http_code}" -X POST \
    -H "Authorization: Bearer $QA_TOKEN" \
    -H "Content-Type: application/json" \
    -d '{
      "memberId": 1,
      "title": "테스트 알림",
      "body": "테스트 알림 내용입니다."
    }' \
    "${BASE_URL}/v1/user/notifications/test/push")

  http_code=$(echo "$response" | tail -n1)
  body=$(echo "$response" | sed '$d')

  if [[ "$http_code" =~ ^(2|4) ]]; then
    echo "✅ PASS: $name (HTTP $http_code)"
    ((PASSED++))
  else
    echo "❌ FAIL: $name (HTTP $http_code)"
    echo "   Response: $body"
    ((FAILED++))
  fi
}

# 테스트 실행
test_get_notifications
test_mark_as_read
test_register_device_token
test_push_notification

# 결과 출력
echo ""
echo "=== ${DOMAIN} 테스트 결과 ==="
echo "Total: $TOTAL, Passed: $PASSED, Failed: $FAILED"

# 결과를 JSON 파일로 저장
echo "{\"domain\": \"${DOMAIN}\", \"total\": $TOTAL, \"passed\": $PASSED, \"failed\": $FAILED}" > "$(dirname "$0")/results/${DOMAIN}.json"
