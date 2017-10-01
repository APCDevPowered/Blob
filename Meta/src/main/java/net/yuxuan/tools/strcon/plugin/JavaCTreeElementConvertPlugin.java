package net.yuxuan.tools.strcon.plugin;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.yuxuan.utils.StringConsumer;

public class JavaCTreeElementConvertPlugin extends BaseConvertPlugin {

	public JavaCTreeElementConvertPlugin() {
		setName("JavaCTreeElementConverter");
	}

	private int indexOfUppercaseLetter(String string, int fromIndex) {
		Matcher matcher = Pattern.compile("\\p{javaUpperCase}").matcher(string);
		if (matcher.find(fromIndex)) {
			return matcher.start();
		} else {
			return -1;
		}
	}

	private void writeUppercaseJavaIdentifier(String identifier, StringBuilder rb) {
		int startSearchUppercaseLetterIndex = 0;
		int uppercaseLetterIndex = -1;
		boolean isFirstWord = true;
		while ((uppercaseLetterIndex = indexOfUppercaseLetter(identifier, startSearchUppercaseLetterIndex)) != -1) {
			rb.append(identifier.substring(startSearchUppercaseLetterIndex - (isFirstWord ? 0 : 1),
					(isFirstWord ? ++uppercaseLetterIndex : uppercaseLetterIndex)).toUpperCase());
			rb.append(isFirstWord ? "" : "_");
			startSearchUppercaseLetterIndex = uppercaseLetterIndex + 1;
			if (isFirstWord) {
				isFirstWord = false;
			}
		}
		rb.append(identifier.substring(startSearchUppercaseLetterIndex - (isFirstWord ? 0 : 1), identifier.length())
				.toUpperCase());
	}

