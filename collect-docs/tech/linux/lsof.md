# 恢复删除但被打开的文件

```shell
# 删除某个文件
rm /var/log/syslog
# 查看此文件当前被什么进程打开
lsof | grep syslog
# COMMAND PID  USER FD TYPE DEVICE SIZE    NODE     NAME
# syslogd 1283 root 2w REG   3,3   5381017 1773647 /var/log/syslog (deleted)
ls -l /proc/[PID]/fd/[FD]
cat /proc/[PID]/fd/[FD] > /var/log/syslog
```

# 常用命令

```shell
# 哪个进程在占用/etc/passwd
lsof /etc/passwd 
# 显示出哪个文件被以courier打头的进程打开，但是并不属于用户zahn
lsof -c courier -u ^zahn 
# 显示哪些文件被pid为30297的进程打开
lsof -p 30297 
# 显示所有在/tmp文件夹中打开的instance和文件的进程。但是symbol文件并不在列
lsof -D /tmp 
# 查看不是用户tony的进程的文件使用情况
# 显示所有打开80端口的进程
lsof -i:80 
# 显示所有打开的端口和UNIX domain文件
lsof -i -U 
# 显示那些进程打开了到www.akadia.com的UDP的123(ntp)端口的链接
lsof -i udp@www.akadia.com:123 
# 不断查看目前ftp连接的情况
# (-r，lsof会永远不断的执行，直到收到中断信号, +r，lsof会一直执行，直到没有档案被显示,缺省是15s刷新)
# (-n 不将IP转换为hostname，缺省是不加上-n参数)
lsof -i tcp@ohaha.ks.edu.tw:ftp -r-n 
```



