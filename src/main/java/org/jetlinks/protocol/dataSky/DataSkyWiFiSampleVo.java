package org.jetlinks.protocol.dataSky;

import org.jetlinks.protocol.common.DedicatedMessage;

import java.io.Serializable;
import java.util.Map;

/**
 * DataSky DS006 的WiFi探测样本
 * @author v-lizy81
 * @version 1.0.0
 * @date 2023/12/1
 * @since V1.3.0
 */
public class DataSkyWiFiSampleVo implements DedicatedMessage, Serializable {
    private static final long serialVersionUID = 1936715602250464518L;

    /**
     * WIFI客户端/手机的MAC地址
     */
    private String      mac;

    /**
     * WIFI客户端/手机的信号强度，如rssi=-75,则信号强度为-75dbm
     */
    private Integer     rssi;

    /**
     * 同一个周期内采集的同一个WIFI客户端/手机的信号强度，提供5个rssi，供滤波算法使用
     */
    private Integer     rssi1;

    private Integer     rssi2;

    private Integer     rssi3;

    private Integer     rssi4;

    private Integer     rssi5;

    /**
     * 手机距离嗅探器的测距距离字段，单位米
     */
    private Float       range;

    /**
     * 目标ssid，手机连接的WIFI的ssid
     */
    private String      ts;

    /**
     * 目标设备的mac地址，手机连接的WIFI的mac地址
     */
    private String      tmc;

    /**
     * 是否与路由器相连, Y/N
     * 服务器收到此三个字段为空，则表示手机没有连接路由器WIFI
     */
    private String      tc;

    /**
     * 手机是否睡眠, Y/N
     * 服务器收到此字段为空，则表示手机没有处于睡眠状态
     */
    private String      ds;

    private String      router;

    /**
     * 曾经连接过的WIFI的SSID
     */
    private String      essid0;
    private String      essid1;
    private String      essid2;
    private String      essid3;
    private String      essid4;
    private String      essid5;
    private String      essid6;

    private static boolean isOfYes(String val) {
        return (val != null && (val.equalsIgnoreCase("y")));
    }

    @Override
    public void readFields(String topic, Map<String, Object> buf) {
        if (topic.equals(DataSkyThingDefine.THING_ID_OF_REPORT_AP_SAMPLE)) {
            buf.put("mac", mac);
            buf.put("range", range);
            buf.put("rssi", rssi);
            buf.put("ssid", router);

            return ;
        }

        if (topic.equals(DataSkyThingDefine.THING_ID_OF_REPORT_CLIENT_SAMPLE)) {
            buf.put("mac", mac);
            buf.put("range", range);

            if (ts != null && ts.length() > 0) {
                buf.put("apSsid", ts);
            } else {
                buf.put("apSsid", router);
            }
            buf.put("apMac", tmc);
            buf.put("apConnected", isOfYes(tc));
            buf.put("inSleepMode", isOfYes(ds));

            buf.put("rssi0", rssi);
            buf.put("rssi1", rssi1);
            buf.put("rssi2", rssi2);
            buf.put("rssi3", rssi3);
            buf.put("rssi4", rssi4);
            buf.put("rssi5", rssi5);

            buf.put("essid0", essid0);
            buf.put("essid1", essid1);
            buf.put("essid2", essid2);
            buf.put("essid3", essid3);
            buf.put("essid4", essid4);
            buf.put("essid5", essid5);
            buf.put("essid6", essid6);

            return ;
        }

    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public Integer getRssi() {
        return rssi;
    }

    public void setRssi(Integer rssi) {
        this.rssi = rssi;
    }

    public Integer getRssi1() {
        return rssi1;
    }

    public void setRssi1(Integer rssi1) {
        this.rssi1 = rssi1;
    }

    public Integer getRssi2() {
        return rssi2;
    }

    public void setRssi2(Integer rssi2) {
        this.rssi2 = rssi2;
    }

    public Integer getRssi3() {
        return rssi3;
    }

    public void setRssi3(Integer rssi3) {
        this.rssi3 = rssi3;
    }

    public Integer getRssi4() {
        return rssi4;
    }

    public void setRssi4(Integer rssi4) {
        this.rssi4 = rssi4;
    }

    public Integer getRssi5() {
        return rssi5;
    }

    public void setRssi5(Integer rssi5) {
        this.rssi5 = rssi5;
    }

    public Float getRange() {
        return range;
    }

    public void setRange(Float range) {
        this.range = range;
    }

    public String getTs() {
        return ts;
    }

    public void setTs(String ts) {
        this.ts = ts;
    }

    public String getTmc() {
        return tmc;
    }

    public void setTmc(String tmc) {
        this.tmc = tmc;
    }

    public String getTc() {
        return tc;
    }

    public void setTc(String tc) {
        this.tc = tc;
    }

    public String getDs() {
        return ds;
    }

    public void setDs(String ds) {
        this.ds = ds;
    }

    public String getRouter() {
        return router;
    }

    public void setRouter(String router) {
        this.router = router;
    }

    public String getEssid0() {
        return essid0;
    }

    public void setEssid0(String essid0) {
        this.essid0 = essid0;
    }

    public String getEssid1() {
        return essid1;
    }

    public void setEssid1(String essid1) {
        this.essid1 = essid1;
    }

    public String getEssid2() {
        return essid2;
    }

    public void setEssid2(String essid2) {
        this.essid2 = essid2;
    }

    public String getEssid3() {
        return essid3;
    }

    public void setEssid3(String essid3) {
        this.essid3 = essid3;
    }

    public String getEssid4() {
        return essid4;
    }

    public void setEssid4(String essid4) {
        this.essid4 = essid4;
    }

    public String getEssid5() {
        return essid5;
    }

    public void setEssid5(String essid5) {
        this.essid5 = essid5;
    }

    public String getEssid6() {
        return essid6;
    }

    public void setEssid6(String essid6) {
        this.essid6 = essid6;
    }

    @Override
    public String toString() {
        return "DataSkyWiFiSampleVo{" +
                "mac='" + mac + '\'' +
                ", rssi=" + rssi +
                ", rssi1=" + rssi1 +
                ", rssi2=" + rssi2 +
                ", rssi3=" + rssi3 +
                ", rssi4=" + rssi4 +
                ", rssi5=" + rssi5 +
                ", range=" + range +
                ", ts='" + ts + '\'' +
                ", tmc='" + tmc + '\'' +
                ", tc='" + tc + '\'' +
                ", ds='" + ds + '\'' +
                ", router='" + router + '\'' +
                ", essid0='" + essid0 + '\'' +
                ", essid1='" + essid1 + '\'' +
                ", essid2='" + essid2 + '\'' +
                ", essid3='" + essid3 + '\'' +
                ", essid4='" + essid4 + '\'' +
                ", essid5='" + essid5 + '\'' +
                ", essid6='" + essid6 + '\'' +
                '}';
    }
}
