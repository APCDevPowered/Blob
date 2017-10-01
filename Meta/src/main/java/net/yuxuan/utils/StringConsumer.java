package net.yuxuan.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * {@code StringConsumer}
 * 类用于表示一个字符串剪裁器，将按要求尝试从提供的字符串头部剪裁部分字符，并返回剪裁的结果。用于对字符串进行解析，并根据结果进行相关操作。
 * 
 * @author yuxuanchiadm
 * @see java.lang.String
 * @see java.util.regex.Pattern
 */
public class StringConsumer {
    /** 用于裁剪的字符串。 */
    private String originalString;
    /** 当前剪裁位置。 */
    private int pointer = 0; // 默认为0
    /** 上一次剪裁共消耗多少字符。 */
    private int lastEatCount = -1; // 默认为-1
    /** 上一次剪裁的结果。 */
    private EatResult lastEatResult = null; // 默认为null

    /**
     * 初始化一个新的字符串剪裁器。
     * 
     * @param originalString
     *            提供给字符串剪裁器的字符串。
     * 
     * @throws NullPointerException
     *             如果{@code originalString}为null。
     */
    public StringConsumer(String originalString) {
        setOriginalString(originalString);
    }

    /**
     * 从字符串头部匹配传入的字符串数组中的所有字符串，如果某一个匹配 则将匹配部分裁剪，并返回第几个字符串匹配，如果任何一个字符串都
     * 不匹配，则返回-1。
     * 
     * @param stringArray
     *            将进行匹配的字符串数组。
     * 
     * @return 匹配及剪裁的结果。
     */
    public StringEatResult eatStrings(String... strings) {
        setLastEatCount(-1);
        for (int i = 0; i < strings.length; i++) {
            if (getOriginalString().startsWith(strings[i], getPointer())) {
                setPointer(strings[i].length() + getPointer());
                setLastEatCount(strings[i].length());
                StringEatResult stringEatResult = new StringEatResult(true,
                        strings, i);
                setLastEatResult(stringEatResult);
                return stringEatResult;
            }
        }
        StringEatResult stringEatResult = new StringEatResult(false, strings);
        setLastEatResult(stringEatResult);
        return stringEatResult;
    }

    /**
     * 从字符串头部匹配传入的正则表达式数组中的所有正则表达式，如果某一个匹配则将匹配部分裁剪，并返回第几个正则表达式匹配，如果
     * 任何一个字符串都不匹配，则返回-1。
     * 
     * @param stringArray
     *            将进行匹配的正则表达式数组。
     * 
     * @return 匹配及剪裁的结果。
     */
    public PatternEatResult eatPattern(Pattern... patterns) {
        setLastEatCount(-1);
        for (int i = 0; i < patterns.length; i++) {
            Pattern pattern = patterns[i];
            Matcher matcher = pattern.matcher(getOriginalString());
            matcher.region(getPointer(), getOriginalString().length());
            if (matcher.lookingAt()) {
                int eatCount = matcher.end() - getPointer();
                setPointer(eatCount + getPointer());
                setLastEatCount(eatCount);
                PatternEatResult patternEatResult = new PatternEatResult(true,
                        patterns, i, matcher);
                setLastEatResult(patternEatResult);
                return patternEatResult;
            }
        }
        PatternEatResult patternEatResult = new PatternEatResult(false,
                patterns);
        setLastEatResult(patternEatResult);
        return patternEatResult;
    }

    /**
     * 从字符串头部匹配任意个空格，如果匹配到至少1个空格，则将匹配部分裁剪，并返回true，否则返回false。空格字符为' '、'\n'、'\r'和
     * '\t'。
     * 
     * @return 匹配及剪裁的结果。
     */
    public EatResult eatSpaces() {
        setLastEatCount(-1);
        int pos;
        for (pos = 0; pos + getPointer() < getOriginalString().length(); pos++) {
            char c = getOriginalString().charAt(pos + getPointer());
            if (c != ' ' && c != '\n' && c != '\r' && c != '\t') {
                break;
            }
        }
        if (pos == 0) {
            EatResult eatResult = new EatResult(false);
            setLastEatResult(eatResult);
            return eatResult;
        }
        setPointer(pos + getPointer());
        setLastEatCount(pos);
        EatResult eatResult = new EatResult(true);
        setLastEatResult(eatResult);
        return eatResult;
    }

