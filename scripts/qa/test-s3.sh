#!/bin/bash
# 파일 업로드 API 테스트

source "$(dirname "$0")/common.sh"

DOMAIN="s3"
PASSED=0
FAILED=0
TOTAL=0

echo "=== ${DOMAIN} 테스트 시작 ==="

# S3 PreSigned URL 생성
test_presigned_url() {
  local name="S3 PreSigned URL 생성"
  ((TOTAL++))

  response=$(curl -s -w "\n%{http_code}" -X POST \
    -H "Authorization: Bearer $QA_TOKEN" \
    -H "Content-Type: application/json" \
    -d '{
      "fileNames": ["test-image.jpg"]
    }' \
    "${BASE_URL}/v1/user/presigned-url")

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
test_presigned_url

# 결과 출력
echo ""
echo "=== ${DOMAIN} 테스트 결과 ==="
echo "Total: $TOTAL, Passed: $PASSED, Failed: $FAILED"

# 결과를 JSON 파일로 저장
echo "{\"domain\": \"${DOMAIN}\", \"total\": $TOTAL, \"passed\": $PASSED, \"failed\": $FAILED}" > "$(dirname "$0")/results/${DOMAIN}.json"
