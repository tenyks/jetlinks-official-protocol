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
    //0x09
    FLOAT {
        @Override
        public Object read(ByteBuf buf, short size) {
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
            if (value == null) {
                buf.writeFloat(0);
            } else {
                buf.writeFloat(((Number) value).floatValue());
            }
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
    }
    ;

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
