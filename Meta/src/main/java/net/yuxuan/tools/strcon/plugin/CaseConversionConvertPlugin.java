package net.yuxuan.tools.strcon.plugin;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import net.yuxuan.utils.StringConsumer;
import net.yuxuan.utils.mode.IMode;
import net.yuxuan.utils.mode.ModeDefinition;
import net.yuxuan.utils.mode.PropertyBoolean;

public class CaseConversionConvertPlugin extends BaseConvertPlugin {

	private IMode mode;

	public static final PropertyBoolean USE_JAVA_IDENTIFIER_CONVERT = new PropertyBoolean(
		"USE_JAVA_IDENTIFIER_CONVERT");

	public static final ModeDefinition TO_LOWERCASE = new ModeDefinition("TO_LOWERCASE", USE_JAVA_IDENTIFIER_CONVERT);
	public static final ModeDefinition TO_UPPERCASE = new ModeDefinition("TO_UPPERCASE", USE_JAVA_IDENTIFIER_CONVERT);
	public static final ModeDefinition TO_FIRST_LETTER_LOWERCASE = new ModeDefinition("TO_FIRST_LETTER_LOWERCASE");
	public static final ModeDefinition TO_FIRST_LETTER_UPPERCASE = new ModeDefinition("TO_FIRST_LETTER_UPPERCASE");

	private final JCaseConversionConvertPluginSetting setting = new JCaseConversionConvertPluginSetting(this);

	public CaseConversionConvertPlugin() {
		setName("CaseConversionConverter");
	}

	public IMode getMode() {
		return mode;
	}

	public void setMode(IMode mode) {
		this.mode = mode;
	}

	public Component getSettingComponent() {
		return setting;
	}

	private int indexOfUppercaseLetter(String string, int fromIndex) {
		Matcher matcher = Pattern.compile("\\p{javaUpperCase}").matcher(string);
		if (matcher.find(fromIndex)) {
			return matcher.start();
		} else {
			return -1;
		}
	}

	@Override
	public boolean process(StringConsumer sc, StringBuilder rb) {
		IMode mode = getMode();
		if (mode == null) {
			return false;
		} else if (mode.getModeDefinition() == TO_LOWERCASE) {
			sc.eatPattern(Pattern.compile("[\\s\\S]*"));
			if (mode.getValue(USE_JAVA_IDENTIFIER_CONVERT)) {
				String string = sc.getLastEatString();
				int startSearchUnderlineIndex = 0;
				int underlineIndex = -1;
				boolean isFirstWord = true;
				while ((underlineIndex = string.indexOf('_', startSearchUnderlineIndex)) != -1) {
					if (isFirstWord) {
						rb.append(string.substring(0, underlineIndex == 0 ? 1 : underlineIndex).toLowerCase());
						isFirstWord = false;
					} else {
						if (startSearchUnderlineIndex == underlineIndex) {
							rb.append("_");
						} else {
							int firstLetter = string.codePointAt(startSearchUnderlineIndex);
							rb.appendCodePoint(Character.toUpperCase(firstLetter));
							rb.append(string.substring(
								(Character.isSupplementaryCodePoint(firstLetter) ? 2 : 1) + startSearchUnderlineIndex,
								underlineIndex).toLowerCase());
						}
					}
					startSearchUnderlineIndex = underlineIndex + 1;
				}
				if (isFirstWord) {
					rb.append(string.substring(startSearchUnderlineIndex, string.length()).toLowerCase());
					isFirstWord = false;
				} else {
					if (startSearchUnderlineIndex < string.length()) {
						int firstLetter = string.codePointAt(startSearchUnderlineIndex);
						rb.appendCodePoint(Character.toUpperCase(firstLetter));
						rb.append(string.substring(
							(Character.isSupplementaryCodePoint(firstLetter) ? 2 : 1) + startSearchUnderlineIndex,
							string.length()).toLowerCase());
					} else {
						rb.append("_");
					}
				}
			} else {
				rb.append(sc.getLastEatString().toLowerCase());
			}
			if (sc.eatEOF().isSuccess() == false) {
				return false;
			}
		} else if (mode.getModeDefinition() == TO_UPPERCASE) {
			sc.eatPattern(Pattern.compile("[\\s\\S]*"));
			if (mode.getValue(USE_JAVA_IDENTIFIER_CONVERT)) {
				String string = sc.getLastEatString();
				int startSearchUppercaseLetterIndex = 0;
				int uppercaseLetterIndex = -1;
				boolean isFirstWord = true;
				while ((uppercaseLetterIndex = indexOfUppercaseLetter(string, startSearchUppercaseLetterIndex)) != -1) {
					rb.append(string.substring(startSearchUppercaseLetterIndex - (isFirstWord ? 0 : 1),
						(isFirstWord ? ++uppercaseLetterIndex : uppercaseLetterIndex)).toUpperCase());
					rb.append(isFirstWord ? "" : "_");
					startSearchUppercaseLetterIndex = uppercaseLetterIndex + 1;
					if (isFirstWord) {
						isFirstWord = false;
					}
				}
				rb.append(string.substring(startSearchUppercaseLetterIndex - (isFirstWord ? 0 : 1), string.length())
					.toUpperCase());
			} else {
				rb.append(sc.getLastEatString().toUpperCase());
			}
			if (sc.eatEOF().isSuccess() == false) {
				return false;
			}
		} else if (mode.getModeDefinition() == TO_FIRST_LETTER_LOWERCASE) {
			sc.eatPattern(Pattern.compile("[\\s\\S]*"));
			String string = sc.getLastEatString();
			if (!string.isEmpty()) {
				int firstLetter = string.codePointAt(0);
				rb.appendCodePoint(Character.toLowerCase(firstLetter));
				rb.append(string.substring(Character.isSupplementaryCodePoint(firstLetter) ? 2 : 1, string.length()));
			}
			if (sc.eatEOF().isSuccess() == false) {
				return false;
			}
		} else if (mode.getModeDefinition() == TO_FIRST_LETTER_UPPERCASE) {
			sc.eatPattern(Pattern.compile("[\\s\\S]*"));
			String string = sc.getLastEatString();
			if (!string.isEmpty()) {
				int firstLetter = string.codePointAt(0);
				rb.appendCodePoint(Character.toUpperCase(firstLetter));
				rb.append(string.substring(Character.isSupplementaryCodePoint(firstLetter) ? 2 : 1, string.length()));
			}
			if (sc.eatEOF().isSuccess() == false) {
				return false;
			}
		} else {
			return false;
		}
		return true;
	}

