#!/bin/bash

# Stop containers
docker stop devlog-mysql devlog-app devlog-redis devlog-nginx

# Remove containers
docker rm devlog-mysql devlog-app devlog-redis devlog-nginx
