package org.jetlinks.protocol.official.lwm2m;

import io.netty.buffer.Unpooled;
import org.jetlinks.core.device.DeviceOperator;
import org.jetlinks.core.message.DeviceMessage;
import org.jetlinks.core.message.codec.EncodedMessage;
import org.jetlinks.core.message.codec.MessageDecodeContext;
import org.jetlinks.core.message.codec.lwm2m.LwM2MResource;
import org.jetlinks.core.message.codec.lwm2m.LwM2MUplinkMessage;
import org.jetlinks.core.message.codec.lwm2m.SimpleLwM2MUplinkMessage;
import org.jetlinks.protocol.official.JetLinksProtocolSupportProvider;
import org.junit.Assert;
import org.junit.Test;
import reactor.core.publisher.Flux;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class JetLinksLwM2MDeviceMessageCodecTest {

    private JetLinksLwM2MDeviceMessageCodec codec = new JetLinksProtocolSupportProvider().createL2M2MDeviceMessageCodec();

    @Test
    public void decodeReportPropertiesMessage() {
        SimpleLwM2MUplinkMessage message =  new SimpleLwM2MUplinkMessage();
        message.setMessageId(1001);
        message.setResource(LwM2MResource.BinaryAppDataContainerReport);
        message.setRegistrationId("2001");
        message.setPayload(Unpooled.wrappedBuffer("{\"messageType\":\"ReportPropertyMessage\",\"properties\":{\"属性ID\":\"属性值\"}}".getBytes(StandardCharsets.UTF_8)));

        MessageDecodeContext context = new MessageDecodeContext() {
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
        };

        Flux<DeviceMessage> flux = codec.decode(context);
        List<DeviceMessage> rst = new ArrayList<>();
        flux.subscribe(rst::add);
        Assert.assertNotNull(rst);
        assertEquals(1, rst.size());
    }

    @Test
    public void encode() {
    }
}