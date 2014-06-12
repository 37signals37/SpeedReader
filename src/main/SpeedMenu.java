package main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JTextField;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import javax.swing.JList;
import javax.swing.JButton;

public class SpeedMenu extends JFrame{

	MasterControlVariables mcv = null;

	private JPanel contentPane;
	public JTextField tf_url;

	Vector<String> readLns = new Vector<String>();
	Vector<String> outLnLns = new Vector<String>();
	private JList list;
	String[] wrdSplt = null;
	String[] sntcSplt = null;

	MyTextPane txtPn = null;
	private JButton btn_playAll;
	private JButton btn_pause;

	boolean running = false;
	Thread rThread = null;
	Long pause = 1000L;
	int wpm = 250;

	private JTextField tfWPM;
	private JButton btn_Pbck;
	private JButton btn_Pfwd;
	private JButton btn_Sbck;
	private JButton btn_Sfwd;
	private JButton btn_plus50;
	private JButton btn_mns50;

	int outLnIndx = 0;
	
	Font fontPlain = null;
	Font fontBold = null;
	
	static Image image = null;
	
	public SpeedMenu(MasterControlVariables mcv) {

		this.mcv = mcv;

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); setBounds(100, 100, 487, 595); contentPane = new JPanel(); contentPane.setBorder(new EmptyBorder(5, 5, 5, 5)); setContentPane(contentPane); contentPane.setLayout(null);		
		txtPn = new MyTextPane(); txtPn.setBounds(10, 335, 451, 210); contentPane.add(txtPn);

		StyledDocument doc = txtPn.getStyledDocument();
		SimpleAttributeSet center = new SimpleAttributeSet();
		StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
		doc.setParagraphAttributes(0, doc.getLength(), center, false);
		
		fontPlain = new Font(Font.DIALOG, Font.PLAIN, 50);
		fontBold = new Font(Font.DIALOG, Font.BOLD, 50);
		txtPn.setFont(fontPlain);

		tf_url = new JTextField(); tf_url.setBounds(10, 11, 451, 20); contentPane.add(tf_url);	tf_url.setColumns(10);

		JScrollPane scrollPane = new JScrollPane(); scrollPane.setBounds(10, 42, 451, 208);	contentPane.add(scrollPane);

		list = new JList(); scrollPane.setViewportView(list);
		list.setFont( list.getFont().deriveFont(Font.PLAIN) );

		JButton btn_playLine = new JButton(">"); btn_playLine.setBounds(202, 256, 41, 23); contentPane.add(btn_playLine);
		btn_playAll = new JButton("Play All"); btn_playAll.setBounds(243, 256, 77, 23);	contentPane.add(btn_playAll);
		btn_pause = new JButton("||"); btn_pause.setBounds(320, 256, 41, 23); contentPane.add(btn_pause);

		tfWPM = new JTextField(); tfWPM.setText("250"); tfWPM.setBounds(224, 306, 47, 20); contentPane.add(tfWPM); tfWPM.setColumns(10);

		btn_Pbck = new JButton("<P"); btn_Pbck.setBounds(10, 280, 65, 23); contentPane.add(btn_Pbck);
		btn_Pfwd = new JButton("P>"); btn_Pfwd.setBounds(75, 280, 65, 23); contentPane.add(btn_Pfwd);
		btn_Sbck = new JButton("<S"); btn_Sbck.setBounds(10, 305, 65, 23); contentPane.add(btn_Sbck);
		btn_Sfwd = new JButton("S>"); btn_Sfwd.setBounds(75, 305, 65, 23); contentPane.add(btn_Sfwd);
		JButton btn_secBck = new JButton("<Sec"); btn_secBck.setBounds(10, 255, 65, 23); contentPane.add(btn_secBck);
		JButton btn_secFwd = new JButton("Sec>"); btn_secFwd.setBounds(75, 255, 65, 23); contentPane.add(btn_secFwd);
		
		btn_plus50 = new JButton("+50"); btn_plus50.setBounds(274, 305, 60, 23); contentPane.add(btn_plus50);
		btn_mns50 = new JButton("-50"); btn_mns50.setBounds(160, 305, 60, 23); contentPane.add(btn_mns50);

