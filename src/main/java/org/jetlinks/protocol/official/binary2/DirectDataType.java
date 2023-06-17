package org.jetlinks.protocol.official.binary2;

import com.google.common.collect.Maps;
import io.netty.buffer.ByteBuf;
import org.jetlinks.protocol.official.ObjectMappers;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public enum DirectDataType {
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
            buf.writeShort(bytes.length);
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
            buf.writeShort(bytes.length);
            buf.writeBytes(bytes);

            return 0;
        }

        @Override
        public short size() { return 0; }
    },
    //0x0D
    ARRAY {
        @Override
        public Object read(ByteBuf buf) {
            int len = buf.readUnsignedShort();
            List<Object> array = new ArrayList<>(len);
            for (int i = 0; i < len; i++) {
                array.add(readFrom(buf));
            }
            return array;
        }

        @Override
        public short write(ByteBuf buf, Object value) {
            Collection<Object> array = (Collection<Object>) value;
            buf.writeShort(array.size());
            for (Object o : array) {
                writeTo(o, buf);
            }

            return 0;
        }

        @Override
        public short size() { return 0; }
    },
    //0x0E
    OBJECT {
        @Override
        public Object read(ByteBuf buf) {
            int len = buf.readUnsignedShort();
            Map<String, Object> data = Maps.newLinkedHashMapWithExpectedSize(len);
            for (int i = 0; i < len; i++) {
                data.put((String) STRING.read(buf), readFrom(buf));
            }
            return data;
        }

        @Override
        public short write(ByteBuf buf, Object value) {
            Map<String, Object> data = value instanceof Map ? ((Map) value) : ObjectMappers.JSON_MAPPER.convertValue(value, Map.class);
            buf.writeShort(data.size());

            for (Map.Entry<String, Object> entry : data.entrySet()) {
                STRING.write(buf, entry.getKey());
                writeTo(entry.getValue(), buf);
            }

            return 0;
        }

        @Override
        public short size() { return 0; }
    };

    private final static DirectDataType[] VALUES = values();

    public abstract Object read(ByteBuf buf);

    public abstract short write(ByteBuf buf, Object value);

    public abstract short size();

    public static Object readFrom(ByteBuf buf) {
        return VALUES[buf.readUnsignedByte()].read(buf);
    }

    public static void writeTo(Object data, ByteBuf buf) {
        DirectDataType type = loopUpType(data);
        buf.writeByte(type.ordinal());
        type.write(buf, data);
    }

    private static DirectDataType loopUpType(Object data) {
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
        } else if (data instanceof Collection) {
            return ARRAY;
        } else if (data instanceof Map) {
            return OBJECT;
        } else {
            throw new IllegalArgumentException("Unsupported data type: " + data.getClass());
        }
    }

}
