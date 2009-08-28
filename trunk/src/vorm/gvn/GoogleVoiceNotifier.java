package vorm.gvn;

import java.awt.AWTException;
import java.awt.Desktop;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.ext.DefaultHandler2;
import org.xml.sax.helpers.XMLReaderFactory;


public class GoogleVoiceNotifier {
	private static Image noMsgImage = Toolkit.getDefaultToolkit().getImage("images/google-voice.png");
	private static Image newMsgImage = Toolkit.getDefaultToolkit().getImage("images/google-voice-new-msg.png");

	private static Timer timer = new Timer();
	private static HttpClient client = new HttpClient();
	private static TrayIcon trayIcon;
	private static OptionsGUI optGUI = new OptionsGUI();
	private static Options curOptions;
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		URL imgURL = GoogleVoiceNotifier.class.getResource("images/google-voice.png");
		noMsgImage = Toolkit.getDefaultToolkit().getImage(imgURL);
		imgURL = GoogleVoiceNotifier.class.getResource("images/google-voice-new-msg.png");
		newMsgImage = Toolkit.getDefaultToolkit().getImage(imgURL);
		
		setupSystrayIcon();
		try {
			Options o = new Options();
			String passwd = getPassword(o);
			if (passwd == null) {
				throw new Exception("User entered a null password");
			}
			o.setPassword(passwd);
			saveOptions(o);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
	
	private static String getPassword(Options o) {
		char[] password = null;
		JLabel label = new JLabel("Please enter the password for the google voice account: " + o.getUsername());
		JPasswordField pass = new JPasswordField();
		Object[] array = { label, pass };
		
		int res = JOptionPane.showConfirmDialog(null, array, 
								"Password for " + o.getUsername(),
								JOptionPane.OK_CANCEL_OPTION,
								JOptionPane.PLAIN_MESSAGE);
		
		if (res == JOptionPane.OK_OPTION) {
			password = pass.getPassword();
		} else {
			return null;
		}
		return new String(password);
	}

	private static int newMessages() {
		int newMessages = 0;
		JSONTokener tokener = new JSONTokener(getInbox());
		JSONObject object = null;
		try {
			object = new JSONObject(tokener);
			JSONObject messages = object.getJSONObject("messages");
			Iterator<?> keys = messages.keys();
			while (keys.hasNext()) {
				Object id = keys.next();
				JSONObject message = messages.getJSONObject(id.toString());
				if (!message.getBoolean("isRead")) {
					newMessages++;
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return newMessages;
	}
	
	private static void login(Options options) {
		PostMethod method = new PostMethod("https://www.google.com/accounts/ServiceLoginAuth?service=grandcentral");
		method.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
		method.addParameter("Email", options.getUsername());
		method.addParameter("Passwd", options.getPassword());
		
		try {
			int statusCode = client.executeMethod(method);
		        
	        if (statusCode != HttpStatus.SC_OK) {
	        	System.err.println("Method failed: " + method.getStatusLine());
	        }
		}
        catch (Exception e) {
        	e.printStackTrace();
        }
	}
	
	private static String getInbox() {
		String out = "";
		String request = "https://www.google.com/voice/inbox/recent/inbox/";
        
        GetMethod method = new GetMethod(request);
        InputStream rstream = null;
        
        try {
	        // Send GET request
	        int statusCode = client.executeMethod(method);
	        
	        if (statusCode != HttpStatus.SC_OK) {
	        	System.err.println("Method failed: " + method.getStatusLine());
	        }
	        
	        // Get the response body
	        rstream = method.getResponseBodyAsStream();
	        StringBuffer json = new StringBuffer();
	        XMLReader parser = XMLReaderFactory.createXMLReader();
	        parser.setContentHandler(new GoogleVoiceNotifier.ResponseHandler(json));
	        parser.parse(new InputSource(rstream));
	        
	        out = json.toString();
        }
        catch (Exception e) {
        	e.printStackTrace();
        }
        
        return out;
	}
	
	private static void checkAndDisplayMessages() {
		if(curOptions == null)
			return;
		int n;
		if((n = newMessages()) > 0) {
    		trayIcon.setImage(newMsgImage);
            trayIcon.displayMessage("New Messages!", 
                "You have " + n + " new messages!",
                TrayIcon.MessageType.INFO);
    	}
    	else {
    		trayIcon.setImage(noMsgImage);
    	}
	}
	
	public static void saveOptions(Options newOpts) {
		curOptions = newOpts;
		login(newOpts);
		setupTimer(newOpts);
	}
	
	private static void setupTimer(Options opts) {
		TimerTask task = new TimerTask() {
        	public void run() {
        		checkAndDisplayMessages();
        	}
        };
        timer.cancel();
        timer.purge();
        timer = new Timer();
        timer.scheduleAtFixedRate(task, 0, opts.getDelay());
	}
	
	private static void setupSystrayIcon() {

		if (SystemTray.isSupported()) {

		    SystemTray tray = SystemTray.getSystemTray();
		    
		    ActionListener exitListener = new ActionListener() {
		        public void actionPerformed(ActionEvent e) {
		            System.out.println("Exiting...");
		            System.exit(0);
		        }
		    };
		    
		    ActionListener optionsListener = new ActionListener() {
		        public void actionPerformed(ActionEvent e) {
		            optGUI.setVisible(true);
		        }
		    };
		            
		    PopupMenu popup = new PopupMenu();
		    MenuItem defaultItem = new MenuItem("Exit");
		    defaultItem.addActionListener(exitListener);
		    MenuItem optionsItem = new MenuItem("Options");
		    optionsItem.addActionListener(optionsListener);
		    
		    popup.add(optionsItem);
		    popup.add(defaultItem);

		    trayIcon = new TrayIcon(noMsgImage, "Google Voice Notifier", popup);

		    ActionListener actionListener = new ActionListener() {
		        public void actionPerformed(ActionEvent e) {
		        	checkAndDisplayMessages();
		        	
		        	if (Desktop.isDesktopSupported()) {
		        		try {
							URI uri = new URI("https://www.google.com/voice/");
							Desktop.getDesktop().browse(uri);
						} catch (URISyntaxException e1) {
							e1.printStackTrace();
						} catch (IOException e1) {
							e1.printStackTrace();
						}
		        	}
		        }
		    };
		            
		    trayIcon.setImageAutoSize(true);
		    trayIcon.addActionListener(actionListener);
		    

		    try {
		        tray.add(trayIcon);
		        if(curOptions != null) {
		    		login(curOptions);
		    		setupTimer(curOptions);
		    	}
		        
		    } catch (AWTException e) {
		        System.err.println("TrayIcon could not be added.");
		    }

		} else {

		    //  TODO System Tray is not supported

		}

	}
	
	private static class ResponseHandler extends DefaultHandler2 {
		private boolean inJSON = false;
		private StringBuffer data;
		
		public ResponseHandler(StringBuffer data) {
			this.data = data;
		}
		
		@Override
		public void endCDATA() throws SAXException {
			super.endCDATA();
		}

		@Override
		public void startCDATA() throws SAXException {
			super.startCDATA();
		}
		
		@Override
		public void characters(char[] ch, int start, int length)
				throws SAXException {
			if (/*inCDATA && */inJSON) {
				data.append(ch);
			}
			super.characters(ch, start, length);
		}

		@Override
		public void endElement(String uri, String localName, String qName)
				throws SAXException {
			if (qName.equals("json")) {
				inJSON = false;
			}
			super.endElement(uri, localName, qName);
		}

		@Override
		public void startElement(String uri, String localName, String qName,
				Attributes attributes) throws SAXException {
			if (qName.equals("json")) {
				inJSON = true;
			}
			super.startElement(uri, localName, qName, attributes);
		}
		
	}

}
