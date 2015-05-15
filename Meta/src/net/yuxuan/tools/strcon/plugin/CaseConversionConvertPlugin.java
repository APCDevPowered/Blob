package net.yuxuan.tools.strcon.plugin;

import net.yuxuan.utils.StringConsumer;

public class CaseConversionConvertPlugin extends BaseConvertPlugin {

    public CaseConversionConvertPlugin() {
        setName("CaseConversionConverter");
    }

    @Override
    public boolean process(StringConsumer sc, StringBuilder rb) {
        rb.append("NOT IMPLEMENTED");
        return true;
    }
}
