#!/bin/bash

##
# 环境准备
##

AppHome=$(cd `dirname $0`; pwd)
AppName=${AppHome##*/}

StdFile="target/std.log"
LogFile="target/log.log"

JvmParams="-Xms64m -Xmx128m -XX:MaxMetaspaceSize=1024m"
JarParams="--app.name=${AppName} --spring.profiles.active=pro"

cd ${AppHome}

##
# 外部命令
##

echo_cmd() {
  echo "$*"
  $@
}

app_jar() {
  echo $(ls target/*.jar 2>/dev/null | xargs -n 1 | tail -1)
}

app_pid() {
  echo $(ps -x | grep "app.name=${AppName}" | grep -v grep | awk '{print $1}')
}

do_kill() {
  echo "正在停止应用"
  echo_cmd "kill $(app_pid)"
}

try_kill_quiet() {
  if [[ -n $(app_pid) ]]; then
    do_kill
  fi
}

do_kill_force() {
  echo "正在停止应用"
  echo_cmd "kill -9 $(app_pid)"
}

try_kill_force_quiet() {
  if [[ -n $(app_pid) ]]; then
    do_kill_force
  fi
}

do_fetch() {
  echo "正在拉取分支：$1"
  git add . >/dev/null
  git stash >/dev/null
  git fetch
  git checkout $1 >/dev/null 2>&1
  git reset --hard origin/$1
}

do_build() {
  echo "正在构建应用"
  echo_cmd "mvn clean package"
}

do_kill() {
  echo "正在停止应用"
  echo_cmd "kill $(app_pid)"
}

try_build() {
  if [[ -z $(app_jar) ]]; then
    do_build
  fi
}

do_start() {
  echo "正在启动应用"
  echo "java ${JvmParams} -jar $(app_jar) ${JarParams} >${StdFile} 2>&1"
  nohup java ${JvmParams} -jar $(app_jar) ${JarParams} >${StdFile} 2>&1 & exit
}

try_start() {
  if [[ -n $(app_pid) ]]; then
    echo "应用已经启动, PID=$(app_pid)"
    exit
  fi
  if [[ -z $(app_jar) ]]; then
    echo "启动应用失败: 未找到JAR文件"
    exit
  fi
  do_start
}

##
# 脚本分支
##

cmd_help() {
  echo "usage: bash app.sh [<command>] [<args>]"
  echo ""
  echo "support commands:"
  echo ""
  echo "   rc                安装应用（预览版本）"
  echo "   rc -f             安装应用（任意版本）"
  echo "   rc -l             应用版本"
  echo ""
  echo "   st                启动应用"
  echo "   st -f             启动应用（强制重新构建）"
  echo "   st -fm            启动应用（拉取主线分支）"
  echo "   st -fd            启动应用（拉取开发分支）"
  echo "   qt                停止应用"
  echo "   qt -f             停止应用（强制停止）"
  echo "   vt                运行状态"
  echo ""
  echo "   fe -d             更新主线分支"
  echo "   fe -d             更新开发分支"
  echo ""
  echo "   std               tail -f ${StdFile}"
  echo "   std -a            less ${StdFile}"
  echo "   std <params>      grep <params> ${StdFile}"
  echo "   log               tail -f ${LogFile}"
  echo "   log -a            less ${LogFile}"
  echo "   log <params>      grep <params> ${LogFile}"
  echo ""
  echo "   help              显示本帮助"
  echo ""
  exit
}

cmd_rc() {
  if [[ $# = 1 ]]; then
    do_fetch develop
    do_build
    if [[ "$(app_jar)" =~ "-rc" ]]; then
      echo_cmd "mvn install"
      exit
    else
      echo "不是RC版本"
      exit
    fi
    exit
  elif [[ $# = 2 && $2 = '-f' ]]; then
    do_fetch develop
    echo_cmd "mvn clean install"
    exit
  elif [[ $# = 2 && $2 = '-l' ]]; then
    echo "$(app_jar)"
    exit
  fi
}

cmd_st() {
  if [[ $# = 1 ]]; then
    try_build
    try_start
    exit
  elif [[ $# = 2 ]]; then
    case $2 in
      -f)
        do_build
        try_kill_force_quiet
        do_start
        exit
      ;;
      -fm)
        do_fetch master
        do_build
        try_kill_force_quiet
        do_start
        exit
      ;;
      -fd)
        do_fetch develop
        do_build
        try_kill_force_quiet
        do_start
        exit
      ;;
    esac
  fi
}

cmd_qt() {
  if [[ -z $(app_pid) ]]; then
    echo "应用未运行"
    exit
  fi
  if [[ $# = 1 ]]; then
    do_kill
    exit
  fi
  if [[ $# = 2 && $2 = -f ]]; then
    do_kill_force
    exit
  fi
}

cmd_vt() {
  if [[ -z $(app_pid) ]]; then
    echo "应用未运行"
    exit
  else
    echo "应用运行中, PID=$(app_pid)"
    exit
  fi
}

cmd_fe() {
  if [[ $# = 2 ]]; then
    case $2 in
      -d)
        do_fetch develop
        exit
      ;;
      -m)
        do_fetch master
        exit
      ;;
    esac
  fi
}

cmd_log() {
  file=$1
  shift

  if [[ $# = 1 ]]; then
    echo_cmd "tail -f ${StdFile}"
    exit
  fi
  if [[ $# = 2 && $2 = -a ]]; then
    echo_cmd "less ${StdFile}"
    exit
  fi
  if [[ $# > 1 ]]; then
    shift
    echo_cmd "grep $@ ${StdFile}"
    exit
  fi
}

unsupport() {
  echo "不支持 'bash app.sh $@' 命令，是否要查看帮助"
  read -p "请输入(y/n): " input
  case ${input} in
    y|Y)
      cmd_help
      exit
    ;;
  esac
}

##
# 派发分支
##

if [[ $# = 0 ]]; then
  cmd_help
  exit
fi

case $1 in
  help)
    cmd_help
  ;;
  rc)
    cmd_rc $*
  ;;
  st)
    cmd_st $*
  ;;
  qt)
    cmd_qt $*
  ;;
  vt)
    cmd_vt
  ;;
  fe)
    cmd_fe $*
  ;;
  std)
    cmd_log ${StdFile} $*
  ;;
  log)
    cmd_log ${LogFile} $*
  ;;
esac

unsupport $@
exit 1
