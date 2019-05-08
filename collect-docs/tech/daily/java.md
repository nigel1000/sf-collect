## 命令
```bash
#查看java进程的PID
jps    
#显示所有可设置参数及默认值
java -XX:+PrintFlagsInitial    
#获取到所有可设置参数及值(手动设置之后的值)
java -XX:+PrintFlagsFinal    
#显示出JVM初始化完毕后所有跟最初的默认值不同的参数及它们的值
java -XX:+PrintCommandLineFlags    
```
