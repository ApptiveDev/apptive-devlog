#!/bin/bash

echo "⚠️ 이 스크립트는 사용하지 않는 Docker 리소스, 로그 파일, Gradle 캐시 등을 삭제합니다."
read -p "계속하시겠습니까? (y/N): " confirm

if [[ "$confirm" != "y" ]]; then
  echo "❌ 작업이 취소되었습니다."
  exit 1
fi

echo ""
echo "🧼 Docker 컨테이너/이미지/볼륨/네트워크 정리 중..."
docker container prune -f
docker image prune -a -f
docker volume prune -f
docker network prune -f
docker builder prune --all -f
docker image prune -f  # Dangling 이미지 정리

echo ""
echo "🧼 Gradle 캐시 정리 중..."
rm -rf ~/.gradle/caches/
rm -rf ~/.gradle/daemon/
rm -rf ~/.gradle/native/

echo ""
echo "🧼 Docker build 캐시 경로 정리 중..."
rm -rf /var/lib/docker/tmp/*
rm -rf /var/lib/docker/overlay2/*
rm -rf /var/lib/docker/buildkit/*
rm -rf /var/lib/docker/containers/*

echo ""
echo "🧼 시스템 로그 정리 중..."
sudo journalctl --vacuum-time=1d
sudo journalctl --vacuum-size=100M  # 로그 크기 제한

echo ""
echo "🧼 임시 파일 정리 중..."
sudo rm -rf /tmp/*
sudo rm -rf /var/tmp/*

echo ""
echo "🧼 apt 캐시 정리 중..."
sudo apt-get clean
sudo apt-get autoclean
sudo apt-get autoremove -y

echo ""
echo "🧼 오래된 커널 이미지 정리 중..."
sudo apt-get remove --purge $(dpkg --list | grep linux-image | awk '{ print $2 }' | grep -v $(uname -r))

echo ""
echo "🧼 오래된 Snap 패키지 정리 중..."
sudo snap set system refresh.retain=2
sudo snap remove $(sudo snap list --all | awk '/disabled/{print $1, $3}' | tr -s ' ' | cut -d ' ' -f 1,2 | tr '\n' ' ')

echo ""
echo "🧼 Firefox 캐시 정리 중..."
rm -rf ~/.cache/mozilla/firefox/*

echo ""
echo "🧼 Chrome 캐시 정리 중..."
rm -rf ~/.cache/google-chrome/*

echo ""
echo "🧼 VSCode 캐시 정리 중..."
rm -rf ~/.config/Code/Cache

echo ""
echo "✅ 디스크 정리 완료!"
df -h
