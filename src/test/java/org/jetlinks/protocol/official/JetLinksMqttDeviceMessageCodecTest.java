package org.jetlinks.protocol.official;

import io.netty.buffer.Unpooled;
import org.jetlinks.core.defaults.CompositeProtocolSupports;
import org.jetlinks.core.device.DeviceInfo;
import org.jetlinks.core.device.DeviceOperator;
import org.jetlinks.core.device.ProductInfo;
import org.jetlinks.core.device.StandaloneDeviceMessageBroker;
import org.jetlinks.core.message.ChildDeviceMessage;
import org.jetlinks.core.message.DerivedMetadataMessage;
import org.jetlinks.core.message.Message;
import org.jetlinks.core.message.codec.*;
import org.jetlinks.core.message.event.EventMessage;
import org.jetlinks.core.message.firmware.UpgradeFirmwareMessage;
import org.jetlinks.core.message.function.FunctionInvokeMessage;
import org.jetlinks.core.message.function.FunctionInvokeMessageReply;
import org.jetlinks.core.message.property.*;
import org.jetlinks.core.server.session.DeviceSession;
import org.jetlinks.protocol.official.mqtt.JetLinksMqttDeviceMessageCodec;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import reactor.core.publisher.Mono;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;

public class JetLinksMqttDeviceMessageCodecTest {

    JetLinksMqttDeviceMessageCodec codec = new JetLinksMqttDeviceMessageCodec();


    TestDeviceRegistry registry;

    private EncodedMessage currentReply;

    @Before
    public void init() {
        registry = new TestDeviceRegistry(new CompositeProtocolSupports(), new StandaloneDeviceMessageBroker());

        registry.register(ProductInfo.builder()
                                     .id("product1")
                                     .protocol("jetlinks")
                                     .build())
                .flatMap(product -> registry.register(DeviceInfo.builder()
                                                                .id("device1")
                                                                .productId("product1")
                                                                .build()))
                .block();

    }

    @Test
    public void testReadProperty() {
        ReadPropertyMessage message = new ReadPropertyMessage();
        message.setDeviceId("device1");
        message.setMessageId("test");
        message.setProperties(Arrays.asList("name", "sn"));
        MqttMessage encodedMessage = codec.encode(createMessageContext(message)).block();


        Assert.assertNotNull(encodedMessage);
        Assert.assertEquals(encodedMessage.getTopic(), "/product1/device1/properties/read");
        System.out.println(encodedMessage.getPayload().toString(StandardCharsets.UTF_8));
    }

    @Test
    public void testReadChildProperty() {
        ReadPropertyMessage message = new ReadPropertyMessage();
        message.setDeviceId("test");
        message.setMessageId("test");
        message.setProperties(Arrays.asList("name", "sn"));
        ChildDeviceMessage childDeviceMessage = new ChildDeviceMessage();
        childDeviceMessage.setChildDeviceMessage(message);
        childDeviceMessage.setChildDeviceId("test");
        childDeviceMessage.setDeviceId("device1");

        MqttMessage encodedMessage = codec.encode(createMessageContext(childDeviceMessage)).block();


        Assert.assertNotNull(encodedMessage);
        Assert.assertEquals(encodedMessage.getTopic(), "/product1/device1/child/test/properties/read");
        System.out.println(encodedMessage.getPayload().toString(StandardCharsets.UTF_8));
    }

    @Test
    public void testReadPropertyReply() {
        Message message = codec.decode(createMessageContext(SimpleMqttMessage.builder()
                                                                             .topic("/product1/device1/properties/read/reply")
                                                                             .payload(Unpooled.wrappedBuffer("{\"messageId\":\"test\",\"properties\":{\"sn\":\"test\"}}"
                                                                                                                     .getBytes()))
                                                                             .build())).blockFirst();

        Assert.assertTrue(message instanceof ReadPropertyMessageReply);
        ReadPropertyMessageReply reply = ((ReadPropertyMessageReply) message);
        Assert.assertTrue(reply.isSuccess());
        Assert.assertEquals(reply.getDeviceId(), "device1");
        Assert.assertEquals(reply.getMessageId(), "test");
        Assert.assertEquals(reply.getProperties().get("sn"), "test");
        System.out.println(reply);
    }

