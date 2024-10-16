package org.jetlinks.protocol.common;


import me.tenyks.core.utils.ShortCodeGenerator;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.springframework.util.StringUtils;

/**
 *
 * @author v-lizy81
 * @date 2024/10/8 22:27
 */
public class SelfEmbedMessageIdReverseMappingShort implements MessageIdReverseMapping<Short> {

    private final String              tpl;

    private final ShortCodeGenerator shortCodeGen;

    public SelfEmbedMessageIdReverseMappingShort(String prefix) {
        this.shortCodeGen = new ShortCodeGenerator();
        this.tpl = StringUtils.isEmpty(prefix) ? "SEMID_%s_%04x" : prefix + "_%s_%04x";
    }

    @Override
    public String mark(Short protocolMsgId) {
        return String.format(tpl, shortCodeGen.next(), protocolMsgId);
    }

    @Override
    public Short take(String thingMsgId) {
        if (thingMsgId == null || thingMsgId.length() < 5) return null;

        char[] buf = new char[4];
        thingMsgId.getChars(thingMsgId.length() - 4, thingMsgId.length(), buf, 0);

        try {
            byte[] bytes = Hex.decodeHex(buf);
            if (bytes.length >= 2) {
                return (short)((0xff00 & ((short)bytes[0] << 8)) | (0x00ff & bytes[1]));
            }
        } catch (DecoderException e) {
            throw new IllegalArgumentException(String.format("从(%s)提取协议消息ID失败", thingMsgId), e);
        }

        return null;
    }


}
