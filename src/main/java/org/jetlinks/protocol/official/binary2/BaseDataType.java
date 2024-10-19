package org.jetlinks.protocol.official.binary2;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.*;
import io.netty.buffer.ByteBuf;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.jetlinks.core.utils.DateUtils;
import org.jetlinks.protocol.official.common.BCD8421BinaryCodec;

import javax.annotation.Nonnull;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.Date;

/**
 * 基础数据类型
 * @author tenyks
 * @since 3.1
 * @version 1.0
 */
public enum BaseDataType {
    //0x00
    NULL {
        @Override
        public Object read(ByteBuf buf, short size) { return null; }

        @Override
        public short write(ByteBuf buf, Object value) { return 0; }

        @Override
        public JsonNode toJson(@Nonnull Object value) {
            return null;
        }

        @Override
        public Object fromJson(@Nonnull JsonNode buf) {
            return null;
        }

        @Override
        public short size() { return 0; }
    },

    //0x01
    BOOLEAN {
        @Override
        public Object read(ByteBuf buf, short size) {
            return buf.readBoolean();
        }

        @Override
        public short write(ByteBuf buf, Object value) {
            if (value == null) {
                buf.writeByte(0);
            } else {
                if (value instanceof Number) {
                    int v = ((Number) value).intValue();
                    buf.writeBoolean(v != 0);
                } else {
                    buf.writeBoolean((Boolean) value);
                }
            }
            return 1;
        }

        @Override
        public JsonNode toJson(@Nonnull Object value) {
            if (value instanceof Boolean) {
                return BooleanNode.valueOf((boolean) value);
            } else if (value instanceof Number) {
                if (((Number) value).floatValue() != 0) {
                    return BooleanNode.getTrue();
                } else {
                    return BooleanNode.getFalse();
                }
            } else if (value instanceof String) {
                String str = (String) value;
                if ("true".equalsIgnoreCase(str) || "yes".equalsIgnoreCase(str)) {
                    return BooleanNode.getTrue();
                } else {
                    return BooleanNode.getFalse();
                }
            }

            return null;
        }

        @Override
        public Object fromJson(@Nonnull JsonNode buf) {
            return buf.booleanValue();
        }

        @Override
        public short size() { return 1; }
    },

    //0x02
    INT8 {
        @Override
        public Object read(ByteBuf buf, short size) {
            return buf.readByte();
        }

        @Override
        public short write(ByteBuf buf, Object value) {
            if (value == null) {
                buf.writeByte(0);
            } else {
                buf.writeByte(((Number) value).byteValue());
            }
            return 1;
        }

        @Override
        public JsonNode toJson(@Nonnull Object value) {
            if (value instanceof Byte) {
                return IntNode.valueOf((Byte)value);
            } else if (value instanceof Short) {
                return IntNode.valueOf((Short)value);
            } else if (value instanceof Integer) {
                return IntNode.valueOf((Integer) value);
            }

            return IntNode.valueOf(((Number) value).intValue());
        }

        @Override
        public Object fromJson(@Nonnull JsonNode buf) {
            return buf.shortValue();
        }

        @Override
        public short size() { return 1; }
    },

    //0x03
    INT16 {
        @Override
        public Object read(ByteBuf buf, short size) {
            return buf.readShort();
        }

        @Override
        public short write(ByteBuf buf, Object value) {
            if (value == null) {
                buf.writeShort(0);
            } else {
                buf.writeShort(((Number) value).intValue());
            }
            return 2;
        }

        @Override
        public JsonNode toJson(@Nonnull Object value) {
            if (value instanceof Byte) {
                return IntNode.valueOf((Byte)value);
            } else if (value instanceof Short) {
                return IntNode.valueOf((Short)value);
            } else if (value instanceof Integer) {
                return IntNode.valueOf((Integer) value);
            }

            return IntNode.valueOf(((Number) value).intValue());
        }

        @Override
        public Object fromJson(@Nonnull JsonNode buf) {
            return buf.shortValue();
        }

        @Override
        public short size() { return 2; }
    },

    //0x04
    INT32 {
        @Override
        public Object read(ByteBuf buf, short size) {
            return buf.readInt();
        }

        @Override
        public short write(ByteBuf buf, Object value) {
            if (value == null) {
                buf.writeInt(0);
            } else {
                buf.writeInt(((Number) value).intValue());
            }
            return 4;
        }

        @Override
        public JsonNode toJson(@Nonnull Object value) {
            if (value instanceof Byte) {
                return IntNode.valueOf((Byte)value);
            } else if (value instanceof Short) {
                return IntNode.valueOf((Short)value);
            } else if (value instanceof Integer) {
                return IntNode.valueOf((Integer) value);
            }

            return IntNode.valueOf(((Number) value).intValue());
        }

        @Override
        public Object fromJson(@Nonnull JsonNode buf) {
            return buf.intValue();
        }

        @Override
        public short size() { return 4; }
    },

    /**
     * 小端在前
     */
    INT32LE {
        @Override
        public Object read(ByteBuf buf, short size) {
            byte[] tmp = new byte[4];
            buf.readBytes(tmp);
            return ((tmp[3] & 0xff) << 24) | ((tmp[2] & 0xff) << 16) | ((tmp[1] & 0xff) << 8) | (tmp[0] & 0xff);
        }

        @Override
        public short write(ByteBuf buf, Object value) {
            if (value == null) {
                buf.writeInt(0);
            } else {
                int n = ((Number) value).intValue();

                byte[] b = new byte[4];
                b[0] = (byte) (n & 0xff);
                b[1] = (byte) (n >> 8 & 0xff);
                b[2] = (byte) (n >> 16 & 0xff);//高字节在后是与java存放内存相反, 与书写顺序相反
                b[3] = (byte) (n >> 24 & 0xff);//数据组结束位,存放内存起始位, 即:高字节在后

                buf.writeBytes(b);
            }

            return 4;
        }

        @Override
        public JsonNode toJson(@Nonnull Object value) {
            if (value instanceof Byte) {
                return IntNode.valueOf((Byte)value);
            } else if (value instanceof Short) {
                return IntNode.valueOf((Short)value);
            } else if (value instanceof Integer) {
                return IntNode.valueOf((Integer) value);
            }

            return IntNode.valueOf(((Number) value).intValue());
        }

        @Override
        public Object fromJson(@Nonnull JsonNode buf) {
            return buf.shortValue();
        }

        @Override
        public short size() { return 4; }
    },

