package front;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import javax.swing.JScrollPane;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.BoxLayout;
import javax.swing.JList;

public class X509CertWindow {

	private JFrame frame;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					X509CertWindow window = new X509CertWindow();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public X509CertWindow() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame("X.509 Certificates");
		frame.setBounds(100, 100, 650, 400);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new GridLayout(0, 2, 0, 0));
		
		JPanel panel = new JPanel();
		frame.getContentPane().add(panel);
		panel.setLayout(new GridLayout(0, 1, 0, 0));
		
		JPanel Buttons = new JPanel();
		panel.add(Buttons);
		Buttons.setLayout(new GridLayout(0, 1, 0, 0));
		
		JButton newPairButton = new JButton("New Pair");
		Buttons.add(newPairButton);
		
		JButton exportPairButton = new JButton("Export Pair");
		Buttons.add(exportPairButton);
		
		JButton importPairButton = new JButton("Import Pair");
		Buttons.add(importPairButton);
		
		JButton signCertButton = new JButton("Sign Certificate");
		Buttons.add(signCertButton);
		
		JPanel certPanel = new JPanel();
		panel.add(certPanel);
		certPanel.setLayout(new BorderLayout(0, 0));
		
		JLabel certLabel = new JLabel("Certificates:");
		certPanel.add(certLabel, BorderLayout.NORTH);
		
		JScrollPane certScrollPane = new JScrollPane();
		certPanel.add(certScrollPane, BorderLayout.CENTER);
		
		JList certList = new JList();
		certScrollPane.setColumnHeaderView(certList);
		
		JButton exportCertButton = new JButton("Export Certificate");
		certPanel.add(exportCertButton, BorderLayout.SOUTH);
		
		JPanel pairPanel = new JPanel();
		frame.getContentPane().add(pairPanel);
		pairPanel.setLayout(new BorderLayout(0, 0));
		
		JLabel pairLable = new JLabel("Pairs:");
		pairPanel.add(pairLable, BorderLayout.NORTH);
		
		JScrollPane pairScrollPane = new JScrollPane();
		pairPanel.add(pairScrollPane, BorderLayout.CENTER);
		
		JList pairList = new JList();
		pairScrollPane.setViewportView(pairList);
	}

}
