package net.yuxuan.tools.strcon.plugin;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import net.yuxuan.utils.Regular;
import net.yuxuan.utils.StringConsumer;
import net.yuxuan.utils.StringConsumer.PatternEatResult;

public class NewBlockPosCodeConvertPlugin extends BaseConvertPlugin {

    public NewBlockPosCodeConvertPlugin() {
        setName("NewBlockPosCodeConverter");
    }

    @Override
    public boolean process(StringConsumer sc, StringBuilder rb) {
        mainloop: while (sc.eatEOF().isSuccess() == false) {
            StringBuffer sb = new StringBuffer();
            int startPointer = sc.getPointer();
            
            Map<String, String> methodNameConvertMap = new HashMap<String, String>();
            
            if(sc.eatPattern(Pattern.compile("(?<otherCode>[\\s\\S]+?)\\.\\s*(?<methodName>(getTileEntity|isAirBlock))\\s*\\(\\s*")).isSuccess() == false) {
                rb.append(sc.getCuttedString());
                rb.append("\n");
                break mainloop;
            }
            PatternEatResult methodPatternEatResult = (PatternEatResult) sc.getLastEatResult();
            String otherCode = methodPatternEatResult.getMatcher().group("otherCode");
            sb.append(otherCode);
            String methodName = methodPatternEatResult.getMatcher().group("methodName");
            String newMethodName = methodNameConvertMap.get(methodName);
            if(newMethodName == null) {
                newMethodName = methodName;
            }
            sb.append(".");
            sb.append(newMethodName);
            sb.append("(");
            String varName = null;
            String[] operatorArray = new String[3];
            String[] stmArray = new String[3];
            for(int i = 0;i < 3;i++) {
                if(sc.eatPattern(Pattern.compile("\\s*(?<varName>[a-zA-Z_$][a-zA-Z0-9_$]*)\\s*" + "\\.\\s*" + "(x|y|z)Coord\\s*")).isSuccess() == false) {
                    rb.append(sc.getOriginalString().substring(startPointer, sc.getPointer()));
                    continue mainloop;
                }
                PatternEatResult parPatternEatResult = (PatternEatResult) sc.getLastEatResult();
                varName = parPatternEatResult.getMatcher().group("varName");
                sc.eatPattern(Pattern.compile("(\\s*(?<operator>\\+|-)\\s*)" + "(?<stm>(" + Regular.JAVA_STRING + "|[^\"])*?)\\s*" + (i == 2 ? "(?=\\))" : "(?=,)")));
                PatternEatResult stmPatternEatResult = (PatternEatResult) sc.getLastEatResult();
                if(stmPatternEatResult.isSuccess())
                {
                    String operator = stmPatternEatResult.getMatcher().group("operator");
                    String stm = stmPatternEatResult.getMatcher().group("stm");
                    operatorArray[i] = operator;
                    stmArray[i] = stm;
                }
                sc.eatSpaces();
                if(i != 2)
                {
                    sc.eatStrings(",");
                    sc.eatSpaces();
                }
            }
            sb.append(varName);
            sb.append(".getPos().add(");
            for(int i = 0;i < 3;i++) {
                if(operatorArray[i] == null) {
                    sb.append("0");
                }
                else {
                    if(operatorArray[i].equals("-")) {
                        sb.append("-");
                    }
                    if(stmArray[i].matches(Regular.JAVA_NUMBER)) {
                        sb.append(stmArray[i]);
                    }
                    else {
                        sb.append("(");
                        sb.append(stmArray[i]);
                        sb.append(")");
                    }
                }
                if(i != 2) {
                    sb.append(", ");
                }
            }
            sb.append(")");
            sc.eatSpaces();
            if(sc.eatStrings(")").isSuccess() == false) {
                rb.append(sc.getOriginalString().substring(startPointer, sc.getPointer()));
                continue mainloop;
            }
            sc.eatSpaces();
            sb.append(")");
            rb.append(sb);
        }
        rb.deleteCharAt(rb.length() - 1);
        return true;
    }
}
