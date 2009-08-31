package vorm.gvn;

import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
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
	private JLabel proxyTypeLabel = new JLabel("Proxy Type");
	private JTextField usrField = new JTextField(30);
	private JPasswordField passField = new JPasswordField(30);
	private JTextField delayField = new JTextField(30);
	private JButton saveButton = new JButton("Save");
	private JButton cancelButton = new JButton("Cancel");
	
	
	public OptionsGUI() {
		setTitle("GV Nofier Options");
		setSize(300, 150); // The GUI dimensions
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
				            		Long.parseLong(delayField.getText()) * 1000));
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
        
        formUtility.addLabel("", form);
        formUtility.addMiddleField(saveButton, form);
        formUtility.addMiddleField(cancelButton, form);
		
		this.add(form);
	}
}
