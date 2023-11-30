package org.jetlinks.protocol.official;

import org.jetlinks.core.defaults.CompositeProtocolSupport;
import org.jetlinks.core.message.codec.DefaultTransport;
import org.jetlinks.core.route.HttpRoute;
import org.jetlinks.core.route.WebsocketRoute;
import org.jetlinks.core.spi.ProtocolSupportProvider;
import org.jetlinks.core.spi.ServiceContext;
import org.jetlinks.protocol.xuebao.XueBaoWaWaProtocolSupport;
import org.jetlinks.protocol.official.core.TopicMessageCodec;
import org.jetlinks.protocol.official.http.JetLinksHttpDeviceMessageCodec;
import org.jetlinks.protocol.official.lwm2m.JetLinksLwM2MDeviceMessageCodec;
import org.jetlinks.protocol.official.mqtt.JetLinksMqttDeviceMessageCodec;
import org.jetlinks.protocol.official.tcp.TcpDeviceMessageCodec;
import org.jetlinks.protocol.official.udp.UDPDeviceMessageCodec;
import org.jetlinks.supports.official.JetLinksDeviceMetadataCodec;
import org.jetlinks.supports.protocol.serial.*;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class JetLinksProtocolSupportProvider implements ProtocolSupportProvider {

    private PluginConfig    pluginConfig;

    @Override
    public Mono<CompositeProtocolSupport> create(ServiceContext context) {
        ensurePluginConfigLoaded();

        return Mono.defer(() -> {
            CompositeProtocolSupport support = new CompositeProtocolSupport();

            support.setId("jetlinks.v3.0");
            support.setName("JetLinks V3.0");
            support.setDescription("JetLinks Protocol Version 3.0");

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
            String tcpCodec = pluginConfig.getTcpCodec();
            support.addConfigMetadata(DefaultTransport.TCP, TcpDeviceMessageCodec.tcpConfig);
            if (XueBaoWaWaProtocolSupport.NAME.equals(tcpCodec)) {
                support.addMessageCodecSupport(XueBaoWaWaProtocolSupport.buildDeviceMessageCodec(pluginConfig));
            } else {
                support.addMessageCodecSupport(new TcpDeviceMessageCodec());
            }
            support.setDocument(DefaultTransport.TCP,
                                "document-tcp.md",
                                JetLinksProtocolSupportProvider.class.getClassLoader());

            //UDP
            support.addConfigMetadata(DefaultTransport.UDP, UDPDeviceMessageCodec.udpConfig);
            support.addMessageCodecSupport(new UDPDeviceMessageCodec());

            //MQTT
            support.addMessageCodecSupport(new JetLinksMqttDeviceMessageCodec());

            //HTTP
            support.addConfigMetadata(DefaultTransport.HTTP, JetLinksHttpDeviceMessageCodec.httpConfig);
            support.addMessageCodecSupport(new JetLinksHttpDeviceMessageCodec());

            //Websocket
            JetLinksHttpDeviceMessageCodec codec = new JetLinksHttpDeviceMessageCodec(DefaultTransport.WebSocket);
            support.addMessageCodecSupport(codec);
            support.addAuthenticator(DefaultTransport.WebSocket, codec);

            support.addRoutes(
                    DefaultTransport.WebSocket,
                    Collections.singleton(
                            WebsocketRoute
                                    .builder()
                                    .path("/{productId:产品ID}/{productId:设备ID}/socket")
                                    .description("通过Websocket接入平台")
                                    .build()
                    ));

            //LwM2M
            JetLinksLwM2MDeviceMessageCodec l2M2MDeviceMessageCodec = createLwM2MDeviceMessageCodec();
            support.addConfigMetadata(DefaultTransport.LwM2M, JetLinksLwM2MDeviceMessageCodec.CONFIG);
            support.addMessageCodecSupport(l2M2MDeviceMessageCodec);
            support.addAuthenticator(DefaultTransport.LwM2M, l2M2MDeviceMessageCodec);

            return Mono.just(support);
        });
    }

    public JetLinksLwM2MDeviceMessageCodec createLwM2MDeviceMessageCodec() {
        return new JetLinksLwM2MDeviceMessageCodec(
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
