#!/bin/bash
set -ex
./gradlew build -x test
docker build -t pre_crack .
ID=$(docker run --cap-add CAP_SYS_PTRACE --cap-add CAP_CHECKPOINT_RESTORE -p8081:8080 -d pre_crack:latest -f Dockerfile)
sleep 5
docker exec -it $ID jcmd 129 JDK.checkpoint
docker commit $ID post_crack
echo "post_crack is built"
