package net.yuxuan.tools.strcon.plugin;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.function.Function;
import java.util.regex.Pattern;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

import com.google.common.base.Supplier;

import net.yuxuan.utils.StringConsumer;
import net.yuxuan.utils.StringConsumer.PatternEatResult;
import net.yuxuan.utils.mode.IMode;

public class SQLToStringLiteralsConvertPlugin extends BaseConvertPlugin {

	private final JSQLToStringLiteralsConvertPluginSetting setting = new JSQLToStringLiteralsConvertPluginSetting(this);

	private String tablePrefix;

	public Function<String, String> postReplace = Function.identity();

	public SQLToStringLiteralsConvertPlugin() {
		setName("SQLToStringLiteralsConverter");
	}

	@Override
	public Component getSettingComponent() {
		return setting;
	}

	private String ifTablePrefix(Supplier<String> supplier) {
		return tablePrefix == null ? "" : supplier.get();
	}

	@Override
	public boolean process(StringConsumer sc, StringBuilder rb) {
		StringBuilder finalString = new StringBuilder();
		while (true) {
			sc.eatPattern(Pattern.compile(
				"(?<sqlExpr>(?<sqlExprBeforeTablePrefix>\\s*(?i:CREATE)\\s+(?<sqlDataDefinition>(?i:TABLE|TYPE))\\s+)"
					+ ifTablePrefix(() -> Pattern.quote(tablePrefix)) + "(?<sqlExprAfterTablePrefix>"
					+ ifTablePrefix(() -> "_") + "(?<tableName>[a-zA-Z_]+)\\s*([\\s\\S]*?);))\\R(\\R|$)"),
				Pattern.compile(
					"(?<sqlExpr>(?<sqlExprBeforeTablePrefix>\\s*(?i:DROP)\\s+(?<sqlDataDefinition>(?i:TABLE|TYPE))\\s+)"
						+ ifTablePrefix(() -> Pattern.quote(tablePrefix)) + "(?<sqlExprAfterTablePrefix>"
						+ ifTablePrefix(() -> "_") + "(?<tableName>[a-zA-Z_]+)\\s*;))\\R(\\R|$)"),
				Pattern
					.compile("(?<sqlExpr>(?<sqlExprBeforeTablePrefix>\\s*(?i:INSERT)\\s+(?i:IGNORE\\s+)?(?i:INTO)\\s+)"
						+ ifTablePrefix(() -> Pattern.quote(tablePrefix)) + "(?<sqlExprAfterTablePrefix>"
						+ ifTablePrefix(() -> "_") + "(?<tableName>[a-zA-Z_]+)\\s*([\\s\\S]*?);))\\R(\\R|$)"),
				Pattern.compile("(?<sqlExpr>(?<sqlExprBeforeTablePrefix>\\s*(?i:UPDATE)\\s+)"
					+ ifTablePrefix(() -> Pattern.quote(tablePrefix)) + "(?<sqlExprAfterTablePrefix>"
					+ ifTablePrefix(() -> "_") + "(?<tableName>[a-zA-Z_]+)\\s*([\\s\\S]*?);))\\R(\\R|$)"),
				Pattern.compile("(?<sqlExpr>(?<sqlExprBeforeTablePrefix>\\s*(?i:SELECT)([\\s\\S]+?)(?i:FROM)\\s+)"
					+ ifTablePrefix(() -> Pattern.quote(tablePrefix)) + "(?<sqlExprAfterTablePrefix>"
					+ ifTablePrefix(() -> "_") + "(?<tableName>[a-zA-Z_]+)\\s*([\\s\\S]*?);))\\R(\\R|$)"));
			PatternEatResult result = sc.getLastEatResult(PatternEatResult.class);
			if (!result.isSuccess())
				break;
			String sqlExprBeforeTablePrefix = result.getMatcher().group("sqlExprBeforeTablePrefix");
			String sqlExprAfterTablePrefix = result.getMatcher().group("sqlExprAfterTablePrefix");
			String sqlExpr = result.getMatcher().group("sqlExpr");
			String tableName = result.getMatcher().group("tableName");
			finalString.append("final String ");
			switch (result.getMatchPatternIndex()) {
			case 0:
				finalString.append("create");
				break;
			case 1:
				finalString.append("drop");
				break;
			case 2:
				finalString.append("insert");
				break;
			case 3:
				finalString.append("update");
				break;
			case 4:
				finalString.append("query");
				break;
			default:
				throw new IllegalStateException();
			}
			CaseConversionConvertPlugin caseConversionConvertPlugin = getStringConverter()
				.getPlugin(CaseConversionConvertPlugin.class);
			StringBuilder temp = new StringBuilder();
			IMode oldMode = caseConversionConvertPlugin.getMode();
			caseConversionConvertPlugin.setMode(CaseConversionConvertPlugin.TO_LOWERCASE.getDefaultMode()
				.withProperty(CaseConversionConvertPlugin.USE_JAVA_IDENTIFIER_CONVERT, true));
			if (!caseConversionConvertPlugin.process(new StringConsumer(tableName), temp))
				return false;
			caseConversionConvertPlugin.setMode(CaseConversionConvertPlugin.TO_FIRST_LETTER_UPPERCASE.getDefaultMode());
			if (!caseConversionConvertPlugin.process(new StringConsumer(temp.toString()), finalString))
				return false;
			caseConversionConvertPlugin.setMode(oldMode);
			switch (result.getMatchPatternIndex()) {
			case 0:
			case 1:
				String sqlDataDefinition = result.getMatcher().group("sqlDataDefinition");
				switch (sqlDataDefinition.toLowerCase()) {
				case "table":
					finalString.append("Table");
					break;
				case "type":
					finalString.append("Type");
					break;
				default:
					throw new IllegalStateException();
				}
				finalString.append("SQL");
				break;
			case 2:
			case 3:
			case 4:
				finalString.append("SQL");
				break;
			default:
				throw new IllegalStateException();
			}
			finalString.append(" =\n");
			if (tablePrefix == null) {
				if (!getStringConverter().getPlugin(MultilineStringIzationConvertPlugin.class)
					.process(new StringConsumer(sqlExpr), finalString))
					return false;
			} else {
				if (!getStringConverter().getPlugin(MultilineStringIzationConvertPlugin.class)
					.process(new StringConsumer(sqlExprBeforeTablePrefix), finalString))
					return false;
				finalString.append(" + tablePrefix + ");
				if (!getStringConverter().getPlugin(MultilineStringIzationConvertPlugin.class)
					.process(new StringConsumer(sqlExprAfterTablePrefix), finalString))
					return false;
			}
			finalString.append(";\n");
		}
		if (!sc.eatEOF().isSuccess())
			return false;
		rb.append(postReplace.apply(finalString.toString()));
		return true;
	}

