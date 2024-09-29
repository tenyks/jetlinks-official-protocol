package me.tenyks.qiyun.protocol;

import org.jetlinks.core.device.DeviceOperator;
import org.jetlinks.core.message.request.DeviceRequestMessage;
import org.jetlinks.core.message.request.DeviceRequestMessageReply;
import org.jetlinks.protocol.common.DeviceRequestHandler;
import org.jetlinks.protocol.common.SimpleDeviceRequestHandler;

import javax.annotation.Nonnull;

/**
 * @author v-lizy81
 * @version 1.0.0
 * @date 2024/9/29
 * @since V3.1.0
 */
public class YKCV1APIBuilder {

    public DeviceRequestHandler build() {
        SimpleDeviceRequestHandler handler = new SimpleDeviceRequestHandler();

        handler.addCallable("CheckFeeTermsRequest", this::ofCheckFeeTermsRequest);
        handler.addCallable("BillingTermsRequest", this::ofBillingTermsRequest);
        handler.addCallable("PileSwitchOnChargingRequest", this::ofPileSwitchOnChargingRequest);

        return handler;
    }

    public DeviceRequestMessageReply    ofCheckFeeTermsRequest(@Nonnull DeviceOperator device,
                                                               @Nonnull DeviceRequestMessage<?> reqMsg) {
        return null;
    }

    public DeviceRequestMessageReply    ofBillingTermsRequest(@Nonnull DeviceOperator device,
                                                               @Nonnull DeviceRequestMessage<?> reqMsg) {
        return null;
    }

    public DeviceRequestMessageReply    ofPileSwitchOnChargingRequest(@Nonnull DeviceOperator device,
                                                                      @Nonnull DeviceRequestMessage<?> reqMsg) {
        return null;
    }



}
