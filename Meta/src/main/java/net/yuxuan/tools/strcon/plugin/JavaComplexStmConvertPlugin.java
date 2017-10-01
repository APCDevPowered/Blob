package net.yuxuan.tools.strcon.plugin;

import net.yuxuan.utils.StringConsumer;

public class JavaComplexStmConvertPlugin extends BaseConvertPlugin {
	public JavaComplexStmConvertPlugin() {
		setName("JavaComplexStmConvertPlugin");
	}

	@Override
	public boolean process(StringConsumer sc, StringBuilder rb) {
		if (eatJavaComplexStm(sc).isSuccess() == false) {
			rb.append("Error: \n");
			rb.append("LastEatCount: " + sc.getLastEatCount() + "\n");
			rb.append("CuttedString: " + sc.getCuttedString() + "\n");
			rb.append("LastEatString: " + sc.getLastEatString() + "\n");
			rb.append("Pointer: " + sc.getPointer() + "\n");
		}
		rb.append(sc.getLastEatString());
		return true;
	}

}