	public static class JSQLToStringLiteralsConvertPluginSetting extends JPanel {

		private static final long serialVersionUID = 1L;

		private final SQLToStringLiteralsConvertPlugin plugin;

		private JTextField tablePrefixSettingTextField;
		private JCheckBox tablePrefixSettingCheckBox;

		private JTextField postReplaceOneFromSettingTextField;
		private JTextField postReplaceOneToSettingTextField;

		private JTextField postReplaceTwoFromSettingTextField;
		private JTextField postReplaceTwoToSettingTextField;

		public JSQLToStringLiteralsConvertPluginSetting(SQLToStringLiteralsConvertPlugin plugin) {
			this.plugin = plugin;

			setLayout(new BorderLayout(0, 0));

			JTabbedPane mainTabbedPane = new JTabbedPane(JTabbedPane.TOP);
			add(mainTabbedPane);

			JPanel generalTabPanel = new JPanel();
			generalTabPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
			mainTabbedPane.addTab("General", null, generalTabPanel, null);

			JPanel generalSettingsPanel = new JPanel();
			generalTabPanel.add(generalSettingsPanel);
			generalSettingsPanel.setLayout(new BoxLayout(generalSettingsPanel, BoxLayout.Y_AXIS));

			JPanel tablePrefixSettingPanel = new JPanel();
			tablePrefixSettingPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
			generalSettingsPanel.add(tablePrefixSettingPanel);

			tablePrefixSettingPanel.add(new JLabel("Table prefix:"));

			tablePrefixSettingTextField = new JTextField();
			tablePrefixSettingPanel.add(tablePrefixSettingTextField);
			tablePrefixSettingTextField.setColumns(20);
			tablePrefixSettingTextField.addCaretListener(new CaretListener() {
				@Override
				public void caretUpdate(CaretEvent e) {
					updateTablePrefixSetting();
				}
			});

			tablePrefixSettingCheckBox = new JCheckBox("Use table prefix");
			tablePrefixSettingPanel.add(tablePrefixSettingCheckBox);
			tablePrefixSettingCheckBox.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					updateTablePrefixSetting();
				}
			});

			JPanel postReplaceOneSettingPanel = new JPanel();
			postReplaceOneSettingPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
			generalSettingsPanel.add(postReplaceOneSettingPanel);

			postReplaceOneSettingPanel.add(new JLabel("Post replace:"));

			postReplaceOneFromSettingTextField = new JTextField();
			postReplaceOneSettingPanel.add(postReplaceOneFromSettingTextField);
			postReplaceOneFromSettingTextField.setColumns(20);
			postReplaceOneFromSettingTextField.addCaretListener(new CaretListener() {
				@Override
				public void caretUpdate(CaretEvent e) {
					updatePostReplaceSetting();
				}
			});

			postReplaceOneSettingPanel.add(new JLabel("to"));

			postReplaceOneToSettingTextField = new JTextField();
			postReplaceOneSettingPanel.add(postReplaceOneToSettingTextField);
			postReplaceOneToSettingTextField.setColumns(20);
			postReplaceOneToSettingTextField.addCaretListener(new CaretListener() {
				@Override
				public void caretUpdate(CaretEvent e) {
					updatePostReplaceSetting();
				}
			});

			JPanel postReplaceTwoSettingPanel = new JPanel();
			postReplaceTwoSettingPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
			generalSettingsPanel.add(postReplaceTwoSettingPanel);

			postReplaceTwoSettingPanel.add(new JLabel("Post replace:"));

			postReplaceTwoFromSettingTextField = new JTextField();
			postReplaceTwoSettingPanel.add(postReplaceTwoFromSettingTextField);
			postReplaceTwoFromSettingTextField.setColumns(20);
			postReplaceTwoFromSettingTextField.addCaretListener(new CaretListener() {
				@Override
				public void caretUpdate(CaretEvent e) {
					updatePostReplaceSetting();
				}
			});

			postReplaceTwoSettingPanel.add(new JLabel("to"));

			postReplaceTwoToSettingTextField = new JTextField();
			postReplaceTwoSettingPanel.add(postReplaceTwoToSettingTextField);
			postReplaceTwoToSettingTextField.setColumns(20);
			postReplaceTwoToSettingTextField.addCaretListener(new CaretListener() {
				@Override
				public void caretUpdate(CaretEvent e) {
					updatePostReplaceSetting();
				}
			});
		}

		private void updateTablePrefixSetting() {
			if (!tablePrefixSettingCheckBox.isSelected()) {
				plugin.tablePrefix = null;
			} else {
				plugin.tablePrefix = tablePrefixSettingTextField.getText();
			}
		}

		private void updatePostReplaceSetting() {
			plugin.postReplace = Function.identity();
			plugin.postReplace = plugin.postReplace.andThen(str -> str
				.replace(postReplaceOneFromSettingTextField.getText(), postReplaceOneToSettingTextField.getText()));
			plugin.postReplace = plugin.postReplace.andThen(str -> str
				.replace(postReplaceTwoFromSettingTextField.getText(), postReplaceTwoToSettingTextField.getText()));
		}
	}
}
