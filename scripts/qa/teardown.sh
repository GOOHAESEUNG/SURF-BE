#!/bin/bash
# ============================================================
# QA Teardown Script - 테스트 데이터 정리
# ============================================================
# Setup에서 생성된 테스트 리소스를 삭제합니다.
# 삭제 순서: Comment → Post → Schedule → Banner (역순)
# ============================================================

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
BASE_URL="${QA_BASE_URL:-http://localhost:8080}"
TEST_IDS_FILE="${SCRIPT_DIR}/test-ids.json"

echo "============================================================"
echo "  QA Teardown - 테스트 데이터 정리"
echo "============================================================"
echo ""

# 토큰 확인
if [ -z "$QA_TOKEN" ]; then
  echo "ERROR: QA_TOKEN이 설정되지 않았습니다."
  echo "  export QA_TOKEN='your-token' 실행 후 다시 시도하세요."
  exit 1
fi

# test-ids.json 확인
if [ ! -f "$TEST_IDS_FILE" ]; then
  echo "WARNING: $TEST_IDS_FILE 파일이 없습니다."
  echo "  삭제할 테스트 데이터가 없습니다."
  exit 0
fi

echo "테스트 ID 파일 로드: $TEST_IDS_FILE"
echo ""

# jq가 있으면 사용, 없으면 grep으로 추출
if command -v jq &> /dev/null; then
  POST_ID=$(jq -r '.resources.post.id // empty' "$TEST_IDS_FILE")
  POST_CREATED=$(jq -r '.resources.post.created' "$TEST_IDS_FILE")
  COMMENT_ID=$(jq -r '.resources.comment.id // empty' "$TEST_IDS_FILE")
  COMMENT_CREATED=$(jq -r '.resources.comment.created' "$TEST_IDS_FILE")
  SCHEDULE_ID=$(jq -r '.resources.schedule.id // empty' "$TEST_IDS_FILE")
  SCHEDULE_CREATED=$(jq -r '.resources.schedule.created' "$TEST_IDS_FILE")
  BANNER_ID=$(jq -r '.resources.banner.id // empty' "$TEST_IDS_FILE")
  BANNER_CREATED=$(jq -r '.resources.banner.created' "$TEST_IDS_FILE")
else
  # jq 없이 grep으로 추출
  POST_ID=$(grep -o '"post"[^}]*"id":[^,}]*' "$TEST_IDS_FILE" | grep -o '"id":[0-9]*' | grep -o '[0-9]*')
  POST_CREATED=$(grep -o '"post"[^}]*"created":[^,}]*' "$TEST_IDS_FILE" | grep -o '"created":[a-z]*' | grep -o 'true\|false')
  COMMENT_ID=$(grep -o '"comment"[^}]*"id":[^,}]*' "$TEST_IDS_FILE" | grep -o '"id":[0-9]*' | grep -o '[0-9]*')
  COMMENT_CREATED=$(grep -o '"comment"[^}]*"created":[^,}]*' "$TEST_IDS_FILE" | grep -o '"created":[a-z]*' | grep -o 'true\|false')
  SCHEDULE_ID=$(grep -o '"schedule"[^}]*"id":[^,}]*' "$TEST_IDS_FILE" | grep -o '"id":[0-9]*' | grep -o '[0-9]*')
  SCHEDULE_CREATED=$(grep -o '"schedule"[^}]*"created":[^,}]*' "$TEST_IDS_FILE" | grep -o '"created":[a-z]*' | grep -o 'true\|false')
  BANNER_ID=$(grep -o '"banner"[^}]*"id":[^,}]*' "$TEST_IDS_FILE" | grep -o '"id":[0-9]*' | grep -o '[0-9]*')
  BANNER_CREATED=$(grep -o '"banner"[^}]*"created":[^,}]*' "$TEST_IDS_FILE" | grep -o '"created":[a-z]*' | grep -o 'true\|false')
fi

DELETED=0
SKIPPED=0
FAILED=0

