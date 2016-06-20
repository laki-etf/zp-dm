package front;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;

import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.BoxLayout;
import javax.swing.JList;
import javax.swing.JTextArea;
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
	private JButton signCertButton;
	private JButton exportCertButton;
	private final String path = "resources/defaultKeyStore.p12";
    private final String password = "password";
    private  CertController controller = new CertController(path, password);
    private CertificateInfo selectedPair;

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
	public void refreshButtons(){
		selectedPair = (CertificateInfo) pairList.getSelectedValue();
		if(selectedPair!=null){
			exportPairButton.setEnabled(true);
			detailsButton.setEnabled(true);
			signCertButton.setEnabled(true);
			if(selectedPair.isSigned()){
				exportCertButton.setEnabled(true);
				signCertButton.setEnabled(false);
			}
		} else{
			exportPairButton.setEnabled(false);
			detailsButton.setEnabled(false);
			signCertButton.setEnabled(false);
			exportCertButton.setEnabled(false);
		}
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
		
		frame.addWindowListener(new WindowListener() {
			
			@Override
			public void windowOpened(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void windowIconified(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void windowDeiconified(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void windowDeactivated(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void windowClosing(WindowEvent e) {
				System.out.print("Saving");
				controller.exportToPKCS12WithAES(path, password);
			}
			
			@Override
			public void windowClosed(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void windowActivated(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		
		JPanel Buttons = new JPanel();
		panel.add(Buttons);
		Buttons.setLayout(new GridLayout(0, 1, 0, 0));
		
		JButton newPairButton = new JButton("New Pair");
		Buttons.add(newPairButton);
		newPairButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				JDialog d=new NewPairDialog(controller);
				d.setVisible(true);
				d.addWindowListener(new WindowListener() {
					
					@Override
					public void windowOpened(WindowEvent e) {	
					}
					
					@Override
					public void windowIconified(WindowEvent e) {
					}
					
					@Override
					public void windowDeiconified(WindowEvent e) {
					}
					
					@Override
					public void windowDeactivated(WindowEvent e) {	
					}
					
					@Override
					public void windowClosing(WindowEvent e) {
					}
					
					@Override
					public void windowClosed(WindowEvent e) {
						pairList.setListData(controller.getCertificateInfoList(false).toArray());
						refreshButtons();
					}
					
					@Override
					public void windowActivated(WindowEvent e) {
					}
				});
				
			}
		});
		
		JButton importPairButton = new JButton("Import Pair");
		Buttons.add(importPairButton);
		importPairButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
			    FileNameExtensionFilter filter = new FileNameExtensionFilter("PKCS12 certificates", "p12");
			    chooser.addChoosableFileFilter(filter);
			    int returnVal = chooser.showOpenDialog(frame);
			    boolean noAES = true;
			    if(returnVal == JFileChooser.APPROVE_OPTION) {
			    	JLabel jPassword = new JLabel("Password");
			        JTextField password = new JPasswordField();
			        Object[] ob = {jPassword, password};
			        int result = JOptionPane.showConfirmDialog(null, ob, "Password", JOptionPane.OK_CANCEL_OPTION);
			        String passwordValue="";
			        if (result == JOptionPane.OK_OPTION) {
			            passwordValue = password.getText();
			            if(noAES){
			            	controller.importKeyStoreNoAES(chooser.getSelectedFile().getPath(), passwordValue);
			            } else {
			            	controller.importKeyStoreWithAES(chooser.getSelectedFile().getPath(), passwordValue);
			            }
			            pairList.setListData(controller.getCertificateInfoList(false).toArray());
						refreshButtons();
			        }
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
//			    FileNameExtensionFilter filter = new FileNameExtensionFilter("p12");
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
			            String path = chooser.getSelectedFile().getPath();
			            if (!path.endsWith(".p12"))
			                path += ".p12";
			            if(passwordValue.isEmpty()){
			            	controller.exportKeyPairToPKCS12NoAES(selectedPair.getAlias(), path, passwordValue);
			            } else {
			            	controller.exportKeyPairToPKCS12WithAES(selectedPair.getAlias(), path, passwordValue);
			            }
			            pairList.setListData(controller.getCertificateInfoList(false).toArray());
						refreshButtons();
			        }
			    }
			}
		});
		
		detailsButton = new JButton("Pair Details");
		detailsButton.setEnabled(false);
		Buttons.add(detailsButton);
		detailsButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				JTextArea tarea = new JTextArea(controller.printX509Certificate(selectedPair.getAlias()));
				tarea.setEditable(false);
				tarea.setSize(600, 600);
				tarea.setLineWrap(true);
				tarea.setWrapStyleWord(true);
				JOptionPane.showMessageDialog(frame, tarea);
			}
		});
		
		signCertButton = new JButton("Sign Certificate");
		signCertButton.setEnabled(false);
		Buttons.add(signCertButton);
		signCertButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				controller.generateCSR(selectedPair.getAlias());
				JTextArea tarea = new JTextArea(controller.printX509Certificate(selectedPair.getAlias()));
				tarea.setEditable(false);
				tarea.setSize(600, 600);
				tarea.setLineWrap(true);
				tarea.setWrapStyleWord(true);
				Object[] ob = {tarea};
				int result = JOptionPane.showConfirmDialog(null, ob, "Certificate", JOptionPane.OK_CANCEL_OPTION);
				if (result == JOptionPane.OK_OPTION){
					controller.signX509Certificate(selectedPair.getAlias());
					pairList.setListData(controller.getCertificateInfoList(false).toArray());
					refreshButtons();
				}
			}
		});
		
		
		exportCertButton = new JButton("Export Certificate");
		exportCertButton.setEnabled(false);
		Buttons.add(exportCertButton);
		exportCertButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
			    int returnVal = chooser.showSaveDialog(frame);
			    if(returnVal == JFileChooser.APPROVE_OPTION) {
			    	String path = chooser.getSelectedFile().getPath();
		            if (!path.endsWith(".cer"))
		                path += ".cer";
		            controller.exportX509Certificate(selectedPair.getAlias(), path);
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
		pairList.setListData(controller.getCertificateInfoList(false).toArray());
		pairList.addListSelectionListener(new ListSelectionListener() {
			
			@Override
			public void valueChanged(ListSelectionEvent e) {
				refreshButtons();
			}
		});
		
	}
}
