package org.jetlinks.protocol.official.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.hswebframework.web.bean.FastBeanCopier;
import org.jetlinks.core.message.*;
import org.jetlinks.core.message.event.EventMessage;
import org.jetlinks.core.message.function.FunctionInvokeMessage;
import org.jetlinks.core.message.function.FunctionInvokeMessageReply;
import org.jetlinks.core.message.property.ReportPropertyMessage;
import org.jetlinks.core.route.LwM2MRoute;
import org.jetlinks.core.utils.TopicUtils;
import org.jetlinks.protocol.official.TopicPayload;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.function.Function;

/**
 * URI指示类型的消息编解码
 */
public enum UriIndicatedMessageCodec {
    //上报属性数据
    reportProperty("/19/0/0", 0,
                   ReportPropertyMessage.class,
                   route -> route
                           .upstream(true)
                           .downstream(false)
                           .group("属性上报")
                           .description("上报物模型属性数据")
                           .example("{\"properties\":{\"属性ID\":\"属性值\"}}")),

    //事件上报
    event("/19/0/0", 0,
          EventMessage.class,
          route -> route
                  .upstream(true)
                  .downstream(false)
                  .group("事件上报")
                  .description("上报物模型事件数据")
                  .example("{\"data\":{\"key\":\"value\"}}")) {
        @Override
        protected void transMqttTopic(String[] topic) {
            topic[topic.length - 1] = "{eventId:事件ID}";
        }

        @Override
        Publisher<DeviceMessage> doDecode(ObjectMapper mapper, String[] topic, byte[] payload) {
            String event = topic[topic.length - 1];

            return Mono.from(super.doDecode(mapper, topic, payload))
                       .cast(EventMessage.class)
                       .doOnNext(e -> e.setEvent(event))
                       .cast(DeviceMessage.class);
        }
    },

    //调用功能
    functionInvoke("/19/1/0", 0,
            FunctionInvokeMessage.class,
            route -> route
                    .upstream(false)
                    .downstream(true)
                    .group("调用功能")
                    .description("平台下发功能调用指令")
                    .example("{\"messageId\":\"消息ID,回复时需要一致.\"," +
                            "\"functionId\":\"功能标识\"," +
                            "\"inputs\":[{\"name\":\"参数名\",\"value\":\"参数值\"}]}")),
    //调用功能回复
    functionInvokeReply("/19/1/0", 0,
            FunctionInvokeMessageReply.class,
            route -> route
                    .upstream(true)
                    .downstream(false)
                    .group("调用功能")
                    .description("设备响应平台下发的功能调用指令")
                    .example("{\"messageId\":\"消息ID,与下发指令中的messageId一致.\"," +
                            "\"output\":\"输出结果,格式与物模型中定义的类型一致\"")),
    //注册
    register("/rd", 0, DeviceRegisterMessage.class),

    //注销
    unregister("/rd", 0, DeviceUnRegisterMessage.class),

    //上线
    online("/rd", 0, DeviceOnlineMessage.class,
            builder -> builder
            .upstream(true)
            .group("状态管理")
            .description("设备上线")),

    //离线
    offline("/rd", 0, DeviceOfflineMessage.class,
            builder -> builder
            .upstream(true)
            .group("状态管理")
            .description("设备离线")),
    ;

    UriIndicatedMessageCodec(String uri, int code,
                             Class<? extends DeviceMessage> type,
                             Function<LwM2MRoute.Builder, LwM2MRoute.Builder> routeCustom) {
        this.pattern = uri.split("/");
        this.code = code;
        this.type = type;
        this.route = routeCustom.apply(toRoute()).build();
    }

    UriIndicatedMessageCodec(String topic, int code,
                             Class<? extends DeviceMessage> type) {
        this.pattern = topic.split("/");
        this.code = code;
        this.type = type;
        this.route = null;
    }

    private final String[]      pattern;
    private final int           code;
    private final LwM2MRoute    route;
    private final Class<? extends DeviceMessage> type;

    protected void transMqttTopic(String[] topic) {

    }

    @SneakyThrows
    private LwM2MRoute.Builder toRoute() {
        String[] topics = new String[pattern.length];
        System.arraycopy(pattern, 0, topics, 0, pattern.length);
        topics[0] = "{productId:产品ID}";
        topics[1] = "{deviceId:设备ID}";
        transMqttTopic(topics);
        StringJoiner joiner = new StringJoiner("/", "/", "");
        for (String topic : topics) {
            joiner.add(topic);
        }
        return LwM2MRoute
                .builder(joiner.toString());
    }

    public LwM2MRoute getRoute() {
        return route;
    }

    public static Flux<DeviceMessage> decode(ObjectMapper mapper, String[] topics, byte[] payload) {
        return Mono
                .justOrEmpty(fromTopic(topics))
                .flatMapMany(topicMessageCodec -> topicMessageCodec.doDecode(mapper, topics, payload));
    }

    public static Flux<DeviceMessage> decode(ObjectMapper mapper, String topic, byte[] payload) {
        return decode(mapper, topic.split("/"), payload);
    }

    public static TopicPayload encode(ObjectMapper mapper, DeviceMessage message) {
        return fromMessage(message)
                .orElseThrow(() -> new UnsupportedOperationException("unsupported message:" + message.getMessageType()))
                .doEncode(mapper, message);
    }

    /**
     * 根据Topic名称查找对应的Codec
     * @param topic     topic名称
     * @return  与指定Topic名称配套的Codec
     */
    static Optional<UriIndicatedMessageCodec> fromTopic(String[] topic) {
        for (UriIndicatedMessageCodec value : values()) {
            if (TopicUtils.match(value.pattern, topic)) {
                return Optional.of(value);
            }
        }
        return Optional.empty();
    }

    /**
     * 根据DeviceMessage类型查找对应的Codec
     * @param message     （物模型）消息
     * @return  与指定物模型消息配套的Codec
     */
    static Optional<UriIndicatedMessageCodec> fromMessage(DeviceMessage message) {
        for (UriIndicatedMessageCodec value : values()) {
            if (value.type == message.getClass()) {
                return Optional.of(value);
            }
        }
        return Optional.empty();
    }

    Publisher<DeviceMessage> doDecode(ObjectMapper mapper, String[] topic, byte[] payload) {
        return Mono
                .fromCallable(() -> {
                    DeviceMessage message = mapper.readValue(payload, type);
                    FastBeanCopier.copy(Collections.singletonMap("deviceId", topic[1]), message);

                    return message;
                });
    }

    @SneakyThrows
    TopicPayload doEncode(ObjectMapper mapper, String[] topics, DeviceMessage message) {
        return TopicPayload.of(String.join("/", topics), mapper.writeValueAsBytes(message));
    }

    @SneakyThrows
    TopicPayload doEncode(ObjectMapper mapper, DeviceMessage message) {
        String[] topics = Arrays.copyOf(pattern, pattern.length);
        return doEncode(mapper, topics, message);
    }

}