    //0x05
    INT64 {
        @Override
        public Object read(ByteBuf buf, short size) {
            return buf.readLong();
        }

        @Override
        public short write(ByteBuf buf, Object value) {
            if (value == null) {
                buf.writeLong(0);
            } else {
                buf.writeLong(((Number) value).longValue());
            }
            return 8;
        }

        @Override
        public JsonNode toJson(@Nonnull Object value) {
            if (value instanceof Byte) {
                return LongNode.valueOf((Byte)value);
            } else if (value instanceof Short) {
                return LongNode.valueOf((Short)value);
            } else if (value instanceof Integer) {
                return LongNode.valueOf((Integer) value);
            } else if (value instanceof Long) {
                return LongNode.valueOf((Long) value);
            }

            return LongNode.valueOf(((Number) value).longValue());
        }

        @Override
        public Object fromJson(@Nonnull JsonNode buf) {
            return buf.longValue();
        }

        @Override
        public short size() { return 8; }
    },

    /**
     * 返回类型：short
     */
    UINT8 {
        @Override
        public Object read(ByteBuf buf, short size) {
            return buf.readUnsignedByte();
        }

        @Override
        public short write(ByteBuf buf, Object value) {
            if (value == null) {
                buf.writeByte(0);
            } else {
                buf.writeByte(((Number) value).intValue());
            }
            return 1;
        }

        @Override
        public JsonNode toJson(@Nonnull Object value) {
            if (value instanceof Byte) {
                return IntNode.valueOf((Byte)value);
            } else if (value instanceof Short) {
                return IntNode.valueOf((Short)value);
            } else if (value instanceof Integer) {
                return IntNode.valueOf((Integer) value);
            }

            return IntNode.valueOf(((Number) value).intValue());
        }

        @Override
        public Object fromJson(@Nonnull JsonNode buf) {
            return buf.shortValue();
        }

        @Override
        public short size() { return 1; }
    },

    /**
     * 大端优先
     */
    UINT16 {
        @Override
        public Object read(ByteBuf buf, short size) {
            return buf.readUnsignedShort();
        }

        @Override
        public short write(ByteBuf buf, Object value) {
            if (value == null) {
                buf.writeShort(0);
            } else {
                buf.writeShort(((Number) value).intValue());
            }
            return 2;
        }

        @Override
        public JsonNode toJson(@Nonnull Object value) {
            if (value instanceof Byte) {
                return IntNode.valueOf((Byte)value);
            } else if (value instanceof Short) {
                return IntNode.valueOf((Short)value);
            } else if (value instanceof Integer) {
                return IntNode.valueOf((Integer) value);
            }

            return IntNode.valueOf(((Number) value).intValue());
        }

        @Override
        public Object fromJson(@Nonnull JsonNode buf) {
            return buf.shortValue();
        }

        @Override
        public short size() { return 2; }
    },

    /**
     * 小端优先
     */
    UINT16LE {
        @Override
        public Object read(ByteBuf buf, short size) {
            return buf.readUnsignedShort();
        }

        @Override
        public short write(ByteBuf buf, Object value) {
            if (value == null) {
                buf.writeShort(0);
            } else {
                int val = ((Number) value).intValue();
                byte b1 = (byte) (val & 0xff);
                byte b2 = (byte) (val >> 8 & 0xff);

                buf.writeByte(b1);
                buf.writeByte(b2);
            }
            return 2;
        }

        @Override
        public JsonNode toJson(@Nonnull Object value) {
            if (value instanceof Byte) {
                return IntNode.valueOf((Byte)value);
            } else if (value instanceof Short) {
                return IntNode.valueOf((Short)value);
            } else if (value instanceof Integer) {
                return IntNode.valueOf((Integer) value);
            }

            return IntNode.valueOf(((Number) value).intValue());
        }

        @Override
        public Object fromJson(@Nonnull JsonNode buf) {
            return buf.shortValue();
        }

        @Override
        public short size() { return 2; }
    },

    //0x0E
    UINT24 {
        @Override
        public Object read(ByteBuf buf, short size) {
            byte b1 = buf.readByte();
            byte b2 = buf.readByte();
            byte b3 = buf.readByte();

            return (int) b1 << 16 | (int) b2 << 8 | b3;
        }

        @Override
        public short write(ByteBuf buf, Object value) {
            if (value == null) {
                buf.writeByte(0);
                buf.writeByte(0);
                buf.writeByte(0);
            } else {
                int v = ((Number) value).intValue();
                buf.writeByte((0x00FF0000 & v) >> 16);
                buf.writeByte((0x0000FF00 & v) >> 8);
                buf.writeByte((0x000000FF & v));
            }
            return 3;
        }

        @Override
        public JsonNode toJson(@Nonnull Object value) {
            if (value instanceof Byte) {
                return IntNode.valueOf((Byte)value);
            } else if (value instanceof Short) {
                return IntNode.valueOf((Short)value);
            } else if (value instanceof Integer) {
                return IntNode.valueOf((Integer) value);
            }

            return IntNode.valueOf(((Number) value).intValue());
        }

        @Override
        public Object fromJson(@Nonnull JsonNode buf) {
            return buf.intValue();
        }

        @Override
        public short size() { return 3; }
    },

    /**
     * 小端优先
     */
    UINT24LE {
        @Override
        public Object read(ByteBuf buf, short size) {
            byte b1 = buf.readByte();
            byte b2 = buf.readByte();
            byte b3 = buf.readByte();

            return  (((int) b3 << 16) & 0x000000FF) |
                    (((int) b2 << 8) & 0x000000FF) |
                    b1;
        }

        @Override
        public short write(ByteBuf buf, Object value) {
            if (value == null) {
                buf.writeByte(0);
                buf.writeByte(0);
                buf.writeByte(0);
            } else {
                int v = ((Number) value).intValue();
                buf.writeByte((0x000000FF & v));
                buf.writeByte((0x0000FF00 & v) >> 8);
                buf.writeByte((0x00FF0000 & v) >> 16);
            }
            return 3;
        }

        @Override
        public JsonNode toJson(@Nonnull Object value) {
            if (value instanceof Byte) {
                return IntNode.valueOf((Byte)value);
            } else if (value instanceof Short) {
                return IntNode.valueOf((Short)value);
            } else if (value instanceof Integer) {
                return IntNode.valueOf((Integer) value);
            }

            return IntNode.valueOf(((Number) value).intValue());
        }

        @Override
        public Object fromJson(@Nonnull JsonNode buf) {
            return buf.intValue();
        }

        @Override
        public short size() { return 2; }
    },

