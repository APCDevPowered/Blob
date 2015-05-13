package net.yuxuan.tools.strcon.plugin;

import java.awt.Component;
import java.util.regex.Pattern;

import net.yuxuan.utils.StringConsumer;

public abstract class BaseConvertPlugin {
	private String name;
	
	/* Example Code
	if (!sc.eatSpaces()) { return false; }
	rb.append(sc.getPreEat() + ":");
	if (sc.eatPattern(Pattern.compile("[0-9]")) == -1) { return false; }
	rb.append(sc.getPreEat() + ":");
	if (!sc.eatEOF()) { return false; }
	rb.append(sc.getPreEat() + ":");
	*/
	public abstract boolean process(StringConsumer sc, StringBuilder rb);
	
	public Component getSettingComponent() {
		return null;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean eatNumber(StringConsumer sc) {
		int lastEatCount = 0;
		StringBuilder lastEatString = new StringBuilder();
		
		if(sc.eatPattern(Pattern.compile("((\\+|-)\\s*)?((((\\d+\\.\\d*)|(\\d*\\.\\d+)|(\\d+))(F|D|f|d)?)|(\\d+))")) == -1) {
			sc.setLastEatString(null);
			sc.setLastEatCount(0);
			return false;
		}
		appendNotNull(lastEatString, sc.getLastEatString());
		lastEatCount += sc.getLastEatCount();
		
		sc.setLastEatString(lastEatString.toString());
		sc.setLastEatCount(lastEatCount);
		return true;
	}
	
	public boolean eatSingleNumberStm(StringConsumer sc) {
		int lastEatCount = 0;
		StringBuilder lastEatString = new StringBuilder();
		
		sc.eatSpaces();
		appendNotNull(lastEatString, sc.getLastEatString());
		lastEatCount += sc.getLastEatCount();
		
		if (eatNumber(sc) == false) {
			sc.setLastEatString(null);
			sc.setLastEatCount(0);
			return false;
		}
		appendNotNull(lastEatString, sc.getLastEatString());
		lastEatCount += sc.getLastEatCount();
		
		sc.eatSpaces();
		appendNotNull(lastEatString, sc.getLastEatString());
		lastEatCount += sc.getLastEatCount();
		
		if (sc.eatPattern(Pattern.compile("(\\+|-|\\*|/|%)")) != -1) {
			appendNotNull(lastEatString, sc.getLastEatString());
			lastEatCount += sc.getLastEatCount();
			
			sc.eatSpaces();
			appendNotNull(lastEatString, sc.getLastEatString());
			lastEatCount += sc.getLastEatCount();
			
			if (eatNumber(sc) == false) {
				sc.setLastEatString(null);
				sc.setLastEatCount(0);
				return false;
			}
			appendNotNull(lastEatString, sc.getLastEatString());
			lastEatCount += sc.getLastEatCount();
			
			sc.eatSpaces();
			appendNotNull(lastEatString, sc.getLastEatString());
			lastEatCount += sc.getLastEatCount();
		}
		
		sc.setLastEatString(lastEatString.toString());
		sc.setLastEatCount(lastEatCount);
		return true;
	}

	public void appendNotNull(StringBuilder stringBuilder, String string) {
		if (string != null) {
			stringBuilder.append(string);
		}
	}
}
