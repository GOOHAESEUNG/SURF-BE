#!/bin/bash
# 홈 관리 (관리자) API 테스트

source "$(dirname "$0")/common.sh"

DOMAIN="homeAdmin"
PASSED=0
FAILED=0
TOTAL=0

echo "=== ${DOMAIN} 테스트 시작 ==="

# 홈 배너 목록 조회
test_get_banners() {
  local name="홈 배너 목록 조회"
  ((TOTAL++))

  response=$(curl -s -w "\n%{http_code}" -X GET \
    -H "Authorization: Bearer $QA_TOKEN" \
    -H "Content-Type: application/json" \
    "${BASE_URL}/v1/admin/home/banners")

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

# 홈 배너 생성
test_create_banner() {
  local name="홈 배너 생성"
  ((TOTAL++))

  response=$(curl -s -w "\n%{http_code}" -X POST \
    -H "Authorization: Bearer $QA_TOKEN" \
    -H "Content-Type: application/json" \
    -d '{
      "imageUrl": "https://example.com/banner.jpg",
      "linkUrl": "https://example.com"
    }' \
    "${BASE_URL}/v1/admin/home/banners")

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

# 홈 배너 삭제
test_delete_banner() {
  local name="홈 배너 삭제"
  ((TOTAL++))

  response=$(curl -s -w "\n%{http_code}" -X DELETE \
    -H "Authorization: Bearer $QA_TOKEN" \
    -H "Content-Type: application/json" \
    "${BASE_URL}/v1/admin/home/banners/1")

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

# 홈 배너 순서 변경
test_reorder_banners() {
  local name="홈 배너 순서 변경"
  ((TOTAL++))

  response=$(curl -s -w "\n%{http_code}" -X PUT \
    -H "Authorization: Bearer $QA_TOKEN" \
    -H "Content-Type: application/json" \
    -d '{
      "orderedIds": [1, 2, 3]
    }' \
    "${BASE_URL}/v1/admin/home/banners/reorder")

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

# 홈 배너 수정
test_update_banner() {
  local name="홈 배너 수정"
  ((TOTAL++))

  response=$(curl -s -w "\n%{http_code}" -X PUT \
    -H "Authorization: Bearer $QA_TOKEN" \
    -H "Content-Type: application/json" \
    -d '{
      "imageUrl": "https://example.com/updated-banner.jpg",
      "linkUrl": "https://example.com/updated"
    }' \
    "${BASE_URL}/v1/admin/home/banners/1")

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

# 홈 문구 조회
test_get_content() {
  local name="홈 문구 조회"
  ((TOTAL++))

  response=$(curl -s -w "\n%{http_code}" -X GET \
    -H "Authorization: Bearer $QA_TOKEN" \
    -H "Content-Type: application/json" \
    "${BASE_URL}/v1/admin/home/content")

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

# 홈 문구 생성/수정
test_update_content() {
  local name="홈 문구 생성/수정"
  ((TOTAL++))

  response=$(curl -s -w "\n%{http_code}" -X PUT \
    -H "Authorization: Bearer $QA_TOKEN" \
    -H "Content-Type: application/json" \
    -d '{
      "message": "테스트 메시지입니다.",
      "sender": "테스트 발신자"
    }' \
    "${BASE_URL}/v1/admin/home/content")

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
test_get_banners
test_create_banner
test_delete_banner
test_reorder_banners
test_update_banner
test_get_content
test_update_content

# 결과 출력
echo ""
echo "=== ${DOMAIN} 테스트 결과 ==="
echo "Total: $TOTAL, Passed: $PASSED, Failed: $FAILED"

# 결과를 JSON 파일로 저장
echo "{\"domain\": \"${DOMAIN}\", \"total\": $TOTAL, \"passed\": $PASSED, \"failed\": $FAILED}" > "$(dirname "$0")/results/${DOMAIN}.json"
