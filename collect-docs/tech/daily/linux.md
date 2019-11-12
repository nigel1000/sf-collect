## 命令
```bash
# 查询占用此端口的进程
lsof -i tcp:port 
# 列出该进程打开的文件
lsof -p PID 

#显示某些文件的大小
du -sh *
#磁盘空间是否还有剩余 -a 全部文件系统列表  -h 方便阅读方式显示 1K=1000，而不是1K=1024 
df -h 
# 显示 M   
ls -lh

#拷贝资源至目标目录下
scp -P1046  hznijianfeng@10.194.69.249:/home/hznijianfeng/1.txt D:\hznijianfeng
scp -P1046 D:\hznijianfeng\2.txt hznijianfeng@10.194.69.249:/home/hznijianfeng/

#debug
ssh -p1046 -L11111:localhost:11230 hznijianfeng@10.194.69.24
ssh -p1046 hznijianfeng@10.194.69.249  
# xshell
ssh hznijianfeng@10.194.69.249 1046 

# 在压缩文件中搜索
bzgrep 'content' file.log.bz2 | wc -l
bzcat file.log.bz2 | grep 'content' | wc -l
less file.log.bz2 | grep 'content' | wc -l

# 清空最大文件
find . -type f -name "*log*" | xargs ls -lSh | more
du -a . | sort -rn | grep log | more
find . -name '*log*' -size +10M -exec du -h {} \;

echo "" > big.file.log
rm -rf a.log # 需要机器重启后才能真正释放占用空间(node 的设计)

```
