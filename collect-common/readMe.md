# 目录
## collect-api
[collect-api/java](collect-api/src/main/java/com/common/collect/api)  
[collect-api/test](collect-api/src/test/java/collect/api)  

## collect-container
[collect-container/java](collect-container/src/main/java/com/common/collect/container)  
[collect-container/test](collect-container/src/test/java/collect/container)  

## collect-debug
[collect-debug/java](collect-debug/src/main/java/com/common/collect/debug)  
[collect-debug/test](collect-debug/src/test/java/collect/debug)  

## collect-model
[collect-model/java](collect-model/src/main/java/com/common/collect/model)  
[collect-model/test](collect-debug/src/test/java/collect/debug/mybatis)  

## collect-util
[collect-util/java](collect-util/src/main/java/com/common/collect/util)  
[collect-util/test](collect-util/src/test/java/collect/util)  

# maven 命令
## 发布命令
```bash
# -am 表示同时处理 选定模块所依赖的模块
# -amd 表示同时处理 依赖选定模块的模块
# -pl 选项后可跟随{groupId}:{artifactId}或者所选模块的相对路径(多个模块以逗号分隔)
# -N 表示不递归子模块
# -P 指定 profileId
mvn clean deploy -Dmaven.test.skip=true -am -pl collect-api,collect-util,collect-container,collect-model -P local
```
## 修改版本号
```bash
# 使用一下插件时，不需要使用下列命令，因为父类版本号已被统一一处
# <plugin>
#     <groupId>org.codehaus.mojo</groupId>
#     <artifactId>flatten-maven-plugin</artifactId>
# </plugin>
# 修改版本
mvn versions:set -DnewVersion=1.0-SNAPSHOT
# 若修改失败,可是使用命令回退版本号:
mvn versions:revert
# 若确认版本，可使用命令:
mvn versions:commit
```

