
## 在线编辑网站
[在线编辑](http://www.plantuml.com/plantuml/uml/)
## 在线示例网站：
[在线示例](http://plantuml.com/zh/)

## 安装指南
### mac
brew install graphviz

### sublime
```
cmd+shift+p  add repository   https://github.com/jvantuyl/sublime_diagram_plugin.git
cmd+shift+p  install package  sublime_diagram_plugin
在 Preferences -> Packages Setting 看到 Diagram ，默认绑定的渲染(graphviz)快捷键是cmd + m 如果不冲突直接使用即可。
临时渲染文件位置:类/private/var/folders/hk/qdkv8g6528ncj9g9mmmgrdg00000gn/
```

### windows
```
/Users/***/Library/Application\ Support/Sublime\ Text\ 3/Packages/sublime_diagram_plugin/diagram/plantuml-8054.jar
java -jar plantuml-8054.jar -verbose test.txt 查看文件夹中多了一个png的图片，说明测试成功。
```
