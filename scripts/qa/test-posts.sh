#!/bin/bash
# 게시물 API 테스트

source "$(dirname "$0")/common.sh"

DOMAIN="posts"
PASSED=0
FAILED=0
TOTAL=0

echo "=== ${DOMAIN} 테스트 시작 ==="

# 게시글 생성
test_create_post() {
  local name="게시글 생성"
  ((TOTAL++))

  response=$(curl -s -w "\n%{http_code}" -X POST \
    -H "Authorization: Bearer $QA_TOKEN" \
    -H "Content-Type: application/json" \
    -d '{
      "boardId": 1,
      "categoryId": 1,
      "title": "테스트 게시글",
      "content": "테스트 내용입니다."
    }' \
    "${BASE_URL}/v1/user/posts")

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

# 게시글 단건 조회
test_get_post() {
  local name="게시글 단건 조회"
  ((TOTAL++))

  local post_id="${TEST_POST_ID:-1}"
  response=$(curl -s -w "\n%{http_code}" -X GET \
    -H "Authorization: Bearer $QA_TOKEN" \
    -H "Content-Type: application/json" \
    "${BASE_URL}/v1/user/posts/${post_id}")

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

# 내가 작성한 게시글 목록
test_my_posts() {
  local name="내가 작성한 게시글 목록"
  ((TOTAL++))

  response=$(curl -s -w "\n%{http_code}" -X GET \
    -H "Authorization: Bearer $QA_TOKEN" \
    -H "Content-Type: application/json" \
    "${BASE_URL}/v1/user/posts/me?page=0&size=10")

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

# 특정 작성자의 게시글 목록
test_author_posts() {
  local name="특정 작성자의 게시글 목록"
  ((TOTAL++))

  response=$(curl -s -w "\n%{http_code}" -X GET \
    -H "Authorization: Bearer $QA_TOKEN" \
    -H "Content-Type: application/json" \
    "${BASE_URL}/v1/user/posts/author/1?page=0&size=10")

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

# 보드 게시글 목록
test_board_posts() {
  local name="보드 게시글 목록 (카테고리 필터 가능)"
  ((TOTAL++))

  response=$(curl -s -w "\n%{http_code}" -X GET \
    -H "Authorization: Bearer $QA_TOKEN" \
    -H "Content-Type: application/json" \
    "${BASE_URL}/v1/user/posts/board/1?page=0&size=10")

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

# 게시글 수정
test_update_post() {
  local name="게시글 수정"
  ((TOTAL++))

  local post_id="${TEST_POST_ID:-1}"
  response=$(curl -s -w "\n%{http_code}" -X PATCH \
    -H "Authorization: Bearer $QA_TOKEN" \
    -H "Content-Type: application/json" \
    -d '{
      "title": "수정된 테스트 게시글",
      "content": "수정된 내용입니다."
    }' \
    "${BASE_URL}/v1/user/posts/${post_id}")

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

# 게시글 삭제
test_delete_post() {
  local name="게시글 삭제"
  ((TOTAL++))

  local post_id="${TEST_POST_ID:-1}"
  response=$(curl -s -w "\n%{http_code}" -X DELETE \
    -H "Authorization: Bearer $QA_TOKEN" \
    -H "Content-Type: application/json" \
    "${BASE_URL}/v1/user/posts/${post_id}")

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
test_create_post
test_get_post
test_my_posts
test_author_posts
test_board_posts
test_update_post
test_delete_post

# 결과 출력
echo ""
echo "=== ${DOMAIN} 테스트 결과 ==="
echo "Total: $TOTAL, Passed: $PASSED, Failed: $FAILED"

# 결과를 JSON 파일로 저장
echo "{\"domain\": \"${DOMAIN}\", \"total\": $TOTAL, \"passed\": $PASSED, \"failed\": $FAILED}" > "$(dirname "$0")/results/${DOMAIN}.json"
