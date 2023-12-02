package org.jetlinks.protocol.dataSky;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jetlinks.core.message.DeviceMessage;
import org.jetlinks.protocol.official.ObjectMappers;
import org.junit.Test;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import reactor.core.publisher.BaseSubscriber;

public class DataSkyDedicatedMessageDecoderTest {

    private DataSkyDedicatedMessageDecoder decoder = new DataSkyDedicatedMessageDecoder();

    private ObjectMapper objectMapper = ObjectMappers.JSON_MAPPER;

    public DataSkyDedicatedMessageDecoderTest() {

    }

    @Test
    public void decode() {
        byte[] buf = "{\"id\":\"00f40f9a\",\"data\":[{\"mac\":\"50:98:b8:8b:fd:76\",\"rssi\":\"-89\",\"range\":\"77.4\"},{\"mac\":\"50:98:b8:8b:fd:85\",\"rssi\":\"-79\",\"range\":\"33.0\"},{\"mac\":\"50:98:b8:8b:fd:a3\",\"rssi\":\"-58\",\"range\":\"5.5\"},{\"mac\":\"92:76:9f:c8:05:7f\",\"rssi\":\"-46\",\"router\":\"MERCURY_711\",\"range\":\"1.9\"},{\"mac\":\"50:98:b8:8b:fd:a1\",\"rssi\":\"-58\",\"router\":\"Beitong\",\"range\":\"5.5\"},{\"mac\":\"14:6b:9c:f4:0f:9a\",\"rssi\":\"-14\",\"router\":\"DataSky_f40f9a\",\"range\":\"1.0\"},{\"mac\":\"10:9f:4f:69:75:bf\",\"rssi\":\"-76\",\"rssi1\":\"-75\",\"router\":\"北通\",\"range\":\"25.5\"},{\"mac\":\"b0:95:8e:9c:23:82\",\"rssi\":\"-69\",\"router\":\"JS_QIDONG\",\"range\":\"14.0\"},{\"mac\":\"f4:2a:7d:f1:4e:59\",\"rssi\":\"-70\",\"router\":\"ZP\",\"range\":\"15.3\"},{\"mac\":\"12:25:51:f8:b9:48\",\"rssi\":\"-88\",\"rssi1\":\"-88\",\"rssi2\":\"-87\",\"rssi3\":\"-86\",\"ts\":\"ZP\",\"tmc\":\"f4:2a:7d:f1:4e:59\",\"tc\":\"N\",\"range\":\"71.0\"},{\"mac\":\"86:73:6a:87:2c:de\",\"rssi\":\"-79\",\"rssi1\":\"-77\",\"ts\":\"ZP\",\"tmc\":\"f4:2a:7d:f1:4e:59\",\"tc\":\"Y\",\"ds\":\"Y\",\"range\":\"33.0\"},{\"mac\":\"20:28:3e:22:2a:81\",\"rssi\":\"-78\",\"router\":\"agora.guest\",\"range\":\"30.3\"},{\"mac\":\"20:28:3e:22:2a:80\",\"rssi\":\"-78\",\"rssi1\":\"-83\",\"rssi2\":\"-81\",\"tmc\":\"d6:42:ae:f8:76:1e\",\"router\":\"agora.io-18F\",\"range\":\"30.3\"},{\"mac\":\"d6:42:ae:f8:76:1e\",\"rssi\":\"-71\",\"rssi1\":\"-71\",\"rssi2\":\"-73\",\"ts\":\"agora.io-18F\",\"tmc\":\"20:28:3e:22:2a:80\",\"tc\":\"Y\",\"ds\":\"Y\",\"range\":\"16.6\"},{\"mac\":\"54:75:95:f2:db:08\",\"rssi\":\"-76\",\"router\":\"JFS会议室\",\"range\":\"25.5\"},{\"mac\":\"10:9f:4f:69:87:8f\",\"rssi\":\"-88\",\"router\":\"北通\",\"range\":\"71.0\"},{\"mac\":\"9c:a6:15:1d:4c:ad\",\"rssi\":\"-82\",\"router\":\"SC_qidong\",\"range\":\"42.6\"},{\"mac\":\"fc:67:1f:06:a8:cd\",\"rssi\":\"-83\",\"ts\":\"JS_QIDONG\",\"tmc\":\"b0:95:8e:9c:23:82\",\"tc\":\"N\",\"range\":\"46.4\"},{\"mac\":\"7c:03:c9:53:40:3d\",\"rssi\":\"-87\",\"router\":\"ChinaNet-aXfi\",\"range\":\"65.2\"},{\"mac\":\"3c:6a:48:02:93:ac\",\"rssi\":\"-89\",\"range\":\"77.4\"},{\"mac\":\"28:6c:07:71:eb:5f\",\"rssi\":\"-85\",\"rssi1\":\"-88\",\"essid0\":\"GZ300ZJ\",\"range\":\"55.0\"},{\"mac\":\"ac:60:89:74:5f:78\",\"rssi\":\"-75\",\"router\":\"ChinaNet-rAgK\",\"range\":\"23.4\"},{\"mac\":\"10:9f:4f:a1:7e:d6\",\"rssi\":\"-70\",\"rssi1\":\"-70\",\"router\":\"北通\",\"range\":\"15.3\"},{\"mac\":\"30:fb:b8:3d:e5:e8\",\"rssi\":\"-46\",\"router\":\"711-WIFI\",\"range\":\"1.9\"},{\"mac\":\"28:d1:27:bf:fc:c1\",\"rssi\":\"-83\",\"router\":\"cs_qidong\",\"range\":\"46.4\"},{\"mac\":\"d8:ae:90:21:36:08\",\"rssi\":\"-87\",\"range\":\"65.2\"},{\"mac\":\"08:1f:71:21:fe:a4\",\"rssi\":\"-88\",\"tmc\":\"34:04:9e:51:62:01\",\"router\":\"HJXDCG\",\"range\":\"71.0\"},{\"mac\":\"3c:37:86:50:df:b1\",\"rssi\":\"-69\",\"router\":\"videotest\",\"range\":\"14.0\"},{\"mac\":\"f4:2a:7d:e0:ac:70\",\"rssi\":\"-80\",\"router\":\"ZP\",\"range\":\"35.9\"},{\"mac\":\"24:cf:24:16:f4:27\",\"rssi\":\"-86\",\"rssi1\":\"-86\",\"tmc\":\"fc:67:1f:06:a8:cd\",\"router\":\"JFS\",\"range\":\"59.9\"},{\"mac\":\"04:f1:69:11:7b:a6\",\"rssi\":\"-65\",\"rssi1\":\"-65\",\"tmc\":\"04:f1:69:11:4d:c9\",\"range\":\"10.0\"},{\"mac\":\"f4:2a:7d:f1:39:3d\",\"rssi\":\"-72\",\"router\":\"ZP\",\"range\":\"18.1\"},{\"mac\":\"04:f1:69:11:7b:a4\",\"rssi\":\"-64\",\"router\":\"HWZM_Qidong\",\"range\":\"9.1\"},{\"mac\":\"74:05:a5:d3:30:76\",\"rssi\":\"-82\",\"router\":\"ios_Qidong\",\"range\":\"42.6\"},{\"mac\":\"20:28:3e:22:2b:80\",\"rssi\":\"-94\",\"router\":\"agora.io-18F\",\"range\":\"118.5\"},{\"mac\":\"50:98:b8:8b:fd:83\",\"rssi\":\"-78\",\"router\":\"Beitong\",\"range\":\"30.3\"},{\"mac\":\"20:28:3e:22:2b:81\",\"rssi\":\"-92\",\"router\":\"agora.guest\",\"range\":\"100.0\"},{\"mac\":\"50:98:b8:8b:fd:74\",\"rssi\":\"-90\",\"router\":\"Beitong\",\"range\":\"84.3\"},{\"mac\":\"26:41:8c:01:66:5c\",\"rssi\":\"-76\",\"router\":\"yao\",\"range\":\"25.5\"}],\"mmac\":\"14:6b:9c:f4:0f:9a\",\"rate\":\"2\",\"time\":\"Wed Jan 15 02:26:28 2020\",\"lat\":\"\",\"lon\":\"\"}".getBytes();
        String[] topics = new String[]{"dataSky", "DEV_ID_ABC", "report"};

        Publisher<DeviceMessage> msgFlux = decoder.decode(ObjectMappers.JSON_MAPPER, topics, buf);
        msgFlux.subscribe(new BaseSubscriber<DeviceMessage>() {
            @Override
            protected void hookOnNext(DeviceMessage value) {
                super.hookOnNext(value);

                System.out.println(value.toJson().toJSONString());
            }
        });

    }
}