	@Override
	public boolean process(StringConsumer sc, StringBuilder rb) {
		String treeName;
		String nodeVariableName;
		String pVariableName;
		// Method Head
		{
			sc.eatSpaces();
			if (sc.eatStrings("public").isSuccess() == false) {
				return false;
			}
			if (sc.eatSpaces().isSuccess() == false) {
				return false;
			}
			if (sc.eatStrings("R").isSuccess() == false) {
				return false;
			}
			if (sc.eatSpaces().isSuccess() == false) {
				return false;
			}
			if (sc.eatStrings("visit").isSuccess() == false) {
				return false;
			}
			if (sc.eatPattern(Pattern.compile("[a-zA-Z]+")).isSuccess() == false) {
				return false;
			}
			String treeNakedName = sc.getLastEatString();
			treeName = treeNakedName + "Tree";
			sc.eatSpaces();
			if (sc.eatStrings("(").isSuccess() == false) {
				return false;
			}
			sc.eatSpaces();
			if (sc.eatStrings(treeName).isSuccess() == false) {
				return false;
			}
			if (sc.eatSpaces().isSuccess() == false) {
				return false;
			}
			if (sc.eatPattern(Pattern.compile("[a-zA-Z]+")).isSuccess() == false) {
				return false;
			}
			nodeVariableName = sc.getLastEatString();
			sc.eatSpaces();
			if (sc.eatStrings(",").isSuccess() == false) {
				return false;
			}
			sc.eatSpaces();
			if (sc.eatStrings("P").isSuccess() == false) {
				return false;
			}
			if (sc.eatSpaces().isSuccess() == false) {
				return false;
			}
			if (sc.eatPattern(Pattern.compile("[a-zA-Z]+")).isSuccess() == false) {
				return false;
			}
			pVariableName = sc.getLastEatString();
			sc.eatSpaces();
			if (sc.eatStrings(")").isSuccess() == false) {
				return false;
			}
			sc.eatSpaces();
		}
		// MethodBody
		{
			sc.eatSpaces();
			if (sc.eatStrings("{").isSuccess() == false) {
				return false;
			}
			sc.eatSpaces();
			rb.append("public static final class " + treeName + "Element<E> ");
			rb.append("extends TreeElement<" + treeName + ", E> {\n");
			rb.append("\t/* @formatter:off */\n");
			// MethodCode
			{
				sc.eatSpaces();
				boolean isFinal = false;
				boolean hasResultVariable = false;
				String resultVariableName = null;
				elementLoop: while (!isFinal) {
					String elementName;
					if (!hasResultVariable) {
						switch (sc.eatStrings("R", "return").getMatchStringIndex()) {
						case 0: {
							hasResultVariable = true;
							if (sc.eatSpaces().isSuccess() == false) {
								return false;
							}
							if (sc.eatPattern(Pattern.compile("[a-zA-Z]+")).isSuccess() == false) {
								return false;
							}
							resultVariableName = sc.getLastEatString();
							sc.eatSpaces();
							if (sc.eatStrings("=").isSuccess() == false) {
								return false;
							}
							sc.eatSpaces();
							if (sc.eatStrings("scan").isSuccess() == false) {
								return false;
							}
							sc.eatSpaces();
							if (sc.eatStrings("(").isSuccess() == false) {
								return false;
							}
							sc.eatSpaces();
							if (sc.eatStrings(nodeVariableName).isSuccess() == false) {
								return false;
							}
							sc.eatSpaces();
							if (sc.eatStrings(".").isSuccess() == false) {
								return false;
							}
							sc.eatSpaces();
							if (sc.eatStrings("get").isSuccess() == false) {
								return false;
							}
							if (sc.eatPattern(Pattern.compile("[a-zA-Z]+")).isSuccess() == false) {
								return false;
							}
							elementName = sc.getLastEatString();
							sc.eatSpaces();
							if (sc.eatStrings("(").isSuccess() == false) {
								return false;
							}
							sc.eatSpaces();
							if (sc.eatStrings(")").isSuccess() == false) {
								return false;
							}
							sc.eatSpaces();
							if (sc.eatStrings(",").isSuccess() == false) {
								return false;
							}
							sc.eatSpaces();
							if (sc.eatStrings(pVariableName).isSuccess() == false) {
								return false;
							}
							sc.eatSpaces();
							if (sc.eatStrings(")").isSuccess() == false) {
								return false;
							}
							sc.eatSpaces();
							if (sc.eatStrings(";").isSuccess() == false) {
								return false;
							}
							sc.eatSpaces();
							break;
						}
						case 1: {
							if (sc.eatSpaces().isSuccess() == false) {
								return false;
							}
							switch (sc.eatStrings("null", "scan").getMatchStringIndex()) {
							case 0: {
								sc.eatSpaces();
								if (sc.eatStrings(";").isSuccess() == false) {
									return false;
								}
								sc.eatSpaces();
								break elementLoop;
							}
							case 1: {
								sc.eatSpaces();
								if (sc.eatStrings("(").isSuccess() == false) {
									return false;
								}
								sc.eatSpaces();
								if (sc.eatStrings(nodeVariableName).isSuccess() == false) {
									return false;
								}
								sc.eatSpaces();
								if (sc.eatStrings(".").isSuccess() == false) {
									return false;
								}
								sc.eatSpaces();
								if (sc.eatStrings("get").isSuccess() == false) {
									return false;
								}
								if (sc.eatPattern(Pattern.compile("[a-zA-Z]+")).isSuccess() == false) {
									return false;
								}
								elementName = sc.getLastEatString();
								sc.eatSpaces();
								if (sc.eatStrings("(").isSuccess() == false) {
									return false;
								}
								sc.eatSpaces();
								if (sc.eatStrings(")").isSuccess() == false) {
									return false;
								}
								sc.eatSpaces();
								if (sc.eatStrings(",").isSuccess() == false) {
									return false;
								}
								sc.eatSpaces();
								if (sc.eatStrings(pVariableName).isSuccess() == false) {
									return false;
								}
								sc.eatSpaces();
								if (sc.eatStrings(")").isSuccess() == false) {
									return false;
								}
								sc.eatSpaces();
								if (sc.eatStrings(";").isSuccess() == false) {
									return false;
								}
								sc.eatSpaces();
								isFinal = true;
								break;
							}
							default:
								return false;
							}
							break;
						}
						default:
							return false;
						}
					} else {
						switch (sc.eatStrings("return", "for", resultVariableName).getMatchStringIndex()) {
						case 0: {
							if (sc.eatSpaces().isSuccess() == false) {
								return false;
							}
							if (sc.eatStrings(resultVariableName).isSuccess() == false) {
								return false;
							}
							sc.eatSpaces();
							if (sc.eatStrings(";").isSuccess() == false) {
								return false;
							}
							sc.eatSpaces();
							break elementLoop;
						}
						case 1: {
							sc.eatSpaces();
							if (sc.eatStrings("(").isSuccess() == false) {
								return false;
							}
							sc.eatSpaces();
							if (sc.eatStrings("Iterable").isSuccess() == false) {
								return false;
							}
							sc.eatSpaces();
							if (sc.eatStrings("<").isSuccess() == false) {
								return false;
							}
							sc.eatSpaces();
							if (sc.eatStrings("?").isSuccess() == false) {
								return false;
							}
							if (sc.eatSpaces().isSuccess() == false) {
								return false;
							}
							if (sc.eatStrings("extends").isSuccess() == false) {
								return false;
							}
							if (sc.eatSpaces().isSuccess() == false) {
								return false;
							}
							if (sc.eatStrings("Tree").isSuccess() == false) {
								return false;
							}
							sc.eatSpaces();
							if (sc.eatStrings(">").isSuccess() == false) {
								return false;
							}
							sc.eatSpaces();
							if (sc.eatPattern(Pattern.compile("[a-zA-Z]+")).isSuccess() == false) {
								return false;
							}
							String forVariableName = sc.getLastEatString();
							sc.eatSpaces();
							if (sc.eatStrings(":").isSuccess() == false) {
								return false;
							}
							sc.eatSpaces();
							if (sc.eatStrings(nodeVariableName).isSuccess() == false) {
								return false;
							}
							sc.eatSpaces();
							if (sc.eatStrings(".").isSuccess() == false) {
								return false;
							}
							sc.eatSpaces();
							if (sc.eatStrings("get").isSuccess() == false) {
								return false;
							}
							if (sc.eatPattern(Pattern.compile("[a-zA-Z]+")).isSuccess() == false) {
								return false;
							}
							elementName = sc.getLastEatString();
							sc.eatSpaces();
							if (sc.eatStrings("(").isSuccess() == false) {
								return false;
							}
							sc.eatSpaces();
							if (sc.eatStrings(")").isSuccess() == false) {
								return false;
							}
							sc.eatSpaces();
							if (sc.eatStrings(")").isSuccess() == false) {
								return false;
							}
							sc.eatSpaces();
							if (sc.eatStrings("{").isSuccess() == false) {
								return false;
							}
							sc.eatSpaces();
							if (sc.eatStrings(resultVariableName).isSuccess() == false) {
								return false;
							}
							sc.eatSpaces();
							if (sc.eatStrings("=").isSuccess() == false) {
								return false;
							}
							sc.eatSpaces();
							if (sc.eatStrings("scanAndReduce").isSuccess() == false) {
								return false;
							}
							sc.eatSpaces();
							if (sc.eatStrings("(").isSuccess() == false) {
								return false;
							}
							sc.eatSpaces();
							if (sc.eatStrings(forVariableName).isSuccess() == false) {
								return false;
							}
							sc.eatSpaces();
							if (sc.eatStrings(",").isSuccess() == false) {
								return false;
							}
							sc.eatSpaces();
							if (sc.eatStrings(pVariableName).isSuccess() == false) {
								return false;
							}
							sc.eatSpaces();
							if (sc.eatStrings(",").isSuccess() == false) {
								return false;
							}
							sc.eatSpaces();
							if (sc.eatStrings(resultVariableName).isSuccess() == false) {
								return false;
							}
							sc.eatSpaces();
							if (sc.eatStrings(")").isSuccess() == false) {
								return false;
							}
							sc.eatSpaces();
							if (sc.eatStrings(";").isSuccess() == false) {
								return false;
							}
							sc.eatSpaces();
							if (sc.eatStrings("}").isSuccess() == false) {
								return false;
							}
							sc.eatSpaces();
							break;
						}
						case 2: {
							sc.eatSpaces();
							if (sc.eatStrings("=").isSuccess() == false) {
								return false;
							}
							sc.eatSpaces();
							if (sc.eatStrings("scanAndReduce").isSuccess() == false) {
								return false;
							}
							sc.eatSpaces();
							if (sc.eatStrings("(").isSuccess() == false) {
								return false;
							}
							sc.eatSpaces();
							if (sc.eatStrings(nodeVariableName).isSuccess() == false) {
								return false;
							}
							sc.eatSpaces();
							if (sc.eatStrings(".").isSuccess() == false) {
								return false;
							}
							sc.eatSpaces();
							if (sc.eatStrings("get").isSuccess() == false) {
								return false;
							}
							if (sc.eatPattern(Pattern.compile("[a-zA-Z]+")).isSuccess() == false) {
								return false;
							}
							elementName = sc.getLastEatString();
							sc.eatSpaces();
							if (sc.eatStrings("(").isSuccess() == false) {
								return false;
							}
							sc.eatSpaces();
							if (sc.eatStrings(")").isSuccess() == false) {
								return false;
							}
							sc.eatSpaces();
							if (sc.eatStrings(",").isSuccess() == false) {
								return false;
							}
							sc.eatSpaces();
							if (sc.eatStrings(pVariableName).isSuccess() == false) {
								return false;
							}
							sc.eatSpaces();
							if (sc.eatStrings(",").isSuccess() == false) {
								return false;
							}
							sc.eatSpaces();
							if (sc.eatStrings(resultVariableName).isSuccess() == false) {
								return false;
							}
							sc.eatSpaces();
							if (sc.eatStrings(")").isSuccess() == false) {
								return false;
							}
							sc.eatSpaces();
							if (sc.eatStrings(";").isSuccess() == false) {
								return false;
							}
							sc.eatSpaces();
							break;
						}
						default:
							return false;
						}
					}
					rb.append("\tpublic static final " + treeName + "Element" + "<DYNAMIC_TYPE>\n");
					rb.append("\t\t");
					writeUppercaseJavaIdentifier(elementName, rb);
					rb.append(" = new " + treeName + "Element" + "<>(\n");
					rb.append("\t\t\tnew TypeConcreter<DYNAMIC_TYPE>() {}.getRawClass()\n");
					rb.append("\t\t);\n");
				}
				sc.eatSpaces();
			}
			rb.append("\t/* @formatter:on */\n\n");
			rb.append("\tpublic " + treeName + "Element" + "(Class<E> elementClass) {\n");
			rb.append("\t\tsuper(" + treeName + ".class, elementClass);\n");
			rb.append("\t}\n");
			rb.append("}\n");
			sc.eatSpaces();
			if (sc.eatStrings("}").isSuccess() == false) {
				return false;
			}
			sc.eatSpaces();
		}
		sc.eatEOF();
		return true;
	}
}
