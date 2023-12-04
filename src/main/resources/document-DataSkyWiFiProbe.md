# 概述
支持DataSky厂家的专有数据协议

# 1. DS-006WIFI探针

## 1.1 认证方式
* ID认证：与`设备标识`比较，如果相等任务认证通过，否则丢弃消息；
* MAC认证：`mac`做为Token，如设备密钥比较

## 1.2 上报MAC采样数据
```http request
POST /{productId}/{deviceId}/event/reportData
Content-Type: application/json

{
	"id": "00f40f9a",
	"data": [ 
	{
		"mac": "50:98:b8:8b:fd:76",
		"rssi": "-89",
		"range": "77.4"
	}, {
		"mac": "50:98:b8:8b:fd:85",
		"rssi": "-79",
		"range": "33.0"
	}, {
		"mac": "50:98:b8:8b:fd:a3",
		"rssi": "-58",
		"range": "5.5"
	}, {
		"mac": "92:76:9f:c8:05:7f",
		"rssi": "-46",
		"router": "MERCURY_711",
		"range": "1.9"
	}, {
		"mac": "50:98:b8:8b:fd:a1",
		"rssi": "-58",
		"router": "Beitong",
		"range": "5.5"
	}, {
		"mac": "14:6b:9c:f4:0f:9a",
		"rssi": "-14",
		"router": "DataSky_f40f9a",
		"range": "1.0"
	}, {
		"mac": "10:9f:4f:69:75:bf",
		"rssi": "-76",
		"rssi1": "-75",
		"router": "北通",
		"range": "25.5"
	}, {
		"mac": "b0:95:8e:9c:23:82",
		"rssi": "-69",
		"router": "JS_QIDONG",
		"range": "14.0"
	}, {
		"mac": "f4:2a:7d:f1:4e:59",
		"rssi": "-70",
		"router": "ZP",
		"range": "15.3"
	}, {
		"mac": "12:25:51:f8:b9:48",
		"rssi": "-88",
		"rssi1": "-88",
		"rssi2": "-87",
		"rssi3": "-86",
		"ts": "ZP",
		"tmc": "f4:2a:7d:f1:4e:59",
		"tc": "N",
		"range": "71.0"
	}, {
		"mac": "86:73:6a:87:2c:de",
		"rssi": "-79",
		"rssi1": "-77",
		"ts": "ZP",
		"tmc": "f4:2a:7d:f1:4e:59",
		"tc": "Y",
		"ds": "Y",
		"range": "33.0"
	}, {
		"mac": "20:28:3e:22:2a:81",
		"rssi": "-78",
		"router": "agora.guest",
		"range": "30.3"
	}, {
		"mac": "20:28:3e:22:2a:80",
		"rssi": "-78",
		"rssi1": "-83",
		"rssi2": "-81",
		"tmc": "d6:42:ae:f8:76:1e",
		"router": "agora.io-18F",
		"range": "30.3"
	}, {
		"mac": "d6:42:ae:f8:76:1e",
		"rssi": "-71",
		"rssi1": "-71",
		"rssi2": "-73",
		"ts": "agora.io-18F",
		"tmc": "20:28:3e:22:2a:80",
		"tc": "Y",
		"ds": "Y",
		"range": "16.6"
	}, {
		"mac": "54:75:95:f2:db:08",
		"rssi": "-76",
		"router": "JFS会议室",
		"range": "25.5"
	}, {
		"mac": "10:9f:4f:69:87:8f",
		"rssi": "-88",
		"router": "北通",
		"range": "71.0"
	}, {
		"mac": "9c:a6:15:1d:4c:ad",
		"rssi": "-82",
		"router": "SC_qidong",
		"range": "42.6"
	}, {
		"mac": "fc:67:1f:06:a8:cd",
		"rssi": "-83",
		"ts": "JS_QIDONG",
		"tmc": "b0:95:8e:9c:23:82",
		"tc": "N",
		"range": "46.4"
	}, {
		"mac": "7c:03:c9:53:40:3d",
		"rssi": "-87",
		"router": "ChinaNet-aXfi",
		"range": "65.2"
	}, {
		"mac": "3c:6a:48:02:93:ac",
		"rssi": "-89",
		"range": "77.4"
	}, {
		"mac": "28:6c:07:71:eb:5f",
		"rssi": "-85",
		"rssi1": "-88",
		"essid0": "GZ300ZJ",
		"range": "55.0"
	}, {
		"mac": "ac:60:89:74:5f:78",
		"rssi": "-75",
		"router": "ChinaNet-rAgK",
		"range": "23.4"
	}, {
		"mac": "10:9f:4f:a1:7e:d6",
		"rssi": "-70",
		"rssi1": "-70",
		"router": "北通",
		"range": "15.3"
	}, {
		"mac": "30:fb:b8:3d:e5:e8",
		"rssi": "-46",
		"router": "711-WIFI",
		"range": "1.9"
	}, {
		"mac": "28:d1:27:bf:fc:c1",
		"rssi": "-83",
		"router": "cs_qidong",
		"range": "46.4"
	}, {
		"mac": "d8:ae:90:21:36:08",
		"rssi": "-87",
		"range": "65.2"
	}, {
		"mac": "08:1f:71:21:fe:a4",
		"rssi": "-88",
		"tmc": "34:04:9e:51:62:01",
		"router": "HJXDCG",
		"range": "71.0"
	}, {
		"mac": "3c:37:86:50:df:b1",
		"rssi": "-69",
		"router": "videotest",
		"range": "14.0"
	}, {
		"mac": "f4:2a:7d:e0:ac:70",
		"rssi": "-80",
		"router": "ZP",
		"range": "35.9"
	}, {
		"mac": "24:cf:24:16:f4:27",
		"rssi": "-86",
		"rssi1": "-86",
		"tmc": "fc:67:1f:06:a8:cd",
		"router": "JFS",
		"range": "59.9"
	}, {
		"mac": "04:f1:69:11:7b:a6",
		"rssi": "-65",
		"rssi1": "-65",
		"tmc": "04:f1:69:11:4d:c9",
		"range": "10.0"
	}, {
		"mac": "f4:2a:7d:f1:39:3d",
		"rssi": "-72",
		"router": "ZP",
		"range": "18.1"
	}, {
		"mac": "04:f1:69:11:7b:a4",
		"rssi": "-64",
		"router": "HWZM_Qidong",
		"range": "9.1"
	}, {
		"mac": "74:05:a5:d3:30:76",
		"rssi": "-82",
		"router": "ios_Qidong",
		"range": "42.6"
	}, {
		"mac": "20:28:3e:22:2b:80",
		"rssi": "-94",
		"router": "agora.io-18F",
		"range": "118.5"
	}, {
		"mac": "50:98:b8:8b:fd:83",
		"rssi": "-78",
		"router": "Beitong",
		"range": "30.3"
	}, {
		"mac": "20:28:3e:22:2b:81",
		"rssi": "-92",
		"router": "agora.guest",
		"range": "100.0"
	}, {
		"mac": "50:98:b8:8b:fd:74",
		"rssi": "-90",
		"router": "Beitong",
		"range": "84.3"
	}, {
		"mac": "26:41:8c:01:66:5c",
		"rssi": "-76",
		"router": "yao",
		"range": "25.5"
	}],
	"mmac": "14:6b:9c:f4:0f:9a",
	"rate": "2",
	"time": "Wed Jan 15 02:26:28 2020",
	"lat": "",
	"lon": ""
}/
```

