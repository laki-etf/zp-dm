package front;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.BoxLayout;
import javax.swing.JList;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;

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
		newPairButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				new NewPairDialog().setVisible(true);
			}
		});
		
		JButton importPairButton = new JButton("Import Pair");
		Buttons.add(importPairButton);
		importPairButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
//			    FileNameExtensionFilter filter = new FileNameExtensionFilter(
//			        "JPG & GIF Images", "jpg", "gif");
//			    chooser.setFileFilter(filter);
			    int returnVal = chooser.showOpenDialog(frame);
			    if(returnVal == JFileChooser.APPROVE_OPTION) {
			    	JLabel jPassword = new JLabel("Password");
			        JTextField password = new JPasswordField();
			        Object[] ob = {jPassword, password};
			        int result = JOptionPane.showConfirmDialog(null, ob, "Password", JOptionPane.OK_CANCEL_OPTION);
			        String passwordValue="";
			        if (result == JOptionPane.OK_OPTION) {
			            passwordValue = password.getText();
			        }
			       System.out.println("You chose to open this file: " +
			            chooser.getSelectedFile().getName());
			    }
				
			}
		});
		
		JButton exportPairButton = new JButton("Export Pair");
		Buttons.add(exportPairButton);
		exportPairButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
//			    FileNameExtensionFilter filter = new FileNameExtensionFilter(
//			        "JPG & GIF Images", "jpg", "gif");
//			    chooser.setFileFilter(filter);
			    int returnVal = chooser.showSaveDialog(frame);
			    if(returnVal == JFileChooser.APPROVE_OPTION) {
			    	JLabel jPassword = new JLabel("Password");
			        JTextField password = new JPasswordField();
			        Object[] ob = {jPassword, password};
			        int result = JOptionPane.showConfirmDialog(null, ob, "Password", JOptionPane.OK_CANCEL_OPTION);
			        String passwordValue="";
			        if (result == JOptionPane.OK_OPTION) {
			            passwordValue = password.getText();
			        }
			       System.out.println("You chose to open this file: " +
			            chooser.getSelectedFile().getName()+passwordValue);
			    }
			}
		});
		
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
		exportCertButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
//			    FileNameExtensionFilter filter = new FileNameExtensionFilter(
//			        "JPG & GIF Images", "jpg", "gif");
//			    chooser.setFileFilter(filter);
			    int returnVal = chooser.showSaveDialog(frame);
			    if(returnVal == JFileChooser.APPROVE_OPTION) {
			       System.out.println("You chose to open this file: " +
			            chooser.getSelectedFile().getName());
			       
			    }
			}
		});
		
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
