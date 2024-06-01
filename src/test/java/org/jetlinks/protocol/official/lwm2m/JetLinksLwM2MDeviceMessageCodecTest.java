package org.jetlinks.protocol.official.lwm2m;

import io.netty.buffer.Unpooled;
import org.jetlinks.core.device.DeviceOperator;
import org.jetlinks.core.message.DeviceMessage;
import org.jetlinks.core.message.Message;
import org.jetlinks.core.message.codec.EncodedMessage;
import org.jetlinks.core.message.codec.MessageDecodeContext;
import org.jetlinks.core.message.codec.MessageEncodeContext;
import org.jetlinks.core.message.codec.lwm2m.LwM2MDownlinkMessage;
import org.jetlinks.core.message.codec.lwm2m.LwM2MResource;
import org.jetlinks.core.message.codec.lwm2m.SimpleLwM2MUplinkMessage;
import org.jetlinks.core.message.function.FunctionInvokeMessage;
import org.jetlinks.core.message.function.FunctionParameter;
import org.jetlinks.protocol.official.JetLinksProtocolSupportProvider;
import org.junit.Assert;
import org.junit.Test;
import reactor.core.publisher.Flux;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

public class JetLinksLwM2MDeviceMessageCodecTest {

    private SimpleLwM2M11DeviceMessageCodec codec = new JetLinksProtocolSupportProvider().createLwM2MDeviceMessageCodec();

    @Test
    public void decodeReportPropertiesMessage() {
        SimpleLwM2MUplinkMessage message =  new SimpleLwM2MUplinkMessage();
        message.setMessageId(1001);
        message.setPath(LwM2MResource.BinaryAppDataContainerReport.getPath());
        message.setRegistrationId("2001");
        message.setPayload(Unpooled.wrappedBuffer("{\"messageType\":\"ReportPropertyMessage\",\"properties\":{\"属性ID\":\"属性值\"}}".getBytes(StandardCharsets.UTF_8)));

        MessageDecodeContext context = new _MessageDecodeContext(message);

        Flux<DeviceMessage> flux = codec.decode(context);
        List<DeviceMessage> rst = new ArrayList<>();
        flux.subscribe(rst::add);
        Assert.assertNotNull(rst);
        assertEquals(1, rst.size());
    }

    @Test
    public void decodeReportEventMessage() {
        SimpleLwM2MUplinkMessage message =  new SimpleLwM2MUplinkMessage();
        message.setMessageId(1001);
        message.setPath(LwM2MResource.BinaryAppDataContainerReport.getPath());
        message.setRegistrationId("2001");
        message.setPayload(Unpooled.wrappedBuffer("{\"messageType\":\"EventMessage\",\"data\":{\"key\":\"value\"}}".getBytes(StandardCharsets.UTF_8)));

        MessageDecodeContext context = new _MessageDecodeContext(message);

        Flux<DeviceMessage> flux = codec.decode(context);
        List<DeviceMessage> rst = new ArrayList<>();
        flux.subscribe(rst::add);
        Assert.assertNotNull(rst);
        assertEquals(1, rst.size());
    }

    @Test
    public void encodeFunctionInvokeMessage() {
        FunctionInvokeMessage message = new FunctionInvokeMessage();
        message.setFunctionId("功能标识");
        message.setMessageId("1002");
        message.setDeviceId("Device-001");
        message.setInputs(new ArrayList<>());
        message.getInputs().add(new FunctionParameter("参数名1", "参数值1"));
        message.getInputs().add(new FunctionParameter("参数名2", new Date()));

        _MessageEncodeContext context = new _MessageEncodeContext(message);

        Flux<LwM2MDownlinkMessage> flux = codec.encode(context);
        List<LwM2MDownlinkMessage> rst = new ArrayList<>();
        flux.subscribe(rst::add);
        Assert.assertNotNull(rst);
        assertEquals(1, rst.size());
    }

    public static class _MessageDecodeContext implements MessageDecodeContext {

        private EncodedMessage  message;

        public _MessageDecodeContext(EncodedMessage message) {
            this.message = message;
        }

        @Nonnull
        @Override
        public EncodedMessage getMessage() {
            return message;
        }

        @Nullable
        @Override
        public DeviceOperator getDevice() {
            return null;
        }
    }

    public static class _MessageEncodeContext implements MessageEncodeContext {

        private DeviceMessage message;

        public _MessageEncodeContext(DeviceMessage message) {
            this.message = message;
        }

        @Nonnull
        @Override
        public Message getMessage() {
            return message;
        }

        @Nullable
        @Override
        public DeviceOperator getDevice() {
            return null;
        }
    }

}