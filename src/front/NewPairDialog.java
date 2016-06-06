package front;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.UIManager;
import java.awt.GridLayout;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import java.awt.Font;
import javax.swing.SpinnerDateModel;
import java.util.Date;
import java.util.Calendar;
import javax.swing.JCheckBox;
import javax.swing.JEditorPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

public class NewPairDialog extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private JTextField textField;
	private JLabel nameLabel;
	private JLabel versionLabel;
	private JTextField cTextField;
	private JTextField stField;
	private JLabel serNumLabel;
	private JLabel signAlgLabel;
	private JLabel cLabel;
	private JLabel stLabel;
	private JTextField lTextField;
	private JLabel lLabel;
	private JTextField oField;
	private JLabel oLabel;
	private JTextField ouTextField;
	private JLabel ouLabel;
	private JTextField cnTextField;
	private JLabel notBeforeLabel;
	private JSpinner notBeforespinner;
	private JLabel notAfterLabel;
	private JSpinner notAfterSpinner;
	private JLabel keyInfoLabel;
	private JLabel placeHolderLabel2;
	private JLabel keyAlgLabel;
	private JComboBox keyAlgBox;
	private JLabel keySizeLabel;
	private JSpinner keySizeSpinner;
	private JLabel extLabel;
	private JLabel placeHolderLabel3;
	private JLabel basConstLabel;
	private JTextField BasicConstrTextField;
	private JCheckBox basicConstrCheckBox;
	private JLabel placeHolderLabel4;
	private JLabel issAltNameLabel;
	private JTextField alrIssNameTextField;
	private JCheckBox issAltNameCheckBox;
	private JLabel placeHolderLabel5;
	private JLabel keyUsageLabel;
	private JTextArea keyUsageTextArea;
	private JCheckBox keyUsageCheckBox;
	private JLabel placeHolderLabel6;
	private JLabel necessaryLabel;
	private JButton genKeyButton;
	private JPanel pairPanel;
	private JLabel publExpLabel;
	private JLabel privExpLabel;
	private JPanel exponentPanel;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			NewPairDialog dialog = new NewPairDialog();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public NewPairDialog() {
		setTitle("Generate New Pair");
		setBounds(100, 100, 750, 700);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new GridLayout(0, 2, 0, 0));
		{
			JPanel dataPanel = new JPanel();
			contentPanel.add(dataPanel);
			dataPanel.setLayout(new GridLayout(0, 2, 0, 0));
			{
				versionLabel = new JLabel("Version*:");
				versionLabel.setForeground(UIManager.getColor("OptionPane.errorDialog.titlePane.foreground"));
				dataPanel.add(versionLabel);
			}
			{
				JSpinner versionSpinner = new JSpinner();
				versionLabel.setLabelFor(versionSpinner);
				versionSpinner.setModel(new SpinnerNumberModel(3, 3, 3, 1));
				dataPanel.add(versionSpinner);
			}
			{
				serNumLabel = new JLabel("Serial Number*: ");
				serNumLabel.setForeground(UIManager.getColor("OptionPane.errorDialog.titlePane.foreground"));
				dataPanel.add(serNumLabel);
			}
			{
				JSpinner serNumSpinner = new JSpinner();
				serNumLabel.setLabelFor(serNumSpinner);
				serNumSpinner.setModel(new SpinnerNumberModel(new Integer(0), new Integer(0), null, new Integer(1)));
				dataPanel.add(serNumSpinner);
			}
			{
				signAlgLabel = new JLabel("Signature Algorithm*: ");
				signAlgLabel.setForeground(UIManager.getColor("OptionPane.errorDialog.titlePane.foreground"));
				dataPanel.add(signAlgLabel);
			}
			{
				JComboBox signAlgBox = new JComboBox();
				signAlgLabel.setLabelFor(signAlgBox);
				signAlgBox.setModel(new DefaultComboBoxModel(new String[] {"SHA-1"}));
				signAlgBox.setSelectedIndex(0);
				signAlgBox.setMaximumRowCount(1);
				dataPanel.add(signAlgBox);
			}
			{
				JLabel issuerLabel = new JLabel("Issuer:");
				issuerLabel.setForeground(UIManager.getColor("CheckBoxMenuItem.acceleratorForeground"));
				issuerLabel.setFont(new Font("Dialog", Font.BOLD, 14));
				dataPanel.add(issuerLabel);
			}
			{
				JLabel placeHolderLabel = new JLabel("");
				dataPanel.add(placeHolderLabel);
			}
			{
				cLabel = new JLabel("C*:");
				cLabel.setForeground(UIManager.getColor("OptionPane.errorDialog.titlePane.foreground"));
				dataPanel.add(cLabel);
			}
			{
				cTextField = new JTextField();
				cLabel.setLabelFor(cTextField);
				dataPanel.add(cTextField);
				cTextField.setColumns(10);
			}
			{
				stLabel = new JLabel("ST*:");
				stLabel.setForeground(UIManager.getColor("OptionPane.errorDialog.titlePane.foreground"));
				dataPanel.add(stLabel);
			}
			{
				stField = new JTextField();
				stLabel.setLabelFor(stField);
				dataPanel.add(stField);
				stField.setColumns(10);
			}
			{
				lLabel = new JLabel("L*:");
				lLabel.setForeground(UIManager.getColor("OptionPane.errorDialog.titlePane.foreground"));
				dataPanel.add(lLabel);
			}
			{
				lTextField = new JTextField();
				lLabel.setLabelFor(lTextField);
				dataPanel.add(lTextField);
				lTextField.setColumns(10);
			}
			{
				oLabel = new JLabel("O*:");
				oLabel.setForeground(UIManager.getColor("OptionPane.errorDialog.titlePane.foreground"));
				dataPanel.add(oLabel);
			}
			{
				oField = new JTextField();
				oLabel.setLabelFor(oField);
				dataPanel.add(oField);
				oField.setColumns(10);
			}
			{
				ouLabel = new JLabel("OU*:");
				ouLabel.setForeground(UIManager.getColor("OptionPane.errorDialog.titlePane.foreground"));
				dataPanel.add(ouLabel);
			}
			{
				ouTextField = new JTextField();
				ouLabel.setLabelFor(ouTextField);
				dataPanel.add(ouTextField);
				ouTextField.setColumns(10);
			}
			{
				JLabel cnLabel = new JLabel("CN*:");
				cnLabel.setForeground(UIManager.getColor("OptionPane.errorDialog.titlePane.foreground"));
				dataPanel.add(cnLabel);
			}
			{
				cnTextField = new JTextField();
				dataPanel.add(cnTextField);
				cnTextField.setColumns(10);
			}
			{
				JLabel validityLabel = new JLabel("Validity:");
				validityLabel.setForeground(UIManager.getColor("CheckBoxMenuItem.acceleratorForeground"));
				validityLabel.setFont(new Font("Dialog", Font.BOLD, 14));
				dataPanel.add(validityLabel);
			}
			{
				JLabel placeHolderLabel1 = new JLabel("");
				dataPanel.add(placeHolderLabel1);
			}
			{
				notBeforeLabel = new JLabel("Not Before*:");
				notBeforeLabel.setForeground(UIManager.getColor("OptionPane.errorDialog.titlePane.foreground"));
				dataPanel.add(notBeforeLabel);
			}
			{
				notBeforespinner = new JSpinner();
				notBeforeLabel.setLabelFor(notBeforespinner);
				notBeforespinner.setModel(new SpinnerDateModel(new Date(1465164000000L), null, null, Calendar.DAY_OF_YEAR));
				dataPanel.add(notBeforespinner);
			}
			{
				notAfterLabel = new JLabel("Not After*:");
				notAfterLabel.setToolTipText("");
				notAfterLabel.setForeground(UIManager.getColor("OptionPane.errorDialog.titlePane.foreground"));
				dataPanel.add(notAfterLabel);
			}
			{
				notAfterSpinner = new JSpinner();
				notAfterLabel.setLabelFor(notAfterSpinner);
				notAfterSpinner.setModel(new SpinnerDateModel(new Date(1465164000000L), null, null, Calendar.DAY_OF_YEAR));
				dataPanel.add(notAfterSpinner);
			}
			{
				keyInfoLabel = new JLabel("Key Info:");
				keyInfoLabel.setForeground(UIManager.getColor("CheckBoxMenuItem.acceleratorForeground"));
				keyInfoLabel.setFont(new Font("Dialog", Font.BOLD, 14));
				dataPanel.add(keyInfoLabel);
			}
			{
				placeHolderLabel2 = new JLabel("");
				dataPanel.add(placeHolderLabel2);
			}
			{
				keyAlgLabel = new JLabel("Key Algorithm*: ");
				keyAlgLabel.setForeground(UIManager.getColor("OptionPane.errorDialog.titlePane.foreground"));
				dataPanel.add(keyAlgLabel);
			}
			{
				keyAlgBox = new JComboBox();
				keyAlgLabel.setLabelFor(keyAlgBox);
				keyAlgBox.setModel(new DefaultComboBoxModel(new String[] {"RSA"}));
				keyAlgBox.setSelectedIndex(0);
				keyAlgBox.setMaximumRowCount(1);
				dataPanel.add(keyAlgBox);
			}
			{
				keySizeLabel = new JLabel("Key Size*:");
				keySizeLabel.setForeground(UIManager.getColor("OptionPane.errorDialog.titlePane.foreground"));
				dataPanel.add(keySizeLabel);
			}
			{
				keySizeSpinner = new JSpinner();
				keySizeSpinner.setModel(new SpinnerNumberModel(new Integer(1024), new Integer(4), null, new Integer(1)));
				dataPanel.add(keySizeSpinner);
			}
			{
				extLabel = new JLabel("Extensions:");
				extLabel.setForeground(UIManager.getColor("CheckBoxMenuItem.acceleratorForeground"));
				extLabel.setFont(new Font("Dialog", Font.BOLD, 14));
				dataPanel.add(extLabel);
			}
			{
				placeHolderLabel3 = new JLabel("");
				dataPanel.add(placeHolderLabel3);
			}
			{
				basConstLabel = new JLabel("Basic Constraints:");
				dataPanel.add(basConstLabel);
			}
			{
				BasicConstrTextField = new JTextField();
				basConstLabel.setLabelFor(BasicConstrTextField);
				dataPanel.add(BasicConstrTextField);
				BasicConstrTextField.setColumns(10);
			}
			{
				placeHolderLabel4 = new JLabel("");
				dataPanel.add(placeHolderLabel4);
			}
			{
				basicConstrCheckBox = new JCheckBox("critical");
				dataPanel.add(basicConstrCheckBox);
			}
			{
				issAltNameLabel = new JLabel("Issuer Alternative Name: ");
				dataPanel.add(issAltNameLabel);
			}
			{
				alrIssNameTextField = new JTextField();
				issAltNameLabel.setLabelFor(alrIssNameTextField);
				dataPanel.add(alrIssNameTextField);
				alrIssNameTextField.setColumns(10);
			}
			{
				placeHolderLabel5 = new JLabel("");
				dataPanel.add(placeHolderLabel5);
			}
			{
				issAltNameCheckBox = new JCheckBox("critical");
				dataPanel.add(issAltNameCheckBox);
			}
			{
				keyUsageLabel = new JLabel("Key Usage:");
				dataPanel.add(keyUsageLabel);
			}
			{
				keyUsageTextArea = new JTextArea();
				keyUsageLabel.setLabelFor(keyUsageTextArea);
				dataPanel.add(keyUsageTextArea);
			}
			{
				placeHolderLabel6 = new JLabel("");
				dataPanel.add(placeHolderLabel6);
			}
			{
				keyUsageCheckBox = new JCheckBox("critical");
				dataPanel.add(keyUsageCheckBox);
			}
			{
				necessaryLabel = new JLabel("* - fields are necessary!");
				necessaryLabel.setHorizontalAlignment(SwingConstants.CENTER);
				necessaryLabel.setForeground(UIManager.getColor("OptionPane.errorDialog.titlePane.foreground"));
				necessaryLabel.setFont(new Font("Dialog", Font.BOLD, 11));
				dataPanel.add(necessaryLabel);
			}
			{
				genKeyButton = new JButton("Generate Pair");
				dataPanel.add(genKeyButton);
			}
		}
		{
			pairPanel = new JPanel();
			contentPanel.add(pairPanel);
			pairPanel.setLayout(new BorderLayout(0, 0));
			{
				exponentPanel = new JPanel();
				pairPanel.add(exponentPanel, BorderLayout.SOUTH);
				{
					privExpLabel = new JLabel("Private Exponent:");
					exponentPanel.add(privExpLabel);
				}
				{
					publExpLabel = new JLabel("Public Exponent:");
					exponentPanel.add(publExpLabel);
				}
			}
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				nameLabel = new JLabel("Name*: ");
				nameLabel.setForeground(UIManager.getColor("OptionPane.errorDialog.titlePane.foreground"));
				buttonPane.add(nameLabel);
			}
			{
				textField = new JTextField();
				nameLabel.setLabelFor(textField);
				buttonPane.add(textField);
				textField.setColumns(10);
			}
			{
				JButton saveButton = new JButton("Save");
				saveButton.setActionCommand("Save");
				buttonPane.add(saveButton);
				getRootPane().setDefaultButton(saveButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
	}
}