    //0x08
    UINT32 {
        @Override
        public Object read(ByteBuf buf, short size) {
            return buf.readUnsignedInt();
        }

        @Override
        public short write(ByteBuf buf, Object value) {
            if (value == null) {
                buf.writeInt(0);
            } else {
                buf.writeInt(((Number) value).intValue());
            }
            return 4;
        }

        @Override
        public JsonNode toJson(@Nonnull Object value) {
            if (value instanceof Byte) {
                return LongNode.valueOf((Byte)value);
            } else if (value instanceof Short) {
                return LongNode.valueOf((Short)value);
            } else if (value instanceof Integer) {
                return LongNode.valueOf((Integer) value);
            } else if (value instanceof Long) {
                return LongNode.valueOf((Long) value);
            }

            return LongNode.valueOf(((Number) value).longValue());
        }

        @Override
        public Object fromJson(@Nonnull JsonNode buf) {
            return buf.longValue();
        }

        @Override
        public short size() { return 4; }
    },

    /**
     * 大端在前，编码输入或解码输出类型：Long
     */
    INT40 {
        @Override
        public Object read(ByteBuf buf, short size) {
            byte b1 = buf.readByte();
            byte b2 = buf.readByte();
            byte b3 = buf.readByte();
            byte b4 = buf.readByte();
            byte b5 = buf.readByte();

            return  (((long) b1 << 32) & 0x000000FF00000000L) |
                    (((long) b2 << 24) & 0x00000000FF000000L) |
                    (((long) b3 << 16) & 0x0000000000FF0000L) |
                    (((long) b4 << 8) &  0x000000000000FF00L)  |
                    b5 &  0x00000000000000FFL;
        }

        @Override
        public short write(ByteBuf buf, Object value) {
            if (value == null) {
                buf.writeByte(0);
                buf.writeByte(0);
                buf.writeByte(0);
                buf.writeByte(0);
                buf.writeByte(0);
            } else {
                long v = ((Number) value).longValue();
                buf.writeByte((byte)((v >> 32)  & 0x00000000000000FF));
                buf.writeByte((byte)((v >> 24)  & 0x00000000000000FF));
                buf.writeByte((byte)((v >> 16)  & 0x00000000000000FF));
                buf.writeByte((byte)((v >> 8)   & 0x00000000000000FF));
                buf.writeByte((byte)((v)        & 0x00000000000000FF));
            }

            return 5;
        }

        @Override
        public JsonNode toJson(@Nonnull Object value) {
            if (value instanceof Byte) {
                return LongNode.valueOf((Byte)value);
            } else if (value instanceof Short) {
                return LongNode.valueOf((Short)value);
            } else if (value instanceof Integer) {
                return LongNode.valueOf((Integer) value);
            } else if (value instanceof Long) {
                return LongNode.valueOf((Long) value);
            }

            return LongNode.valueOf(((Number) value).longValue());
        }

        @Override
        public Object fromJson(@Nonnull JsonNode buf) {
            return buf.longValue();
        }

        @Override
        public short size() { return 5; }
    },

    /**
     * 小端在前，编码输入或解码输出类型：Long
     */
    INT40LE {
        @Override
        public Object read(ByteBuf buf, short size) {
            byte b1 = buf.readByte();
            byte b2 = buf.readByte();
            byte b3 = buf.readByte();
            byte b4 = buf.readByte();
            byte b5 = buf.readByte();

            return  (((long) b5 & 0x00000000000000FF) << 32 ) |
                    (((long) b4 & 0x00000000000000FF) << 24)  |
                    (((long) b3 & 0x00000000000000FF) << 16)  |
                    (((long) b2 & 0x00000000000000FF) << 8)  |
                    ((long) b1 & 0x00000000000000FF);
        }

        @Override
        public short write(ByteBuf buf, Object value) {
            if (value == null) {
                buf.writeByte(0);
                buf.writeByte(0);
                buf.writeByte(0);
                buf.writeByte(0);
                buf.writeByte(0);
            } else {
                long v = ((Number) value).longValue();
                buf.writeByte((byte)((v)       & 0x00000000000000FF));
                buf.writeByte((byte)((v >> 8)   & 0x00000000000000FF));
                buf.writeByte((byte)((v >> 16)  & 0x00000000000000FF));
                buf.writeByte((byte)((v >> 24)  & 0x00000000000000FF));
                buf.writeByte((byte)((v >> 32)  & 0x00000000000000FF));
            }

            return 5;
        }

        @Override
        public JsonNode toJson(@Nonnull Object value) {
            if (value instanceof Byte) {
                return LongNode.valueOf((Byte)value);
            } else if (value instanceof Short) {
                return LongNode.valueOf((Short)value);
            } else if (value instanceof Integer) {
                return LongNode.valueOf((Integer) value);
            } else if (value instanceof Long) {
                return LongNode.valueOf((Long) value);
            }

            return LongNode.valueOf(((Number) value).longValue());
        }

        @Override
        public Object fromJson(@Nonnull JsonNode buf) {
            return buf.longValue();
        }

        @Override
        public short size() { return 5; }
    },

