#!/usr/bin/env bash

# 设置项目根目录
base=$(cd `dirname $0`/..; pwd)
cd ${base}

# 更新 pom.xml 版本号
mvn versions:set -DnewVersion=$1
mvn versions:commit

# 提交 pom.xml 版本号
git add pom.xml
git commit -m "chore: set version to v$1"
