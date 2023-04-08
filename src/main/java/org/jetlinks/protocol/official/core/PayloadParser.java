package org.jetlinks.protocol.official.core;

import com.alibaba.fastjson.JSONObject;

public interface PayloadParser {

    JSONObject parse(String uri, byte[] payload);

}
