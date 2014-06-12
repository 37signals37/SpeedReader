package main;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class BrowserLoader {
	
	Desktop desktop = Desktop.getDesktop();
	
	public void loadBrowser(String uriText){
		try {
			URI uri = new URI(uriText);
			desktop.browse(uri);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}			
	}
	
	
}
