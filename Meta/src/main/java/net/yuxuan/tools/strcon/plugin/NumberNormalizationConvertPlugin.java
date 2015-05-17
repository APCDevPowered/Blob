package net.yuxuan.tools.strcon.plugin;

import net.yuxuan.utils.StringConsumer;

public class NumberNormalizationConvertPlugin extends BaseConvertPlugin {

    public NumberNormalizationConvertPlugin() {
        setName("NumberNormalizationConverter");
    }

    @Override
    public boolean process(StringConsumer sc, StringBuilder rb) {
        rb.append("NOT IMPLEMENTED");
        return true;
    }
}
