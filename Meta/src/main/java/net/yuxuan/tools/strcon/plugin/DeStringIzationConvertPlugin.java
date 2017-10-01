package net.yuxuan.tools.strcon.plugin;

import java.util.regex.Pattern;

import net.yuxuan.utils.Regular;
import net.yuxuan.utils.StringConsumer;

public class DeStringIzationConvertPlugin extends BaseConvertPlugin {

	private static final int[] HEX_CODE_POINTS = toCodePointArray("0123456789ABCDEFabcdef");

	public DeStringIzationConvertPlugin() {
		setName("DeStringIzationConverter");
	}

	private static int[] toCodePointArray(String string) {
		int codePointCount = string.codePointCount(0, string.length());
		int[] codePointArray = new int[codePointCount];
		for (int i = 0; i < codePointCount; i++) {
			codePointArray[i] = string.codePointAt(i);
		}
		return codePointArray;
	}

	private boolean containsCodePoint(int[] codePoints, int codePoint) {
		for (int current : codePoints) {
			if (codePoint == current) return true;
		}
		return false;
	}

	@Override
	public boolean process(StringConsumer sc, StringBuilder rb) {
        sc.eatSpaces();
        if (sc.eatPattern(Pattern.compile(Regular.JAVA_STRING)).isSuccess() == false) { return false; }
        String string = sc.getLastEatString();
        String stringValue = string.substring(1, string.length() - 1);
        int codePointCount = stringValue.codePointCount(0, stringValue.length());
        int codePointIndex = 0;
        StringBuilder builder = new StringBuilder();
        while (codePointIndex < codePointCount)
        {
            int currentCodePoint = stringValue.codePointAt(codePointIndex);
            codePointIndex++;
            if (currentCodePoint == '\\')
            {
                int nextCodePoint = stringValue.codePointAt(codePointIndex);
                codePointIndex++;
                switch (nextCodePoint)
                {
	                case 'n':
	                	builder.append('\n');
	                	break;
	                case 'r':
	                	builder.append('\r');
	                	break;
	                case 't':
	                	builder.append('\t');
	                	break;
	                case '"':
	                	builder.append('"');
	                	break;
	                case '\'':
	                	builder.append('\'');
	                	break;
	                case '\\':
	                	builder.append('\\');
	                	break;
                    case 'b':
                        builder.append('\b');
                        break;
                    case 'f':
                        builder.append('\f');
                        break;
                    case 'u':
                        int codePoint1 = stringValue.codePointAt(codePointIndex + 0);
                        int codePoint2 = stringValue.codePointAt(codePointIndex + 1);
                        int codePoint3 = stringValue.codePointAt(codePointIndex + 2);
                        int codePoint4 = stringValue.codePointAt(codePointIndex + 3);
                        if (containsCodePoint(HEX_CODE_POINTS, codePoint1) == false) return false;
                        if (containsCodePoint(HEX_CODE_POINTS, codePoint2) == false) return false;
                        if (containsCodePoint(HEX_CODE_POINTS, codePoint3) == false) return false;
                        if (containsCodePoint(HEX_CODE_POINTS, codePoint4) == false) return false;
                        StringBuilder unicodeCodeBuilder = new StringBuilder();
                        unicodeCodeBuilder.appendCodePoint(codePoint1);
                        unicodeCodeBuilder.appendCodePoint(codePoint2);
                        unicodeCodeBuilder.appendCodePoint(codePoint3);
                        unicodeCodeBuilder.appendCodePoint(codePoint4);
                        builder.appendCodePoint(Integer.valueOf(unicodeCodeBuilder.toString(), 16).intValue());
                        codePointIndex += 4;
                        break;
                    default:
                    	return false;
                }
            }
            else
            {
                builder.appendCodePoint(currentCodePoint);
            }
        }
        rb.append(builder.toString());
        sc.eatSpaces();
        if(sc.eatEOF().isSuccess() == false) { return false; }
        return true;
    }
}
