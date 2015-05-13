package net.yuxuan.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * {@code StringConsumer}
 * �����ڱ�ʾһ���ַ���������������Ҫ���Դ��ṩ���ַ���ͷ�����ò����ַ��������ؼ��õĽ�������ڶ��ַ������н����������ݽ��������ز�����
 * 
 * @author yuxuanchiadm
 * @see java.lang.String
 * @see java.util.regex.Pattern
 */
public class StringConsumer {
	/** ��ǰ���ڲü����ַ����� */
	protected String string;
	/** ��һ�μ��ù����Ķ����ַ��� */
	protected int preEat; // Ĭ��Ϊ0

	/**
	 * ��ʼ��һ���µ��ַ�����������
	 * 
	 * @param string
	 *            �ṩ���ַ������������ַ�����
	 */
	public StringConsumer(String string) {
		this.string = string;
	}

	/**
	 * ���ַ���ͷ��ƥ�䴫����ַ��������е������ַ��������ĳһ��ƥ�� ��ƥ�䲿�ֲü��������صڼ����ַ���ƥ�䣬����κ�һ���ַ�����
	 * ��ƥ�䣬�򷵻�-1��
	 * 
	 * @param stringArray
	 *            ������ƥ����ַ������顣
	 * @return �ɹ��򷵻صڼ����ַ���ƥ�䣬���򷵻�-1��
	 */
	public int eatStrings(String... stringArray) {
		preEat = 0;
		for (int i = 0; i < stringArray.length; i++) {
			if (string.startsWith(stringArray[i])) {
				string = string.substring(stringArray[i].length());
				preEat = stringArray[i].length();
				return i;
			}
		}
		return -1;
	}

	/**
	 * ���ַ���ͷ��ƥ�䴫���������ʽ�����е�����������ʽ�����ĳһ��ƥ����ƥ�䲿�ֲü��������صڼ���������ʽƥ�䣬���
	 * �κ�һ���ַ�������ƥ�䣬�򷵻�-1��
	 * 
	 * @param stringArray
	 *            ������ƥ���������ʽ���顣
	 * @return �ɹ��򷵻صڼ���������ʽƥ�䣬���򷵻�-1��
	 */
	public int eatPattern(Pattern... patternArray) {
		preEat = 0;
		for (int i = 0; i < patternArray.length; i++) {
			Pattern pattern = patternArray[i];
			Matcher matcher = pattern.matcher(string);
			if (matcher.lookingAt()) {
				string = string.substring(matcher.end());
				preEat = matcher.end();
				return i;
			}
		}
		return -1;
	}

	/**
	 * ���ַ���ͷ��ƥ��������ո����ƥ�䵽����1���ո���ƥ�䲿�ֲü���������true�����򷵻�false���ո��ַ�Ϊ' '��'\n'��'\r'��
	 * '\t'��
	 * 
	 * @return �Ƿ�ɹ�ƥ��ո�
	 */
	public boolean eatSpaces() {
		preEat = 0;
		int pos;
		for (pos = 0; pos < string.length(); pos++) {
			char c = string.charAt(pos);
			if (c != ' ' && c != '\n' && c != '\r' && c != '\t') {
				break;
			}
		}
		if (pos == 0) {
			return false;
		}
		string = string.substring(pos);
		preEat = pos;
		return true;
	}

	/**
	 * ���ַ���ͷ��ƥ��һ��EOF�����ƥ�䣬�򷵻�true�����򷵻�false��ע��˺�����������κ��ַ�����ʼ������
	 * <code>{@link #eatEOF()}</code>
	 * ���ص�ֵΪ0��������ε��÷���true�����ڲ��ı�״̬������£���ε���Ҳ����true��
	 * 
	 * @return �Ƿ�ɹ�ƥ��EOF��
	 */
	public boolean eatEOF() {
		preEat = 0;
		return string.isEmpty();
	}

	/**
	 * ���ص�ǰ���ڲü����ַ�����
	 * 
	 * @return ��ǰ���ڲü����ַ�����
	 */
	public String getString() {
		return string;
	}

	/**
	 * �������ڲü����ַ�����ע���������<code>{@link #eatEOF()}</code>���ص�ֵΪ0��
	 * 
	 * @param string
	 *            ���ڲü����ַ�����
	 */
	public void setString(String string) {
		this.string = string;
		preEat = 0;
	}

	/**
	 * ������һ�βü����ü��˶����ַ���ע�������һ�βü�ʧ�ܣ�Ĭ��Ϊ0�������βü�Ҳ���ܲ������κ��ַ�
	 * <code>{@link #eatEOF()}</code>�����Բ����Դ˺�������ֵ�Ƿ�Ϊ0���ж��ϴβü��Ƿ�ɹ���
	 * 
	 * @return ��һ�βü����ü��˶����ַ���
	 */
	public int getPreEat() {
		return preEat;
	}

	/**
	 * ������һ�βü����ü��˶����ַ�����ͨ�����ⲿ���뽫����Դ˶������ͳһΪ1���������ʹ�á�
	 * 
	 * @param preEat
	 *            ���ü��˶����ַ���
	 */
	public void setPreEat(int preEat) {
		this.preEat = preEat;
	}
}
