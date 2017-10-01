package net.yuxuan.tools.strcon.plugin;

import java.util.regex.Pattern;

import net.yuxuan.utils.StringConsumer;

public class MultilineStringIzationConvertPlugin extends BaseConvertPlugin {

	private static final char[] HEX = "0123456789ABCDEF".toCharArray();

	public MultilineStringIzationConvertPlugin() {
		setName("MultilineStringIzationConvertPlugin");
	}

	@Override
	public boolean process(StringConsumer sc, StringBuilder sb) {
		sc.eatPattern(Pattern.compile("[\\s\\S]*"));
		String string = sc.getLastEatString();
		sb.append('\"');
		int codePointCount = string.codePointCount(0, string.length());
		for (int index = 0; index < codePointCount; index++) {
			int codePoint = string.codePointAt(index);
			switch (codePoint) {
			case '\n':
				sb.append("\" + \"\\n\" +\n\"");
				break;
			case '\r':
				if (index + 1 < codePointCount && string.codePointAt(index + 1) == '\n') {
					index++;
					sb.append("\" + \"\\r\\n\" +\n\"");
				} else
					sb.append("\" + \"\\r\" +\n\"");
				break;
			case '\t':
				sb.append("\\t");
				break;
			case '"':
				sb.append("\\\"");
				break;
			case '\'':
				sb.append("\\\'");
				break;
			case '\\':
				sb.append("\\\\");
				break;
			case '\b':
				sb.append("\\b");
				break;
			case '\f':
				sb.append("\\f");
				break;
			default:
				if (isCommonChar(codePoint)) {
					sb.appendCodePoint(codePoint);
				} else {
					char[] chars = Character.toChars(codePoint);
					for (char c : chars) {
						sb.append("\\u");
						int hex1 = ((c & 0xFFFF) >>> 12) & 0xF;
						int hex2 = ((c & 0xFFFF) >>> 8) & 0xF;
						int hex3 = ((c & 0xFFFF) >>> 4) & 0xF;
						int hex4 = ((c & 0xFFFF) >>> 0) & 0xF;
						sb.append(HEX[hex1]);
						sb.append(HEX[hex2]);
						sb.append(HEX[hex3]);
						sb.append(HEX[hex4]);
					}
				}
				break;
			}
		}
		sb.append('\"');
		if (sc.eatEOF().isSuccess() == false) {
			return false;
		}
		return true;
	}

	private static boolean isCommonChar(int codePoint) {
		switch (Character.getType(codePoint)) {
		case Character.UPPERCASE_LETTER:
		case Character.LOWERCASE_LETTER:
		case Character.TITLECASE_LETTER:
		case Character.MODIFIER_LETTER:
		case Character.OTHER_LETTER:
		case Character.DECIMAL_DIGIT_NUMBER:
		case Character.LETTER_NUMBER:
		case Character.OTHER_NUMBER:
		case Character.SPACE_SEPARATOR:
		case Character.LINE_SEPARATOR:
		case Character.PARAGRAPH_SEPARATOR:
		case Character.DASH_PUNCTUATION:
		case Character.START_PUNCTUATION:
		case Character.END_PUNCTUATION:
		case Character.CONNECTOR_PUNCTUATION:
		case Character.OTHER_PUNCTUATION:
		case Character.MATH_SYMBOL:
		case Character.CURRENCY_SYMBOL:
		case Character.MODIFIER_SYMBOL:
		case Character.OTHER_SYMBOL:
		case Character.INITIAL_QUOTE_PUNCTUATION:
		case Character.FINAL_QUOTE_PUNCTUATION:
			return true;
		default:
			return false;
		}
	}
}
