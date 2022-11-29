#!/bin/bash
BUILD_JAR=$(ls /home/ec2-user/action/server/wiselife/build/libs/wiselife-0.0.1-SNAPSHOT.jar)
JAR_NAME=$(basename $BUILD_JAR)

echo "> 현재 시간: $(date)" >> /home/ec2-user/action/deploy.log

echo "> build 파일명: $JAR_NAME" >> /home/ec2-user/action/deploy.log

echo "> build 파일 복사" >> /home/ec2-user/action/deploy.log
DEPLOY_PATH=/home/ec2-user/action/
cp $BUILD_JAR $DEPLOY_PATH

echo "> 현재 실행중인 애플리케이션 pid 확인" >> /home/ec2-user/action/deploy.log
CURRENT_PID=$(pgrep -f $JAR_NAME)

if [ -z $CURRENT_PID ]
then
  echo "> 현재 구동중인 애플리케이션이 없으므로 종료하지 않습니다." >> /home/ec2-user/action/deploy.log
else
  echo "> kill -15 $CURRENT_PID" >> /home/ec2-user/action/deploy.log
  sudo kill -15 $CURRENT_PID
  sleep 5
fi

# SIGTERM의 경우 종료까지 시간이 걸릴 수 있으므로 종료 되었는지 확인하며 최대 30초까지 대기하도록 하는 로직
WAIT_COUNT=0
while [ -n $(pgrep -f $JAR_NAME) -o $WAIT_COUNT -ne 5 ]
do
  sleep 5
  WAIT_COUNT=$((WAIT_COUNT + 1))
done

DEPLOY_JAR=$DEPLOY_PATH$JAR_NAME
echo "> DEPLOY_JAR 배포"    >> /home/ec2-user/action/deploy.log
sudo nohup java -jar $DEPLOY_JAR --spring.profiles.active=prod >> /home/ec2-user/deploy.log 2>/home/ec2-user/action/deploy_err.log &
echo >> /home/ec2-user/action/deploy.log

