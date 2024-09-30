package org.jetlinks.protocol.official;

import me.tenyks.qiyun.protocol.YKCV1ProtocolSupport;
import org.jetlinks.core.defaults.CompositeProtocolSupport;
import org.jetlinks.core.message.codec.DefaultTransport;
import org.jetlinks.core.route.HttpRoute;
import org.jetlinks.core.route.WebsocketRoute;
import org.jetlinks.core.spi.ProtocolSupportProvider;
import org.jetlinks.core.spi.ServiceContext;
import me.tenyks.dataSky.DataSkyDedicatedMessageCodec;
import me.tenyks.dataSky.DataSkyProtocolSupport;
import org.jetlinks.protocol.e53.E53IAxProtocolSupport;
import me.tenyks.michong.MiChongV2ProtocolSupport;
import org.jetlinks.protocol.official.http.QiYunHttpDeviceMessageCodec;
import org.jetlinks.protocol.official.lwm2m.QiYunLwM2MAuthenticator;
import org.jetlinks.protocol.qiyun.mqtt.QiYunMqttStaticCodeAuthenticator;
import org.jetlinks.protocol.qiyun.mqtt.QiYunOverMqttDeviceMessageCodec;
import org.jetlinks.protocol.qiyun.tcp.QiYunTcpStaticCodeAuthenticator;
import me.tenyks.xuebao.XueBaoWaWaProtocolSupport;
import org.jetlinks.protocol.official.core.TopicMessageCodec;
import org.jetlinks.protocol.official.http.JetLinksHttpDeviceMessageCodec;
import org.jetlinks.protocol.official.lwm2m.SimpleLwM2M11DeviceMessageCodec;
import org.jetlinks.protocol.official.mqtt.JetLinksMqttDeviceMessageCodec;
import org.jetlinks.protocol.official.tcp.TcpDeviceMessageCodec;
import org.jetlinks.protocol.official.udp.UDPDeviceMessageCodec;
import org.jetlinks.supports.official.JetLinksDeviceMetadataCodec;
import org.jetlinks.supports.protocol.serial.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class JetLinksProtocolSupportProvider implements ProtocolSupportProvider {

    private static final Logger log = LoggerFactory.getLogger(JetLinksProtocolSupportProvider.class);

    private PluginConfig    pluginConfig;

    @Override
    public Mono<CompositeProtocolSupport> create(ServiceContext context) {
        log.info("开始加载JetLinksProtocolSupportProvider");

        ensurePluginConfigLoaded();

        return Mono.defer(() -> {
            CompositeProtocolSupport support = new CompositeProtocolSupport();

            support.setId("jetlinks.v3.0");
            support.setName("JetLinks V3.0");
            support.setDescription("JetLinks Protocol Version 3.0");

            //MQTT
            if (MiChongV2ProtocolSupport.NAME_AND_VER.equals(pluginConfig.getMQTTCodec())) {
                QiYunOverMqttDeviceMessageCodec codec;
                codec = MiChongV2ProtocolSupport.buildDeviceMessageCodec(pluginConfig);

                support.addRoutes(DefaultTransport.MQTT, codec.collectRoutes());
                support.setDocument(DefaultTransport.MQTT,
                        "document-mqtt-MiChong.md",
                        JetLinksProtocolSupportProvider.class.getClassLoader());

                support.addAuthenticator(DefaultTransport.MQTT, new QiYunMqttStaticCodeAuthenticator());
                support.addConfigMetadata(DefaultTransport.MQTT, QiYunMqttStaticCodeAuthenticator.CONFIG);
                support.addMessageCodecSupport(codec);
            } else {
                support.addRoutes(DefaultTransport.MQTT, Arrays
                        .stream(TopicMessageCodec.values())
                        .map(TopicMessageCodec::getRoute)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList())
                );
                support.setDocument(DefaultTransport.MQTT,
                        "document-mqtt.md",
                        JetLinksProtocolSupportProvider.class.getClassLoader());

                support.addAuthenticator(DefaultTransport.MQTT, new JetLinksAuthenticator());
                support.addConfigMetadata(DefaultTransport.MQTT, JetLinksMqttDeviceMessageCodec.mqttConfig);
                support.addMessageCodecSupport(new JetLinksMqttDeviceMessageCodec());
            }

            //HTTP
            support.addRoutes(DefaultTransport.HTTP, Stream
                    .of(TopicMessageCodec.reportProperty,
                        TopicMessageCodec.event,
                        TopicMessageCodec.online,
                        TopicMessageCodec.offline)
                    .map(TopicMessageCodec::getRoute)
                    .filter(route -> route != null && route.isUpstream())
                    .map(route -> HttpRoute
                            .builder()
                            .address(route.getTopic())
                            .group(route.getGroup())
                            .contentType(MediaType.APPLICATION_JSON)
                            .method(HttpMethod.POST)
                            .description(route.getDescription())
                            .example(route.getExample())
                            .build())
                    .collect(Collectors.toList())
            );

            support.setDocument(DefaultTransport.HTTP,
                                "document-http.md",
                                JetLinksProtocolSupportProvider.class.getClassLoader());

            support.setMetadataCodec(new JetLinksDeviceMetadataCodec());

            //TCP
            support.addConfigMetadata(DefaultTransport.TCP, TcpDeviceMessageCodec.tcpConfig);
            if (XueBaoWaWaProtocolSupport.NAME_AND_VER.equals(pluginConfig.getTcpCodec())) {
                support.addMessageCodecSupport(XueBaoWaWaProtocolSupport.buildDeviceMessageCodec(pluginConfig));
            } else if (YKCV1ProtocolSupport.NAME_AND_VER.equals(pluginConfig.getTcpCodec())) {
                support.addMessageCodecSupport(YKCV1ProtocolSupport.buildDeviceMessageCodec(pluginConfig));
                support.addAuthenticator(DefaultTransport.TCP, new QiYunTcpStaticCodeAuthenticator());
                support.addConfigMetadata(DefaultTransport.TCP, QiYunTcpStaticCodeAuthenticator.CONFIG);
            } else {
                support.addMessageCodecSupport(new TcpDeviceMessageCodec());
            }
            support.setDocument(DefaultTransport.TCP,
                                "document-tcp.md",
                                JetLinksProtocolSupportProvider.class.getClassLoader());

            //UDP
            support.addConfigMetadata(DefaultTransport.UDP, UDPDeviceMessageCodec.udpConfig);
            support.addMessageCodecSupport(new UDPDeviceMessageCodec());

            //HTTP
            String httpCodec = pluginConfig.getHttpCodec();
            if (DataSkyProtocolSupport.NAME.equals(httpCodec)) {
                support.addMessageCodecSupport(new QiYunHttpDeviceMessageCodec(new DataSkyDedicatedMessageCodec()));
                support.addConfigMetadata(DefaultTransport.HTTP, QiYunHttpDeviceMessageCodec.httpConfig);
            } else {
                support.addMessageCodecSupport(new JetLinksHttpDeviceMessageCodec());
                support.addConfigMetadata(DefaultTransport.HTTP, JetLinksHttpDeviceMessageCodec.httpConfig);
            }


            //Websocket
            JetLinksHttpDeviceMessageCodec codec = new JetLinksHttpDeviceMessageCodec(DefaultTransport.WebSocket);
            support.addMessageCodecSupport(codec);
            support.addAuthenticator(DefaultTransport.WebSocket, codec);

            support.addRoutes(
                    DefaultTransport.WebSocket,
                    Collections.singleton(
                            WebsocketRoute.builder()
                                    .path("/{productId:产品ID}/{productId:设备ID}/socket")
                                    .description("通过Websocket接入平台")
                                    .build()
                    ));

            //LwM2M
            support.addConfigMetadata(DefaultTransport.LwM2M, QiYunLwM2MAuthenticator.CONFIG);
            support.addAuthenticator(DefaultTransport.LwM2M, new QiYunLwM2MAuthenticator());

            SimpleLwM2M11DeviceMessageCodec l2M2MDeviceMessageCodec = createLwM2MDeviceMessageCodec();
            if (E53IAxProtocolSupport.NAME_OF_IA2.equals(pluginConfig.getLwM2MCodec())) {
                support.addMessageCodecSupport(E53IAxProtocolSupport.buildDeviceMessageCodec(pluginConfig));
            } else {
                support.addMessageCodecSupport(l2M2MDeviceMessageCodec);
            }

            return Mono.just(support);
        });
    }

    public SimpleLwM2M11DeviceMessageCodec createLwM2MDeviceMessageCodec() {
        return new SimpleLwM2M11DeviceMessageCodec(
                createJsonParserSuit(),
                createJsonWriterSuit()
        );
    }

    private PayloadParserSuit createJsonParserSuit() {
        return new DefaultPayloadParserSuit().add("*", new JsonPayloadParser(ObjectMappers.JSON_MAPPER));
    }

    private PayloadWriterSuit   createJsonWriterSuit() {
        return new DefaultPayloadWriterSuit().add("*", new JsonPayloadWriter(ObjectMappers.JSON_MAPPER));
    }

    private synchronized void ensurePluginConfigLoaded() {
        if (this.pluginConfig != null) return ;

        this.pluginConfig = PluginConfig.loadDefault();
    }

}
