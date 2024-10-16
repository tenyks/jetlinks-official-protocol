package me.tenyks.qiyun.protocol;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.jetlinks.core.device.DeviceOperator;
import org.jetlinks.core.message.event.EventMessage;
import org.jetlinks.core.message.event.ThingEventMessage;
import org.jetlinks.core.message.request.DefaultDeviceRequestMessageReply;
import org.jetlinks.core.message.request.DeviceRequestMessage;
import org.jetlinks.core.message.request.DeviceRequestMessageReply;
import org.jetlinks.core.utils.DateUtils;
import org.jetlinks.protocol.common.DeviceRequestHandler;
import org.jetlinks.protocol.common.SimpleDeviceRequestHandler;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import javax.annotation.Nonnull;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author v-lizy81
 * @version 1.0.0
 * @date 2024/9/29
 * @since V3.1.0
 */
public class YKCV1APIBuilder {

    public static final AtomicInteger SeqNo = new AtomicInteger();

    /**
     * 当前计费条款编号：11
     */
    private static final Short CUR_TERMS_NO = (byte) 11;

    private static final JSONObject BILLING_TERMS = JSON.parseObject("{\"termsNo\":0,\"sharpEUP\":200000,\"sharpSUP\":40000,\"peakEUP\":200000,\"peakSUP\":40000,\"shoulderEUP\":400000,\"shoulderSUP\":40000,\"offPeakEUP\":500000,\"offPeakSUP\":40000,\"withLostRate\":0,\"rateNoOf00000030\":3,\"rateNoOf00300100\":3,\"rateNoOf01000130\":3,\"rateNoOf01300200\":3,\"rateNoOf02000230\":3,\"rateNoOf02300300\":3,\"rateNoOf03000330\":3,\"rateNoOf03300400\":3,\"rateNoOf04000430\":3,\"rateNoOf04300500\":3,\"rateNoOf05000530\":3,\"rateNoOf05300600\":3,\"rateNoOf06000630\":3,\"rateNoOf06300700\":3,\"rateNoOf07000730\":3,\"rateNoOf07300800\":3,\"rateNoOf08000830\":0,\"rateNoOf08300900\":0,\"rateNoOf09000930\":0,\"rateNoOf09301000\":0,\"rateNoOf10001030\":0,\"rateNoOf10301100\":0,\"rateNoOf11001130\":0,\"rateNoOf11301200\":0,\"rateNoOf12001230\":2,\"rateNoOf12301300\":2,\"rateNoOf13001330\":2,\"rateNoOf13301400\":2,\"rateNoOf14001430\":0,\"rateNoOf14301500\":0,\"rateNoOf15001530\":0,\"rateNoOf15301600\":0,\"rateNoOf16001630\":0,\"rateNoOf16301700\":0,\"rateNoOf17001730\":0,\"rateNoOf17301800\":0,\"rateNoOf18001830\":1,\"rateNoOf18301900\":1,\"rateNoOf19001930\":1,\"rateNoOf19302000\":1,\"rateNoOf20002030\":1,\"rateNoOf20302100\":1,\"rateNoOf21002130\":1,\"rateNoOf21302200\":1,\"rateNoOf22002230\":2,\"rateNoOf22302300\":2,\"rateNoOf23002330\":2,\"rateNoOf23302400\":2}");

    static {
        BILLING_TERMS.put("termsNo", CUR_TERMS_NO);
    }

    public DeviceRequestHandler build() {
        SimpleDeviceRequestHandler handler = new SimpleDeviceRequestHandler();

        handler.addCallableSilent("CheckFeeTermsRequest", this::ofCheckFeeTermsRequest);
        handler.addCallableSilent("BillingTermsRequest", this::ofBillingTermsRequest);
        handler.addCallable("PileSwitchOnChargingRequest", this::ofPileSwitchOnChargingRequest);

        return handler;
    }

    public DeviceRequestMessageReply    ofCheckFeeTermsRequest(@Nonnull DeviceOperator device,
                                                               @Nonnull DeviceRequestMessage<?> reqMsg) {
        Number termsNo = reqMsg.getInputNum("termsNo");

        DefaultDeviceRequestMessageReply reply = new DefaultDeviceRequestMessageReply().from(reqMsg);
        reply.functionId("CheckFeeTermsRequestReply");
        JSONObject output = new JSONObject();
        if (termsNo != null && termsNo.shortValue() == CUR_TERMS_NO) {
            // 匹配
            output.put("termsNo", CUR_TERMS_NO);
            output.put("rstFlag", (byte) 0x00);
        } else {
            // 不匹配
            output.put("termsNo", CUR_TERMS_NO);
            output.put("rstFlag", (byte) 0x01);
        }
        reply.setOutputs(output);

        return reply;
    }

    public DeviceRequestMessageReply    ofBillingTermsRequest(@Nonnull DeviceOperator device,
                                                               @Nonnull DeviceRequestMessage<?> reqMsg) {
        DefaultDeviceRequestMessageReply reply = new DefaultDeviceRequestMessageReply().from(reqMsg);
        reply.functionId("BillingTermsRequestReply");
        JSONObject output = new JSONObject(BILLING_TERMS);
        reply.setOutputs(output);

        return reply;
    }

    private String  buildTransNo(String pileNo, Number gunNo) {
        int seqNo = SeqNo.incrementAndGet() % 10000;
        return String.format("%s%02d%s%04d", pileNo, gunNo.byteValue(), DateUtils.toYYMMDD12(new Date()), seqNo);
    }

    public Tuple2<DeviceRequestMessageReply, ThingEventMessage>
    ofPileSwitchOnChargingRequest(@Nonnull DeviceOperator device, @Nonnull DeviceRequestMessage<?> reqMsg) {

        String pileNo = reqMsg.getInputStr("pileNo");
        Number gunNo = reqMsg.getInputNum("gunNo");

        DefaultDeviceRequestMessageReply reply = new DefaultDeviceRequestMessageReply().from(reqMsg);
        reply.functionId("PileSwitchOnChargingRequestReply");
        JSONObject output = new JSONObject();
        String transNo = buildTransNo(pileNo, gunNo);
        output.put("transNo", transNo);
        output.put("pileNo", pileNo);
        output.put("cardDisplayNo", "8610010000000001");
        output.put("accountAmount", (int)10000);
        output.put("rstFlag", (byte)0x00);
        output.put("reasonCode", "0000");

        reply.setOutputs(output);

        EventMessage eventMsg = new EventMessage();
        eventMsg.event("PileSwitchOnChargingEvent");
        eventMsg.timestamp(System.currentTimeMillis());
        eventMsg.setDeviceId(device.getDeviceId());
        eventMsg.setMessageId("BG_" + transNo);

        JSONObject eventData = new JSONObject(reqMsg.getInputs());
        eventMsg.setData(eventData);

        eventData.put("transNo", transNo);
        eventData.put("rstCode", "PASS");
        eventData.put("rstDesc", "通过");

        return Tuples.of(reply, eventMsg);
    }

}
