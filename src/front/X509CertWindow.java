package front;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.BoxLayout;
import javax.swing.JList;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.ListSelectionModel;

import back.CertController;

public class X509CertWindow {
	private JFrame frame;
	private JList pairList;
	private JButton exportPairButton;
	private JButton detailsButton;
	private final String path = "resources/defaultKeyStore.p12";
    private final String password = "password";
    private  CertController controller = new CertController(path, password);

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
		
		exportPairButton = new JButton("Export Pair");
		exportPairButton.setEnabled(false);
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
		
		detailsButton = new JButton("Pair Details");
		detailsButton.setEnabled(false);
		Buttons.add(detailsButton);
		
		JButton signCertButton = new JButton("Sign Certificate");
		signCertButton.setEnabled(false);
		Buttons.add(signCertButton);
		
		JPanel certPanel = new JPanel();
		panel.add(certPanel);
		certPanel.setLayout(new BorderLayout(0, 0));
		
		JLabel certLabel = new JLabel("Certificates:");
		certPanel.add(certLabel, BorderLayout.NORTH);
		
		JScrollPane certScrollPane = new JScrollPane();
		certPanel.add(certScrollPane, BorderLayout.CENTER);
		
		JList certList = new JList();
		certList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		certScrollPane.setColumnHeaderView(certList);
		
		JButton exportCertButton = new JButton("Export Certificate");
		exportCertButton.setEnabled(false);
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
		
		pairList = new JList();
		pairList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		pairScrollPane.setViewportView(pairList);
		pairList.setListData(controller.getCertificateInfoList(true).toArray());
		pairList.addListSelectionListener(new ListSelectionListener() {
			
			@Override
			public void valueChanged(ListSelectionEvent e) {
				//String s=(String) pairList.getSelectedValue();
				//System.out.print(s);
				exportPairButton.setEnabled(true);
				detailsButton.setEnabled(true);
			}
		});
	}
}