    //0x09
    FLOAT {
        @Override
        public Object read(ByteBuf buf, short size) {
            int intVal = buf.readInt();

            return Float.intBitsToFloat(intVal);
        }

        public Object _read(ByteBuf buf, short size) {
            //获取 字节数组转化成的16进制字符串
            String BinaryStr = bytes2BinaryStr(buf, size);
            //符号位S
            long s = Long.parseLong(BinaryStr.substring(0, 1));
            //指数位E
            long e = Long.parseLong(BinaryStr.substring(1, 9),2);
            //位数M
            String M = BinaryStr.substring(9);
            float m = 0, a, b;
            for (int i = 0; i < M.length(); i++) {
                a = Integer.parseInt(M.charAt(i) + "");
                b = (float) Math.pow(2, i + 1);
                m = m + (a / b);
            }

            return (float) ((Math.pow(-1, s)) * (1+m) * (Math.pow(2,(e-127))));
        }

        @Override
        public short write(ByteBuf buf, Object value) {
            float fVal;

            if (value == null) {
                fVal = 0.0f;
            } else if (value instanceof Float) {
                fVal = (Float) value;
            } else {
                fVal = ((Number) value).floatValue();
            }

            buf.writeInt(Float.floatToIntBits(fVal));
            return 4;
        }

        @Override
        public JsonNode toJson(@Nonnull Object value) {
            if (value instanceof Byte) {
                return FloatNode.valueOf((Byte)value);
            } else if (value instanceof Short) {
                return FloatNode.valueOf((Short)value);
            } else if (value instanceof Integer) {
                return FloatNode.valueOf((Integer) value);
            } else if (value instanceof Float) {
                return FloatNode.valueOf((Float) value);
            }

            return FloatNode.valueOf(((Number) value).floatValue());
        }

        @Override
        public Object fromJson(@Nonnull JsonNode buf) {
            return buf.floatValue();
        }

        @Override
        public short size() { return 4; }
    },
    //0x0A
    DOUBLE {
        @Override
        public Object read(ByteBuf buf, short size) {
            return buf.readDouble();
        }

        @Override
        public short write(ByteBuf buf, Object value) {
            if (value == null) {
                buf.writeDouble(0);
            } else {
                buf.writeDouble(((Number) value).doubleValue());
            }
            return 8;
        }

        @Override
        public JsonNode toJson(@Nonnull Object value) {
            if (value instanceof Byte) {
                return DoubleNode.valueOf((Byte)value);
            } else if (value instanceof Short) {
                return DoubleNode.valueOf((Short)value);
            } else if (value instanceof Integer) {
                return DoubleNode.valueOf((Integer) value);
            } else if (value instanceof Float) {
                return DoubleNode.valueOf((Float) value);
            } else if (value instanceof Double) {
                return DoubleNode.valueOf((Double) value);
            }

            return FloatNode.valueOf(((Number) value).floatValue());
        }

        @Override
        public Object fromJson(@Nonnull JsonNode buf) {
            return buf.floatValue();
        }

        @Override
        public short size() { return 8; }
    },

    //0x0B
    STRING {
        @Override
        public Object read(ByteBuf buf, short size) {
            byte[] bytes = new byte[size];
            buf.readBytes(bytes);
            return new String(bytes, StandardCharsets.UTF_8);
        }

        @Override
        public short write(ByteBuf buf, Object value) {
            if (value == null) return 0;

            byte[] bytes = ((String) value).getBytes();
            buf.writeBytes(bytes);
            return (short)bytes.length;
        }

        @Override
        public JsonNode toJson(@Nonnull Object value) {
            if (value instanceof String) {
                return TextNode.valueOf((String) value);
            }

            return TextNode.valueOf(value.toString());
        }

        @Override
        public Object fromJson(@Nonnull JsonNode buf) {
            return buf.textValue();
        }

        @Override
        public short size() { return 0; }
    },

    /**
     * 字节数值：长度为2, 编码输入或解码输出类型：byte[]
     */
    BYTES02 {
        @Override
        public Object read(ByteBuf buf, short size) {
            byte[] bytes = new byte[size()]; //TODO 优化性能
            buf.readBytes(bytes);
            return bytes;
        }

        @Override
        public short write(ByteBuf buf, Object value) {
            if (value == null) return 0;

            byte[] bytes = ((byte[]) value);
            buf.writeBytes(bytes, 0, size());
            return size();
        }

        @Override
        public JsonNode toJson(@Nonnull Object value) {
            //TODO 待实现
            return null;
        }

        @Override
        public Object fromJson(@Nonnull JsonNode buf) {
            //TODO 待实现
            return null;
        }

        @Override
        public short size() { return 2; }
    },

