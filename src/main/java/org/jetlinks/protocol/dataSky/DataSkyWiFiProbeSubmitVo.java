package org.jetlinks.protocol.dataSky;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jetlinks.protocol.common.DedicatedMessage;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * DataSkyWiFi探针上报的Vo
 *
 * @author v-lizy81
 * @version 1.0.0
 * @date 2023/12/1
 * @since V1.3.0
 */
public class DataSkyWiFiProbeSubmitVo implements DedicatedMessage, Serializable {
    private static final long serialVersionUID = -1684923728538537232L;

    /**
     * 嗅探器设备标识
     */
    private String      id;

    /**
     * 设备MAC地址
     */
    private String      mmac;

    private Integer     rate;

    /**
     * 采样时间，有可能是延迟上报的
     */
    @JsonFormat(pattern = "EEE MMM dd HH:mm:ss yyyy", locale = "zh_CN")
    private Date        time;

    /**
     * 经度
     */
    private Float       lat;

    /**
     * 维度
     */
    private Float       lon;

    private List<DataSkyWiFiSampleVo>   data;

    @Override
    public void readFields(String topic, Map<String, Object> buf) {
        if (topic.equals(DataSkyThingDefine.THING_ID_OF_REPORT_PROPERTIES)) {
            buf.put("mac", mmac);
            buf.put("rate", rate);
            buf.put("latitude", lat);
            buf.put("longitude", lon);
            if (time != null) buf.put("latestUpdateTime", time.getTime());

            return ;
        }

        if (time != null) buf.put("sampleTime", time.getTime());
    }

