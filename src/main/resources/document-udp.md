## 使用UDP推送设备数据

### 认证说明
TLK
0x0B{LEN}{TOKEN_STR}


CONNECT报文:
```text
clientId: 设备ID
username: secureId+"|"+timestamp
password: md5(secureId+"|"+timestamp+"|"+secureKey)
 ```

说明: secureId以及secureKey在创建设备产品或设备实例时进行配置.
timestamp为当前时间戳(毫秒),与服务器时间不能相差5分钟.
md5为32位,不区分大小写.

### 上报属性例子:

```http request
POST /{productId}/{deviceId}/properties/report
Authorization: Bearer {产品或者设备中配置的Token}
Content-Type: application/json

{
 "properties":{
   "temp":38.5
 }
}
```

### 上报事件例子:

```http request
POST /{productId}/{deviceId}/event/{eventId}
Authorization: Bearer {产品或者设备中配置的Token}
Content-Type: application/json

{
 "data":{
   "address": ""
 }
}
```