	public static class JCaseConversionConvertPluginSetting extends JPanel {

		private static final long serialVersionUID = 1L;

		public JCaseConversionConvertPluginSetting(CaseConversionConvertPlugin plugin) {
			setLayout(new GridLayout(1, 2));

			JPanel buttonPanel = new JPanel() {
				private static final long serialVersionUID = 1L;

				private Component currentModeSettingComponent;
				private JPanel noModeSettingPanel;

				{
					noModeSettingPanel = new JPanel();
					noModeSettingPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
					noModeSettingPanel.setBorder(BorderFactory.createTitledBorder("Info"));

					JLabel noModeSettingLabel = new JLabel("NoModeSetting");
					noModeSettingPanel.add(noModeSettingLabel);

					ButtonGroup buttonGroup = new ButtonGroup();

					JRadioButton radioButtonToLowercase = new JRadioButton();
					radioButtonToLowercase.setText("toLowercase");
					buttonGroup.add(radioButtonToLowercase);
					radioButtonToLowercase.addItemListener(new ItemListener() {

						protected JCheckBox useJavaIdentifierConverterCheckBox;

						private JPanel radioButtonToLowercaseSettingPanel = new JPanel() {
							private static final long serialVersionUID = 1L;

							{
								useJavaIdentifierConverterCheckBox = new JCheckBox();
								useJavaIdentifierConverterCheckBox.setText("useJavaIdentifierConverter");
								useJavaIdentifierConverterCheckBox.addItemListener(new ItemListener() {
									@Override
									public void itemStateChanged(ItemEvent e) {
										if (e.getStateChange() == ItemEvent.SELECTED) {
											plugin.setMode(TO_LOWERCASE.getDefaultMode()
												.withProperty(USE_JAVA_IDENTIFIER_CONVERT, true));
										} else if (e.getStateChange() == ItemEvent.DESELECTED) {
											plugin.setMode(TO_LOWERCASE.getDefaultMode()
												.withProperty(USE_JAVA_IDENTIFIER_CONVERT, false));
										}
									}
								});
								add(useJavaIdentifierConverterCheckBox);
							}
						};

						{
							radioButtonToLowercaseSettingPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
							radioButtonToLowercaseSettingPanel
								.setBorder(BorderFactory.createTitledBorder("ModeSetting"));
						}

						@Override
						public void itemStateChanged(ItemEvent e) {
							if (e.getStateChange() == ItemEvent.SELECTED) {
								plugin.setMode(TO_LOWERCASE.getDefaultMode().withProperty(USE_JAVA_IDENTIFIER_CONVERT,
									useJavaIdentifierConverterCheckBox.isSelected()));
								setModeSettingComponent(radioButtonToLowercaseSettingPanel);
							} else if (e.getStateChange() == ItemEvent.DESELECTED) {
								setModeSettingComponent(null);
							}
						}
					});
					add(radioButtonToLowercase);

					JRadioButton radioButtonToUppercase = new JRadioButton();
					radioButtonToUppercase.setText("toUppercase");
					buttonGroup.add(radioButtonToUppercase);
					radioButtonToUppercase.addItemListener(new ItemListener() {

						protected JCheckBox useJavaIdentifierConverterCheckBox;

						private JPanel radioButtonToUppercaseSettingPanel = new JPanel() {
							private static final long serialVersionUID = 1L;

							{
								useJavaIdentifierConverterCheckBox = new JCheckBox();
								useJavaIdentifierConverterCheckBox.setText("useJavaIdentifierConverter");
								useJavaIdentifierConverterCheckBox.addItemListener(new ItemListener() {
									@Override
									public void itemStateChanged(ItemEvent e) {
										if (e.getStateChange() == ItemEvent.SELECTED) {
											plugin.setMode(TO_UPPERCASE.getDefaultMode()
												.withProperty(USE_JAVA_IDENTIFIER_CONVERT, true));
										} else if (e.getStateChange() == ItemEvent.DESELECTED) {
											plugin.setMode(TO_UPPERCASE.getDefaultMode()
												.withProperty(USE_JAVA_IDENTIFIER_CONVERT, false));
										}
									}
								});
								add(useJavaIdentifierConverterCheckBox);
							}
						};

						{
							radioButtonToUppercaseSettingPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
							radioButtonToUppercaseSettingPanel
								.setBorder(BorderFactory.createTitledBorder("ModeSetting"));
						}

						@Override
						public void itemStateChanged(ItemEvent e) {
							if (e.getStateChange() == ItemEvent.SELECTED) {
								plugin.setMode(TO_UPPERCASE.getDefaultMode().withProperty(USE_JAVA_IDENTIFIER_CONVERT,
									useJavaIdentifierConverterCheckBox.isSelected()));
								setModeSettingComponent(radioButtonToUppercaseSettingPanel);
							} else if (e.getStateChange() == ItemEvent.DESELECTED) {
								setModeSettingComponent(null);
							}
						}
					});
					add(radioButtonToUppercase);

					JRadioButton radioButtonToFirstLetterLowercase = new JRadioButton();
					radioButtonToFirstLetterLowercase.setText("toFirstLetterLowercase");
					buttonGroup.add(radioButtonToFirstLetterLowercase);
					radioButtonToFirstLetterLowercase.addItemListener(new ItemListener() {
						@Override
						public void itemStateChanged(ItemEvent e) {
							if (e.getStateChange() == ItemEvent.SELECTED) {
								plugin.setMode(TO_FIRST_LETTER_LOWERCASE.getDefaultMode());
							} else if (e.getStateChange() == ItemEvent.DESELECTED) {

							}
						}
					});
					add(radioButtonToFirstLetterLowercase);

					JRadioButton radioButtonToFirstLetterUppercase = new JRadioButton();
					radioButtonToFirstLetterUppercase.setText("toFirstLetterUppercase");
					buttonGroup.add(radioButtonToFirstLetterUppercase);
					radioButtonToFirstLetterUppercase.addItemListener(new ItemListener() {
						@Override
						public void itemStateChanged(ItemEvent e) {
							if (e.getStateChange() == ItemEvent.SELECTED) {
								plugin.setMode(TO_FIRST_LETTER_UPPERCASE.getDefaultMode());
							} else if (e.getStateChange() == ItemEvent.DESELECTED) {

							}
						}
					});
					add(radioButtonToFirstLetterUppercase);

					radioButtonToLowercase.setSelected(true);
				}

				private void setModeSettingComponent(Component component) {
					if (component != null) {
						if (currentModeSettingComponent != null) {
							JCaseConversionConvertPluginSetting.this.remove(currentModeSettingComponent);
						} else {
							JCaseConversionConvertPluginSetting.this.remove(noModeSettingPanel);
						}
						currentModeSettingComponent = component;
						JCaseConversionConvertPluginSetting.this.add(component,
							JCaseConversionConvertPluginSetting.this.getComponentCount());
						JCaseConversionConvertPluginSetting.this.updateUI();
					} else {
						if (currentModeSettingComponent != null) {
							JCaseConversionConvertPluginSetting.this.remove(currentModeSettingComponent);
							JCaseConversionConvertPluginSetting.this.add(noModeSettingPanel,
								JCaseConversionConvertPluginSetting.this.getComponentCount());
							JCaseConversionConvertPluginSetting.this.updateUI();
							currentModeSettingComponent = null;
						}
					}
				}
			};
			buttonPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
			buttonPanel.setBorder(BorderFactory.createTitledBorder("Mode"));
			add(buttonPanel, 0);
		}
	}
}