    public static void main(String[] args) throws JsonProcessingException {
        String jsonStr = "{\"id\":\"00f40f9a\",\"data\":[{\"mac\":\"10:9f:4f:a1:7e:d6\",\"rssi\":\"-72\",\"router\":\"北通\",\"range\":\"18.1\"},{\"mac\":\"30:fb:b8:3d:e5:e8\",\"rssi\":\"-45\",\"router\":\"711-WIFI\",\"range\":\"1.8\"},{\"mac\":\"ac:60:89:74:5f:78\",\"rssi\":\"-74\",\"router\":\"ChinaNet-rAgK\",\"range\":\"21.5\"},{\"mac\":\"f4:2a:7d:e0:ac:70\",\"rssi\":\"-80\",\"router\":\"ZP\",\"range\":\"35.9\"},{\"mac\":\"26:cf:24:06:f4:27\",\"rssi\":\"-82\",\"tmc\":\"06:f9:f8:28:3f:25\",\"range\":\"42.6\"},{\"mac\":\"f8:6f:b0:71:27:27\",\"rssi\":\"-70\",\"router\":\"jfs\",\"range\":\"15.3\"},{\"mac\":\"14:6b:9c:f4:0f:9a\",\"rssi\":\"-63\",\"router\":\"DataSky_f40f9a\",\"range\":\"8.4\"},{\"mac\":\"d8:ae:90:21:36:08\",\"rssi\":\"-89\",\"range\":\"77.4\"},{\"mac\":\"28:d1:27:bf:fc:c1\",\"rssi\":\"-80\",\"router\":\"cs_qidong\",\"range\":\"35.9\"},{\"mac\":\"3c:37:86:50:df:b1\",\"rssi\":\"-68\",\"router\":\"videotest\",\"range\":\"12.9\"},{\"mac\":\"20:28:3e:22:2b:81\",\"rssi\":\"-95\",\"router\":\"agora.guest\",\"range\":\"129.1\"},{\"mac\":\"50:98:b8:8b:fd:85\",\"rssi\":\"-76\",\"range\":\"25.5\"},{\"mac\":\"f4:2a:7d:f1:39:3d\",\"rssi\":\"-74\",\"router\":\"ZP\",\"range\":\"21.5\"},{\"mac\":\"04:f1:69:11:7b:a4\",\"rssi\":\"-65\",\"router\":\"HWZM_Qidong\",\"range\":\"10.0\"},{\"mac\":\"74:05:a5:d3:30:76\",\"rssi\":\"-82\",\"router\":\"ios_Qidong\",\"range\":\"42.6\"},{\"mac\":\"50:98:b8:8b:fd:a3\",\"rssi\":\"-57\",\"range\":\"5.0\"},{\"mac\":\"20:28:3e:22:2b:80\",\"rssi\":\"-93\",\"router\":\"agora.io-18F\",\"range\":\"108.9\"},{\"mac\":\"92:76:9f:c8:05:7f\",\"rssi\":\"-38\",\"router\":\"MERCURY_711\",\"range\":\"1.0\"},{\"mac\":\"50:98:b8:8b:fd:a1\",\"rssi\":\"-56\",\"router\":\"Beitong\",\"range\":\"4.6\"},{\"mac\":\"26:41:8c:01:66:5c\",\"rssi\":\"-77\",\"router\":\"yao\",\"range\":\"27.8\"},{\"mac\":\"50:98:b8:8b:fd:83\",\"rssi\":\"-75\",\"router\":\"Beitong\",\"range\":\"23.4\"},{\"mac\":\"f0:fe:6b:8f:da:66\",\"rssi\":\"-64\",\"rssi1\":\"-62\",\"essid0\":\"SMARTISAN_SERVER\",\"range\":\"9.1\"},{\"mac\":\"fc:67:1f:06:a8:cd\",\"rssi\":\"-91\",\"tmc\":\"0a:a1:4a:c3:f5:19\",\"range\":\"91.8\"},{\"mac\":\"50:98:b8:8b:fd:76\",\"rssi\":\"-90\",\"range\":\"84.3\"},{\"mac\":\"b0:95:8e:9c:23:82\",\"rssi\":\"-69\",\"router\":\"JS_QIDONG\",\"range\":\"14.0\"},{\"mac\":\"f4:2a:7d:f1:4e:59\",\"rssi\":\"-73\",\"tmc\":\"12:25:51:f8:b9:48\",\"router\":\"ZP\",\"range\":\"19.7\"},{\"mac\":\"d6:42:ae:f8:76:1e\",\"rssi\":\"-73\",\"ts\":\"agora.io-18F\",\"tmc\":\"20:28:3e:22:2a:80\",\"tc\":\"Y\",\"ds\":\"Y\",\"range\":\"19.7\"},{\"mac\":\"20:28:3e:22:2a:80\",\"rssi\":\"-78\",\"router\":\"agora.io-18F\",\"range\":\"30.3\"},{\"mac\":\"86:73:6a:87:2c:de\",\"rssi\":\"-86\",\"ts\":\"ZP\",\"tmc\":\"f4:2a:7d:f1:4e:59\",\"tc\":\"N\",\"range\":\"59.9\"},{\"mac\":\"20:28:3e:22:2a:81\",\"rssi\":\"-78\",\"router\":\"agora.guest\",\"range\":\"30.3\"},{\"mac\":\"10:9f:4f:69:75:bf\",\"rssi\":\"-72\",\"rssi1\":\"-72\",\"router\":\"北通\",\"range\":\"18.1\"},{\"mac\":\"12:25:51:f8:b9:48\",\"rssi\":\"-88\",\"ts\":\"ZP\",\"tmc\":\"f4:2a:7d:f1:4e:59\",\"tc\":\"N\",\"range\":\"71.0\"},{\"mac\":\"f0:92:b4:44:c2:d9\",\"rssi\":\"-92\",\"router\":\"ChinaNet-9T9g\",\"range\":\"100.0\"},{\"mac\":\"50:98:b8:8b:fd:74\",\"rssi\":\"-89\",\"tmc\":\"76:7b:ec:5c:7b:2d\",\"router\":\"Beitong\",\"range\":\"77.4\"},{\"mac\":\"54:75:95:f2:db:08\",\"rssi\":\"-80\",\"tmc\":\"fc:67:1f:06:a8:cd\",\"router\":\"JFS会议室\",\"range\":\"35.9\"},{\"mac\":\"d2:c9:f2:0e:bc:a0\",\"rssi\":\"-79\",\"ts\":\"JS_QIDONG\",\"tmc\":\"b0:95:8e:9c:23:82\",\"tc\":\"Y\",\"range\":\"33.0\"},{\"mac\":\"3c:6a:48:02:93:ac\",\"rssi\":\"-89\",\"router\":\"KL510\",\"range\":\"77.4\"},{\"mac\":\"9c:a6:15:1d:4c:ad\",\"rssi\":\"-84\",\"router\":\"SC_qidong\",\"range\":\"50.5\"},{\"mac\":\"7c:03:c9:53:40:3d\",\"rssi\":\"-85\",\"router\":\"ChinaNet-aXfi\",\"range\":\"55.0\"}],\"mmac\":\"14:6b:9c:f4:0f:9a\",\"rate\":\"2\",\"time\":\"Wed Jan 15 02:26:50 2020\",\"lat\":\"\",\"lon\":\"\"}";

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        DataSkyWiFiProbeSubmitVo vo = mapper.readValue(jsonStr, DataSkyWiFiProbeSubmitVo.class);
        System.out.println(vo);
    }

    public List<DataSkyWiFiSampleVo> getData() {
        return data;
    }

    public void setData(List<DataSkyWiFiSampleVo> data) {
        this.data = data;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMmac() {
        return mmac;
    }

    public void setMmac(String mmac) {
        this.mmac = mmac;
    }

    public Integer getRate() {
        return rate;
    }

    public void setRate(Integer rate) {
        this.rate = rate;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public Float getLat() {
        return lat;
    }

    public void setLat(Float lat) {
        this.lat = lat;
    }

    public Float getLon() {
        return lon;
    }

    public void setLon(Float log) {
        this.lon = log;
    }

    @Override
    public String toString() {
        return "DataSkyWiFiProbeSubmitVo{" +
                "id='" + id + '\'' +
                ", mmac='" + mmac + '\'' +
                ", rate=" + rate +
                ", time=" + time +
                ", lat=" + lat +
                ", lon=" + lon +
                ", data=" + data +
                '}';
    }
}
