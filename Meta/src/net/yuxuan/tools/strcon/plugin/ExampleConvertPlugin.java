package net.yuxuan.tools.strcon.plugin;

import net.yuxuan.utils.StringConsumer;

public class ExampleConvertPlugin extends BaseConvertPlugin {
    public ExampleConvertPlugin() {
        setName("ExampleConverter");
    }
    
    @Override
    public boolean process(StringConsumer sc, StringBuilder rb) {
        rb.append("NOT IMPLEMENTED");
        return true;
    }
}