    /**
     * 字节数值：长度为4, 编码输入或解码输出类型：byte[]
     */
    BYTES04 {
        @Override
        public Object read(ByteBuf buf, short size) {
            byte[] bytes = new byte[size()]; //TODO 优化性能
            buf.readBytes(bytes);
            return bytes;
        }

        @Override
        public short write(ByteBuf buf, Object value) {
            if (value == null) return 0;

            byte[] bytes = ((byte[]) value);
            buf.writeBytes(bytes, 0, size());
            return size();
        }

        @Override
        public JsonNode toJson(@Nonnull Object value) {
            //TODO 待实现
            return null;
        }

        @Override
        public Object fromJson(@Nonnull JsonNode buf) {
            //TODO 待实现
            return null;
        }

        @Override
        public short size() { return 4; }
    },
    /**
     * 字节数值：长度为8, 编码输入或解码输出类型：byte[]
     */
    BYTES08 {
        @Override
        public Object read(ByteBuf buf, short size) {
            byte[] bytes = new byte[size()]; //TODO 优化性能
            buf.readBytes(bytes);
            return bytes;
        }

        @Override
        public short write(ByteBuf buf, Object value) {
            if (value == null) return 0;

            byte[] bytes = ((byte[]) value);
            buf.writeBytes(bytes, 0, size());
            return size();
        }

        @Override
        public JsonNode toJson(@Nonnull Object value) {
            //TODO 待实现
            return null;
        }

        @Override
        public Object fromJson(@Nonnull JsonNode buf) {
            //TODO 待实现
            return null;
        }

        @Override
        public short size() { return 8; }
    },
    /**
     * 字符数组，长度为4（超过将截断）， 编码输入或解码输出类型：String
     */
    CHARS04 {

        @Override
        public Object read(ByteBuf buf, short size) {
            byte[] bytes = new byte[size()];
            buf.readBytes(bytes);
            return new String(bytes, StandardCharsets.UTF_8);
        }

        @Override
        public short write(ByteBuf buf, Object value) {
            if (value == null) return 0;

            byte[] bytes = ((String) value).getBytes();
            buf.writeBytes(bytes, 0, size()); //TODO 优化\0字符
            return size();
        }

        @Override
        public JsonNode toJson(@Nonnull Object value) {
            //TODO 待实现
            return null;
        }

        @Override
        public Object fromJson(@Nonnull JsonNode buf) {
            //TODO 待实现
            return null;
        }

        @Override
        public short size() { return 4; }
    },
    /**
     * 字符数组，长度为7（超过将截断）， 编码输入或解码输出类型：String
     */
    CHARS07 {
        @Override
        public Object read(ByteBuf buf, short size) {
            byte[] bytes = new byte[size()];
            buf.readBytes(bytes);
            return new String(bytes, StandardCharsets.UTF_8);
        }

        @Override
        public short write(ByteBuf buf, Object value) {
            if (value == null) return 0;

            byte[] bytes = ((String) value).getBytes();
            buf.writeBytes(bytes, 0, size()); //TODO 优化\0字符
            return size();
        }

        @Override
        public JsonNode toJson(@Nonnull Object value) {
            //TODO 待实现
            return null;
        }

        @Override
        public Object fromJson(@Nonnull JsonNode buf) {
            //TODO 待实现
            return null;
        }

        @Override
        public short size() { return 7; }
    },
    /**
     * 字符数组，长度为8（超过将截断）， 编码输入或解码输出类型：String
     */
    CHARS08 {
        @Override
        public Object read(ByteBuf buf, short size) {
            byte[] bytes = new byte[size()];
            buf.readBytes(bytes);

            int len = 0;
            for (int i = 0; i < size(); i++) {
                if (bytes[i] == 0) {
                    break;
                } else {
                    len = i;
                }
            }

            return new String(bytes, 0, len, StandardCharsets.UTF_8);
        }

        @Override
        public short write(ByteBuf buf, Object value) {
            if (value == null) return 0;

            byte[] bytes = ((String) value).getBytes();
            buf.writeBytes(bytes, 0, size()); //TODO 优化\0字符
            return size();
        }

        @Override
        public JsonNode toJson(@Nonnull Object value) {
            //TODO 待实现
            return null;
        }

        @Override
        public Object fromJson(@Nonnull JsonNode buf) {
            //TODO 待实现
            return null;
        }

        @Override
        public short size() { return 8; }
    },
    /**
     * 字符数组，长度为16（超过将截断）， 编码输入或解码输出类型：String
     */
    CHARS16 {
        @Override
        public Object read(ByteBuf buf, short size) {
            byte[] bytes = new byte[size()];
            buf.readBytes(bytes);
            return new String(bytes, StandardCharsets.UTF_8);
        }

        @Override
        public short write(ByteBuf buf, Object value) {
            if (value == null) return 0;

            byte[] bytes = ((String) value).getBytes();
            buf.writeBytes(bytes, 0, size()); //TODO 优化\0字符
            return size();
        }

        @Override
        public JsonNode toJson(@Nonnull Object value) {
            //TODO 待实现
            return null;
        }

        @Override
        public Object fromJson(@Nonnull JsonNode buf) {
            //TODO 待实现
            return null;
        }

        @Override
        public short size() { return 16; }
    },
    /**
     * 字符数组，长度为17（超过将截断）， 编码输入或解码输出类型：String
     */
    CHARS17 {
        @Override
        public Object read(ByteBuf buf, short size) {
            byte[] bytes = new byte[size()];
            buf.readBytes(bytes);
            return new String(bytes, StandardCharsets.UTF_8);
        }

        @Override
        public short write(ByteBuf buf, Object value) {
            if (value == null) return 0;

            byte[] bytes = ((String) value).getBytes();
            buf.writeBytes(bytes, 0, size()); //TODO 优化\0字符
            return size();
        }

        @Override
        public JsonNode toJson(@Nonnull Object value) {
            //TODO 待实现
            return null;
        }

        @Override
        public Object fromJson(@Nonnull JsonNode buf) {
            //TODO 待实现
            return null;
        }

        @Override
        public short size() { return 17; }
    },
    /**
     * 字符数组，长度为32（超过将截断）， 输入输出类型：String
     */
    CHARS32 {
        @Override
        public Object read(ByteBuf buf, short size) {
            byte[] bytes = new byte[size()];
            buf.readBytes(bytes);
            return new String(bytes, StandardCharsets.UTF_8);
        }

        @Override
        public short write(ByteBuf buf, Object value) {
            if (value == null) return 0;

            byte[] bytes = ((String) value).getBytes();
            buf.writeBytes(bytes, 0, size()); //TODO 优化\0字符
            return size();
        }

        @Override
        public JsonNode toJson(@Nonnull Object value) {
            //TODO 待实现
            return null;
        }

        @Override
        public Object fromJson(@Nonnull JsonNode buf) {
            //TODO 待实现
            return null;
        }

        @Override
        public short size() { return 32; }
    },
    /**
     * 8421BCD码，长度1字节或2个数字字符（超过将截断），编码输入或解码输出类型：String（合法字符'0'~'9','-','+')
     */
    BCD01_STR {
        @Override
        public Object read(ByteBuf buf, short size) {
            byte[] bytes = new byte[size()];
            buf.readBytes(bytes);
            return BCD8421BinaryCodec.decode(bytes);
        }

        @Override
        public short write(ByteBuf buf, Object value) {
            if (value == null) return 0;

            byte[] bytes = BCD8421BinaryCodec.encodeWithPadding((String) value, size());
            buf.writeBytes(bytes, 0, size());

            return size();
        }

        @Override
        public JsonNode toJson(@Nonnull Object value) {
            //TODO 待实现
            return null;
        }

        @Override
        public Object fromJson(@Nonnull JsonNode buf) {
            //TODO 待实现
            return null;
        }

        @Override
        public short size() { return 1; }
    },
    /**
     * 8421BCD码，长度2字节或4个数字字符（超过将截断），编码输入或解码输出类型：String（合法字符'0'~'9','-','+')
     */
    BCD02_STR {
        @Override
        public Object read(ByteBuf buf, short size) {
            byte[] bytes = new byte[size()];
            buf.readBytes(bytes);
            return BCD8421BinaryCodec.decode(bytes);
        }

        @Override
        public short write(ByteBuf buf, Object value) {
            if (value == null) return 0;

            byte[] bytes = BCD8421BinaryCodec.encodeWithPadding((String) value, size());
            buf.writeBytes(bytes, 0, size());

            return size();
        }

        @Override
        public JsonNode toJson(@Nonnull Object value) {
            //TODO 待实现
            return null;
        }

        @Override
        public Object fromJson(@Nonnull JsonNode buf) {
            //TODO 待实现
            return null;
        }

        @Override
        public short size() { return 2; }
    },
    /**
     * 8421BCD码，长度7字节或14个数字字符（超过将截断），编码输入或解码输出类型：String（合法字符'0'~'9','-','+')
     */
    BCD07_STR {
        @Override
        public Object read(ByteBuf buf, short size) {
            byte[] bytes = new byte[size()];
            buf.readBytes(bytes);
            return BCD8421BinaryCodec.decode(bytes);
        }

        @Override
        public short write(ByteBuf buf, Object value) {
            if (value == null) return 0;

            byte[] bytes = BCD8421BinaryCodec.encodeWithPadding((String) value, size());
            buf.writeBytes(bytes, 0, size());

            return size();
        }

        @Override
        public JsonNode toJson(@Nonnull Object value) {
            //TODO 待实现
            return null;
        }

        @Override
        public Object fromJson(@Nonnull JsonNode buf) {
            //TODO 待实现
            return null;
        }

        @Override
        public short size() { return 7; }
    },
    /**
     * 8421BCD码，长度8字节或16个数字字符（超过将截断），编码输入或解码输出类型：String（合法字符'0'~'9','-','+')
     */
    BCD08_STR {
        @Override
        public Object read(ByteBuf buf, short size) {
            byte[] bytes = new byte[size()];
            buf.readBytes(bytes);
            return BCD8421BinaryCodec.decode(bytes);
        }

        @Override
        public short write(ByteBuf buf, Object value) {
            if (value == null) return 0;

            byte[] bytes = BCD8421BinaryCodec.encodeWithPadding((String) value, size());
            buf.writeBytes(bytes, 0, size());

            return size();
        }

        @Override
        public JsonNode toJson(@Nonnull Object value) {
            //TODO 待实现
            return null;
        }

        @Override
        public Object fromJson(@Nonnull JsonNode buf) {
            //TODO 待实现
            return null;
        }

        @Override
        public short size() { return 8; }
    },
    /**
     * 8421BCD码，长度10字节或20个数字字符（超过将截断），编码输入或解码输出类型：String（合法字符'0'~'9','-','+')
     */
    BCD10_STR {
        @Override
        public Object read(ByteBuf buf, short size) {
            byte[] bytes = new byte[size()];
            buf.readBytes(bytes);
            return BCD8421BinaryCodec.decode(bytes);
        }

        @Override
        public short write(ByteBuf buf, Object value) {
            if (value == null) return 0;

            byte[] bytes = BCD8421BinaryCodec.encodeWithPadding((String) value, size());
            buf.writeBytes(bytes, 0, size());

            return size();
        }

        @Override
        public JsonNode toJson(@Nonnull Object value) {
            //TODO 待实现
            return null;
        }

        @Override
        public Object fromJson(@Nonnull JsonNode buf) {
            //TODO 待实现
            return null;
        }

        @Override
        public short size() { return 10; }
    },
    /**
     * 8421BCD码，长度16字节或32个数字字符（超过将截断），编码输入或解码输出类型：String（合法字符'0'~'9','-','+')
     */
    BCD16_STR {
        @Override
        public Object read(ByteBuf buf, short size) {
            byte[] bytes = new byte[size()];
            buf.readBytes(bytes);
            return BCD8421BinaryCodec.decode(bytes);
        }

        @Override
        public short write(ByteBuf buf, Object value) {
            if (value == null) return 0;

            byte[] bytes = BCD8421BinaryCodec.encodeWithPadding((String) value, size());
            buf.writeBytes(bytes, 0, size());

            return size();
        }

        @Override
        public JsonNode toJson(@Nonnull Object value) {
            //TODO 待实现
            return null;
        }

        @Override
        public Object fromJson(@Nonnull JsonNode buf) {
            //TODO 待实现
            return null;
        }

        @Override
        public short size() { return 16; }
    },
    
