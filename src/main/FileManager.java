package main;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JScrollPane;
import javax.swing.JList;
import javax.swing.JLabel;

//import org.apache.commons.io.FileUtils;

public class FileManager extends JFrame {

	main.MasterControlVariables mcv = null;
	
	private JPanel contentPane;

	String drcDrctyPth = null;
	String actDrctyPth = null;
	
	Vector<String> v_dirFls = new Vector<String>();
	
	public File dirHomeFldr = new File("H:\\SPYTicks2 Directories"); 
	public File actHomeFldr = new File("H:\\SPYTicks2");
	
	public FileManager(main.MasterControlVariables mcv) {
		
		this.mcv = mcv;
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); setBounds(100, 100, 450, 300); contentPane = new JPanel(); contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));setContentPane(contentPane); contentPane.setLayout(null);
 
		JScrollPane scrollPane = new JScrollPane(); scrollPane.setBounds(10, 38, 414, 212);	contentPane.add(scrollPane);
		JList jlist_fileViewer = new JList();scrollPane.setViewportView(jlist_fileViewer);
		JLabel lbl_dirPath = new JLabel("New label"); lbl_dirPath.setBounds(10, 11, 414, 14); contentPane.add(lbl_dirPath);
		
	}
	
	public void addEntryToFileDirectory(File actFl){

		File actPrntFldr = actFl.getParentFile();
		File dirPrntFldr = new File(actPrntFldr.getPath().replace("SPYTicks2", "SPYTicks2 Directories"));
		
		if(!dirPrntFldr.exists()){
			updateFolderFiles(actPrntFldr, false);
		}else{
			Vector<String> outLns = new Vector<String>();
			File dirFl = new File(dirPrntFldr.getPath() + "\\Directory Files.txt");	
			
			if(!dirFl.exists()){
				outLns.add(actFl.getName());
				writeOutLns(dirFl, outLns);
			}else{
				outLns.addAll(mcv.ff.getFilesInLns(dirFl));
				HashSet<String> flNms = new HashSet<String>(outLns);
				flNms.add(actFl.getName());
				outLns.clear();
				outLns.addAll(flNms);
				Collections.sort(outLns);
				writeOutLns(dirFl, outLns);
			}
		}	
	}
	
	public String getFileExt(File fl){
		
		String flNm = fl.getName();
		
		if(flNm.contains(".")){
			return flNm.substring(flNm.lastIndexOf("."), flNm.length());
		}
						
		return null;
	}
	
	public Vector<String> getFileNamesFromFolder(File inFldr){
		Vector<String> outNms = new Vector<String>();

		File drctyFl = new File(inFldr.getPath().replace("SPYTicks2", "SPYTicks2 Directories") + "\\Directory Files.txt");
		if(!drctyFl.exists()){
			updateFolderFiles(inFldr, false);
		}
		outNms.addAll(mcv.ff.getFilesInLns(drctyFl));
		Collections.sort(outNms);
		
		return outNms;
	}
	
	public void updateAllDirectories(){
		
		File actFldr = actHomeFldr;
		File drcFldr = dirHomeFldr;
			
		File[] fldrFls = actFldr.listFiles();
		
		String ext = null;

		Vector<String> fldrFlNms = new Vector<String>();
		
		updateFolderFiles(actHomeFldr, true);

		
	}
	
	public void updateFolderFiles(File actFldr, boolean writeSubDirectories){
		File dirFldr = new File(actFldr.getPath().replace("H:\\SPYTicks2", "H:\\SPYTicks2 Directories"));
		Vector<String> fldrFlNms = new Vector<String>();
		File[] fldrFls = actFldr.listFiles();
		File subDir = null;

		for(File fl: fldrFls){
			fldrFlNms.add(fl.getName());
			
			if(fl.isDirectory() && writeSubDirectories){
				subDir = new File(fl.getPath().replace("H:\\SPYTicks2", "H:\\SPYTicks2 Directories"));
				if(!subDir.exists()){
					subDir.mkdirs();	
				}
				updateFolderFiles(fl, writeSubDirectories);
			}
		}
		
		if(!dirFldr.exists()){
			dirFldr.mkdirs();
		}
		
		writeOutLns(new File(dirFldr.getPath() + "\\Directory Files.txt"), fldrFlNms);
		
	}
	
	public void writeOutLns(File outFl, Vector<String> outLns){
		
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(outFl));
			for(String s: outLns){
				out.write(s); out.newLine();
			}
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
