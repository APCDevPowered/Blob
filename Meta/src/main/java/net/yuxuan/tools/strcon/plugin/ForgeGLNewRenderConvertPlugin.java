package net.yuxuan.tools.strcon.plugin;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import net.yuxuan.utils.StringConsumer;
import net.yuxuan.utils.StringConsumer.PatternEatResult;

public class ForgeGLNewRenderConvertPlugin extends BaseConvertPlugin {
	public ForgeGLNewRenderConvertPlugin() {
		setName("ForgeGLNewRenderConvertPlugin");
	}

	@Override
	public boolean process(StringConsumer sc, StringBuilder rb) {
		String originalString = sc.getOriginalString();
		Type type = Type.getType(originalString);
		if (type == null) return false;
		sc.eatSpaces();
		rb.append(sc.getLastEatString());
		if (sc.eatStrings("worldRenderer").isSuccess() == false) return false;
		rb.append("worldRenderer");
		sc.eatSpaces();
		if (sc.eatStrings(".").isSuccess() == false) return false;
		rb.append(".");
		String beginMethodGLModeParameter;
		String beginMethodFormatParameter = type.getFormatString();
		switch(sc.eatStrings("startDrawingQuads", "startDrawing").getMatchString()) {
		case "startDrawingQuads":
			beginMethodGLModeParameter = "GL11.GL_QUADS";
			sc.eatSpaces();
			if (sc.eatStrings("(").isSuccess() == false) return false;
			sc.eatSpaces();
			if (sc.eatStrings(")").isSuccess() == false) return false;
			sc.eatSpaces();
			break;
		case "startDrawing":
			sc.eatSpaces();
			if (sc.eatStrings("(").isSuccess() == false) return false;
			sc.eatSpaces();
			if (sc.eatPattern(Pattern.compile("(?<beginMethodGLModeParameter>.*?)\\s*\\)")).isSuccess() == false) return false;
			beginMethodGLModeParameter = sc.getLastEatResult(PatternEatResult.class).getMatcher().group("beginMethodGLModeParameter");
			break;
		default:
			return false;
		}
		rb.append("begin(");
		rb.append(beginMethodGLModeParameter);
		rb.append(", ");
		rb.append(beginMethodFormatParameter);
		rb.append(")");
		sc.eatSpaces();
		if (sc.eatStrings(";").isSuccess() == false) return false;
		rb.append(";");
		
		while (true) {
			if (sc.eatSpaces().isSuccess()) rb.append(sc.getLastEatString());
			if (sc.eatStrings("worldRenderer").isSuccess() == false) {
				if (sc.eatEOF().isSuccess() == true) break;
				if (sc.eatPattern(Pattern.compile("[\\s\\S]*?(?=worldRenderer)")).isSuccess() == false) return false;
				rb.append(sc.getLastEatString());
				continue;
			}
			rb.append("worldRenderer");
			
			sc.eatSpaces();
			if (sc.eatStrings(".").isSuccess() == false) return false;
			sc.eatSpaces();
			if (sc.eatStrings(type.getOldMethodName()).isSuccess() == false) return false;
			sc.eatSpaces();
			if (sc.eatStrings("(").isSuccess() == false) return false;
			boolean hasComma = false;
			// pos
			if (type.getNewMethodTypeSet().contains(Type.NewMethodType.POS)) {
				if (hasComma) {
					sc.eatSpaces();
					if (sc.eatStrings(",").isSuccess() == false) return false;
					sc.eatSpaces();
					hasComma = false;
				}
				
				String posMethodXParameter;
				String posMethodYParameter;
				String posMethodZParameter;
				
				sc.eatSpaces();
				if (eatJavaComplexStm(sc).isSuccess() == false) return false;
				posMethodXParameter = sc.getLastEatString();
				sc.eatSpaces();
				
				if (sc.eatStrings(",").isSuccess() == false) return false;
				
				sc.eatSpaces();
				if (eatJavaComplexStm(sc).isSuccess() == false) return false;
				posMethodYParameter = sc.getLastEatString();
				sc.eatSpaces();
				
				if (sc.eatStrings(",").isSuccess() == false) return false;
				
				sc.eatSpaces();
				if (eatJavaComplexStm(sc).isSuccess() == false) return false;
				posMethodZParameter = sc.getLastEatString();
				sc.eatSpaces();
				
				rb.append(".pos(");
				rb.append(posMethodXParameter);
				rb.append(", ");
				rb.append(posMethodYParameter);
				rb.append(", ");
				rb.append(posMethodZParameter);
				rb.append(")");
				
				hasComma = true;
			}
			// tex
			if (type.getNewMethodTypeSet().contains(Type.NewMethodType.TEX)) {
				if (hasComma) {
					sc.eatSpaces();
					if (sc.eatStrings(",").isSuccess() == false) return false;
					sc.eatSpaces();
					hasComma = false;
				}
				
				String texMethodUParameter;
				String texMethodVParameter;
				
				sc.eatSpaces();
				if (eatJavaComplexStm(sc).isSuccess() == false) return false;
				texMethodUParameter = sc.getLastEatString();
				sc.eatSpaces();
				
				if (sc.eatStrings(",").isSuccess() == false) return false;
				
				sc.eatSpaces();
				if (eatJavaComplexStm(sc).isSuccess() == false) return false;
				texMethodVParameter = sc.getLastEatString();
				sc.eatSpaces();
				
				rb.append(".tex(");
				rb.append(texMethodUParameter);
				rb.append(", ");
				rb.append(texMethodVParameter);
				rb.append(")");
			}
			
			sc.eatSpaces();
			if (sc.eatStrings(")").isSuccess() == false) return false;
			sc.eatSpaces();
			if (sc.eatStrings(";").isSuccess() == false) return false;
			
			rb.append(".endVertex();");
		}
		return true;
	}

	private enum Type {
		VERTEX_ONLY(Collections.unmodifiableSet(new HashSet<NewMethodType>(Arrays.asList(
				NewMethodType.POS)))
				,"DefaultVertexFormats.POSITION", "addVertex"),
		VERTEX_TEXTURE(Collections.unmodifiableSet(new HashSet<NewMethodType>(Arrays.asList(
				NewMethodType.POS, NewMethodType.TEX)))
				,"DefaultVertexFormats.POSITION_TEX", "addVertexWithUV");
		
		private enum NewMethodType {
			POS, TEX
		}

		private final Set<NewMethodType> newMethodTypeSet;
		private final String formatString;
		private final String oldMethodName;

		private Type(Set<NewMethodType> newMethodTypeSet, String formatString, String oldMethodName) {
			this.newMethodTypeSet = newMethodTypeSet;
			this.formatString = formatString;
			this.oldMethodName = oldMethodName;
		}

		public static Type getType(String s) {
			if (Pattern.compile("\\.\\s*addVertexWithUV\\s*\\(").matcher(s).find())
				return VERTEX_TEXTURE;
			else if (Pattern.compile("\\.\\s*addVertex\\s*\\(").matcher(s).find())
				return VERTEX_ONLY;
			else
				return null;
		}

		public Set<NewMethodType> getNewMethodTypeSet() {
			return newMethodTypeSet;
		}
		
		public String getFormatString() {
			return formatString;
		}
		
		public String getOldMethodName() {
			return oldMethodName;
		}
	}
}
