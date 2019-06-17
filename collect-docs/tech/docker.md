
# 组成组件
## 客户端
用户与服务交互的工具，当前指的是各种操作的命令。  
## 服务
后台运行的服务（守护进程），处理客户端的请求。  
管理镜像，容器，网络，磁盘等资源。  
客户端和服务可以在同一台机器，也可以在不同机器。  
## 仓库
存储管理镜像的地方，提供类git的pull/push操作。
## 镜像&容器
镜像是一个只读模板（定义要一个软件的组件集合），用来创建容器。
镜像是一个静态的文件，这个文件需要运行就需要变为容器。   
容器就是镜像的一个实例，容器 = new 镜像()。  
### 镜像
镜像包含着运行这个软件所需的内容，包括代码，运行时，库，环境变量，配置文件等。  
镜像可以基于其他镜像，譬如tomcat需要运行在os上，所以tomcat镜像需要继续os镜像。  
### 容器
容器是用镜像创建的运行实例，可以利用容器独立运行一个或一组应用。

#  容器数据卷(运行时数据存储)
容器数据卷就是做**数据的持久化**和**容器间的数据共享**，数据卷独立于容器的生命周期。容器关闭或者删除，数据也不会丢失。  
容器映射到宿主机的目录，应用在容器内的数据同步到宿主机磁盘上（持久化）。  
多个不同的容器映射到宿主机的同一个目录，就可以实现不同容器间的数据共享（共享）。    

# 命令
## 帮助命令
docker version：查看 Docker 客户端和服务的版本。  
docker info：查看 Docker 的基本信息，如有多少容器、多少镜像、Docker 根目录等等。  
docker --help：查看 Docker 的帮助信息，这个命令可以查看所有 Docker 支持的命令
## 镜像命令
docker images：查看本地主机上所有的镜像。  
docker images image 指定某个具体的镜像查看对应信息。  
docker rmi image：删除本地的镜像，可以加上 -f 参数进行强制删除。  
docker search image：根据镜像名称搜索远程仓库中的镜像。  
docker pull image:[tag] ：搜索到某个镜像之后就可以从远程拉取镜像
docker push：推送某个镜像到远程  
## 容器命令
docker run [OPTIONS] IMAGE [COMMAND] [ARG...]：基于某个镜像运行一个容器，如果本地有指定的镜像则使用本地镜像，如果没有则从远程拉取对应的镜像后启动。  
例子：  
docker run -it -p8888:8080 tomcat  
docker run -d -p8888:8080 -v /home/root:/usr/local/tomcat/logs tomcat   
比较重要的参数：  
-d：启动容器，并且后台运行。(与 -it 二选一)  
-i：以交互模式运行容器，通常与 -t 同时使用。  
-t：为容器重新分配一个伪输入终端，通常与 -i 同时使用（容器启动后进入到容器内部的命令窗口）。  
-P：随机端口映射，容器内部端口随机映射到主机的端口。  
-p：指定端口映射，格式为：主机端口：容器端口。  
-v：建立宿主机与容器目录的同步(在宿主机上对文件的修改也会同步到容器内部)。主机目录：容器目录  
--name="tomcat"：为容器指定一个名称（如果不指定，则有个随机的名字）。  
  
进入到容器后可以通过 exit 命令退出容器，也可以通过 Ctrl+P+Q 快捷键退出容器。  
exit 会退出并且关闭容器，而 Ctrl+P+Q 快捷键只是单纯的退出，容器还在运。    

docker ps：查看正在运行的容器的信息，容器的唯一 id，启动时间等。  
docker ps -a： 查看运行中与停止的所有容器。  

docker attach [OPTIONS] id： 通过 attach 命令+容器的 id 再次进入容器。  
docker exec [OPTIONS] id： 可以不进入容器而在运行的容器中执行命令。  
docker stop id：停止容器
docker kill id：强制停止容器
docker restart id：重启容器
docker rm id：删除某个容器
docker inspect id：查看容器的详情，也能查看镜像详情。  

