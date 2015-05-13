package net.yuxuan.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * {@code StringConsumer}
 * 类用于表示一个字符串消耗器，将按要求尝试从提供的字符串头部剪裁部分字符，并返回剪裁的结果。用于对字符串进行解析，并根据结果进行相关操作。
 * 
 * @author yuxuanchiadm
 * @see java.lang.String
 * @see java.util.regex.Pattern
 */
public class StringConsumer {
	/** 当前用于裁剪的字符串。 */
	protected String string;
	/** 上一次剪裁共消耗多少字符。 */
	protected int lastEatCount; // 默认为0
	/** 最后剪裁掉的字符串。 */
	protected String lastEatString; // 默认为null

	/**
	 * 初始化一个新的字符串剪裁器。
	 * 
	 * @param string
	 *            提供给字符串剪裁器的字符串。
	 */
	public StringConsumer(String string) {
		this.string = string;
	}

	/**
	 * 从字符串头部匹配传入的字符串数组中的所有字符串，如果某一个匹配 则将匹配部分裁剪，并返回第几个字符串匹配，如果任何一个字符串都
	 * 不匹配，则返回-1。
	 * 
	 * @param stringArray
	 *            将进行匹配的字符串数组。
	 * @return 成功则返回第几个字符串匹配，否则返回-1。
	 */
	public int eatStrings(String... stringArray) {
		lastEatString = null;
		lastEatCount = 0;
		for (int i = 0; i < stringArray.length; i++) {
			if (string.startsWith(stringArray[i])) {
				lastEatString = string.substring(0, stringArray[i].length());
				lastEatCount = stringArray[i].length();
				string = string.substring(stringArray[i].length());
				return i;
			}
		}
		return -1;
	}

	/**
	 * 从字符串头部匹配传入的正则表达式数组中的所有正则表达式，如果某一个匹配则将匹配部分裁剪，并返回第几个正则表达式匹配，如果
	 * 任何一个字符串都不匹配，则返回-1。
	 * 
	 * @param stringArray
	 *            将进行匹配的正则表达式数组。
	 * @return 成功则返回第几个正则表达式匹配，否则返回-1。
	 */
	public int eatPattern(Pattern... patternArray) {
		lastEatString = null;
		lastEatCount = 0;
		for (int i = 0; i < patternArray.length; i++) {
			Pattern pattern = patternArray[i];
			Matcher matcher = pattern.matcher(string);
			if (matcher.lookingAt()) {
				lastEatString = string.substring(0, matcher.end());
				lastEatCount = matcher.end();
				string = string.substring(matcher.end());
				return i;
			}
		}
		return -1;
	}

	/**
	 * 从字符串头部匹配任意个空格，如果匹配到至少1个空格，则将匹配部分裁剪，并返回true，否则返回false。空格字符为' '、'\n'、'\r'和
	 * '\t'。
	 * 
	 * @return 是否成功匹配空格。
	 */
	public boolean eatSpaces() {
		lastEatString = null;
		lastEatCount = 0;
		int pos;
		for (pos = 0; pos < string.length(); pos++) {
			char c = string.charAt(pos);
			if (c != ' ' && c != '\n' && c != '\r' && c != '\t') {
				break;
			}
		}
		if (pos == 0) {
			return false;
		}
		lastEatString = string.substring(0, pos);
		lastEatCount = pos;
		string = string.substring(pos);
		return true;
	}

	/**
	 * 从字符串头部匹配一个EOF，如果匹配，则返回true，否则返回false。注意此函数不会剪裁任何字符，并始终设置
	 * <code>{@link #getLastEatCount()}</code> 返回的值为0。如果单次调用返回true，则
	 * <code>{@link #getLastEatString()}</code>返回空字符串，而且在不改变状态的情况下，多次调用也返回true。
	 * 
	 * @return 是否成功匹配EOF。
	 */
	public boolean eatEOF() {
		lastEatString = null;
		lastEatCount = 0;
		if (!string.isEmpty()) {
			return false;
		}
		lastEatString = "";
		lastEatCount = 0;
		return true;
	}

	/**
	 * 返回当前用于裁剪的字符串。
	 * 
	 * @return 当前用于裁剪的字符串。
	 */
	public String getString() {
		return string;
	}

	/**
	 * 设置用于裁剪的字符串。注意这回重置<code>{@link #eatEOF()}</code>返回的值为0。
	 * 
	 * @param string
	 *            用于裁剪的字符串。
	 */
	public void setString(String string) {
		this.string = string;
		lastEatCount = 0;
	}

	/**
	 * 返回上一次裁剪共裁剪了多少字符。注意如果上一次裁剪失败，默认返回0。但单次裁剪也可能不消耗任何字符，例如
	 * <code>{@link #eatEOF()}</code>，所以不能以此函数返回值是否为0，判断上次裁剪是否成功。
	 * 
	 * @return 上一次裁剪共裁剪了多少字符。
	 */
	public int getLastEatCount() {
		return lastEatCount;
	}

	/**
	 * 设置上一次裁剪共裁剪了多少字符。这通常于外部代码将多个对此对象操作统一为1个的情况下使用。
	 * 
	 * @param lastEatCount
	 *            共裁剪了多少字符。
	 */
	public void setLastEatCount(int lastEatCount) {
		this.lastEatCount = lastEatCount;
	}

	/**
	 * 返回上一次裁剪掉的字符串。注意如果上一次裁剪失败，默认返回null。
	 * 
	 * @return 上一次剪裁掉的字符串。
	 */
	public String getLastEatString() {
		return lastEatString;
	}

	/**
	 * 设置上一次裁剪共裁剪了多少字符。这通常于外部代码将多个对此对象操作统一为1个的情况下使用。
	 * 
	 * @param lastEatString
	 *            最后剪裁掉的字符串。
	 */
	public void setLastEatString(String lastEatString) {
		this.lastEatString = lastEatString;
	}
}