    /**
     * 从字符串头部匹配一个EOF，如果匹配，则返回true，否则返回false。注意此函数不会剪裁任何字符，并成功时设置
     * <code>{@link #getLastEatCount()}</code> 返回的值为0，否则为-1。如果单次调用返回true，则
     * <code>{@link #getLastEatString()}</code>返回空字符串，而且在不改变状态的情况下，多次调用也返回true。
     * 
     * @return 匹配及剪裁的结果。
     */
    public EatResult eatEOF() {
        setLastEatCount(-1);
        if (getPointer() < getOriginalString().length()) {
            EatResult eatResult = new EatResult(false);
            setLastEatResult(eatResult);
            return eatResult;
        }
        setLastEatCount(0);
        EatResult eatResult = new EatResult(true);
        setLastEatResult(eatResult);
        return eatResult;
    }

    /**
     * 返回用于裁剪的字符串。
     * 
     * @return 当前用于裁剪的字符串。
     */
    public String getOriginalString() {
        return originalString;
    }

    /**
     * 设置用于裁剪的字符串。注意这会重置<code>{@link #getPointer()}</code>返回的值为0，
     * <code>{@link #getLastEatCount()}</code>返回的值为-1，
     * <code>{@link #getLastEatString()}</code>返回的值为null。
     * <code>{@link #getLastEatResult()}</code>返回的值为null。
     * 
     * @param originalString
     *            用于裁剪的字符串。
     * 
     * @throws NullPointerException
     *             如果{@code originalString}为null。
     */
    public void setOriginalString(String originalString) {
        if (originalString == null) {
            throw new NullPointerException();
        }
        this.originalString = originalString;
        setPointer(0);
        setLastEatCount(-1);
        setLastEatResult(null);
    }

    /**
     * 获取当前剪裁位置。
     * 
     * @return 当前剪裁位置。
     */
    public int getPointer() {
        return Math.min(pointer, getOriginalString().length()); // 安全第一
    }

    /**
     * 设置当前剪裁位置。
     * 
     * @param pointer
     *            当前剪裁位置。
     * 
     * @throws IndexOutOfBoundsException
     *             如果{@code pointer}小于0，或者大于{@code originalString}的长度。
     */
    public void setPointer(int pointer) {
        if (pointer < 0 || pointer > getOriginalString().length()) {
            throw new IndexOutOfBoundsException();
        }
        this.pointer = pointer;
    }

    /**
     * 返回被剪裁过的字符串。
     * 
     * @return 剪裁过的字符串。
     */
    public String getCuttedString() {
        return getOriginalString().substring(getPointer());
    }

    /**
     * 返回上一次裁剪共裁剪了多少字符。注意如果上一次裁剪失败，返回-1。
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
     * 
     * @throws IndexOutOfBoundsException
     *             如果{@code lastEatCount}小于0且不等于-1，或者大于{@code pointer}的长度。
     */
    public void setLastEatCount(int lastEatCount) {
        if ((lastEatCount < 0 && lastEatCount != -1)
                || lastEatCount > getPointer()) {
            throw new IndexOutOfBoundsException();
        }
        this.lastEatCount = lastEatCount;
    }

    /**
     * 返回上一次裁剪掉的字符串。注意如果上一次裁剪失败，默认返回null。此函数的返回值根据{@code lastEatCount}计算得出。
     * 
     * @return 上一次剪裁掉的字符串。
     */
    public String getLastEatString() {
        if (getLastEatCount() == -1) {
            return null;
        }
        return getOriginalString().substring(getPointer() - getLastEatCount(),
                getPointer());
    }

    /**
     * 返回上一次剪裁的结果。注意如果上一次裁剪失败，默认返回null。
     * 
     * @return 上一次剪裁的结果。
     */
    public EatResult getLastEatResult() {
        return lastEatResult;
    }
    
    /**
     * 返回上一次剪裁的结果。注意如果上一次裁剪失败，默认返回null。
     * 
     * @param resultClass 上一次剪裁结果的类型.
     * @return 上一次剪裁的结果。
     */
    public <T> T getLastEatResult(Class<T> resultClass) {
    	return resultClass.cast(getLastEatResult());
    }

