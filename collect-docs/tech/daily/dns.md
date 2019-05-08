# 命令
```bash
#显示网络信息
ipconfig/ifconfig    
#查询域名对应的ip同时也显示了网关地址
nslookup host    
#查看本地dns服务器地址
cat /etc/resolv.conf    
#查询域名的A纪录，[A,MX,NS,CNAME,TXT] ，默认是a
dig host a   
#根据地址指定dns服务器来解析域名
dig @202.106.0.20 www.oolec.com a    
#采用tcp方式，默认采用udp协议进行查询
dig host a +tcp    
#显示从根域逐级查询的过程
dig host a +trace    
#清除缓存的域名
/etc/init.d/nscd restart 
#本地域名映射表
/etc/hosts    
```

# 小知识
## 修改hosts后生效方法(清楚DNS缓存)
```
Windows
开始 -> 运行 -> 输入cmd -> 在CMD窗口输入 ipconfig /flushdns

Linux 
终端输入 sudo rcnscd restart
对于systemd发行版，请使用命令 sudo systemctl restart NetworkManager

Mac OS X
终端输入 sudo killall -HUP mDNSResponder

Android 
开启飞行模式 -> 关闭飞行模式

通用方法 拔网线(断网) -> 插网线(重新连接网络)
如不行请清空浏览器缓存（请使用谷歌Chrome浏览器）
```