    //0x0C
    BINARY {
        @Override
        public Object read(ByteBuf buf, short size) {
            byte[] bytes = new byte[size];
            buf.readBytes(bytes);
            return bytes;
        }

        @Override
        public short write(ByteBuf buf, Object value) {
            if (value == null) return 0;

            byte[] bytes = (byte[]) value;
            buf.writeBytes(bytes);

            return (short) bytes.length;
        }

        @Override
        public JsonNode toJson(@Nonnull Object value) {
            //TODO 待实现
            return null;
        }

        @Override
        public Object fromJson(@Nonnull JsonNode buf) {
            //TODO 待实现
            return null;
        }

        @Override
        public short size() { return 0; }
    },
    //0x0D
    HEX_STR {
        @Override
        public Object read(ByteBuf buf, short size) {
            byte[] bytes = new byte[size];
            buf.readBytes(bytes);
            return Hex.encodeHexString(bytes);
        }

        @Override
        public short write(ByteBuf buf, Object value) {
            if (value == null) return 0;

            byte[] bytes;
            try {
                bytes = Hex.decodeHex((String) value);
            } catch (DecoderException e) {
                throw new RuntimeException("解码HEX字符串失败：", e);
            }
            buf.writeBytes(bytes);

            return (short)(bytes.length);
        }

        @Override
        public JsonNode toJson(@Nonnull Object value) {
            //TODO 待实现
            return null;
        }

        @Override
        public Object fromJson(@Nonnull JsonNode buf) {
            //TODO 待实现
            return null;
        }

        @Override
        public short size() { return 0; }
    },
    /**
     * 固定长度，长度2字节或4个十六进制表示字符（超过将截断），编码输入或解码输出类型：String
     */
    HEX02_STR {
        @Override
        public Object read(ByteBuf buf, short size) {
            byte[] bytes = new byte[size()];
            buf.readBytes(bytes);
            return Hex.encodeHexString(bytes);
        }

        @Override
        public short write(ByteBuf buf, Object value) {
            if (value == null) return 0;

            byte[] bytes;
            try {
                bytes = Hex.decodeHex((String) value);
            } catch (DecoderException e) {
                throw new RuntimeException("解码HEX字符串失败：", e);
            }
            if (bytes.length > 2) {
                buf.writeBytes(bytes, 0, size() * 2);
            } else {
                buf.writeBytes(bytes);
                for (int i = bytes.length; i < 2; i++) {
                    buf.writeByte((byte) 0);
                }
            }

            return size();
        }

        @Override
        public JsonNode toJson(@Nonnull Object value) {
            //TODO 待实现
            return null;
        }

        @Override
        public Object fromJson(@Nonnull JsonNode buf) {
            //TODO 待实现
            return null;
        }

        @Override
        public short size() { return 2; }
    },
    /**
     * 固定长度，长度4字节或8个十六进制表示字符（超过将截断），编码输入或解码输出类型：String
     */
    HEX04_STR {
        @Override
        public Object read(ByteBuf buf, short size) {
            byte[] bytes = new byte[size()];
            buf.readBytes(bytes);
            return Hex.encodeHexString(bytes);
        }

        @Override
        public short write(ByteBuf buf, Object value) {
            if (value == null) return 0;

            byte[] bytes;
            try {
                bytes = Hex.decodeHex((String) value);
            } catch (DecoderException e) {
                throw new RuntimeException("解码HEX字符串失败：", e);
            }
            if (bytes.length > 4) {
                buf.writeBytes(bytes, 0, size() * 2);
            } else {
                buf.writeBytes(bytes);
                for (int i = bytes.length; i < 4; i++) {
                    buf.writeByte((byte) 0);
                }
            }

            return size();
        }

        @Override
        public JsonNode toJson(@Nonnull Object value) {
            //TODO 待实现
            return null;
        }

        @Override
        public Object fromJson(@Nonnull JsonNode buf) {
            //TODO 待实现
            return null;
        }

        @Override
        public short size() { return 4; }
    },
    /**
     * 固定长度，长度8字节或16个十六进制表示字符（超过将截断），编码输入或解码输出类型：String
     */
    HEX08_STR {
        @Override
        public Object read(ByteBuf buf, short size) {
            byte[] bytes = new byte[size()];
            buf.readBytes(bytes);
            return Hex.encodeHexString(bytes);
        }

        @Override
        public short write(ByteBuf buf, Object value) {
            if (value == null) return 0;

            byte[] bytes;
            try {
                bytes = Hex.decodeHex((String) value);
            } catch (DecoderException e) {
                throw new RuntimeException("解码HEX字符串失败：", e);
            }
            if (bytes.length > size()) {
                buf.writeBytes(bytes, 0, size() * 2);
            } else {
                buf.writeBytes(bytes);
                for (int i = bytes.length; i < size(); i++) {
                    buf.writeByte((byte) 0);
                }
            }

            return size();
        }

        @Override
        public JsonNode toJson(@Nonnull Object value) {
            //TODO 待实现
            return null;
        }

        @Override
        public Object fromJson(@Nonnull JsonNode buf) {
            //TODO 待实现
            return null;
        }

        @Override
        public short size() { return 8; }
    },
    /**
     * 固定长度，长度16字节或32个十六进制表示字符（超过将截断），编码输入或解码输出类型：String
     */
    HEX16_STR {
        @Override
        public Object read(ByteBuf buf, short size) {
            byte[] bytes = new byte[size()];
            buf.readBytes(bytes);
            return Hex.encodeHexString(bytes);
        }

        @Override
        public short write(ByteBuf buf, Object value) {
            if (value == null) return 0;

            byte[] bytes;
            try {
                bytes = Hex.decodeHex((String) value);
            } catch (DecoderException e) {
                throw new RuntimeException("解码HEX字符串失败：", e);
            }
            if (bytes.length > 4) {
                buf.writeBytes(bytes, 0, size() * 2);
            } else {
                buf.writeBytes(bytes);
                for (int i = bytes.length; i < 4; i++) {
                    buf.writeByte((byte) 0);
                }
            }

            return size();
        }

        @Override
        public JsonNode toJson(@Nonnull Object value) {
            //TODO 待实现
            return null;
        }

        @Override
        public Object fromJson(@Nonnull JsonNode buf) {
            //TODO 待实现
            return null;
        }

        @Override
        public short size() { return 16; }
    },
    /**
     * 动态长度的BASE64编码的, 编码输入或解码输出类型：String
     */
    BASE64_STR {
        @Override
        public Object read(ByteBuf buf, short size) {
            byte[] bytes = new byte[size];
            buf.readBytes(bytes);
            return Base64.encodeBase64String(bytes);
        }

        @Override
        public short write(ByteBuf buf, Object value) {
            if (value == null) return 0;

            byte[] bytes = Base64.decodeBase64((String) value);
            buf.writeBytes(bytes);

            return (short)(bytes.length);
        }

        @Override
        public JsonNode toJson(@Nonnull Object value) {
            //TODO 待实现
            return null;
        }

        @Override
        public Object fromJson(@Nonnull JsonNode buf) {
            //TODO 待实现
            return null;
        }

        @Override
        public short size() { return 0; }
    },

