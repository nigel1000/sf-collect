
#!/bin/sh

# alias rm="sh /Users/nijianfeng/Documents/projects/sf-collect/collect-script/shell/remove.sh"

# 遇到不存在的变量时报错
set -u
# 任何一个语句返回非真则退出bash
set -e

pwdDir=`pwd`
trashDir="/Users/nijianfeng/.Trash"
stamp=`date "+%Y%m%d%H%M%S"`
recordFile=${trashDir}/${stamp}-recordFile.txt
recoveryFile=${trashDir}/${stamp}-recoveryFile.txt
touch ${recordFile}
touch ${recoveryFile}

echo ${pwdDir} | tee -a ${recordFile}

for i in $*; do
    if [[ $i == -* ]];then
        continue
    fi
    fileName=`basename $i`
    echo mv $i ${trashDir}/${stamp}.${fileName} | tee -a ${recordFile}
    # 将输入的参数对应文件 mv 至 .Trash 目录
    mv $i ${trashDir}/${stamp}.${fileName}
    echo mv ${trashDir}/${stamp}.${fileName} ${pwdDir}/$i | tee -a ${recoveryFile}
done