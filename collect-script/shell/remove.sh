
#!/bin/sh

# alias rm="sh /Users/nijianfeng/Documents/projects/sf-collect/collect-script/shell/remove.sh"

# 定义文件夹目录.Trash
TRASH_DIR="/Users/nijianfeng/.Trash"
PWD=`pwd`
DATE=`date "+%Y%m%d%H%M%S"`
TARGET_DIR=$TRASH_DIR/$DATE/
mkdir -p "$TARGET_DIR"
RECORD_FILE=$TARGET_DIR/."操作纪录-"$DATE.txt
touch "$RECORD_FILE"
echo "当前目录：$PWD" >> "$RECORD_FILE"

for i in $*; do
    if [[ "$i" == -* ]];then
        continue
    fi
    echo "move $i to $TARGET_DIR" >> "$RECORD_FILE"
    # 将输入的参数对应文件 mv 至 .Trash 目录
    mv "$i" "$TARGET_DIR"
done