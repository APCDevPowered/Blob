package net.yuxuan.tools.strcon;

import javax.swing.JFrame;
import javax.swing.JPanel;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;

import net.yuxuan.tools.strcon.plugin.BaseConvertPlugin;
import net.yuxuan.tools.strcon.plugin.GLRenderCodeConvertPlugin;
import net.yuxuan.tools.strcon.plugin.TestConvertPlugin;
import net.yuxuan.utils.StringConsumer;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class StringConvert extends JFrame {
	private static final long serialVersionUID = 1L;
	
	private Map<String, BaseConvertPlugin> plugins = new HashMap<String, BaseConvertPlugin>();
	
	private BaseConvertPlugin currentPlugin;
	
	private JButton convert;
	private JPanel buttonPanel;
	private JTextArea result;
	private JScrollPane resultScrollPane;
	private JTextArea input;
	private JScrollPane inputScrollPane;
	private JPanel panel;
	private JPanel inputPanel;
	private JPanel resultPanel;
	private JLabel inputLabel;
	private JLabel resultLabel;
	private JTabbedPane tabbedPane;

	public StringConvert() {
		setTitle("StringConvert - yuxuanchiadm");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(800, 600);

		getContentPane().setLayout(new BorderLayout(0, 0));

		panel = new JPanel();
		getContentPane().add(panel, BorderLayout.CENTER);
		panel.setLayout(new GridLayout(1, 0, 0, 0));

		inputPanel = new JPanel();
		panel.add(inputPanel);
		inputPanel.setLayout(new BorderLayout(0, 0));

		inputScrollPane = new JScrollPane();
		inputPanel.add(inputScrollPane);

		input = new JTextArea();
		inputScrollPane.setViewportView(input);

		inputLabel = new JLabel("Input");
		inputLabel.setHorizontalAlignment(SwingConstants.CENTER);
		inputPanel.add(inputLabel, BorderLayout.NORTH);

		resultPanel = new JPanel();
		panel.add(resultPanel);
		resultPanel.setLayout(new BorderLayout(0, 0));

		resultScrollPane = new JScrollPane();
		resultPanel.add(resultScrollPane);

		result = new JTextArea();
		resultScrollPane.setViewportView(result);
		result.setEditable(false);

		resultLabel = new JLabel("Result");
		resultLabel.setHorizontalAlignment(SwingConstants.CENTER);
		resultPanel.add(resultLabel, BorderLayout.NORTH);

		buttonPanel = new JPanel();
		getContentPane().add(buttonPanel, BorderLayout.SOUTH);

		convert = new JButton("Convert");
		convert.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(currentPlugin == null) {
					result.setText("<ERROR>\nNo plugin selected.");
				}
				StringConsumer sc = new StringConsumer(input.getText());
				StringBuilder rb = new StringBuilder();
				try {
					if (!currentPlugin.process(sc, rb)) {
						result.setText("<ERROR>\nError when parsing.");
						return;
					}
				} catch (Exception e2) {
					StringWriter stringWriter = new StringWriter();
					PrintWriter printWriter  = new PrintWriter(stringWriter);
					e2.printStackTrace(printWriter);
					printWriter.flush();
					result.setText("<ERROR>\n" + stringWriter.toString());
					return;
				}
				result.setText(rb.toString());
			}
		});
		buttonPanel.add(convert);
		
		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		getContentPane().add(tabbedPane, BorderLayout.NORTH);
		tabbedPane.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				updateSelectedPlugin();
			}
		});
	}
	
	public boolean registerPlugin(BaseConvertPlugin plugin) {
		if(plugin.getName() == null || plugins.containsKey(plugin.getName())) {
			return false;
		}
		plugins.put(plugin.getName(), plugin);
		Component settingComponent = plugin.getSettingComponent();
		if(settingComponent == null)
		{
			JLabel label = new JLabel("<No Settings>");
			label.setHorizontalAlignment(SwingConstants.CENTER);
			settingComponent = label;
		}
		tabbedPane.addTab(plugin.getName(), settingComponent);
		updateSelectedPlugin();
		return true;
	}

	private void updateSelectedPlugin() {
		int selectedIndex = tabbedPane.getSelectedIndex();
		if(selectedIndex != -1) {
			String name = tabbedPane.getTitleAt(selectedIndex);
			BaseConvertPlugin plugin = plugins.get(name);
			currentPlugin = plugin;
		}
	}

	public static void main(String[] args) {
		StringConvert stringConvert = new StringConvert();
		stringConvert.registerPlugin(new TestConvertPlugin());
		stringConvert.registerPlugin(new GLRenderCodeConvertPlugin());
		stringConvert.setVisible(true);
	}
}