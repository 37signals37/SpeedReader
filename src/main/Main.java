package main;

import java.awt.Font;
import java.io.File;
import java.util.Vector;

import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

public class Main {

	public static void main(String[] args) {

		MasterControlVariables mcv = new MasterControlVariables();
		
		SpeedMenu mm = new SpeedMenu(mcv);
		mm.setVisible(true);
		
		mm.tf_url.setText("http://en.wikipedia.org/wiki/PlayStation_3");		
		mm.getHTMLcontent("http://en.wikipedia.org/wiki/PlayStation_3");		
	}
}
