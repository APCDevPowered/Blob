package net.yuxuan.tools.strcon.plugin;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.regex.Pattern;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

import net.yuxuan.utils.StringConsumer;
import net.yuxuan.utils.StringConsumer.PatternEatResult;
import net.yuxuan.utils.mode.IMode;

public class SQLToStringLiteralsConvertPlugin extends BaseConvertPlugin {

	private final JSQLToStringLiteralsConvertPluginSetting setting = new JSQLToStringLiteralsConvertPluginSetting(this);

	private String tablePrefix;

	public String postReplaceFrom = "";
	public String postReplaceTo = "";

	public SQLToStringLiteralsConvertPlugin() {
		setName("SQLToStringLiteralsConverter");
	}

	@Override
	public Component getSettingComponent() {
		return setting;
	}

	@Override
	public boolean process(StringConsumer sc, StringBuilder rb) {
		String tablePrefixRegex = tablePrefix == null ? "" : Pattern.quote(tablePrefix);
		StringBuilder finalString = new StringBuilder();
		while (true) {
			sc.eatPattern(Pattern
				.compile("(?<sqlExpr>(?<sqlExprBeforeTablePrefix>\\s*(?i:CREATE)\\s+(?i:TABLE)\\s+)" + tablePrefixRegex
					+ "(?<sqlExprAfterTablePrefix>_(?<tableName>\\S*?)\\s*\\(([\\s\\S]*?);))\\R(\\R|$)"));
			PatternEatResult result = sc.getLastEatResult(PatternEatResult.class);
			if (!result.isSuccess())
				break;
			String sqlExprBeforeTablePrefix = result.getMatcher().group("sqlExprBeforeTablePrefix");
			String sqlExprAfterTablePrefix = result.getMatcher().group("sqlExprAfterTablePrefix");
			String sqlExpr = result.getMatcher().group("sqlExpr");
			String tableName = result.getMatcher().group("tableName");
			finalString.append("final String create");
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
			finalString.append("TableSQL =\n");
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
		rb.append(finalString.toString().replace(postReplaceFrom, postReplaceTo));
		return true;
	}

	public static class JSQLToStringLiteralsConvertPluginSetting extends JPanel {

		private static final long serialVersionUID = 1L;

		private final SQLToStringLiteralsConvertPlugin plugin;

		private JTextField tablePrefixSettingTextField;
		private JCheckBox tablePrefixSettingCheckBox;

		private JTextField postReplaceFromSettingTextField;
		private JTextField postReplaceToSettingTextField;

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

			JPanel postReplaceSettingPanel = new JPanel();
			postReplaceSettingPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
			generalSettingsPanel.add(postReplaceSettingPanel);

			postReplaceSettingPanel.add(new JLabel("Post replace:"));

			postReplaceFromSettingTextField = new JTextField();
			postReplaceSettingPanel.add(postReplaceFromSettingTextField);
			postReplaceFromSettingTextField.setColumns(20);
			postReplaceFromSettingTextField.addCaretListener(new CaretListener() {
				@Override
				public void caretUpdate(CaretEvent e) {
					updatePostReplaceSetting();
				}
			});

			postReplaceSettingPanel.add(new JLabel("to"));

			postReplaceToSettingTextField = new JTextField();
			postReplaceSettingPanel.add(postReplaceToSettingTextField);
			postReplaceToSettingTextField.setColumns(20);
			postReplaceToSettingTextField.addCaretListener(new CaretListener() {
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
			plugin.postReplaceFrom = postReplaceFromSettingTextField.getText();
			plugin.postReplaceTo = postReplaceToSettingTextField.getText();
		}
	}
}
