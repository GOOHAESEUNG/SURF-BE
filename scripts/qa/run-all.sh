#!/bin/bash
# ============================================================
# QA Run All Script - 전체 테스트 실행
# ============================================================
# 실행 순서: Setup → Test → Teardown
# ============================================================

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
SKIP_SETUP=false
SKIP_TEARDOWN=false
SKIP_DESTRUCTIVE_CHECK=false
DESTRUCTIVE_MODE=""  # skip_all, skip_critical, run_all

# 옵션 파싱
while [[ $# -gt 0 ]]; do
  case $1 in
    --skip-setup)
      SKIP_SETUP=true
      shift
      ;;
    --skip-teardown)
      SKIP_TEARDOWN=true
      shift
      ;;
    --safe)
      # 안전 모드: 모든 파괴적 API 스킵
      SKIP_DESTRUCTIVE_CHECK=true
      DESTRUCTIVE_MODE="skip_all"
      shift
      ;;
    --dangerous)
      # 위험 모드: 모든 API 실행 (확인 없이)
      SKIP_DESTRUCTIVE_CHECK=true
      DESTRUCTIVE_MODE="run_all"
      shift
      ;;
    --help)
      echo "Usage: $0 [OPTIONS]"
      echo ""
      echo "Options:"
      echo "  --skip-setup     Setup 단계 건너뛰기 (기존 test-ids.json 사용)"
      echo "  --skip-teardown  Teardown 단계 건너뛰기 (테스트 데이터 유지)"
      echo "  --safe           안전 모드: 모든 파괴적 API 스킵 (권장)"
      echo "  --dangerous      위험 모드: 모든 API 실행 (주의!)"
      echo "  --help           도움말 표시"
      exit 0
      ;;
    *)
      echo "Unknown option: $1"
      exit 1
      ;;
  esac
done

echo "============================================================"
echo "          QA 전체 테스트 시작"
echo "============================================================"
echo ""
echo "  Base URL: ${QA_BASE_URL:-http://localhost:8080}"
echo "  Skip Setup: $SKIP_SETUP"
echo "  Skip Teardown: $SKIP_TEARDOWN"
echo "  Destructive Mode: ${DESTRUCTIVE_MODE:-interactive}"
echo ""

# 토큰 확인
if [ -z "$QA_TOKEN" ]; then
  echo "ERROR: QA_TOKEN이 설정되지 않았습니다."
  echo "  export QA_TOKEN='your-token' 실행 후 다시 시도하세요."
  exit 1
fi

# ============================================================
# Phase 0: 파괴적 API 확인
# ============================================================
SKIP_LIST_FILE="${SCRIPT_DIR}/.skip-apis"

if [ "$SKIP_DESTRUCTIVE_CHECK" = false ]; then
  # 대화형 모드: 사용자에게 확인
  if [ -f "${SCRIPT_DIR}/check-destructive.sh" ]; then
    bash "${SCRIPT_DIR}/check-destructive.sh"

    if [ $? -ne 0 ]; then
      echo "파괴적 API 확인이 취소되었습니다."
      exit 1
    fi
  fi
else
  # 비대화형 모드: 옵션에 따라 자동 처리
  > "$SKIP_LIST_FILE"

  if [ "$DESTRUCTIVE_MODE" = "skip_all" ]; then
    echo "안전 모드: 모든 파괴적 API를 스킵합니다."
    if command -v jq &> /dev/null && [ -f "${SCRIPT_DIR}/destructive-apis.json" ]; then
      jq -r '.apis[].path' "${SCRIPT_DIR}/destructive-apis.json" >> "$SKIP_LIST_FILE"
    fi
  elif [ "$DESTRUCTIVE_MODE" = "run_all" ]; then
    echo "위험 모드: 모든 API를 실행합니다. (파괴적 API 포함)"
  fi
fi

echo ""

# ============================================================
# Phase 1: Setup
# ============================================================
if [ "$SKIP_SETUP" = false ]; then
  echo "============================================================"
  echo "  Phase 1: Setup - 테스트 데이터 생성"
  echo "============================================================"
  echo ""

  if [ -f "${SCRIPT_DIR}/setup.sh" ]; then
    bash "${SCRIPT_DIR}/setup.sh"
    SETUP_EXIT_CODE=$?

    if [ $SETUP_EXIT_CODE -ne 0 ]; then
      echo ""
      echo "WARNING: Setup에서 일부 리소스 생성에 실패했습니다."
      echo "  테스트는 계속 진행됩니다."
    fi
  else
    echo "WARNING: setup.sh 파일이 없습니다. Setup 건너뜁니다."
  fi

  echo ""
