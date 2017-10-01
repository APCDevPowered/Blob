package net.yuxuan.utils;

public class Regular {
	public static final String JAVA_NUMBER = "((?<numberSign>\\+|-)\\s*)?(?<numberBodyWithTypeSuffix>(?<numberBody>(?<fractionNumber>(?<fractionNumberIntegerPartOnly>\\d+\\.)|(?<fractionNumberFractionalPartOnly>\\.\\d+)|(?<fractionNumberFull>\\d+\\.\\d+))|(?<integerNumber>\\d+))(?<TypeSuffix>F|D|f|d)?)";
	// @formatter:off
    /*
     *  group {
     *  	token "\""
     *  	zeroOrMany #possessive {
     * 			group #or {
     * 				oneOrMany #possessive {
	 * 					choose #anti {
	 * 						token "\\"
	 * 						token "\""
	 * 					}
	 * 				}
	 * 				oneOrMany #possessive {
	 * 					group {
	 * 						token "\\"
	 * 						group #or {
	 * 							choose {
	 * 								token "n"
	 * 								token "r"
	 * 								token "t"
	 * 								token "\""
	 * 								token "\'"
	 * 								token "\\"
	 * 								token "b"
	 * 								token "f"
	 * 							}
	 * 							logicalGroup {
	 * 								token "u"
	 * 								repeat #const(4) {
	 * 									choose {
	 * 										tokenRange "0" "9"
	 * 										tokenRange "A" "F"
	 * 										tokenRange "a" "f"
	 * 									}
	 * 								}
	 * 							}
	 * 						}
	 * 					}
	 * 				}
     * 			}
     *  	}
     *  	token "\""
     *  }
     */
    // @formatter:on
	public static final String JAVA_STRING = "(\"([^\\\\\"]++|(\\\\([nrt\"\'\\\\bf]|u[0-9A-Fa-f]{4}))++)*+\")";
}
