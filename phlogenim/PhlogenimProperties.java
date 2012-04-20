package phlogenim;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class PhlogenimProperties {

	static PhlogenimProperties instance = null;
	static final String configFileName = "phlogenim/phlogenim.config";
	static final String comment = "Configuration file for phlogenim";
	private boolean saveOnModify;
	private java.util.Properties p;
	
	public PhlogenimProperties() {
		p = new java.util.Properties();
		saveOnModify = true;
	}
	
	public static PhlogenimProperties getProperties( ) {
		if ( instance == null ) {
			instance = new PhlogenimProperties();
			try {
				instance.p.load( new FileInputStream( configFileName ) );
			} catch( IOException e ) {
				System.err.println( e.toString() );
				System.err.println( "Using default configuration...");
				instance = getDefaultProperties();
			}
		}
		return instance;
	}
	
	public static PhlogenimProperties getDefaultProperties() {
		PhlogenimProperties dp = new PhlogenimProperties();
		dp.set( "background.color", "ffffff");
		dp.set( "foreground.color", "0000ff");
		dp.set( "wall.color", "0f0f00");
		dp.set( "ball.color", "000000");
		
		return dp;
	}
	
	public String get( String key, String def ) {
		return p.getProperty( key, def );
	}
	public String set( String key, String value ) {
		String v = (String)p.setProperty( key, value );
		
		if ( saveOnModify ) {
			try {
				p.store( new FileOutputStream( configFileName ), comment );
			} catch( IOException e ) {
				System.err.println( e.toString() + " will not attempt auto-save...");
				saveOnModify = false;
			}
		}
		return v;
	}
	public String get( String key ) {
		return p.getProperty( key );
	}

}
