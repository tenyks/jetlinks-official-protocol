package org.jetlinks.protocol.official.binary2;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.buffer.UnpooledDirectByteBuf;
import io.netty.handler.codec.base64.Base64Encoder;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
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
        public short size() { return 8; }
    },
    //0x06
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
        public short size() { return 1; }
    },
    //0x07
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
        public short size() { return 2; }
    },
    UINT16LE { //小端优先
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
        public short size() { return 3; }
    },
    UINT24LE { //小端优先
        @Override
        public Object read(ByteBuf buf, short size) {
            byte b1 = buf.readByte();
            byte b2 = buf.readByte();
            byte b3 = buf.readByte();

            return (int) b3 << 16 | (int) b2 << 8 | b1;
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
        public short size() { return 4; }
    },
    //0x08
    UINT40 {
        @Override
        public Object read(ByteBuf buf, short size) {
            //TODO 补实现
            return buf.readUnsignedInt();
        }

        @Override
        public short write(ByteBuf buf, Object value) {
            //TODO 补实现
            if (value == null) {
                buf.writeInt(0);
            } else {
                buf.writeInt(((Number) value).intValue());
            }
            return 4;
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
        public short size() { return 0; }
    },
    BYTES8 {
        //TODO
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
            buf.writeBytes(bytes, 0, size());
            return (short)bytes.length;
        }

        @Override
        public short size() { return 8; }
    },//0x0B
    //0x0B
    CHARS4 {

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
            buf.writeBytes(bytes, 0, size());
            return (short) bytes.length;
        }

        @Override
        public short size() { return 4; }
    },
    CHARS7 {

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
            buf.writeBytes(bytes, 0, size());
            return (short)bytes.length;
        }

        @Override
        public short size() { return 7; }
    },
    CHARS8 {

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
            buf.writeBytes(bytes, 0, size());
            return (short)bytes.length;
        }

        @Override
        public short size() { return 8; }
    },//0x0B
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
            buf.writeBytes(bytes, 0, size());
            return (short)bytes.length;
        }

        @Override
        public short size() { return 16; }
    },
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
            buf.writeBytes(bytes, 0, size());
            return (short) bytes.length;
        }

        @Override
        public short size() { return 17; }
    },
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
            buf.writeBytes(bytes, 0, size());
            return (short) bytes.length;
        }

        @Override
        public short size() { return 32; }
    },
    BCD16 {
        //TODO
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
            buf.writeBytes(bytes, 0, size());
            return (short) bytes.length;
        }

        @Override
        public short size() { return 32; }
    },
    BCD07 {
        //TODO
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
            buf.writeBytes(bytes, 0, size());
            return (short) bytes.length;
        }

        @Override
        public short size() { return 32; }
    },
    BCD08 {
        //TODO
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
            buf.writeBytes(bytes, 0, size());
            return (short) bytes.length;
        }

        @Override
        public short size() { return 32; }
    },
    BCD10 {
        //TODO
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
            buf.writeBytes(bytes, 0, size());
            return (short) bytes.length;
        }

        @Override
        public short size() { return 32; }
    },
    BCD02 {
        //TODO
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
            buf.writeBytes(bytes, 0, size());
            return (short) bytes.length;
        }

        @Override
        public short size() { return 32; }
    },
    BCD01 {
        //TODO
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
            buf.writeBytes(bytes, 0, size());
            return (short) bytes.length;
        }

        @Override
        public short size() { return 32; }
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
        public short size() { return 0; }
    },//0x0D
    HEX_STR_4 {
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
            if (bytes.length > 4) {
                buf.writeBytes(bytes, 0, 8);
            } else {
                buf.writeBytes(bytes);
                for (int i = bytes.length; i < 4; i++) {
                    buf.writeByte((byte) 0);
                }
            }

            return (short)(4);
        }

        @Override
        public short size() { return 4; }
    },
    HEX_STR_8 {
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
            if (bytes.length > 4) {
                buf.writeBytes(bytes, 0, 8);
            } else {
                buf.writeBytes(bytes);
                for (int i = bytes.length; i < 4; i++) {
                    buf.writeByte((byte) 0);
                }
            }

            return (short)(4);
        }

        @Override
        public short size() { return 4; }
    },
    HEX_STR_16 {
        //TODO
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
            if (bytes.length > 4) {
                buf.writeBytes(bytes, 0, 8);
            } else {
                buf.writeBytes(bytes);
                for (int i = bytes.length; i < 4; i++) {
                    buf.writeByte((byte) 0);
                }
            }

            return (short)(4);
        }

        @Override
        public short size() { return 4; }
    },
    //0x0D
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
        public short size() { return 0; }
    },

    //CP56Time2a
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
        public short size() { return 7; }
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