    @Test
    public void testChildReadPropertyReply() {
        Message message = codec.decode(createMessageContext(SimpleMqttMessage.builder()
                                                                             .topic("/product1/device1/child/test/properties/read/reply")
                                                                             .payload(Unpooled.wrappedBuffer("{\"messageId\":\"test\",\"properties\":{\"sn\":\"test\"}}"
                                                                                                                     .getBytes()))
                                                                             .build())).blockFirst();

        Assert.assertTrue(message instanceof ChildDeviceMessage);
        ChildDeviceMessage childReply = ((ChildDeviceMessage) message);

        Assert.assertEquals(childReply.getDeviceId(), "device1");
        Assert.assertEquals(childReply.getMessageId(), "test");

        ReadPropertyMessageReply reply = (ReadPropertyMessageReply) childReply.getChildDeviceMessage();
        ;
        Assert.assertTrue(reply.isSuccess());
        Assert.assertEquals(reply.getDeviceId(), "test");
        Assert.assertEquals(reply.getMessageId(), "test");
        Assert.assertEquals(reply.getProperties().get("sn"), "test");
        System.out.println(reply);
    }

    @Test
    public void testWriteProperty() {
        WritePropertyMessage message = new WritePropertyMessage();
        message.setDeviceId("device1");
        message.setMessageId("test");
        message.setProperties(Collections.singletonMap("sn", "123"));
        MqttMessage encodedMessage = codec.encode(createMessageContext(message)).block();


        Assert.assertNotNull(encodedMessage);
        Assert.assertEquals(encodedMessage.getTopic(), "/product1/device1/properties/write");
        System.out.println(encodedMessage.getPayload().toString(StandardCharsets.UTF_8));
    }

    @Test
    public void testChildWriteProperty() {
        WritePropertyMessage message = new WritePropertyMessage();
        message.setDeviceId("device1");
        message.setMessageId("test");
        message.setProperties(Collections.singletonMap("sn", "123"));

        ChildDeviceMessage childDeviceMessage = new ChildDeviceMessage();
        childDeviceMessage.setChildDeviceMessage(message);
        childDeviceMessage.setChildDeviceId("test");
        childDeviceMessage.setDeviceId("device1");


        MqttMessage encodedMessage = codec.encode(createMessageContext(childDeviceMessage)).block();


        Assert.assertNotNull(encodedMessage);
        Assert.assertEquals(encodedMessage.getTopic(), "/product1/device1/child/device1/properties/write");
        System.out.println(encodedMessage.getPayload().toString(StandardCharsets.UTF_8));
    }

    @Test
    public void testWritePropertyReply() {
        Message message = codec.decode(createMessageContext(SimpleMqttMessage.builder()
                                                                             .topic("/product1/device1/properties/write/reply")
                                                                             .payload(Unpooled.wrappedBuffer("{\"messageId\":\"test\",\"properties\":{\"sn\":\"test\"}}"
                                                                                                                     .getBytes()))
                                                                             .build())).blockFirst();

        Assert.assertTrue(message instanceof WritePropertyMessageReply);
        WritePropertyMessageReply reply = ((WritePropertyMessageReply) message);
        Assert.assertTrue(reply.isSuccess());
        Assert.assertEquals(reply.getDeviceId(), "device1");
        Assert.assertEquals(reply.getMessageId(), "test");
        Assert.assertEquals(reply.getProperties().get("sn"), "test");
        System.out.println(reply);
    }