# Dockerfile 语法
## FROM
指定基础镜像，当前镜像是基于哪个镜像创建的，FROM 指令必是 Dockerfile 文件中的首条命令。  
```
FROM <image> [AS<name>]  
FROM <image>[:<tag>][AS <name>]  
FROM<image>[@<digest>] [AS <name>] 
```
## LABEL
给镜像添加元数据，可以用 LABEL 命令替换 MAINTAINER 命令。指定一些作者、邮箱等信息。  
```
LABEL<key>=<value><key>=<value><key>=<value> ...
```
## ENV
设置环境变量，设置的变量可供后面指令使用。  
```
ENV <key><value>
ENV <key>=<value>
```
## WORKDIR
设置工作目录，在该指令后的 RUN、CMD、ENTRYPOINT, COPY、ADD 指令都会在该目录执行。如果该目录不存在，则会创建！  
```
WORKDIR /path/to/workdir
```
## RUN
在当前镜像的最上面创建一个新层，并且能执行任何的命令，然后对执行的结果进行提交。  
提交后的结果镜像在 Dockerfile 的后续步骤中可以使用。  
RUN 指令是在构建镜像时候执行的  
```
RUN <command>
RUN ["executable","param1", "param2"]
```
## ADD
从宿主机拷贝文件或者文件夹到镜像，也可以复制一个网络文件！如果拷贝的文件是一个压缩包，会自动解压缩    
```
ADD[--chown=<user>:<group>] <src>... <dest>
ADD[--chown=<user>:<group>] ["<src>",..."<dest>"]
```
## COPY
从宿主机拷贝文件或者文件夹到镜像，不能复制网络文件也不会自动解压缩  
```
COPY[--chown=<user>:<group>] <src>... <dest>
COPY[--chown=<user>:<group>] ["<src>",..."<dest>"]
```
## VOLUME
VOLUME 用于创建挂载点，一般配合 run 命令的 -v 参数使用  
```
VOLUME ["/data"]
```
## EXPOSE
指定容器运行时对外暴露的端口，但是该指定实际上不会发布该端口，它的功能是镜像构建者和容器运行者之间的记录文件。
回到容器命令中的 run 命令部分，run 命令有 -p 和 -P 两个参数。    
如果是 -P 就是随机端口映射，容器内会随机映射到 EXPOSE 指定的端口，   
如果是 -p 就是指定端口映射，告诉运维人员容器内需要映射的端口号。
```
EXPOSE <port>[<port>/<protocol>...]
```
## CMD
指定容器启动时默认运行的命令，在一个 Dockerfile 文件中，如果有多个 CMD 命令，只有一个最后一个会生效。  
CMD 指令是在每次容器运行的时候执行的，docker run 命令会覆盖 CMD 的命令     
```
CMD["executable","param1","param2"]
CMD["param1","param2"]
CMD command param1 param2
```
## ENTRYPOINT
指定启动容器时要运行的命令  
如果有多个 ENTRYPOINT 命令，也只有一个最后一个会生效  
docker run command 命令不会覆盖 ENTRYPOINT  
docker run 命令中指定的任何参数都会被当做参数传递给 ENTRYPOINT  
如果指定了 ENTRYPOINT，则 CMD 指定的命令不会执行  
```
ENTRYPOINT["executable", "param1", "param2"]
ENTRYPOINT command param1 param2
```
### RUN、CMD、ENTRYPOINT 区别
RUN 指令是在镜像构建时运行，而后两个是在容器启动时执行   
CMD 指令设置的命令是容器启动时默认运行的命令，如果 docker run 没有指定任何的命令，那容器启动的时候就会执行 CMD 指定的命令。  
ENTRYPOINT 指令，如果设置了则优先使用 ENTRYPOINT 不使用 CMD， 并且可以通过 dockerrun 给该指令设置的命令传参。CMD 有点类似代码中的缺省参。   
## USER
用于指定运行镜像所使用的用户。  
```
USER <user>[:<group>]
USER <UID>[:<GID>]
```
## ARG
指定在镜像构建时可传递的变量，定义的变量可以通过 dockerbuild --build-arg = 的方式在构建时设置。  
```
ARG <name>[=<defaultvalue>]
```
## ONBUILD
当所构建的镜像被当做其他镜像的基础镜像时，ONBUILD 指定的命令会被触发  
```
ONBUILD [INSTRUCTION] 
```
## STOPSIGNAL
设置当容器停止时所要发送的系统调用信号  
```
STOPSIGNAL signal
```
## HEALTHCHECK
设置如何检测一个容器的运行状况  
```
HEALTHCHECK [OPTIONS] CMD command （在容器内运行运行命令检测容器的运行情况）
HEALTHCHECK NONE （禁止从父镜像继承检查）
```
## SHELL
用于设置执行命令所使用的默认的 Shell 类型  
```
SHELL ["executable","parameters"]
```
## 构建
Dockerfile 执行顺序是从上到下，顺序执行。  
每条指令都会创建一个新的镜像层，并对镜像进行提交。  
编写好 Dockerfile 文件后，就需要使用 dockerbuild 命令对镜像进行构建了。  
docker build [OPTIONS] PATH | URL 
docker build -t myApp:1.0.1 /home/root
-f：指定要使用的 Dockerfile 路径，如果不指定，则在当前工作目录寻找 Dockerfile 文件  
-t：镜像的名字及标签，通常 name:tag 或者 name 格式；可以在一次构建中为一个镜像设置多个标签。  

