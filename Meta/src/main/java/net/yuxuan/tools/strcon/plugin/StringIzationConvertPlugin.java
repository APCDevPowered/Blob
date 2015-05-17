package net.yuxuan.tools.strcon.plugin;

import java.util.regex.Pattern;

import net.yuxuan.utils.StringConsumer;

public class StringIzationConvertPlugin extends BaseConvertPlugin {

    public StringIzationConvertPlugin() {
        setName("StringIzationConverter");
    }

    @Override
    public boolean process(StringConsumer sc, StringBuilder rb) {
        sc.eatPattern(Pattern.compile("[\\s\\S]*"));
        String string = sc.getLastEatString();
        StringBuilder builder = new StringBuilder();
        int codePointCount = string.codePointCount(0, string.length());
        for (int index = 0; index < codePointCount; index++) {
            int c = string.codePointAt(index);
            if (c == Character.getNumericValue('\\')) {
                builder.append("\\\\");
            } else if (c == '\r') {
                builder.append("\\r");
            } else if (c == '\n') {
                builder.append("\\n");
            } else if (c == '\b') {
                builder.append("\\b");
            } else if (c == '\f') {
                builder.append("\\f");
            } else {
                builder.appendCodePoint(c);
            }
        }
        rb.append('\"');
        rb.append(builder.toString());
        rb.append('\"');
        if(sc.eatEOF().isSuccess() == false) { return false; }
        return true;
    }
}
