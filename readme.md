## 简介

本项目是一个基于Java和Vert.x开发的，具有运行时动态加载插件的微信机器人框架，旨在为Java开发者提供一个快捷接入微信机器人的框架。

项目本体仅提供消息管理、事件发布等核心功能，所有业务功能均有插件形式实现。

框架通过**adapter**进行机器人实现适配，目前实现的adapter有：
* `jbot-adapter-gewe`：gewe适配器，对接gewe
* `jbot-adapter-apad`：pad协议适配器，对接pad协议

适配器未适配所有功能，后续陆续进行适配。

## 使用方法

### 使用docker

通过`docker pull xmoxmo/jbot`命令拉取镜像运行（只有linux/amd64），感谢`xmo`佬构建的镜像。

### 本地使用&&IDEA内运行

1. install `jbot-build`模块
2. install `jbot-bom`模块
3. install `jbot-core`模块
4. package `jbot-adapter-apad`
5. 将打包后的 `jbot-adapter-apad` jar包添加到启动类路径
![img.png](doc/img.png)
6. 启动 `jbot-impl` 模块中的 `main` 方法