    @Test
    public void testWriteChildPropertyReply() {
        Message message = codec.decode(createMessageContext(SimpleMqttMessage.builder()
                                                                             .topic("/product1/device1/child/test/properties/write/reply")
                                                                             .payload(Unpooled.wrappedBuffer("{\"messageId\":\"test\",\"properties\":{\"sn\":\"test\"}}"
                                                                                                                     .getBytes()))
                                                                             .build())).blockFirst();

        Assert.assertTrue(message instanceof ChildDeviceMessage);
        ChildDeviceMessage childReply = ((ChildDeviceMessage) message);

        Assert.assertEquals(childReply.getDeviceId(), "device1");
        Assert.assertEquals(childReply.getMessageId(), "test");

        WritePropertyMessageReply reply = (WritePropertyMessageReply) childReply.getChildDeviceMessage();
        ;
        Assert.assertTrue(reply.isSuccess());
        Assert.assertEquals(reply.getDeviceId(), "test");
        Assert.assertEquals(reply.getMessageId(), "test");
        Assert.assertEquals(reply.getProperties().get("sn"), "test");
        System.out.println(reply);
    }


    @Test
    public void testInvokeFunction() {
        FunctionInvokeMessage message = new FunctionInvokeMessage();
        message.setDeviceId("device1");
        message.setMessageId("test");
        message.setFunctionId("playVoice");
        message.addInput("file", "http://baidu.com/1.mp3");

        MqttMessage encodedMessage = codec.encode(createMessageContext(message)).block();


        Assert.assertNotNull(encodedMessage);
        Assert.assertEquals(encodedMessage.getTopic(), "/product1/device1/function/invoke");
        System.out.println(encodedMessage.getPayload().toString(StandardCharsets.UTF_8));
    }

    @Test
    public void testFirmwareUpgrade() {
        UpgradeFirmwareMessage message = new UpgradeFirmwareMessage();
        message.setDeviceId("device1");
        message.setMessageId("test");
        message.setVersion("1.0");
        message.setUrl("http://baidu.com/1.mp3");

        MqttMessage encodedMessage = codec.encode(createMessageContext(message)).block();


        Assert.assertNotNull(encodedMessage);
        Assert.assertEquals(encodedMessage.getTopic(), "/product1/device1/firmware/upgrade");
        System.out.println(encodedMessage.getPayload().toString(StandardCharsets.UTF_8));
    }

    @Test
    public void testInvokeFunctionReply() {
        Message message = codec.decode(createMessageContext(SimpleMqttMessage.builder()
                                                                             .topic("/product1/device1/function/invoke/reply")
                                                                             .payload(Unpooled.wrappedBuffer("{\"messageId\":\"test\",\"output\":\"ok\"}"
                                                                                                                     .getBytes()))
                                                                             .build())).blockFirst();

        Assert.assertTrue(message instanceof FunctionInvokeMessageReply);
        FunctionInvokeMessageReply reply = ((FunctionInvokeMessageReply) message);
        Assert.assertTrue(reply.isSuccess());
        Assert.assertEquals(reply.getDeviceId(), "device1");
        Assert.assertEquals(reply.getMessageId(), "test");
        Assert.assertEquals(reply.getOutput(), "ok");
        System.out.println(reply);
    }

    @Test
    public void testInvokeChildFunctionReply() {
        Message message = codec.decode(createMessageContext(SimpleMqttMessage.builder()
                                                                             .topic("/product1/device1/child/test/function/invoke/reply")
                                                                             .payload(Unpooled.wrappedBuffer("{\"messageId\":\"test\",\"output\":\"ok\"}"
                                                                                                                     .getBytes()))
                                                                             .build())).blockFirst();

        Assert.assertTrue(message instanceof ChildDeviceMessage);
        ChildDeviceMessage childReply = ((ChildDeviceMessage) message);

        Assert.assertEquals(childReply.getDeviceId(), "device1");
        Assert.assertEquals(childReply.getMessageId(), "test");

        FunctionInvokeMessageReply reply = (FunctionInvokeMessageReply) childReply.getChildDeviceMessage();
        ;
        Assert.assertTrue(reply.isSuccess());
        Assert.assertEquals(reply.getDeviceId(), "test");
        Assert.assertEquals(reply.getMessageId(), "test");
        Assert.assertEquals(reply.getOutput(), "ok");
        System.out.println(reply);
    }

