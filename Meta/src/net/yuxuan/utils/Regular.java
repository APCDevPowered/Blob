package net.yuxuan.utils;

public class Regular {
    public static final String JAVA_NUMBER = "((?<numberSign>\\+|-)\\s*)?(?<numberBodyWithTypeSuffix>(?<numberBody>(?<fractionNumber>(?<fractionNumberIntegerPartOnly>\\d+\\.)|(?<fractionNumberFractionalPartOnly>\\.\\d+)|(?<fractionNumberFull>\\d+\\.\\d+))|(?<integerNumber>\\d+))(?<TypeSuffix>F|D|f|d)?)";
    public static final String JAVA_STRING = "\"((\\\\(b|f|n|r|t|\"|\'|\\\\|u([0-9a-fA-F]){4}))|[^\\\\\"])*\"";
}