# 1. 댓글 삭제 (댓글이 게시글에 종속되므로 먼저 삭제)
echo "[1/4] 테스트 댓글 삭제..."
if [ -n "$COMMENT_ID" ] && [ "$COMMENT_ID" != "null" ] && [ "$COMMENT_CREATED" = "true" ]; then
  response=$(curl -s -w "\n%{http_code}" -X DELETE \
    -H "Authorization: Bearer $QA_TOKEN" \
    -H "Content-Type: application/json" \
    "${BASE_URL}/v1/user/posts/${POST_ID}/comments/${COMMENT_ID}" 2>/dev/null)

  http_code=$(echo "$response" | tail -n1)

  if [[ "$http_code" =~ ^2 ]] || [ "$http_code" = "404" ]; then
    echo "  - Comment ID $COMMENT_ID 삭제됨"
    ((DELETED++))
  else
    echo "  - Comment ID $COMMENT_ID 삭제 실패 (HTTP $http_code)"
    ((FAILED++))
  fi
else
  echo "  - 삭제할 댓글 없음 (건너뜀)"
  ((SKIPPED++))
fi
echo ""

# 2. 게시글 삭제
echo "[2/4] 테스트 게시글 삭제..."
if [ -n "$POST_ID" ] && [ "$POST_ID" != "null" ] && [ "$POST_CREATED" = "true" ]; then
  response=$(curl -s -w "\n%{http_code}" -X DELETE \
    -H "Authorization: Bearer $QA_TOKEN" \
    -H "Content-Type: application/json" \
    "${BASE_URL}/v1/user/posts/${POST_ID}" 2>/dev/null)

  http_code=$(echo "$response" | tail -n1)

  if [[ "$http_code" =~ ^2 ]] || [ "$http_code" = "404" ]; then
    echo "  - Post ID $POST_ID 삭제됨"
    ((DELETED++))
  else
    echo "  - Post ID $POST_ID 삭제 실패 (HTTP $http_code)"
    ((FAILED++))
  fi
else
  echo "  - 삭제할 게시글 없음 (건너뜀)"
  ((SKIPPED++))
fi
echo ""

# 3. 일정 삭제
echo "[3/4] 테스트 일정 삭제..."
if [ -n "$SCHEDULE_ID" ] && [ "$SCHEDULE_ID" != "null" ] && [ "$SCHEDULE_CREATED" = "true" ]; then
  response=$(curl -s -w "\n%{http_code}" -X DELETE \
    -H "Authorization: Bearer $QA_TOKEN" \
    -H "Content-Type: application/json" \
    "${BASE_URL}/v1/admin/schedules/${SCHEDULE_ID}" 2>/dev/null)

  http_code=$(echo "$response" | tail -n1)

  if [[ "$http_code" =~ ^2 ]] || [ "$http_code" = "404" ]; then
    echo "  - Schedule ID $SCHEDULE_ID 삭제됨"
    ((DELETED++))
  else
    echo "  - Schedule ID $SCHEDULE_ID 삭제 실패 (HTTP $http_code)"
    ((FAILED++))
  fi
else
  echo "  - 삭제할 일정 없음 (건너뜀)"
  ((SKIPPED++))
fi
echo ""

# 4. 배너 삭제
echo "[4/4] 테스트 배너 삭제..."
if [ -n "$BANNER_ID" ] && [ "$BANNER_ID" != "null" ] && [ "$BANNER_CREATED" = "true" ]; then
  response=$(curl -s -w "\n%{http_code}" -X DELETE \
    -H "Authorization: Bearer $QA_TOKEN" \
    -H "Content-Type: application/json" \
    "${BASE_URL}/v1/admin/home/banners/${BANNER_ID}" 2>/dev/null)

  http_code=$(echo "$response" | tail -n1)

  if [[ "$http_code" =~ ^2 ]] || [ "$http_code" = "404" ]; then
    echo "  - Banner ID $BANNER_ID 삭제됨"
    ((DELETED++))
  else
    echo "  - Banner ID $BANNER_ID 삭제 실패 (HTTP $http_code)"
    ((FAILED++))
  fi
else
  echo "  - 삭제할 배너 없음 (건너뜀)"
  ((SKIPPED++))
fi
echo ""

# test-ids.json 백업 후 삭제
if [ -f "$TEST_IDS_FILE" ]; then
  mv "$TEST_IDS_FILE" "${TEST_IDS_FILE}.bak"
  echo "테스트 ID 파일 백업: ${TEST_IDS_FILE}.bak"
fi

echo "============================================================"
echo "  Teardown 완료!"
echo "============================================================"
echo ""
echo "  결과: 삭제됨 $DELETED, 건너뜀 $SKIPPED, 실패 $FAILED"
echo "============================================================"