    /**
     * 设置上一次剪裁的结果。
     * 
     * @param lastEatResult
     *            剪裁的结果。
     */
    public void setLastEatResult(EatResult lastEatResult) {
        this.lastEatResult = lastEatResult;
    }

    /**
     * {@code EatResult} 类用于表示字符串消耗器的单次剪裁结果。
     * 
     * @author yuxuanchiadm
     * @see net.yuxuan.utils.StringConsumer
     */
    public class EatResult {
        /** 是否成功剪裁字符串。 */
        private final boolean isSuccess;
        /** 剪裁掉的字符数量。 */
        private final int eatCount;
        /** 剪裁掉的字符串。 */
        private final String eatString;

        /**
         * 初始化一个新的字符串剪裁器剪裁结果。
         * 
         * @param isSuccess
         *            是否成功消耗字符串。
         */
        public EatResult(boolean isSuccess) {
            this.isSuccess = isSuccess;
            this.eatCount = getLastEatCount();
            this.eatString = getLastEatString();
        }

        /**
         * 初始化一个新的字符串剪裁器剪裁结果。
         * 
         * @param isSuccess
         *            是否成功消耗字符串。
         * 
         * @param eatCount
         *            剪裁掉的字符数量。
         * 
         * @param eatString
         *            剪裁掉的字符串。
         */
        public EatResult(boolean isSuccess, int eatCount, String eatString) {
            this.isSuccess = isSuccess;
            this.eatCount = eatCount;
            this.eatString = eatString;
        }

        /**
         * 返回是否成功剪裁字符串。
         * 
         * @return 是否成功剪裁字符串。
         */
        public boolean isSuccess() {
            return isSuccess;
        }

        /**
         * 返回剪裁掉的字符数量。
         * 
         * @return 剪裁掉的字符数量。
         */
        public int getEatCount() {
            return eatCount;
        }

        /**
         * 剪裁掉的字符串。
         * 
         * @return 剪裁掉的字符串。
         */
        public String getEatString() {
            return eatString;
        }
    }

    /**
     * {@code StringEatResult} 类用于表示字符串消耗器的单次字符串剪裁结果。
     * 
     * @author yuxuanchiadm
     * @see net.yuxuan.utils.StringConsumer
     */
    public class StringEatResult extends EatResult {
        /** 所有字符串。 */
        private final String[] strings;
        /** 匹配的字符串索引位置。 */
        private final int matchStringIndex;

        /**
         * 初始化一个新的字符串剪裁器剪裁结果。
         * 
         * @param isSuccess
         *            是否成功消耗字符串。
         * 
         * @param strings
         *            所有字符串。
         */
        public StringEatResult(boolean isSuccess, String[] strings) {
            super(isSuccess);
            this.strings = strings;
            this.matchStringIndex = -1;
        }

        /**
         * 初始化一个新的字符串剪裁器剪裁结果。
         * 
         * @param isSuccess
         *            是否成功消耗字符串。
         * 
         * @param strings
         *            所有字符串。
         * 
         * @param matchStringIndex
         *            匹配的字符串索引位置。
         */
        public StringEatResult(boolean isSuccess, String[] strings,
                int matchStringIndex) {
            super(isSuccess);
            this.strings = strings;
            this.matchStringIndex = matchStringIndex;
        }

        /**
         * 初始化一个新的字符串剪裁器剪裁结果。
         * 
         * @param isSuccess
         *            是否成功消耗字符串。
         * 
         * @param eatCount
         *            剪裁掉的字符数量。
         * 
         * @param eatString
         *            剪裁掉的字符串。
         * 
         * @param strings
         *            所有字符串。
         */
        public StringEatResult(boolean isSuccess, int eatCount,
                String eatString, String[] strings) {
            super(isSuccess, eatCount, eatString);
            this.strings = strings;
            this.matchStringIndex = -1;
        }

        /**
         * 初始化一个新的字符串剪裁器剪裁结果。
         * 
         * @param isSuccess
         *            是否成功消耗字符串。
         * 
         * @param eatCount
         *            剪裁掉的字符数量。
         * 
         * @param eatString
         *            剪裁掉的字符串。
         * 
         * @param strings
         *            所有字符串。
         * 
         * @param matchStringIndex
         *            匹配的字符串索引位置。
         */
        public StringEatResult(boolean isSuccess, int eatCount,
                String eatString, String[] strings, int matchStringIndex) {
            super(isSuccess, eatCount, eatString);
            this.strings = strings;
            this.matchStringIndex = matchStringIndex;
        }

