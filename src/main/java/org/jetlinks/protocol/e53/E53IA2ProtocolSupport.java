package org.jetlinks.protocol.e53;

import org.jetlinks.protocol.common.mapping.ThingAnnotation;
import org.jetlinks.protocol.official.binary2.BaseDataType;
import org.jetlinks.protocol.official.binary2.DefaultFieldDeclaration;
import org.jetlinks.protocol.official.binary2.DefaultStructDeclaration;

/**
 * E53版IA2协议
 *
 * 消息报文字节结构:
 * <li>MagicId：UINT8，0xFA11上线消息, 0xFA37下行消息</li>
 * <li>消息ID：UINT16，Big-End</li>
 * <li>消息编码：UINT8，参考物模型定义</li>
 * <li>输入/输出参数负载总字节数：UINT16</li>
 * <li>参数1：参考物模型定义</li>
 * <li>参数N：参考物模型定义</li>
 *
 * @author v-lizy81
 * @date 2024/3/20 21:42
 */
public class E53IA2ProtocolSupport {

    public static final String NAME_OF_IA2 = "E53_IA2_V1.0.0";



    /**
     * 心跳上报结构【事件】：机器 -> 服务器
     */
    private static DefaultStructDeclaration buildReportPongStructDcl() {
        DefaultStructDeclaration structDcl = new DefaultStructDeclaration("数据上报结构【消息】", "CMD:0x10");

        structDcl.enableDecode();
        structDcl.addThingAnnotation(ThingAnnotation.Event("ReportData"));

        structDcl.addField(buildMessageIdFieldDcl());
        structDcl.addField(buildPackageLengthFieldDcl((byte) 21));
        structDcl.addField(buildCmdFieldDcl((byte)0x35));

        structDcl.addField(new DefaultFieldDeclaration("MAC码", "machineMAC", BaseDataType.STRING, (short) 8,  (short) 12)
                .addMeta(ThingAnnotation.EventData())
                .addMeta(ThingAnnotation.DeviceId()));

//        structDcl.addField(buildCRCFieldDcl((short) 21));

        return structDcl;
    }

    private static DefaultFieldDeclaration buildMessageIdFieldDcl() {
        return new DefaultFieldDeclaration("消息ID", "messageId", BaseDataType.INT16, (short) 1)
                .addMeta(ThingAnnotation.MsgId());
    }

    private static DefaultFieldDeclaration buildMessageTypeIdFieldDcl() {
        return new DefaultFieldDeclaration("消息类型ID", "messageTypeId", BaseDataType.INT8, (short) 1)
                .addMeta(ThingAnnotation.MsgId());
    }

    private static DefaultFieldDeclaration buildPackageLengthFieldDcl(byte defaultValue) {
        return new DefaultFieldDeclaration("包长度", "packageLength", BaseDataType.UINT8, (short) 6)
                .setDefaultValue(defaultValue);
    }

    private static DefaultFieldDeclaration buildCmdFieldDcl(Byte defaultVal) {
        return new DefaultFieldDeclaration("CMD字段", "functionId", BaseDataType.INT8, (short) 7).setDefaultValue(defaultVal);
    }
}
