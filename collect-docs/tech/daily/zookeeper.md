## 客户端
```bash
#连接默认zookeeper服务器
zkcli    
#连接指定的zookeeper服务器
zkcli -server ip:port    
#创建节点，-s表示顺序，-e表示临时，默认是持久节点，acl缺省表示不做任何权限限制
create -s -e path data [acl]    
#显示path下的节点，不递归显示,watch注册监听，命令行可忽视
ls path [watch]    
#显示当前节点下的节点和当前节点的属性信息
ls2 path   
#获取path的属性信息和数据内容
get path [watch]    
#更新path的数据内容，version是做类似cas的功能的对应dataversion，命令行可忽略
set path data [version]    
#删除节点，不能递归删除，只能删除叶子节点
delete path [version]    
#设置节点acl,例子(scheme:id:password=:perm)-(digest:example:sha-1(base64(pwd))=:cdrwa) create delete read write admin
setacl path acl    
#获取path节点的acl
getacl path    
#查看path的属性信息
stat path    
#退出zkcli
quit 
```

## 四字命令介绍(开放远程jmx端口，用jconsole观察更直观更全面)
```
telnet ip port     连接上后通过执行四字命令操作
conf    服务器的配置信息打印 datadir datalogdir ticktime等
cons    输出所有客户端连接的详细信息
crst    重置所有客户端连接的统计信息
dump    输出当前几圈所有会话信息
envi    服务器运行时的环境信息
ruok    输出服务器是否正在运行 i‘m ok 只能说明端口打开着
stat/srvr    输出服务器运行时的状态信息，srvr不会将客户端的连接情况输出
srst    重置服务器的统计信息
wchs    输出服务器上watcher的概要信息
wchc/wchp    输出服务器上watchs的详细信息,p是以节点路径进行归组显示，c是以watch进行归组
mntr    输出比stat更新详尽的服务器统计信息
```
