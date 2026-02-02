#!/bin/bash
# ============================================================
# QA Destructive API Check Script
# ============================================================
# 파괴적인 API가 있는지 확인하고 사용자에게 실행 여부를 묻습니다.
# ============================================================

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
DESTRUCTIVE_FILE="${SCRIPT_DIR}/destructive-apis.json"
ENDPOINTS_FILE="${SCRIPT_DIR}/endpoints.json"
SKIP_LIST_FILE="${SCRIPT_DIR}/.skip-apis"

# 색상 정의
RED='\033[0;31m'
YELLOW='\033[1;33m'
GREEN='\033[0;32m'
NC='\033[0m' # No Color

echo "============================================================"
echo "  파괴적 API 검사"
echo "============================================================"
echo ""

# jq 확인
if ! command -v jq &> /dev/null; then
  echo "WARNING: jq가 설치되지 않아 파괴적 API 검사를 건너뜁니다."
  exit 0
fi

# 파일 확인
if [ ! -f "$DESTRUCTIVE_FILE" ]; then
  echo "WARNING: destructive-apis.json 파일이 없습니다."
  exit 0
fi

# 파괴적 API 목록 추출
echo "파괴적 API 목록을 확인합니다..."
echo ""

# Critical APIs
CRITICAL_APIS=$(jq -r '.apis[] | select(.level == "critical") | "  [\(.level | ascii_upcase)] \(.method) \(.path) - \(.description)"' "$DESTRUCTIVE_FILE" 2>/dev/null)

# High APIs
HIGH_APIS=$(jq -r '.apis[] | select(.level == "high") | "  [\(.level | ascii_upcase)] \(.method) \(.path) - \(.description)"' "$DESTRUCTIVE_FILE" 2>/dev/null)

# Medium APIs
MEDIUM_APIS=$(jq -r '.apis[] | select(.level == "medium") | "  [\(.level | ascii_upcase)] \(.method) \(.path) - \(.description)"' "$DESTRUCTIVE_FILE" 2>/dev/null)

# 출력
if [ -n "$CRITICAL_APIS" ]; then
  echo -e "${RED}[CRITICAL] 복구 불가능한 API:${NC}"
  echo "$CRITICAL_APIS"
  echo ""
fi

if [ -n "$HIGH_APIS" ]; then
  echo -e "${YELLOW}[HIGH] 데이터 삭제 API:${NC}"
  echo "$HIGH_APIS"
  echo ""
fi

if [ -n "$MEDIUM_APIS" ]; then
  echo -e "${GREEN}[MEDIUM] 상태 변경 API:${NC}"
  echo "$MEDIUM_APIS"
  echo ""
fi

echo "============================================================"
echo ""

# 사용자에게 선택 요청
echo "파괴적 API 실행 옵션을 선택하세요:"
echo ""
echo "  1) 모든 파괴적 API 스킵 (권장)"
echo "  2) CRITICAL만 스킵, 나머지 실행"
echo "  3) 모든 API 실행 (주의!)"
echo "  4) 개별 선택"
echo ""

read -p "선택 (1-4, 기본값=1): " choice
choice=${choice:-1}

# 스킵 목록 파일 생성
> "$SKIP_LIST_FILE"

case $choice in
  1)
    echo ""
    echo "모든 파괴적 API를 스킵합니다."
    jq -r '.apis[].path' "$DESTRUCTIVE_FILE" >> "$SKIP_LIST_FILE"
    ;;
  2)
    echo ""
    echo "CRITICAL API만 스킵합니다."
    jq -r '.apis[] | select(.level == "critical") | .path' "$DESTRUCTIVE_FILE" >> "$SKIP_LIST_FILE"
    ;;
  3)
    echo ""
    echo -e "${RED}경고: 모든 API를 실행합니다. 데이터 손실이 발생할 수 있습니다!${NC}"
    read -p "정말 계속하시겠습니까? (yes/no): " confirm
    if [ "$confirm" != "yes" ]; then
      echo "취소되었습니다. 모든 파괴적 API를 스킵합니다."
      jq -r '.apis[].path' "$DESTRUCTIVE_FILE" >> "$SKIP_LIST_FILE"
    fi
    ;;
  4)
    echo ""
    echo "개별 API 선택:"
    echo ""

    # 각 API에 대해 개별 확인
    jq -c '.apis[]' "$DESTRUCTIVE_FILE" | while read api; do
      method=$(echo "$api" | jq -r '.method')
      path=$(echo "$api" | jq -r '.path')
      desc=$(echo "$api" | jq -r '.description')
      level=$(echo "$api" | jq -r '.level')
      impact=$(echo "$api" | jq -r '.impact')

      echo "─────────────────────────────────────────────"
      echo -e "  ${YELLOW}$method $path${NC}"
      echo "  설명: $desc"
      echo "  위험도: $level"
      echo "  영향: $impact"
      echo ""

      read -p "  이 API를 실행하시겠습니까? (y/N): " run_api
      if [ "$run_api" != "y" ] && [ "$run_api" != "Y" ]; then
        echo "$path" >> "$SKIP_LIST_FILE"
        echo "  → 스킵됨"
      else
        echo "  → 실행됨"
      fi
      echo ""
    done
    ;;
  *)
    echo "잘못된 선택입니다. 모든 파괴적 API를 스킵합니다."
    jq -r '.apis[].path' "$DESTRUCTIVE_FILE" >> "$SKIP_LIST_FILE"
    ;;
esac

echo ""
echo "============================================================"

# 스킵 목록 출력
if [ -s "$SKIP_LIST_FILE" ]; then
  SKIP_COUNT=$(wc -l < "$SKIP_LIST_FILE" | tr -d ' ')
  echo "스킵할 API: ${SKIP_COUNT}개"
  echo "스킵 목록이 저장됨: $SKIP_LIST_FILE"
else
  echo "모든 API를 실행합니다."
fi

echo "============================================================"
