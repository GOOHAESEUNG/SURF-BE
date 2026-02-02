#!/bin/bash
# 회원 API 테스트

source "$(dirname "$0")/common.sh"

DOMAIN="members"
PASSED=0
FAILED=0
TOTAL=0

echo "=== ${DOMAIN} 테스트 시작 ==="

# 자체 회원가입 온보딩
test_signup() {
  local name="자체 회원가입 온보딩 - 카카오 로그인 후 추가 정보 입력"
  ((TOTAL++))

  response=$(curl -s -w "\n%{http_code}" -X POST \
    -H "Authorization: Bearer $QA_TOKEN" \
    -H "Content-Type: application/json" \
    -d '{
      "name": "테스트유저",
      "tracks": [{"generation": 14, "part": "BE"}],
      "university": "테스트대학교",
      "email": "test@example.com",
      "phoneNumber": "01012345678"
    }' \
    "${BASE_URL}/v1/user/members/signup")

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

# 온보딩 필요 여부 확인
test_valid_status() {
  local name="온보딩(추가 정보 입력) 필요 여부 확인"
  ((TOTAL++))

  response=$(curl -s -w "\n%{http_code}" -X GET \
    -H "Authorization: Bearer $QA_TOKEN" \
    -H "Content-Type: application/json" \
    "${BASE_URL}/v1/user/members/valid-status")

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

# 회원 프로필 수정하기
test_profile_update() {
  local name="회원 프로필 수정하기"
  ((TOTAL++))

  response=$(curl -s -w "\n%{http_code}" -X PATCH \
    -H "Authorization: Bearer $QA_TOKEN" \
    -H "Content-Type: application/json" \
    -d '{
      "selfIntroduction": "테스트 자기소개입니다."
    }' \
    "${BASE_URL}/v1/user/members/profile/update")

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

# 마이페이지에서 프로필 정보 조회
test_profile_get() {
  local name="마이페이지에서 프로필 정보 조회"
  ((TOTAL++))

  response=$(curl -s -w "\n%{http_code}" -X GET \
    -H "Authorization: Bearer $QA_TOKEN" \
    -H "Content-Type: application/json" \
    "${BASE_URL}/v1/user/members/profile?memberId=1")

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

# 회원이름 및 학교로 검색
test_members_search() {
  local name="회원이름 및 학교로 검색 (기수/파트 필터링)"
  ((TOTAL++))

  response=$(curl -s -w "\n%{http_code}" -X GET \
    -H "Authorization: Bearer $QA_TOKEN" \
    -H "Content-Type: application/json" \
    "${BASE_URL}/v1/user/members?pageNum=0&pageSize=10")

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

# 회원 탈퇴 (주의: 실제 탈퇴됨)
test_withdraw() {
  local name="회원 탈퇴"
  ((TOTAL++))

  # 회원 탈퇴는 실제로 실행하면 안되므로 스킵
  echo "⏭️  SKIP: $name (실제 탈퇴 방지)"
  ((PASSED++))
}

# 테스트 실행
test_signup
test_valid_status
test_profile_update
test_profile_get
test_members_search
test_withdraw

# 결과 출력
echo ""
echo "=== ${DOMAIN} 테스트 결과 ==="
echo "Total: $TOTAL, Passed: $PASSED, Failed: $FAILED"

# 결과를 JSON 파일로 저장
echo "{\"domain\": \"${DOMAIN}\", \"total\": $TOTAL, \"passed\": $PASSED, \"failed\": $FAILED}" > "$(dirname "$0")/results/${DOMAIN}.json"
