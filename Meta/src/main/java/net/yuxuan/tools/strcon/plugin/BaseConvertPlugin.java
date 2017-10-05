package net.yuxuan.tools.strcon.plugin;

import java.awt.Component;
import java.util.regex.Pattern;

import net.yuxuan.tools.strcon.StringConverter;
import net.yuxuan.utils.Regular;
import net.yuxuan.utils.StringConsumer;
import net.yuxuan.utils.StringConsumer.EatResult;

public abstract class BaseConvertPlugin {
	private StringConverter stringConverter;

	private String name;

	public abstract boolean process(StringConsumer sc, StringBuilder rb);

	public final void initStringConverter(StringConverter stringConverter) {
		if (this.stringConverter != null)
			throw new IllegalStateException("StringConverter already initialized");
		this.stringConverter = stringConverter;
	}

	public StringConverter getStringConverter() {
		return stringConverter;
	}

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

		if (sc.eatPattern(Pattern.compile(Regular.JAVA_NUMBER)).isSuccess() == false) {
			sc.setLastEatCount(-1);
			sc.setPointer(sc.getPointer() - lastEatCount);
			EatResult eatResult = sc.new EatResult(false);
			sc.setLastEatResult(eatResult);
			return eatResult;
		}
		lastEatCount += maxWithZero(sc.getLastEatCount());

		sc.setLastEatCount(lastEatCount);
		EatResult eatResult = sc.new EatResult(true);
		sc.setLastEatResult(eatResult);
		return eatResult;
	}

	public EatResult eatSingleNumberStm(StringConsumer sc) {
		int lastEatCount = 0;

		sc.eatSpaces();
		lastEatCount += maxWithZero(sc.getLastEatCount());

		if (eatNumber(sc).isSuccess() == false) {
			sc.setLastEatCount(-1);
			sc.setPointer(sc.getPointer() - lastEatCount);
			EatResult eatResult = sc.new EatResult(false);
			sc.setLastEatResult(eatResult);
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
				sc.setLastEatCount(-1);
				sc.setPointer(sc.getPointer() - lastEatCount);
				EatResult eatResult = sc.new EatResult(false);
				sc.setLastEatResult(eatResult);
				return eatResult;
			}
			lastEatCount += maxWithZero(sc.getLastEatCount());

			sc.eatSpaces();
			lastEatCount += maxWithZero(sc.getLastEatCount());
		}

		sc.setLastEatCount(lastEatCount);
		EatResult eatResult = sc.new EatResult(true);
		sc.setLastEatResult(eatResult);
		return eatResult;
	}

	public EatResult eatJavaComplexStm(StringConsumer sc) {
		int lastEatCount = 0;

		sc.eatSpaces();
		lastEatCount += maxWithZero(sc.getLastEatCount());

		boolean isEmpty = true;
		int numNested = 0;

		while (true) {
			sc.eatStrings("(");
			lastEatCount += maxWithZero(sc.getLastEatCount());

			if (sc.getLastEatResult().isSuccess()) {
				isEmpty = false;
				numNested++;

				sc.eatSpaces();
				lastEatCount += maxWithZero(sc.getLastEatCount());
				continue;
			}

			sc.eatPattern(Pattern.compile("([^\"(),;]++|" + Regular.JAVA_STRING + "++)*"));
			lastEatCount += maxWithZero(sc.getLastEatCount());

			if (sc.getLastEatCount() <= 0) {
				if (numNested == 0) {
					if (isEmpty) {
						sc.setLastEatCount(-1);
						sc.setPointer(sc.getPointer() - lastEatCount);
						EatResult eatResult = sc.new EatResult(false);
						sc.setLastEatResult(eatResult);
						return eatResult;
					}
					break;
				} else {
					if (sc.eatPattern(Pattern.compile("[),]")).isSuccess() == false) {
						sc.setLastEatCount(-1);
						sc.setPointer(sc.getPointer() - lastEatCount);
						EatResult eatResult = sc.new EatResult(false);
						sc.setLastEatResult(eatResult);
						return eatResult;
					}
					lastEatCount += maxWithZero(sc.getLastEatCount());

					switch (sc.getLastEatString()) {
					case ")":
						sc.eatSpaces();
						lastEatCount += maxWithZero(sc.getLastEatCount());

						numNested--;
						break;
					case ",":
						sc.eatSpaces();
						lastEatCount += maxWithZero(sc.getLastEatCount());
						break;
					default:
						sc.setLastEatCount(-1);
						sc.setPointer(sc.getPointer() - lastEatCount);
						EatResult eatResult = sc.new EatResult(false);
						sc.setLastEatResult(eatResult);
						return eatResult;
					}
				}
			} else {
				isEmpty = false;
			}
		}

		sc.setLastEatCount(lastEatCount);
		EatResult eatResult = sc.new EatResult(true);
		sc.setLastEatResult(eatResult);
		return eatResult;
	}

	public int maxWithZero(int i) {
		return Math.max(i, 0);
	}
}
