# 概述


## 物模型定义

### 属性

|编码|类型|名称|描述|
|---|---|---|---|
|lat|Float|经度|GPS|
|lon|Float|维度|GPS|
|lut|Date|最后上报样本时间|格式`yyyy-mm-dd HH:Mi:SS`|
|mac|String|网卡物理地址||


### 服务

#### A. 上报属性
* 编码：`ReportProperties`

#### B. 上报AP样本
* 编码：`ReportAPSample`
* 输出参数：
|编码|类型|名称|描述|
|---|---|---|---|
|lat|Float|经度|GPS|
|lon|Float|维度|GPS|
|lut|Date|最后上报样本时间|格式`yyyy-mm-dd HH:Mi:SS`|
|mac|String|网卡物理地址||