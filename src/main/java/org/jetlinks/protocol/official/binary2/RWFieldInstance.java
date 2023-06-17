package org.jetlinks.protocol.official.binary2;

/**
 * 字段实例
 *
 * @author v-lizy81
 * @date 2023/6/13 23:58
 */
public class RWFieldInstance {

    private String  code;

    private Object  value;

    private int     offset;

    private int     countOfBytes;

    public RWFieldInstance(String code, Object value, int offset, int countOfBytes) {
        this.code = code;
        this.value = value;
        this.offset = offset;
        this.countOfBytes = countOfBytes;
    }

    public int  getIntValue() {
        return ((Number) value).intValue();
    }

    public int  getShortValue() {
        return ((Number) value).shortValue();
    }

    public String getCode() {
        return code;
    }

    public Object getValue() {
        return value;
    }

    public int getOffset() {
        return offset;
    }

    public int getCountOfBytes() {
        return countOfBytes;
    }

    @Override
    public String toString() {
        return String.format("%s/%d/%d/%s", code, offset, countOfBytes, value);
    }
}
