package org.jetlinks.protocol.official;

import org.junit.Test;

public class PluginSettingsTest {

    @Test
    public void loadDefault() {
        PluginConfig config = PluginConfig.loadDefault();

        System.out.println(config.getConfig("tcp.codec"));
    }
}