    /**
     * 分多段数字：01 01 01，不支持写，编码输入或解码输出类型：String
     */
    Num010101_Str {
        @Override
        public Object read(ByteBuf buf, short size) {
            byte b1 = buf.readByte();
            byte b2 = buf.readByte();
            byte b3 = buf.readByte();

            return String.format("%d.%d.%d", b1, b2, b3);
        }

        @Override
        public short write(ByteBuf buf, Object value) {
            buf.writeByte(0);
            buf.writeByte(0);
            buf.writeByte(0);

            return 3;
        }

        @Override
        public JsonNode toJson(@Nonnull Object value) {
            //TODO 待实现
            return null;
        }

        @Override
        public Object fromJson(@Nonnull JsonNode buf) {
            //TODO 待实现
            return null;
        }

        @Override
        public short size() { return 3; }
    },

    /**
     * 分多段数字：01 02 03 0102 010101，不支持写，编码输入或解码输出类型：String
     */
    Num0101010203_Str {
        @Override
        public Object read(ByteBuf buf, short size) {
            byte b1 = buf.readByte();
            byte b2 = buf.readByte();
            byte b3 = buf.readByte();
            short b4 = buf.readShort();

            byte b6 = buf.readByte();
            byte b7 = buf.readByte();
            byte b8 = buf.readByte();

            int bp4 = (0x00ff & (int)b6 << 16) | (0x00ff & (int)b7 << 8) | b8;

            return String.format("%d-%d-%d-%d-%d", b1, b2, b3, b4, bp4);
        }

        @Override
        public short write(ByteBuf buf, Object value) {
            buf.writeByte(0);
            buf.writeByte(0);
            buf.writeByte(0);

            return 3;
        }

        @Override
        public JsonNode toJson(@Nonnull Object value) {
            //TODO 待实现
            return null;
        }

        @Override
        public Object fromJson(@Nonnull JsonNode buf) {
            //TODO 待实现
            return null;
        }

        @Override
        public short size() { return 3; }
    },

