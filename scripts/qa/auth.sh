#!/bin/bash
# OAuth Callback 인증 - 수동 토큰 설정

echo "=== JWT 토큰 설정 ==="
echo "1. 브라우저에서 로그인 후 개발자 도구에서 토큰 복사"
echo "2. 아래 명령어로 환경변수 설정:"
echo ""
echo "   export QA_TOKEN='your-jwt-token-here'"
echo ""

if [ -z "$QA_TOKEN" ]; then
  echo "⚠️  QA_TOKEN이 설정되지 않았습니다."
  exit 1
else
  echo "✅ QA_TOKEN이 설정되어 있습니다."
fi
