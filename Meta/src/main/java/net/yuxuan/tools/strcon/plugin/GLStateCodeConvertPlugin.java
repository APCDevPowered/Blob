package net.yuxuan.tools.strcon.plugin;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.yuxuan.utils.Regular;
import net.yuxuan.utils.StringConsumer;
import net.yuxuan.utils.StringConsumer.PatternEatResult;

public class GLStateCodeConvertPlugin extends BaseConvertPlugin {

    public GLStateCodeConvertPlugin() {
        setName("GLStateCodeConverter");
    }

    @Override
    public boolean process(StringConsumer sc, StringBuilder rb) {
        while(sc.eatEOF().isSuccess() == false) {
            rb.append("GlStateManager.");
            sc.eatSpaces();
            if(sc.eatStrings("GL").isSuccess() == false) { return false; }
            if(sc.eatPattern(Pattern.compile("[0-9][0-9]")).isSuccess() == false) { return false; }
            sc.eatSpaces();
            if(sc.eatStrings(".").isSuccess() == false) { return false; }
            sc.eatSpaces();
            if(sc.eatStrings("gl").isSuccess() == false) { return false; }
            if(sc.eatPattern(Pattern.compile("[a-zA-Z_$][a-zA-Z0-9_$]*")).isSuccess() == false) { return false; }
            String methodName = sc.getLastEatString();
            methodName = Character.toLowerCase(methodName.charAt(0)) + methodName.substring(1);
            sc.eatSpaces();
            if(sc.eatStrings("(").isSuccess() == false) { return false; }
            sc.eatSpaces();
            if (methodName.equals("disable") || methodName.equals("enable")) {
                if(sc.eatStrings("GL").isSuccess() == false) { return false; }
                if(sc.eatPattern(Pattern.compile("[0-9][0-9]")).isSuccess() == false) { return false; }
                sc.eatSpaces();
                if(sc.eatStrings(".").isSuccess() == false) { return false; }
                sc.eatSpaces();
                if(sc.eatStrings("GL_").isSuccess() == false) { return false; }
                if(sc.eatPattern(Pattern.compile("[a-zA-Z_$][a-zA-Z0-9_$]*")).isSuccess() == false) { return false; }
                String capability = sc.getLastEatString();
                String[] capabilitySplitByUnderline = capability.split("_");
                capability = "";
                for(String capabilitySplitEntry : capabilitySplitByUnderline)
                {
                    if(capabilitySplitEntry.matches("([1-4](B|I|F|D))"))
                    {
                        capability += capabilitySplitEntry;
                    }
                    else
                    {
                        capability += capabilitySplitEntry.charAt(0) + capabilitySplitEntry.substring(1).toLowerCase();
                    }
                }
                rb.append(methodName);
                rb.append(capability);
                rb.append("();\n");
                sc.eatSpaces();
                if(sc.eatStrings(")").isSuccess() == false) { return false; }
                sc.eatSpaces();
                if(sc.eatStrings(";").isSuccess() == false) { return false; }
                sc.eatSpaces();
            } else {
                Pattern pattern = Pattern.compile("(?<twoChar>[1-4](b|i|f|d))|(?<oneChar>b|i|f|d)$");
                Matcher matcher = pattern.matcher(methodName);
                if (matcher.find()) {
                    if (matcher.group("twoChar") != null) {
                        methodName = methodName.substring(0, methodName.length() - 2);
                    }
                    if (matcher.group("oneChar") != null) {
                        methodName = methodName.substring(0, methodName.length() - 1);
                    }
                }
                rb.append(methodName);
                rb.append("(");
                if(sc.eatPattern(Pattern.compile("(?<parameter>(" + Regular.JAVA_STRING + "|[^\"])*?)\\s*\\)\\s*;\\s*")).isSuccess() == false) { return false; }
                rb.append(((PatternEatResult) sc.getLastEatResult()).getMatcher().group("parameter"));
                rb.append(");\n");
            }
        }
        rb.deleteCharAt(rb.length() - 1);
        return true;
    }
}