fi

# ============================================================
# Phase 2: Test
# ============================================================
echo "============================================================"
echo "  Phase 2: Test - API 테스트 실행"
echo "============================================================"
echo ""

# 결과 디렉토리 초기화
rm -rf "${SCRIPT_DIR}/results"
mkdir -p "${SCRIPT_DIR}/results"

# 테스트 시작 시간
START_TIME=$(date +%s)

# 각 도메인 테스트 실행
TEST_COUNT=0
for test_file in "${SCRIPT_DIR}"/test-*.sh; do
  if [ -f "$test_file" ]; then
    echo ""
    echo "------------------------------------------------------------"
    bash "$test_file"
    ((TEST_COUNT++))
  fi
done

# 테스트 종료 시간
END_TIME=$(date +%s)
DURATION=$((END_TIME - START_TIME))

echo ""
echo "------------------------------------------------------------"
echo "  테스트 실행 완료 (${DURATION}초 소요, ${TEST_COUNT}개 도메인)"
echo "------------------------------------------------------------"

# ============================================================
# Phase 3: Teardown
# ============================================================
if [ "$SKIP_TEARDOWN" = false ]; then
  echo ""
  echo "============================================================"
  echo "  Phase 3: Teardown - 테스트 데이터 정리"
  echo "============================================================"
  echo ""

  if [ -f "${SCRIPT_DIR}/teardown.sh" ]; then
    bash "${SCRIPT_DIR}/teardown.sh"
  else
    echo "WARNING: teardown.sh 파일이 없습니다. Teardown 건너뜁니다."
  fi

  echo ""
fi

# ============================================================
# Phase 4: 결과 집계
# ============================================================
echo "============================================================"
echo "  Phase 4: 결과 집계"
echo "============================================================"
echo ""

TOTAL_TESTS=0
TOTAL_PASSED=0
TOTAL_FAILED=0

# jq 사용 가능 여부 확인
if command -v jq &> /dev/null; then
  for result_file in "${SCRIPT_DIR}/results"/*.json; do
    if [ -f "$result_file" ]; then
      domain=$(jq -r '.domain // "unknown"' "$result_file" 2>/dev/null)
      total=$(jq -r '.total // 0' "$result_file" 2>/dev/null)
      passed=$(jq -r '.passed // 0' "$result_file" 2>/dev/null)
      failed=$(jq -r '.failed // 0' "$result_file" 2>/dev/null)

      if [ "$total" != "null" ] && [ "$total" != "0" ]; then
        TOTAL_TESTS=$((TOTAL_TESTS + total))
        TOTAL_PASSED=$((TOTAL_PASSED + passed))
        TOTAL_FAILED=$((TOTAL_FAILED + failed))

        # 성공률 계산
        if [ "$total" -gt 0 ]; then
          rate=$((passed * 100 / total))
          printf "  %-20s %d/%d passed (%d%%)\n" "$domain" "$passed" "$total" "$rate"
        fi
      fi
    fi
  done
else
  echo "  WARNING: jq가 설치되지 않아 상세 집계를 할 수 없습니다."
  echo "  각 도메인별 결과는 results/*.json 파일을 확인하세요."
fi

# 성공률 계산
if [ $TOTAL_TESTS -gt 0 ]; then
  SUCCESS_RATE=$((TOTAL_PASSED * 100 / TOTAL_TESTS))
else
  SUCCESS_RATE=0
fi

echo ""
echo "============================================================"
echo "                    최종 결과"
echo "============================================================"
echo ""
echo "  Total:  $TOTAL_TESTS tests"
echo "  Passed: $TOTAL_PASSED"
echo "  Failed: $TOTAL_FAILED"
echo "  Rate:   ${SUCCESS_RATE}%"
echo ""
echo "============================================================"

# 전체 결과를 JSON 파일로 저장
cat > "${SCRIPT_DIR}/results/summary.json" << EOF
{
  "timestamp": "$(date -u +"%Y-%m-%dT%H:%M:%SZ")",
  "duration": $DURATION,
  "total": $TOTAL_TESTS,
  "passed": $TOTAL_PASSED,
  "failed": $TOTAL_FAILED,
  "successRate": $SUCCESS_RATE
}
EOF

echo "  결과 저장: ${SCRIPT_DIR}/results/summary.json"
echo ""

# 실패한 테스트가 있으면 exit code 1 반환
if [ $TOTAL_FAILED -gt 0 ]; then
  exit 1
fi
