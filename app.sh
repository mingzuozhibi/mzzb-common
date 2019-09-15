#!/usr/bin/env bash

##
# 定位应用所在目录
##

APP_HOME=$(cd `dirname $0`; pwd)
APP_NAME=${APP_HOME##*/}

cd ${APP_HOME}

##
# 定义应用启动命令
##

JAVA_OPTS="-Xms64m -Xmx128m -XX:MaxMetaspaceSize=1024m"
JAR_OPTS="--app.name=${APP_NAME} --spring.profiles.active=pro"

STD_FILE="target/std.log"
WEB_FILE="target/web.log"

start_app() {
  if [[ -z "$(jar_file)" ]]; then
    build_app
  fi
  if [[ -z "$(jar_file)" ]]; then
    echo "启动应用失败：未找到JAR文件"
  else
    echo "正在启动应用"
    echo "java ${JAVA_OPTS} -jar $(jar_file) ${JAR_OPTS} >${STD_FILE} 2>&1"
    nohup java ${JAVA_OPTS} -jar $(jar_file) ${JAR_OPTS} >${STD_FILE} 2>&1 & exit 0
  fi
}

kill_app() {
  echo "正在停止应用: kill $(app_pid)"
  kill $(app_pid)
}

kill_app_focus() {
  echo "正在停止应用: kill -9 $(app_pid)"
  kill -9 $(app_pid)
}

build_app() {
  echo "正在构建应用"
  mvn clean package >/dev/null
}

app_pid() {
  echo $(ps -x | grep "app.name=${APP_NAME}" | grep -v grep | awk '{print $1}')
}

jar_file() {
  echo $(ls target/*.jar | xargs -n 1 | tail -1) 2>/dev/null
}

unsupport() {
  echo "不支持 '$@' 命令，请使用 help 命令查看帮助"
}

##
# 执行指定脚本命令
##

if [[ $# = 1 ]]; then
  case $1 in
    help)
      echo "usage: run.sh [<command>] [<args>]"
      echo ""
      echo "support commands:"
      echo ""
      echo "   dev-rc            部署RC版本"
      echo "   dev-vt            显示RC版本"
      echo ""
      echo "   compile           mvn clean compile"
      echo "   package           mvn clean package"
      echo "   install           mvn clean install"
      echo ""
      echo "   fe -m             git fetch && git reset --hard origin/master"
      echo "   fe -d             git fetch && git reset --hard origin/develop"
      echo "   fe <branch>       git fetch && git reset --hard origin/<branch>"
      echo ""
      echo "   help              显示本帮助"
      echo ""
      exit 0
    ;;
    dev-rc)
      git fetch && git reset --hard origin/develop
      mvn clean install
      exit 0
    ;;
    dev-vt)
      echo $(jar_file); exit 0
    ;;
    compile)
      mvn clean compile; exit 0
    ;;
    package)
      mvn clean package; exit 0
    ;;
    install)
      mvn clean install; exit 0
    ;;
  esac
elif [[ $# = 2 ]]; then
  case $1 in
    fe)
      case $2 in
        -m) git fetch && git reset --hard origin/master; exit 0;;
        -d) git fetch && git reset --hard origin/develop; exit 0;;
        *) git fetch && git reset --hard origin/$2; exit 0;;
      esac
    ;;
  esac
fi

unsupport $@
exit 1
