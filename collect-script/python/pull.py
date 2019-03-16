#!/usr/bin/env python 
# -*- coding:utf-8 -*-

# Created by nijianfeng at 18/6/13

import sys
import os
import threading
import time

# alias pull="python3 /Users/nijianfeng/Documents/projects/sf-collect/collect-script/python/pull.py"

# 默认扫描地址
def_path = "/Users/nijianfeng/Documents/projects/"
#  全局锁
threadLock = threading.Lock()

# 执行 git pull 的任务线程
class PullThread(threading.Thread):

    def __init__(self, path):
        threading.Thread.__init__(self)
        self.__path = path

    def run(self):
        threadLock.acquire()
        print("开始 git pull：" + self.__path)
        os.chdir(self.__path)
        os.system("git pull ")
        print("结束pull：" + self.__path)
        threadLock.release()

# 收集 git 项目的路径地址
paths = []
def pull_path(dir_path=def_path):
    files = os.listdir(dir_path)
    for f in files:
        file_path = dir_path + '/' + f
        if os.path.isdir(file_path):
            if f == ".git":
                paths.append(dir_path)
                break
            else:
                pull_path(file_path)

# 默认一个。 此脚本文件名
# 获取入参扫描地址
args = sys.argv
argsLen = len(args)
if 1 == argsLen:
    print(args[0] + " have no args!")
    print("do you want to pull " + def_path)
    print("please input y to continue >>> ")
    if input() in ["Y", "y", "yes"]:
        pull_path()
    else:
        sys.exit(0)
else:
    for i in range(1, argsLen):
        if os.path.isdir(args[i]):
            pull_path(args[i])
        else:
            print(args[i] + " is not a dir!")
            continue

# 执行 git pull
threads = []
for p in paths:
    t = PullThread(p)
    threads.append(t)
    t.start()

for temp in threads:
    temp.join()
