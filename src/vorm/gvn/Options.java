package vorm.gvn;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class Options {
	private static final String PROP_FILE = ".gvnotifier.conf";
	
	private static final String USERNAME_OPTION = "google.voice.username";
	private static final String DELAY_OPTION = "google.voice.delay";
	private static final String PROXY_TYPE_OPTION = "google.voice.proxyType";
	private static final String PROXY_HOST_OPTION = "google.voice.proxyHost";
	private static final String PROXY_PORT_OPTION = "google.voice.proxyPort";
	private static final String CHECK_FOR_UPDATES_OPTION = "google.voice.checkForUpdates";
	
	public enum ProxyType { NONE, SOCKS, HTTP };
	
	private String username;
	private String password;
	
	private ProxyType proxyType;
	private String proxyHost;
	private int    proxyPort;
	
	private long delay;
	
	private boolean checkForUpdates;
	
	private Properties prop;
	
	public Options(String username, String password, long delay) {
		this(username, password, delay, ProxyType.NONE, "", 1080, true);
	}
	
	public Options(String username, String password, long delay, 
	                        ProxyType type, String host, int port, boolean checkForUpdates) {
	    this.username  = username;
        this.password  = password;
        this.delay     = delay;
        this.proxyHost = host;
        this.proxyType = type;
        this.proxyPort = port;
        this.checkForUpdates = checkForUpdates;
        
        prop = new Properties();
        try {
            File f = new File(PROP_FILE);
            
            prop.setProperty(USERNAME_OPTION, this.username);
            prop.setProperty(DELAY_OPTION, Long.toString(this.delay));
            if (proxyType != null || proxyType != ProxyType.NONE) { 
                prop.setProperty(PROXY_TYPE_OPTION, proxyType.toString());
                prop.setProperty(PROXY_HOST_OPTION, proxyHost);
                prop.setProperty(PROXY_PORT_OPTION, Integer.toString(proxyPort));
            }
            
            prop.setProperty(CHECK_FOR_UPDATES_OPTION, Boolean.toString(checkForUpdates));
            
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
		this.username = prop.getProperty(USERNAME_OPTION);
		if (this.username == null) {
			throw new Exception("Username was not in properties file");
		}
		this.delay = new Long(prop.getProperty(DELAY_OPTION));
		try {
			this.proxyType = Enum.valueOf(ProxyType.class, prop.getProperty(PROXY_TYPE_OPTION));
			if (this.proxyType != null) {
				this.proxyHost = prop.getProperty(PROXY_HOST_OPTION);
				this.proxyPort = new Integer(prop.getProperty(PROXY_PORT_OPTION));
			}
		} catch (IllegalArgumentException e) {
			this.proxyType = ProxyType.NONE;
		}
		
		try {
			this.checkForUpdates = Boolean.parseBoolean((String)prop.get(CHECK_FOR_UPDATES_OPTION));
		} catch (IllegalArgumentException e) {
			this.checkForUpdates = true;
		}
	}
	
	public static Options getDefaultOptions() {
		return new Options("","",60000,ProxyType.NONE,"",1080,true);
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
		prop.setProperty(USERNAME_OPTION, username);
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
		prop.setProperty(DELAY_OPTION, Long.toString(this.delay));
		try {
			prop.store(new FileOutputStream(new File(PROP_FILE)) , "");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public ProxyType getProxyType() {
        return proxyType;
    }

    public String getProxyHost() {
        return proxyHost;
    }

    public int getProxyPort() {
        return proxyPort;
    }

	public boolean isCheckForUpdates() {
		return checkForUpdates;
	}

	public void setCheckForUpdates(boolean checkForUpdates) {
		this.checkForUpdates = checkForUpdates;
	}
}
