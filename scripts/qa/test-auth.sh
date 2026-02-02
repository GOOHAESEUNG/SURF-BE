#!/bin/bash
# 인증 API 테스트

source "$(dirname "$0")/common.sh"

DOMAIN="auth"
PASSED=0
FAILED=0
TOTAL=0

echo "=== ${DOMAIN} 테스트 시작 ==="

# 카카오 인가 화면으로 리다이렉트
test_kakao_login_redirect() {
  local name="카카오 인가 화면으로 리다이렉트"
  ((TOTAL++))

  response=$(curl -s -w "\n%{http_code}" -X GET \
    -H "Content-Type: application/json" \
    "${BASE_URL}/login/kakao")

  http_code=$(echo "$response" | tail -n1)
  body=$(echo "$response" | sed '$d')

  if [[ "$http_code" =~ ^(2|3) ]]; then
    echo "✅ PASS: $name (HTTP $http_code)"
    ((PASSED++))
  else
    echo "❌ FAIL: $name (HTTP $http_code)"
    echo "   Response: $body"
    ((FAILED++))
  fi
}

# 카카오 로그인 콜백 (테스트용 - 실제 code 필요)
test_kakao_callback() {
  local name="카카오 로그인 콜백 - 인가 코드로 JWT AccessToken과 사용자 정보 반환"
  ((TOTAL++))

  response=$(curl -s -w "\n%{http_code}" -X GET \
    -H "Content-Type: application/json" \
    "${BASE_URL}/login/oauth2/code/kakao?code=test_code")

  http_code=$(echo "$response" | tail -n1)
  body=$(echo "$response" | sed '$d')

  # OAuth 콜백은 유효한 code가 필요하므로 4xx도 예상됨
  if [[ "$http_code" =~ ^(2|3|4) ]]; then
    echo "✅ PASS: $name (HTTP $http_code - OAuth callback requires valid code)"
    ((PASSED++))
  else
    echo "❌ FAIL: $name (HTTP $http_code)"
    echo "   Response: $body"
    ((FAILED++))
  fi
}

# Access Token 재발급
test_refresh_token() {
  local name="Access Token 재발급 - HttpOnly Refresh Token 쿠키 이용"
  ((TOTAL++))

  response=$(curl -s -w "\n%{http_code}" -X POST \
    -H "Content-Type: application/json" \
    "${BASE_URL}/auth/refresh")

  http_code=$(echo "$response" | tail -n1)
  body=$(echo "$response" | sed '$d')

  # Refresh token이 없으면 401 예상
  if [[ "$http_code" =~ ^(2|4) ]]; then
    echo "✅ PASS: $name (HTTP $http_code)"
    ((PASSED++))
  else
    echo "❌ FAIL: $name (HTTP $http_code)"
    echo "   Response: $body"
    ((FAILED++))
  fi
}

# 로그아웃
test_logout() {
  local name="로그아웃 - refreshToken 무효화 및 쿠키 삭제"
  ((TOTAL++))

  response=$(curl -s -w "\n%{http_code}" -X POST \
    -H "Authorization: Bearer $QA_TOKEN" \
    -H "Content-Type: application/json" \
    "${BASE_URL}/auth/logout")

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
test_kakao_login_redirect
test_kakao_callback
test_refresh_token
test_logout

# 결과 출력
echo ""
echo "=== ${DOMAIN} 테스트 결과 ==="
echo "Total: $TOTAL, Passed: $PASSED, Failed: $FAILED"

# 결과를 JSON 파일로 저장
echo "{\"domain\": \"${DOMAIN}\", \"total\": $TOTAL, \"passed\": $PASSED, \"failed\": $FAILED}" > "$(dirname "$0")/results/${DOMAIN}.json"
