package org.jetlinks.protocol.official.binary2;

import io.netty.buffer.ByteBuf;

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
        public Object read(ByteBuf buf) { return null; }

        @Override
        public short write(ByteBuf buf, Object value) { return 0; }

        @Override
        public short size() { return 0; }
    },

    //0x01
    BOOLEAN {
        @Override
        public Object read(ByteBuf buf) {
            return buf.readBoolean();
        }

        @Override
        public short write(ByteBuf buf, Object value) {
            buf.writeBoolean((Boolean) value);
            return 1;
        }

        @Override
        public short size() { return 1; }
    },
    //0x02
    INT8 {
        @Override
        public Object read(ByteBuf buf) {
            return buf.readByte();
        }

        @Override
        public short write(ByteBuf buf, Object value) {
            buf.writeByte((Byte) value);
            return 1;
        }

        @Override
        public short size() { return 1; }
    },
    //0x03
    INT16 {
        @Override
        public Object read(ByteBuf buf) {
            return buf.readShort();
        }

        @Override
        public short write(ByteBuf buf, Object value) {
            buf.writeShort((Short) value);
            return 2;
        }

        @Override
        public short size() { return 2; }
    },
    //0x04
    INT32 {
        @Override
        public Object read(ByteBuf buf) {
            return buf.readInt();
        }

        @Override
        public short write(ByteBuf buf, Object value) {
            buf.writeInt((Integer) value);
            return 4;
        }

        @Override
        public short size() { return 4; }
    },
    //0x05
    INT64 {
        @Override
        public Object read(ByteBuf buf) {
            return buf.readLong();
        }

        @Override
        public short write(ByteBuf buf, Object value) {
            buf.writeLong((Long) value);
            return 8;
        }

        @Override
        public short size() { return 8; }
    },
    //0x06
    UINT8 {
        @Override
        public Object read(ByteBuf buf) {
            return buf.readUnsignedByte();
        }

        @Override
        public short write(ByteBuf buf, Object value) {
            buf.writeByte((Byte) value);
            return 1;
        }

        @Override
        public short size() { return 1; }
    },
    //0x07
    UINT16 {
        @Override
        public Object read(ByteBuf buf) {
            return buf.readUnsignedShort();
        }

        @Override
        public short write(ByteBuf buf, Object value) {
            buf.writeShort((Short) value);
            return 2;
        }

        @Override
        public short size() { return 2; }
    },
    //0x08
    UINT32 {
        @Override
        public Object read(ByteBuf buf) {
            return buf.readUnsignedInt();
        }

        @Override
        public short write(ByteBuf buf, Object value) {
            buf.writeInt((Integer) value);
            return 4;
        }

        @Override
        public short size() { return 4; }
    },
    //0x09
    FLOAT {
        @Override
        public Object read(ByteBuf buf) {
            return buf.readFloat();
        }

        @Override
        public short write(ByteBuf buf, Object value) {
            buf.writeFloat((Float) value);
            return 4;
        }

        @Override
        public short size() { return 4; }
    },
    //0x0A
    DOUBLE {
        @Override
        public Object read(ByteBuf buf) {
            return buf.readDouble();
        }

        @Override
        public short write(ByteBuf buf, Object value) {
            buf.writeDouble((Double) value);
            return 8;
        }

        @Override
        public short size() { return 8; }
    },
    //0x0B
    STRING {
        @Override
        public Object read(ByteBuf buf) {
            int len = buf.readUnsignedShort();
            byte[] bytes = new byte[len];
            buf.readBytes(bytes);
            return new String(bytes, StandardCharsets.UTF_8);
        }

        @Override
        public short write(ByteBuf buf, Object value) {

            byte[] bytes = ((String) value).getBytes();
            buf.writeBytes(bytes);

            return 8;
        }

        @Override
        public short size() { return 0; }
    },
    //0x0C
    BINARY {
        @Override
        public Object read(ByteBuf buf) {
            int len = buf.readUnsignedShort();
            byte[] bytes = new byte[len];
            buf.readBytes(bytes);
            return bytes;
        }

        @Override
        public short write(ByteBuf buf, Object value) {
            byte[] bytes = (byte[]) value;
            buf.writeBytes(bytes);

            return 0;
        }

        @Override
        public short size() { return 0; }
    },
    //0x0D
    HEX_STR {
        @Override
        public Object read(ByteBuf buf) {
            int len = buf.readUnsignedShort();
            byte[] bytes = new byte[len];
            buf.readBytes(bytes);
            return bytes;
        }

        @Override
        public short write(ByteBuf buf, Object value) {
            byte[] bytes = (byte[]) value;
            buf.writeBytes(bytes);

            return 0;
        }

        @Override
        public short size() { return 0; }
    }
    ;

    private final static BaseDataType[] VALUES = values();

    public abstract Object read(ByteBuf buf);

    /**
     *
     * @param buf       输出BUF，（非空）；
     * @param value     取值，（可空），空值时补为0的字节
     * @return  写入的字节数
     */
    public abstract short write(ByteBuf buf, Object value);

    public abstract short size();

    public static Object readFrom(ByteBuf buf) {
        return VALUES[buf.readUnsignedByte()].read(buf);
    }

    public static void writeTo(Object data, ByteBuf buf) {
        BaseDataType type = loopUpType(data);
        buf.writeByte(type.ordinal());
        type.write(buf, data);
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
