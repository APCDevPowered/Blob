package net.yuxuan.utils;

public class Regular {
    public static final String JAVA_NUMBER = "((\\+|-)\\s*)?((((\\d+\\.\\d*)|(\\d*\\.\\d+)|(\\d+))(F|D|f|d)?)|(\\d+))";
    public static final String JAVA_STRING = "\"((\\\\(b|f|n|r|t|\"|\'|\\\\|u([0-9a-fA-F]){4}))|[^\\\\\"])*\"";
}