        /**
         * 返回所有字符串。
         * 
         * @return 所有字符串。
         */
        public String[] getStrings() {
            return strings;
        }

        /**
         * 返回匹配的字符串索引位置。
         * 
         * @return 匹配的字符串索引位置。
         */
        public int getMatchStringIndex() {
            return matchStringIndex;
        }

        /**
         * 返回匹配的字符串。
         * 
         * @return 匹配的字符串。
         */
        public String getMatchString() {
            return strings[matchStringIndex];
        }
    }

    /**
     * {@code PatternEatResult} 类用于表示字符串消耗器的单次正则表达式剪裁结果。
     * 
     * @author yuxuanchiadm
     * @see net.yuxuan.utils.StringConsumer
     */
    public class PatternEatResult extends EatResult {
        /** 所有正则表达式。 */
        private final Pattern[] patterns;
        /** 匹配的正则表达式索引位置。 */
        private final int matchPatternIndex;
        /** 匹配器。 */
        private final Matcher matcher;

        /**
         * 初始化一个新的字符串剪裁器剪裁结果。
         * 
         * @param isSuccess
         *            是否成功消耗字符串。
         * 
         * @param patterns
         *            所有正则表达式。
         */
        public PatternEatResult(boolean isSuccess, Pattern[] patterns) {
            super(isSuccess);
            this.patterns = patterns;
            this.matchPatternIndex = -1;
            this.matcher = null;
        }

        /**
         * 初始化一个新的字符串剪裁器剪裁结果。
         * 
         * @param isSuccess
         *            是否成功消耗字符串。
         * 
         * @param patterns
         *            所有正则表达式。
         * 
         * @param matchPatternIndex
         *            匹配的正则表达式索引位置。
         * 
         * @param matcher
         *            匹配器。
         */
        public PatternEatResult(boolean isSuccess, Pattern[] patterns,
                int matchPatternIndex, Matcher matcher) {
            super(isSuccess);
            this.patterns = patterns;
            this.matchPatternIndex = matchPatternIndex;
            this.matcher = matcher;
        }

        /**
         * 初始化一个新的字符串剪裁器剪裁结果。
         * 
         * @param isSuccess
         *            是否成功消耗字符串。
         * 
         * @param eatCount
         *            剪裁掉的字符数量。
         * 
         * @param eatString
         *            剪裁掉的字符串。
         * 
         * @param patterns
         *            所有正则表达式。
         */
        public PatternEatResult(boolean isSuccess, int eatCount,
                String eatString, Pattern[] patterns) {
            super(isSuccess, eatCount, eatString);
            this.patterns = patterns;
            this.matchPatternIndex = -1;
            this.matcher = null;
        }

        /**
         * 初始化一个新的字符串剪裁器剪裁结果。
         * 
         * @param isSuccess
         *            是否成功消耗字符串。
         * 
         * @param eatCount
         *            剪裁掉的字符数量。
         * 
         * @param eatString
         *            剪裁掉的字符串。
         * 
         * @param patterns
         *            所有正则表达式。
         * 
         * @param matchPatternIndex
         *            匹配的正则表达式索引位置。
         * 
         * @param matcher
         *            匹配器。
         */
        public PatternEatResult(boolean isSuccess, int eatCount,
                String eatString, Pattern[] patterns, int matchPatternIndex,
                Matcher matcher) {
            super(isSuccess, eatCount, eatString);
            this.patterns = patterns;
            this.matchPatternIndex = matchPatternIndex;
            this.matcher = matcher;
        }

        /**
         * 返回所有正则表达式。
         * 
         * @return 所有正则表达式。
         */
        public Pattern[] getPatterns() {
            return patterns;
        }

        /**
         * 返回匹配的正则表达式索引位置。
         * 
         * @return 匹配的正则表达式索引位置。
         */
        public int getMatchPatternIndex() {
            return matchPatternIndex;
        }

        /**
         * 返回匹配的正则表达式。
         * 
         * @return 匹配的正则表达式。
         */
        public Pattern getMatchPattern() {
            return patterns[matchPatternIndex];
        }

        /**
         * 返回匹配器。
         * 
         * @return 匹配器。
         */
        public Matcher getMatcher() {
            return matcher;
        }
    }
}
