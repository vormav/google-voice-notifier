package vorm.gvn;

import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

public class OptionsGUI extends JFrame {
	private static final long serialVersionUID = 1L;
	
	private JPanel form = new JPanel();
	private JLabel usrLabel = new JLabel("Email:");
	private JLabel passLabel = new JLabel("Password:");
	private JLabel delayLabel = new JLabel("Delay (in seconds):");
	private JLabel proxyTypeLabel = new JLabel("Proxy Type:");
	private JLabel proxyHostLabel = new JLabel("Proxy Host:");
	private JLabel proxyPortLabel = new JLabel("Proxy Port:");
	private JTextField usrField = new JTextField(30);
	private JPasswordField passField = new JPasswordField(30);
	private JTextField delayField = new JTextField(30);
	private JButton saveButton = new JButton("Save");
	private JButton cancelButton = new JButton("Cancel");
	private JComboBox proxyTypeComboBox = new JComboBox(
	        new Options.ProxyType[] {null, Options.ProxyType.HTTP, Options.ProxyType.SOCKS});
	private JTextField proxyHostField = new JTextField(30);
	private JTextField proxyPortField = new JTextField(30);
	
	
	public OptionsGUI() {
		setTitle("GV Nofier Options");
		setSize(300, 200); // The GUI dimensions
		setLocation(new Point(150, 150)); //The GUI position
		setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);

		ActionListener cancel = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
	            setVisible(false);
	        }
		};
		cancelButton.addActionListener(cancel);
		
		ActionListener save = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
	            setVisible(false);
	            GoogleVoiceNotifier.saveOptions(
	            		new Options(usrField.getText(), 
				            		new String(passField.getPassword()), 
				            		Long.parseLong(delayField.getText()) * 1000,
				            		(Options.ProxyType)proxyTypeComboBox.getSelectedItem(),
				            		proxyHostField.getText(),
				            		new Integer(proxyPortField.getText())));
	        }
		};
		saveButton.addActionListener(save);
		
        form.setLayout(new GridBagLayout());
        FormUtility formUtility = new FormUtility();
        
        formUtility.addLabel(usrLabel, form);
        formUtility.addLastField(usrField, form);
        
        formUtility.addLabel(passLabel, form);
        formUtility.addLastField(passField, form);
        
        formUtility.addLabel(delayLabel, form);
        formUtility.addLastField(delayField, form);
        
        formUtility.addLabel(proxyTypeLabel, form);
        formUtility.addLastField(proxyTypeComboBox, form);
        
        formUtility.addLabel(proxyHostLabel, form);
        formUtility.addLastField(proxyHostField, form);
        
        formUtility.addLabel(proxyPortLabel, form);
        formUtility.addLastField(proxyPortField, form);
        
        formUtility.addLabel("", form);
        formUtility.addMiddleField(saveButton, form);
        formUtility.addMiddleField(cancelButton, form);
		
		this.add(form);
	}
	
	public void displayOptions(Options o) {
	    if (o != null) {
	        usrField.setText(o.getUsername());
	        passField.setText(o.getPassword());
	        delayField.setText(Long.toString(o.getDelay()/1000));

	        if (o.getProxyType() == null) {
	            proxyTypeComboBox.setSelectedIndex(0);
	            proxyHostField.setText("");
	            proxyPortField.setText("1080");
	        } else {
	            proxyTypeComboBox.setSelectedItem(o.getProxyType());
	            proxyHostField.setText(o.getProxyHost());
	            proxyPortField.setText(Integer.toString(o.getProxyPort()));
	        }
	    }
	    setVisible(true);
	}
}
