docker network create devlog-net

docker network connect devlog-net devlog-mysql
docker network connect devlog-net devlog-redis
docker network connect devlog-net devlog-app
docker network connect devlog-net devlog-nginx

docker restart devlog-mysql
docker restart devlog-redis
docker restart devlog-app
docker restart devlog-nginx