## 1.3 物模型定义

### 1.3.1 属性

|编码|类型|描述|
|---|---|---|
|latitude|Float|经度||
|longitude|Float|维度||
|rate|Integer|采样参数|
|latestUpdateTime|Long|最后上报样本的时间戳，单位：`毫秒`|
|mac|String|探针网卡物理地址||

### 1.3.2 服务

#### A. 上报属性
* 编码：`ReportProperties`

#### B. 上报AP样本
* 事件编码：`ReportAPSample`
* 输出参数：封装类型`Map<String, Object>`

|编码|类型|描述|
|---|---|---|
|rssi|Integer|信号强度|
|ssid|String|AP的ssid|
|range|Float|估算的远近距离，单位：米|
|sampleTime|Long|采样时间戳，单位：`毫秒`|
|mac|String|网卡物理地址|

#### C. 上报WiFi客户端样本
* 事件编码：`ReportClientSample`
* 输出参数：封装类型`Map<String, Object>`

|编码|类型|描述|
|---|---|---|
|apSsid|String|链接的AP的ssid|
|apMac|String|链接的AP的MAC地址|
|apConnected|Boolean|是否已链接的AP|
|rssi0|Integer|同一采样周期内信号强度的样本，单位：`DB`|
|rssi1|Integer|同一采样周期内信号强度的样本，单位：`DB`|
|rssi2|Integer|同一采样周期内信号强度的样本，单位：`DB`|
|rssi3|Integer|同一采样周期内信号强度的样本，单位：`DB`|
|rssi4|Integer|同一采样周期内信号强度的样本，单位：`DB`|
|rssi5|Integer|同一采样周期内信号强度的样本，单位：`DB`|
|recentSsid0|String|曾经链接过的AP的ssid|
|recentSsid1|String|曾经链接过的AP的ssid|
|recentSsid2|String|曾经链接过的AP的ssid|
|recentSsid3|String|曾经链接过的AP的ssid|
|recentSsid4|String|曾经链接过的AP的ssid|
|recentSsid5|String|曾经链接过的AP的ssid|
|recentSsid6|String|曾经链接过的AP的ssid|
|range|Float|估算的远近距离，单位：米|
|sampleTime|Long|采样时间戳，单位：`毫秒`|
|mac|String|网卡物理地址|

