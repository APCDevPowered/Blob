package net.yuxuan.tools.strcon.plugin;

import java.awt.Component;
import java.util.regex.Pattern;

import net.yuxuan.utils.Regular;
import net.yuxuan.utils.StringConsumer;
import net.yuxuan.utils.StringConsumer.EatResult;

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

    public EatResult eatNumber(StringConsumer sc) {
        int lastEatCount = 0;
        
        if(sc.eatPattern(Pattern.compile(Regular.JAVA_NUMBER)).isSuccess() == false) {
            sc.setLastEatCount(0);
            EatResult eatResult = sc.new EatResult(false);
            sc.setEatResult(eatResult);
            return eatResult;
        }
        lastEatCount += maxWithZero(sc.getLastEatCount());
        
        sc.setLastEatCount(lastEatCount);
        EatResult eatResult = sc.new EatResult(true);
        sc.setEatResult(eatResult);
        return eatResult;
    }
    
    public EatResult eatSingleNumberStm(StringConsumer sc) {
        int lastEatCount = 0;
        
        sc.eatSpaces();
        lastEatCount += maxWithZero(sc.getLastEatCount());
        
        if (eatNumber(sc).isSuccess() == false) {
            sc.setLastEatCount(0);
            EatResult eatResult = sc.new EatResult(false);
            sc.setEatResult(eatResult);
            return eatResult;
        }
        lastEatCount += maxWithZero(sc.getLastEatCount());
        
        sc.eatSpaces();
        lastEatCount += maxWithZero(sc.getLastEatCount());
        
        if (sc.eatPattern(Pattern.compile("(\\+|-|\\*|/|%)")).isSuccess()) {
            lastEatCount += maxWithZero(sc.getLastEatCount());
            
            sc.eatSpaces();
            lastEatCount += maxWithZero(sc.getLastEatCount());
            
            if (eatNumber(sc).isSuccess() == false) {
                sc.setLastEatCount(0);
                EatResult eatResult = sc.new EatResult(false);
                sc.setEatResult(eatResult);
                return eatResult;
            }
            lastEatCount += maxWithZero(sc.getLastEatCount());
            
            sc.eatSpaces();
            lastEatCount += maxWithZero(sc.getLastEatCount());
        }
        
        sc.setLastEatCount(lastEatCount);
        EatResult eatResult = sc.new EatResult(true);
        sc.setEatResult(eatResult);
        return eatResult;
    }
    public int maxWithZero(int i) {
        return Math.max(i, 0);
    }
}