		tf_url.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {					
					getHTMLcontent(tf_url.getText());
				}
			}
		});
		
		btn_playLine.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				running = true;
				Thread t = new Thread() {
					public void run() {
						playLine(list.getSelectedIndex());        
					}
				};			
				rThread = t;
				rThread.start();
			}
		});

		btn_playAll.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				running = true;
				Thread t = new Thread() {
					public void run() {
						playAll(list.getSelectedIndex());        
					}
				};			
				rThread = t;
				rThread.start();
			}
		});

		btn_pause.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				running = false;
				rThread.interrupt();				
			}
		});

		btn_secFwd.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				jumpToSection("fwd");
			}
		});
		
		btn_secBck.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				jumpToSection("bck");
			}
		});
		
		btn_Pbck.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
	
			}
		});
		
		btn_Pfwd.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
	
			}
		});
		
		btn_Sbck.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
	
			}
		});
		
		btn_Sfwd.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
	
			}
		});
		
		
		tfWPM.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {					
					wpm = Integer.valueOf(tfWPM.getText());
				}
			}
		});

		btn_plus50.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				wpm = Integer.valueOf(tfWPM.getText()) + 50;
				tfWPM.setText(String.valueOf(wpm));
			}
		});

		btn_mns50.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				wpm = Integer.valueOf(tfWPM.getText()) - 50;
				if(wpm < 1){wpm = 1;}
				tfWPM.setText(String.valueOf(wpm));
			}
		});


	}

	private void jumpToSection(String dir) {
		outLnIndx = list.getSelectedIndex();
		
		int setIndx = -1;
		
		if(dir.equalsIgnoreCase("fwd")){
			for(int iii = outLnIndx; iii < outLnLns.size(); iii++){
				if(outLnLns.get(iii).contains("<html><b>") && outLnIndx != iii){
					setIndx = iii;
					break;
				}			
			}
		}
			
		if(dir.equalsIgnoreCase("bck")){
			for(int iii = outLnIndx; iii >= 0; iii--){
				if(outLnLns.get(iii).contains("<html><b>") && outLnIndx != iii){
					setIndx = iii;
					break;
				}			
			}
		}
		if(setIndx == -1){
			setIndx = 0;
		}
		list.setSelectedIndex(setIndx);
		list.ensureIndexIsVisible(setIndx);
	}
	
	public void getHTMLcontent(String urlAddress){
		String stString = null;
		readLns.clear();		
		readLns.addAll(mcv.htmlR.getParsedParagraphLines(urlAddress));

//readLns.addAll(mcv.ff.getFilesInLns(new File("c:\\Users\\BeckyBot\\tits.txt")));
//mcv.htmlR.removeNonBodyNonHeadersNonParagraphLines(readLns);
//mcv.htmlR.parseHTMLlines(readLns);
//mcv.htmlR.setHeaderLinesinBold(readLns);

		outLnLns.clear();
		for(String s: readLns){
			
			if(s.contains("<html><b>")){				
				outLnLns.add(s);
			}else{
				wrdSplt = s.split(" ");
				
				if(wrdSplt.length < 7){
					outLnLns.add(s);
				}else{
					stString = "    ";
					for(int iii = 0; iii < 7; iii++){
						stString = stString + " " + wrdSplt[iii];
					}
					stString = stString + "...";
					outLnLns.add(stString);
				}		
			}
		}
		list.setListData(outLnLns);
		list.setSelectedIndex(0);
	}

	public void playLine(int lnNum){
		//		String line = "How to Use Buttons, Check Boxes, and Radio Buttons How to Use Buttons, Check Boxes, and Radio Buttons How to Use Buttons, Check Boxes, and Radio Buttons How to Use Buttons, Check Boxes, and Radio Buttons How to Use Buttons, Check Boxes, and Radio Buttons";
		String line = readLns.get(lnNum);
		
		if(line.contains("<html><b>")){
			txtPn.setFont(fontBold);
		}else{
			txtPn.setFont(fontPlain);
		}
		
		line = line.replace("<html><b>", "");
		line = line.replace("</b></html>", "");

		wrdSplt = line.split(" ");

		Long time1 = null;
		Long time2 = null;

	
		
		for(int iii = 0; iii < wrdSplt.length; iii++){

			time1 = System.currentTimeMillis();
			txtPn.setText("\n" + wrdSplt[iii]);
			setPauseTime(wrdSplt[iii]);
			time2 = System.currentTimeMillis();	

			while(time2 - time1 < pause){
				time2 = System.currentTimeMillis();	
			}

			if(!running){
				break;
			}	
		}
	}

	private void playAll(int selectedIndex) {
		for(int iii = selectedIndex; iii < readLns.size(); iii++){
			list.setSelectedIndex(iii);
			playLine(iii);

			if(!running){
				break;
			}			
		}
	}

	private void setPauseTime(String word){
		pause = (long) Double.parseDouble(String.valueOf(60.0/wpm * 1000));
		
		if(word.length() > 7){			
			pause = (long) Double.parseDouble(String.valueOf(60.0/wpm * (1000 + (word.length()-7) * 100)));			
		}		
		
		if(word.contains(",")){			
			pause = (long) (Double.parseDouble(String.valueOf(pause)) * 1.5);
		}else if(word.contains(".") || word.contains(".[")){			
			pause = (long) (Double.parseDouble(String.valueOf(pause)) * 2);
		}
		
		if(txtPn.getFont().isBold()){
			pause = (long) (Double.parseDouble(String.valueOf(pause)) * 2);
		}
		
		
		System.out.println(word + "   " + word.length() + "    " + pause);
	}
	
	private static class MyTextPane extends JTextPane {
        public MyTextPane() {
            super();
            setOpaque(false);

            // this is needed if using Nimbus L&F - see http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6687960
            setBackground(new Color(0,0,0,0));
        }

        @Override
        protected void paintComponent(Graphics g) {
            // set background green - but can draw image here too
//            g.setColor(Color.GREEN);
//            g.fillRect(0, 0, getWidth(), getHeight());

            // uncomment the following to draw an image
            try {                
                image = ImageIO.read(new File("c:\\Users\\BeckyBot\\workspace1\\SpeedReader\\lib\\txtPnBckgrnd.png"));

//                getClass().getClassLoader().getResourceAsStream(
//                	    "src/com/utils/Configuration.xml");
             } catch (IOException ex) {
                  // handle exception...
             }
             g.drawImage(image, 0, 0, this);


            super.paintComponent(g);
        }
    }
	
}