    /**
     * CP56Time2a编码的日期，编码输入或解码输出类型：java.util.Date
     */
    CP56Time2a {
        @Override
        public Object read(ByteBuf buf, short size) {
            byte[] bytes = new byte[size];
            buf.readBytes(bytes);

            int milSec1 = bytes[0] < 0 ? 256 + bytes[0] : bytes[0];
            int milSec2 = bytes[1] < 0 ? 256 + bytes[1] : bytes[1];
            int milSec = milSec2 * 256 + milSec1;
            int minute = bytes[2] & 0x3F;
            int hour = bytes[3] & 0x1F;
            int day = bytes[4] & 0x1F;
            int month = bytes[5] & 0x0F;
            int year = bytes[6] & 0x7F;


            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.YEAR, 2000 + year);
            cal.set(Calendar.MONTH, month - 1);
            cal.set(Calendar.DAY_OF_MONTH, day);
            cal.set(Calendar.HOUR_OF_DAY, hour);
            cal.set(Calendar.MINUTE, minute);
            cal.set(Calendar.SECOND, milSec / 1000);
            cal.set(Calendar.MILLISECOND, milSec % 1000);

            return cal.getTime();
        }

        @Override
        public short write(ByteBuf buf, Object value) {
            if (value == null) return 0;

            Calendar cal = Calendar.getInstance();
            if (value instanceof Date) {
                cal.setTime((Date) value);
            } else if (value instanceof Long) {
                cal.setTimeInMillis((Long) value);
            } else {
                return 0;
            }

            int milSec = cal.get(Calendar.SECOND) * 1000 + cal.get(Calendar.MILLISECOND);
            int minute = cal.get(Calendar.MINUTE);
            int hour = cal.get(Calendar.HOUR_OF_DAY);
            int dayOfMonth = cal.get(Calendar.DAY_OF_MONTH); // 取值范围：1~31
            int dayOfWeak = cal.get(Calendar.DAY_OF_WEEK); // 取值范围：1~7
            int month = cal.get(Calendar.MONTH) + 1;
            int year = cal.get(Calendar.YEAR) - 2000;

            if (dayOfWeak == Calendar.SUNDAY) {
                dayOfWeak = 7;
            } else {
                dayOfWeak--;
            }

            byte[] bytes = new byte[size()];
            bytes[0] = (byte) (milSec & 0xFF);
            bytes[1] = (byte) ((milSec >> 8) & 0xFF);
            bytes[2] = (byte) (minute & 0x3F);
            bytes[3] = (byte) (hour & 0x1F);
            bytes[4] = (byte) (dayOfWeak << 5 | dayOfMonth & 0x1F);
            bytes[5] = (byte) (month & 0x0F);
            bytes[6] = (byte) (year & 0x7F);

            buf.writeBytes(bytes);

            return (short)(bytes.length);
        }

        @Override
        public JsonNode toJson(@Nonnull Object value) {
            //TODO 待实现
            return null;
        }

        @Override
        public Object fromJson(@Nonnull JsonNode buf) {
            //TODO 待实现
            return null;
        }

        @Override
        public short size() { return 7; }
    },

    /**
     * 日期，编码输入输出类型：java.util.Date
     */
    Date19 {
        @Override
        public Object read(ByteBuf buf, short size) {
            //TODO 待实现
            return null;
        }

        @Override
        public short write(ByteBuf buf, Object value) {
            //TODO 待实现
            return 0;
        }

        @Override
        public JsonNode toJson(@Nonnull Object value) {
            if (value instanceof Long) {
                return TextNode.valueOf(DateUtils.toYYYYMMDDHHmmss19(new Date((Long) value)));
            }

            return TextNode.valueOf(DateUtils.toYYYYMMDDHHmmss19((Date) value));
        }

        @Override
        public Object fromJson(@Nonnull JsonNode buf) {
            return DateUtils.fromYYYYMMDDHHmmss19(buf.textValue());
        }

        @Override
        public short size() {
            return 0;
        }
    };

    private final static BaseDataType[] VALUES = values();

    public abstract Object read(ByteBuf buf, short size);

    /**
     *
     * @param buf       输出BUF，（非空）；
     * @param value     取值，（可空），空值时补为0的字节
     * @return  写入的字节数
     */
    public abstract short write(ByteBuf buf, Object value);

    public abstract JsonNode    toJson(@Nonnull Object value);

    public abstract Object      fromJson(@Nonnull JsonNode buf);

    public abstract short size();

    /**
     * 将字节数组转换成16进制字符串
     */
    public static String bytes2BinaryStr(ByteBuf buf, int length){
        StringBuilder binaryStr = new StringBuilder();

        for (int i = 0; i < length; i++) {
            String str = Integer.toBinaryString((buf.readByte() & 0xFF) + 0x100).substring(1);
            binaryStr.append(str);
        }

        return binaryStr.toString();
    }

    private static BaseDataType loopUpType(Object data) {
        if (data == null) {
            return NULL;
        } else if (data instanceof Boolean) {
            return BOOLEAN;
        } else if (data instanceof Byte) {
            return INT8;
        } else if (data instanceof Short) {
            return INT16;
        } else if (data instanceof Integer) {
            return INT32;
        } else if (data instanceof Long) {
            return INT64;
        } else if (data instanceof Float) {
            return FLOAT;
        } else if (data instanceof Double) {
            return DOUBLE;
        } else if (data instanceof String) {
            return STRING;
        } else if (data instanceof byte[]) {
            return BINARY;
        } else {
            throw new IllegalArgumentException("Unsupported data type: " + data.getClass());
        }
    }

}
