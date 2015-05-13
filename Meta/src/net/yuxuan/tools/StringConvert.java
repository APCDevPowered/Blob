package net.yuxuan.tools;

import javax.swing.JFrame;
import javax.swing.JPanel;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;

import net.yuxuan.utils.StringConsumer;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.swing.JLabel;
import javax.swing.SwingConstants;

public class StringConvert extends JFrame {
	private static final long serialVersionUID = 1L;
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

	public StringConvert() {
		setTitle("StringConvert");
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
				StringConsumer sc = new StringConsumer(input.getText());
				StringBuilder rb = new StringBuilder();
				try {
					if (!process(sc, rb)) {
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
	}

	public boolean process(StringConsumer sc, StringBuilder rb) {
		/* Example Code
		if (!sc.eatSpaces()) {
			return false;
		}
		rb.append(sc.getPreEat() + ":");
		if (sc.eatPattern(Pattern.compile("[0-9]")) == -1) {
			return false;
		}
		rb.append(sc.getPreEat() + ":");
		if (!sc.eatEOF()) {
			return false;
		}
		rb.append(sc.getPreEat() + ":");
		*/
		return true;
	}

	public static void main(String[] args) {
		new StringConvert().setVisible(true);
	}
}