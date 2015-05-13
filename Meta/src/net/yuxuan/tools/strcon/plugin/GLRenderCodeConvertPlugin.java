package net.yuxuan.tools.strcon.plugin;

import net.yuxuan.utils.StringConsumer;

public class GLRenderCodeConvertPlugin extends BaseConvertPlugin {

	public GLRenderCodeConvertPlugin() {
		setName("GLRenderCodeConverter");
	}
	
	@Override
	public boolean process(StringConsumer sc, StringBuilder rb) {
		while(!sc.eatEOF()) {
			String firstParsString = "";
			String secondParsString = "";
			
			rb.append("worldRenderer.addVertexWithUV(");
			sc.eatSpaces();
			if(sc.eatStrings("GL11") == -1) { return false; }
			sc.eatSpaces();
			if(sc.eatStrings(".") == -1) { return false; }
			sc.eatSpaces();
			if(sc.eatStrings("glTexCoord2") == -1) { return false; }
			if(sc.eatStrings("f", "d") == -1) { return false; }
			sc.eatSpaces();
			if(sc.eatStrings("(") == -1) { return false; }
			sc.eatSpaces();
			if(eatSingleNumberStm(sc) == false) { return false; }
			secondParsString += sc.getLastEatString() + ", ";
			sc.eatSpaces();
			if(sc.eatStrings(",") == -1) { return false; }
			sc.eatSpaces();
			if(eatSingleNumberStm(sc) == false) { return false; }
			secondParsString += sc.getLastEatString();
			sc.eatSpaces();
			if(sc.eatStrings(")") == -1) { return false; }
			sc.eatSpaces();
			if(sc.eatStrings(";") == -1) { return false; }
			sc.eatSpaces();
			
			if(sc.eatStrings("GL11") == -1) { return false; }
			sc.eatSpaces();
			if(sc.eatStrings(".") == -1) { return false; }
			sc.eatSpaces();
			if(sc.eatStrings("glVertex3") == -1) { return false; }
			if(sc.eatStrings("f", "d") == -1) { return false; }
			sc.eatSpaces();
			if(sc.eatStrings("(") == -1) { return false; }
			sc.eatSpaces();
			if(eatSingleNumberStm(sc) == false) { return false; }
			firstParsString += sc.getLastEatString() + ", ";
			sc.eatSpaces();
			if(sc.eatStrings(",") == -1) { return false; }
			sc.eatSpaces();
			if(eatSingleNumberStm(sc) == false) { return false; }
			firstParsString += sc.getLastEatString() + ", ";
			sc.eatSpaces();
			if(sc.eatStrings(",") == -1) { return false; }
			sc.eatSpaces();
			if(eatSingleNumberStm(sc) == false) { return false; }
			firstParsString += sc.getLastEatString();
			sc.eatSpaces();
			if(sc.eatStrings(")") == -1) { return false; }
			sc.eatSpaces();
			if(sc.eatStrings(";") == -1) { return false; }
			sc.eatSpaces();
			rb.append(firstParsString);
			rb.append(", ");
			rb.append(secondParsString);
			rb.append(");\n");
		}
		return true;
	}

}
