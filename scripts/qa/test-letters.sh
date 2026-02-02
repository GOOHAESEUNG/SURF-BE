#!/bin/bash
# 쪽지 API 테스트

source "$(dirname "$0")/common.sh"

DOMAIN="letters"
PASSED=0
FAILED=0
TOTAL=0

echo "=== ${DOMAIN} 테스트 시작 ==="

# 쪽지 전송
test_send_letter() {
  local name="쪽지 전송"
  ((TOTAL++))

  response=$(curl -s -w "\n%{http_code}" -X POST \
    -H "Authorization: Bearer $QA_TOKEN" \
    -H "Content-Type: application/json" \
    -d '{
      "receiverId": 1,
      "title": "테스트 쪽지",
      "content": "테스트 쪽지 내용입니다.",
      "replyEmail": "test@example.com"
    }' \
    "${BASE_URL}/v1/user/letters")

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

# 보낸 쪽지 목록 조회
test_get_sent_letters() {
  local name="보낸 쪽지 목록 조회"
  ((TOTAL++))

  response=$(curl -s -w "\n%{http_code}" -X GET \
    -H "Authorization: Bearer $QA_TOKEN" \
    -H "Content-Type: application/json" \
    "${BASE_URL}/v1/user/letters/sent?page=0&size=10")

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

# 테스트 실행
test_send_letter
test_get_sent_letters

# 결과 출력
echo ""
echo "=== ${DOMAIN} 테스트 결과 ==="
echo "Total: $TOTAL, Passed: $PASSED, Failed: $FAILED"

# 결과를 JSON 파일로 저장
echo "{\"domain\": \"${DOMAIN}\", \"total\": $TOTAL, \"passed\": $PASSED, \"failed\": $FAILED}" > "$(dirname "$0")/results/${DOMAIN}.json"