### 1.3.3 TSL
```TSL
{
  "properties": [
    {
      "id": "latitude",
      "name": "经度",
      "valueType": {
        "type": "float",
        "unit": "度",
        "scale": 4
      },
      "expands": {
        "source": "device",
        "type": [
          "report"
        ],
        "metrics": []
      }
    },
    {
      "id": "longitude",
      "name": "维度",
      "valueType": {
        "type": "float",
        "unit": "度",
        "scale": 4
      },
      "expands": {
        "source": "device",
        "type": [
          "report"
        ],
        "metrics": []
      }
    },
    {
      "id": "rate",
      "name": "采样参数",
      "valueType": {
        "type": "int"
      },
      "expands": {
        "source": "device",
        "type": [
          "report"
        ],
        "metrics": []
      }
    },
    {
      "id": "latestUpdateTime",
      "name": "最后上报样本的时间戳",
      "valueType": {
        "type": "long",
        "unit": "milliseconds"
      },
      "expands": {
        "source": "device",
        "type": [
          "report"
        ],
        "metrics": []
      }
    },
    {
      "id": "mac",
      "name": "探针网卡物理地址",
      "valueType": {
        "type": "string",
        "expands": {
          "maxLength": 64
        }
      },
      "expands": {
        "source": "device",
        "type": [
          "report"
        ],
        "metrics": []
      }
    }
  ],
  "events": [
    {
      "id": "ReportAPSample",
      "name": "上报AP样本",
      "expands": {
        "level": "ordinary"
      },
      "valueType": {
        "type": "object",
        "properties": [
          {
            "valueType": {
              "expands": {},
              "type": "int",
              "unit": "分贝"
            },
            "id": "rssi",
            "name": "信号强度"
          },
          {
            "valueType": {
              "expands": {
                "maxLength": 64
              },
              "type": "string"
            },
            "name": "AP的ssid",
            "id": "ssid"
          },
          {
            "valueType": {
              "expands": {},
              "type": "float",
              "scale": 2,
              "unit": "meter"
            },
            "name": "估算的远近距离",
            "id": "range"
          },
          {
            "valueType": {
              "expands": {},
              "type": "long",
              "unit": "milliseconds"
            },
            "id": "sampleTime",
            "name": "采样时间戳"
          },
          {
            "valueType": {
              "expands": {
                "maxLength": 64
              },
              "type": "string"
            },
            "name": "网卡物理地址",
            "id": "mac"
          }
        ]
      }
    },
    {
      "id": "ReportClientSample",
      "name": "上报WiFi客户端样本",
      "expands": {
        "level": "ordinary"
      },
      "valueType": {
        "type": "object",
        "properties": [
          {
            "valueType": {
              "expands": {
                "maxLength": 128
              },
              "type": "string"
            },
            "id": "apSsid",
            "name": "链接的AP的ssid"
          },
          {
            "valueType": {
              "expands": {
                "maxLength": 64
              },
              "type": "string"
            },
            "name": "链接的AP的MAC地址",
            "id": "apMac"
          },
          {
            "valueType": {
              "trueText": "是",
              "trueValue": "true",
              "falseText": "否",
              "falseValue": "false",
              "expands": {},
              "type": "boolean"
            },
            "name": "是否已链接的AP",
            "id": "apConnected"
          },
          {
            "valueType": {
              "expands": {},
              "type": "int",
              "unit": "分贝"
            },
            "id": "rssi0",
            "name": "信号强度0"
          },
          {
            "valueType": {
              "expands": {},
              "type": "int",
              "unit": "分贝"
            },
            "name": "信号强度1",
            "id": "rssi1"
          },
          {
            "valueType": {
              "expands": {},
              "type": "int",
              "unit": "分贝"
            },
            "name": "信号强度2",
            "id": "rssi2"
          },
          {
            "valueType": {
              "expands": {},
              "type": "int",
              "unit": "分贝"
            },
            "name": "信号强度3",
            "id": "rssi3"
          },
          {
            "valueType": {
              "expands": {},
              "type": "int",
              "unit": "分贝"
            },
            "name": "信号强度4",
            "id": "rssi4"
          },
          {
            "valueType": {
              "expands": {},
              "type": "int",
              "unit": "分贝"
            },
            "name": "信号强度5",
            "id": "rssi5"
          },
          {
            "valueType": {
              "expands": {
                "maxLength": 128
              },
              "type": "string"
            },
            "id": "recentSsid0",
            "name": "曾经链接过的AP的ssid 0"
          },
          {
            "valueType": {
              "expands": {
                "maxLength": 128
              },
              "type": "string"
            },
            "id": "recentSsid1",
            "name": "曾经链接过的AP的ssid 1"
          },
          {
            "valueType": {
              "expands": {
                "maxLength": 128
              },
              "type": "string"
            },
            "id": "recentSsid2",
            "name": "曾经链接过的AP的ssid 2"
          },
          {
            "valueType": {
              "expands": {
                "maxLength": 128
              },
              "type": "string"
            },
            "id": "recentSsid3",
            "name": "曾经链接过的AP的ssid 3"
          },
          {
            "valueType": {
              "expands": {
                "maxLength": 128
              },
              "type": "string"
            },
            "id": "recentSsid4",
            "name": "曾经链接过的AP的ssid 4"
          },
          {
            "valueType": {
              "expands": {
                "maxLength": 128
              },
              "type": "string"
            },
            "id": "recentSsid5",
            "name": "曾经链接过的AP的ssid 5"
          },
          {
            "valueType": {
              "expands": {
                "maxLength": 128
              },
              "type": "string"
            },
            "id": "recentSsid6",
            "name": "曾经链接过的AP的ssid 6"
          },
          {
            "valueType": {
              "expands": {},
              "type": "float",
              "scale": 4,
              "unit": "meter"
            },
            "id": "range",
            "name": "估算的远近距离"
          },
          {
            "valueType": {
              "expands": {},
              "type": "long",
              "unit": "milliseconds"
            },
            "name": "采样时间戳",
            "id": "sampleTime"
          },
          {
            "valueType": {
              "expands": {
                "maxLength": 64
              },
              "type": "string"
            },
            "id": "mac",
            "name": "网卡物理地址"
          }
        ]
      }
    }
  ]
}
```