    @Test
    public void testEvent() {
        Message message = codec.decode(createMessageContext(SimpleMqttMessage.builder()
                                                                             .topic("/product1/device1/event/temp")
                                                                             .payload(Unpooled.wrappedBuffer("{\"messageId\":\"test\",\"data\":100}"
                                                                                                                     .getBytes()))
                                                                             .build())).blockFirst();

        Assert.assertTrue(message instanceof EventMessage);
        EventMessage reply = ((EventMessage) message);
        Assert.assertEquals(reply.getDeviceId(), "device1");
        Assert.assertEquals(reply.getMessageId(), "test");
        Assert.assertEquals(reply.getData(), 100);
        System.out.println(reply);
    }

    @Test
    public void testChildEvent() {
        Message message = codec.decode(createMessageContext(SimpleMqttMessage.builder()
                                                                             .topic("/product1/device1/child/test/event/temp")
                                                                             .payload(Unpooled.wrappedBuffer("{\"messageId\":\"test\",\"data\":100}"
                                                                                                                     .getBytes()))
                                                                             .build())).blockFirst();

        Assert.assertTrue(message instanceof ChildDeviceMessage);

        EventMessage reply = ((EventMessage) ((ChildDeviceMessage) message).getChildDeviceMessage());
        Assert.assertEquals(reply.getDeviceId(), "test");
        Assert.assertEquals(reply.getMessageId(), "test");
        Assert.assertEquals(reply.getData(), 100);
        System.out.println(reply);
    }

    @Test
    public void testPropertiesReport() {
        Message message = codec.decode(createMessageContext(
                                       SimpleMqttMessage
                                               .builder()
                                               .topic("/product1/device1/properties/report")
                                               .payload(Unpooled.wrappedBuffer(("{\"messageId\":\"test\"," +
                                                       "\"properties\":{\"sn\":\"test\"}," +
                                                       "\"propertySourceTimes\":{\"sn\":10086}" +
                                                       "}").getBytes()))
                                               .build()))
                               .blockFirst();

        Assert.assertTrue(message instanceof ReportPropertyMessage);
        ReportPropertyMessage reply = ((ReportPropertyMessage) message);
        Assert.assertNotNull(reply.getPropertySourceTimes());
        Assert.assertEquals(reply.getPropertySourceTimes().get("sn"), Long.valueOf(10086L));

        Assert.assertEquals(reply.getDeviceId(), "device1");
        Assert.assertEquals(reply.getMessageId(), "test");
        Assert.assertEquals(reply.getProperties(), Collections.singletonMap("sn", "test"));

        System.out.println(reply);
    }


    @Test
    public void testChildPropertiesReport() {
        Message message = codec.decode(createMessageContext(SimpleMqttMessage.builder()
                                                                             .topic("/product1/device1/child/test/properties/report")
                                                                             .payload(Unpooled.wrappedBuffer("{\"messageId\":\"test\",\"properties\":{\"sn\":\"test\"}}"
                                                                                                                     .getBytes()))
                                                                             .build())).blockFirst();

        Assert.assertTrue(message instanceof ChildDeviceMessage);

        ReportPropertyMessage reply = ((ReportPropertyMessage) ((ChildDeviceMessage) message).getChildDeviceMessage());
        Assert.assertEquals(reply.getDeviceId(), "test");
        Assert.assertEquals(reply.getMessageId(), "test");
        Assert.assertEquals(reply.getProperties(), Collections.singletonMap("sn", "test"));
        System.out.println(reply);
    }


