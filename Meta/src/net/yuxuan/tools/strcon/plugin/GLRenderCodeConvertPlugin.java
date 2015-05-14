package net.yuxuan.tools.strcon.plugin;

import java.util.regex.Pattern;

import net.yuxuan.utils.StringConsumer;

public class GLRenderCodeConvertPlugin extends BaseConvertPlugin {

    public GLRenderCodeConvertPlugin() {
        setName("GLRenderCodeConverter");
    }
    
    @Override
    public boolean process(StringConsumer sc, StringBuilder rb) {
        while(sc.eatEOF().isSuccess() == false) {
            String firstParsString = "";
            String secondParsString = "";
            
            rb.append("worldRenderer.addVertexWithUV(");
            sc.eatSpaces();
            if(sc.eatStrings("GL").isSuccess() == false) { return false; }
            if(sc.eatPattern(Pattern.compile("[0-9][0-9]")).isSuccess() == false) { return false; }
            sc.eatSpaces();
            if(sc.eatStrings(".").isSuccess() == false) { return false; }
            sc.eatSpaces();
            if(sc.eatStrings("glTexCoord2").isSuccess() == false) { return false; }
            if(sc.eatStrings("f", "d").isSuccess() == false) { return false; }
            sc.eatSpaces();
            if(sc.eatStrings("(").isSuccess() == false) { return false; }
            sc.eatSpaces();
            if(eatSingleNumberStm(sc).isSuccess() == false) { return false; }
            secondParsString += sc.getLastEatString() + ", ";
            sc.eatSpaces();
            if(sc.eatStrings(",").isSuccess() == false) { return false; }
            sc.eatSpaces();
            if(eatSingleNumberStm(sc).isSuccess() == false) { return false; }
            secondParsString += sc.getLastEatString();
            sc.eatSpaces();
            if(sc.eatStrings(")").isSuccess() == false) { return false; }
            sc.eatSpaces();
            if(sc.eatStrings(";").isSuccess() == false) { return false; }
            sc.eatSpaces();
            
            if(sc.eatStrings("GL").isSuccess() == false) { return false; }
            if(sc.eatPattern(Pattern.compile("[0-9][0-9]")).isSuccess() == false) { return false; }
            sc.eatSpaces();
            if(sc.eatStrings(".").isSuccess() == false) { return false; }
            sc.eatSpaces();
            if(sc.eatStrings("glVertex3").isSuccess() == false) { return false; }
            if(sc.eatStrings("f", "d").isSuccess() == false) { return false; }
            sc.eatSpaces();
            if(sc.eatStrings("(").isSuccess() == false) { return false; }
            sc.eatSpaces();
            if(eatSingleNumberStm(sc).isSuccess() == false) { return false; }
            firstParsString += sc.getLastEatString() + ", ";
            sc.eatSpaces();
            if(sc.eatStrings(",").isSuccess() == false) { return false; }
            sc.eatSpaces();
            if(eatSingleNumberStm(sc).isSuccess() == false) { return false; }
            firstParsString += sc.getLastEatString() + ", ";
            sc.eatSpaces();
            if(sc.eatStrings(",").isSuccess() == false) { return false; }
            sc.eatSpaces();
            if(eatSingleNumberStm(sc).isSuccess() == false) { return false; }
            firstParsString += sc.getLastEatString();
            sc.eatSpaces();
            if(sc.eatStrings(")").isSuccess() == false) { return false; }
            sc.eatSpaces();
            if(sc.eatStrings(";").isSuccess() == false) { return false; }
            sc.eatSpaces();
            rb.append(firstParsString);
            rb.append(", ");
            rb.append(secondParsString);
            rb.append(");\n");
        }
        return true;
    }
}
