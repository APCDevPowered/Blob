package net.yuxuan.tools.strcon.plugin;

import net.yuxuan.utils.StringConsumer;

public class TestConvertPlugin extends BaseConvertPlugin {
	public TestConvertPlugin() {
		setName("TestConverter");
	}
	
	@Override
	public boolean process(StringConsumer sc, StringBuilder rb) {
		rb.append("Just Test");
		return true;
	}
}
