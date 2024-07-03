package org.jetlinks.protocol.official;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * 编解码插件设定
 *
 * @author v-lizy81
 * @date 2023/7/2 21:31
 */
public class PluginConfig {

    public static final String DEFAULT_RESOURCE = "plugin.properties";

    public static final String TCP_CODEC_KEY = "tcp.codec";
    public static final String UDP_CODEC_KEY = "udp.codec";
    public static final String HTTP_CODEC_KEY = "http.codec";
    public static final String LWM2M_CODEC_KEY = "lwm2m.codec";
    public static final String MQTT_CODEC_KEY = "mqtt.codec";
    public static final String MQTT_MANUFACTURER_KEY = "mqtt.manufacturer";


    private static final Logger log = LoggerFactory.getLogger(PluginConfig.class);

    private final Properties properties;

    public PluginConfig(Properties properties) {
        this.properties = properties;
    }

    public String getConfig(String key) {
        return properties.getProperty(key);
    }

    public String getConfig(String key, String defaultVal) {
        return properties.getProperty(key, defaultVal);
    }

    public static PluginConfig loadDefault() {
        Properties p = new Properties();

        InputStream inputStream = null;
        try {
            inputStream = PluginConfig.class.getClassLoader().getResourceAsStream(DEFAULT_RESOURCE);
            if (inputStream != null) {
                p.load(inputStream);
            }
        } catch (IOException e) {
            log.error("[PluginSettings]加载插件配置文件({})失败：", DEFAULT_RESOURCE, e);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    log.error("[PluginSettings]关闭文件流失败：", e);
                }
            }
        }

        return new PluginConfig(p);
    }

    public String getTcpCodec() {
        return getConfig(TCP_CODEC_KEY);
    }

    public String getHttpCodec() {
        return getConfig(HTTP_CODEC_KEY);
    }

    public String getUdpCodecKey() {
        return getConfig(UDP_CODEC_KEY);
    }

    public String getLwM2MCodec() {
        return getConfig(LWM2M_CODEC_KEY);
    }

    public String getMQTTCodec() {
        return getConfig(MQTT_CODEC_KEY);
    }

    public String getMQTTManufacturer() {
        return getConfig(MQTT_MANUFACTURER_KEY);
    }

}
