#!/bin/bash
set -e

# Gradle 빌드 (테스트 제외)
echo "🔨 Gradle 빌드 중 (테스트 제외)..."
./gradlew clean build -x test

# ~/apptive_devlog/certs/ 디렉토리 권한 및 소유자 설정
echo "🔐 ~/apptive_devlog/certs/ 디렉토리 권한 및 소유자 설정 중..."
sudo chown -R root:root ~/apptive_devlog/certs/
sudo find ~/apptive_devlog/certs/ -type f -exec chmod 644 {} \;
sudo find ~/apptive_devlog/certs/ -type d -exec chmod 755 {} \;

# 추가된 경로들에 대해서도 권한 설정
echo "🔐 추가 인증서 디렉토리 권한 및 소유자 설정 중..."
sudo chown -R root:root ~/apptive_devlog/certs/app_certs/
sudo chown -R root:root ~/apptive_devlog/certs/mysql_certs/
sudo chown -R root:root ~/apptive_devlog/certs/redis_certs/
sudo chown -R root:root ~/apptive_devlog/certs/nginx_certs/

# 파일 권한 644 설정
sudo find ~/apptive_devlog/certs/app_certs/ -type f -exec chmod 644 {} \;
sudo find ~/apptive_devlog/certs/mysql_certs/ -type f -exec chmod 644 {} \;
sudo find ~/apptive_devlog/certs/redis_certs/ -type f -exec chmod 644 {} \;
sudo find ~/apptive_devlog/certs/nginx_certs/ -type f -exec chmod 644 {} \;

# 디렉토리 권한 755 설정
sudo find ~/apptive_devlog/certs/app_certs/ -type d -exec chmod 755 {} \;
sudo find ~/apptive_devlog/certs/mysql_certs/ -type d -exec chmod 755 {} \;
sudo find ~/apptive_devlog/certs/redis_certs/ -type d -exec chmod 755 {} \;
sudo find ~/apptive_devlog/certs/nginx_certs/ -type d -exec chmod 755 {} \;

chmod 644 ./my.cnf

echo "✅ 권한 및 소유자 설정 완료"

echo "🐳 Docker 이미지 빌드 중 (캐시 무시)..."
if ! docker-compose build --no-cache; then
    echo "❌ Docker 이미지 빌드에 실패했습니다. 종료합니다."
    exit 1
fi

echo "🚀 컨테이너 시작 중..."
if ! docker-compose up -d; then
    echo "❌ Docker 컨테이너 실행에 실패했습니다. 종료합니다."
    exit 1
fi

echo "✅ 모든 컨테이너가 실행 중입니다."

# 컨테이너 로그 확인
echo "📜 컨테이너 로그 확인 중..."
docker-compose logs -f &

# 서비스 상태 확인
echo "🧐 서비스 상태 확인 중..."
docker-compose ps

# 환경 변수 검증 (생략 처리)
echo "🔍 환경 변수 검증 중... (건너뜀)"
true

# 불필요한 Docker 리소스 정리
echo "🧹 불필요한 Docker 리소스 정리 중..."
docker system prune -f

echo "✅ 배포가 완료되었습니다!"