    @Test
    public void testMetadataDerived() {
        Message message = codec.decode(createMessageContext(SimpleMqttMessage.builder()
                                                                             .topic("/product1/device1/metadata/derived")
                                                                             .payload(Unpooled.wrappedBuffer("{\"messageId\":\"test\",\"metadata\":\"1\"}"
                                                                                                                     .getBytes()))
                                                                             .build())).blockFirst();

        Assert.assertTrue(message instanceof DerivedMetadataMessage);
        DerivedMetadataMessage reply = ((DerivedMetadataMessage) message);
        Assert.assertEquals(reply.getDeviceId(), "device1");
        Assert.assertEquals(reply.getMessageId(), "test");
        Assert.assertEquals(reply.getMetadata(), "1");
        System.out.println(reply);
    }

    @Test
    public void testChildMetadataDerived() {
        Message message = codec.decode(createMessageContext(SimpleMqttMessage
                                                                    .builder()
                                                                    .topic("/product1/device1/child/test/metadata/derived")
                                                                    .payload(Unpooled.wrappedBuffer("{\"messageId\":\"test\",\"metadata\":\"1\"}"
                                                                                                            .getBytes()))
                                                                    .build())).blockFirst();

        Assert.assertTrue(message instanceof ChildDeviceMessage);

        DerivedMetadataMessage reply = ((DerivedMetadataMessage) ((ChildDeviceMessage) message).getChildDeviceMessage());
        Assert.assertEquals(reply.getDeviceId(), "test");
        Assert.assertEquals(reply.getMessageId(), "test");
        Assert.assertEquals(reply.getMetadata(), "1");
        System.out.println(reply);
    }

    @Test
    public void testTimeSync() {
        Message message = codec.decode(createMessageContext(SimpleMqttMessage
                                                                    .builder()
                                                                    .topic("/product1/device1/time-sync")
                                                                    .payload(Unpooled.wrappedBuffer("{\"messageId\":\"test\"}"
                                                                                                            .getBytes()))
                                                                    .build()))
                               .blockFirst();

        Assert.assertNull(message);

        Assert.assertNotNull(currentReply);

        Assert.assertEquals(((MqttMessage) currentReply).getTopic(), "/product1/device1/time-sync/reply");
        Assert.assertTrue(currentReply.payloadAsString().contains("timestamp"));

    }

    public void testTopic() {

    }

    public MessageEncodeContext createMessageContext(Message message) {
        System.out.println(message.toString());
        return new MessageEncodeContext() {
            @Nonnull
            @Override
            public Message getMessage() {
                return message;
            }

            @Override
            public DeviceOperator getDevice() {
                return registry.getDevice("device1").block();
            }

            @Override
            public Mono<DeviceOperator> getDevice(String deviceId) {
                return registry.getDevice(deviceId);
            }
        };
    }


    public MessageDecodeContext createMessageContext(EncodedMessage message) {
        System.out.println(message.toString());
        return new FromDeviceMessageContext() {
            @Nonnull
            @Override
            public EncodedMessage getMessage() {
                return message;
            }

            @Override
            public DeviceSession getSession() {
                return new MockSession();
            }

            @Override
            public DeviceOperator getDevice() {
                return registry.getDevice("device1").block();
            }

            @Override
            public Mono<DeviceOperator> getDevice(String deviceId) {
                return registry.getDevice(deviceId);
            }
        };
    }

    class MockSession implements DeviceSession {

        @Override
        public String getId() {
            return "device1";
        }

        @Override
        public String getDeviceId() {
            return "device1";
        }

        @Nullable
        @Override
        public DeviceOperator getOperator() {
            return registry.getDevice("device1").block();
        }

        @Override
        public long lastPingTime() {
            return 0;
        }

        @Override
        public long connectTime() {
            return 0;
        }

        @Override
        public Mono<Boolean> send(EncodedMessage encodedMessage) {
            currentReply = encodedMessage;
            return Mono.just(true);
        }

        @Override
        public Transport getTransport() {
            return null;
        }

        @Override
        public void close() {

        }

        @Override
        public void ping() {

        }

        @Override
        public boolean isAlive() {
            return false;
        }

        @Override
        public void onClose(Runnable call) {

        }
    }

}