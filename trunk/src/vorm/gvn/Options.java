package vorm.gvn;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Options {
	private static final String PROP_FILE = ".gvnotifier.conf";
	private static final String USERNAME = "google.voice.username";
	private static final String DELAY = "google.voice.delay";
	
	private String username;
	private String password;
	
	private long delay;
	
	private Properties prop;
	
	public Options(String username, String password, long delay) {
		this.username = username;
		this.password = password;
		this.delay = delay;
		
		prop = new Properties();
		try {
			File f = new File(PROP_FILE);
			
			prop.setProperty(USERNAME, this.username);
			prop.setProperty(DELAY, Long.toString(this.delay));
			
			prop.store(new FileOutputStream(f) , "");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * I throw an exception here if the file doesn't exist, or have the correct structure
	 * so that the program can know that nothing was loaded, and to have the old,
	 * default behavior.
	 */
	public Options() throws Exception {
		prop = new Properties();
		
		File f = new File(PROP_FILE);
		prop.load(new FileReader(f));
		this.username = prop.getProperty(USERNAME);
		if (this.username == null) {
			throw new Exception("Username was not in properties file");
		}
		this.delay = new Long(prop.getProperty(DELAY));
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
		prop.setProperty(USERNAME, username);
		try {
			prop.store(new FileOutputStream(new File(PROP_FILE)) , "");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public long getDelay() {
		return delay;
	}

	public void setDelay(long delay) {
		this.delay = delay;
		prop.setProperty(DELAY, Long.toString(this.delay));
		try {
			prop.store(new FileOutputStream(new File(PROP_FILE)) , "");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
}
