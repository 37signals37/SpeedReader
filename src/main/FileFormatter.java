package main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import javax.swing.text.DateFormatter;

//import org.apache.commons.io.FileUtils;
//import org.apache.commons.io.filefilter.DirectoryFileFilter;
//import org.apache.commons.io.filefilter.FileFilterUtils;
//
//import com.lowagie.text.pdf.codec.Base64.InputStream;

public class FileFormatter {

	private main.MasterControlVariables mcv;
	private BufferedReader in;
	private BufferedWriter out;
	private String line;
	private String[] lineSplit;
	private TreeSet<String> linesSet = new TreeSet<String>();
	private HashMap<String, String> lines = new HashMap<String, String>();
	private HashMap<String, String> srtdLines = new HashMap<String, String>();
	private TreeSet<String> keys = new TreeSet<String>();
	private Iterator i;
	
	public FileFormatter(main.MasterControlVariables mcv){
		this.mcv = mcv;
	}
	
	public void appendLineToFile(File fl, String s){
		try {
			out = new BufferedWriter(new FileWriter(fl, true));
			out.append(s);
			out.newLine();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void appendLinesToFile(File fl, Vector<String> outLns){
		try {
			out = new BufferedWriter(new FileWriter(fl, true));
			for(String s: outLns){
				out.append(s);
				out.newLine();
			}
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void convertToZipFile(String filePath){
		
		String outFileName = new String();
		byte[] buf = new byte[1024];

		try {
			// Create the ZIP file
			if(filePath.contains(".txt")){
				outFileName = filePath.replace(".txt", ".zip");
			}
			if(filePath.contains(".tck")){
				outFileName = filePath.replace(".tck", ".zip");
			}

			ZipOutputStream out = new ZipOutputStream(new FileOutputStream(outFileName));

			// Compress the files

			FileInputStream in = new FileInputStream(filePath);

			// Add ZIP entry to output stream.
			out.putNextEntry(new ZipEntry(outFileName));

			// Transfer bytes from the file to the ZIP file
			int len;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}

			// Complete the entry
			out.closeEntry();
			in.close();
			
			// Complete the ZIP file
			out.close();
		}
		catch (IOException e) {
		}
		
		File file = new File(filePath);
		file.delete();
	} 
	
	public boolean fileExists(String flPth){
		
		flPth = flPth.replace(".txt", "");
		flPth = flPth.replace(".zip", "");
		
		File test = new File(flPth + ".txt");
		if(test.exists()){return true;}
		
		test = new File(flPth + ".zip");
		if(test.exists()){return true;}
		
		return false;
	}
	
	public BufferedReader getBufferedReaderIn(String filePath){

		BufferedReader in = null;
		ZipFile zf;
		Enumeration entries;
		ZipEntry ze;
		String line;

		if(filePath.substring(filePath.length()-4, filePath.length()).equalsIgnoreCase(".txt") 
		|| filePath.substring(filePath.length()-4, filePath.length()).equalsIgnoreCase(".zip")){
			filePath = filePath.substring(0,filePath.length()-4);
		}
		
		File file = new File(filePath + ".txt");
		if(file.exists()){
			try {
				in = new BufferedReader(new FileReader(file));
				return in;
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
		
		file = new File(filePath + ".zip");
		if(file.exists()){
			try {
				zf = new ZipFile(file);
				entries = zf.entries();
				ze = (ZipEntry) entries.nextElement();
				in = new BufferedReader(new InputStreamReader(zf.getInputStream(ze)));
				return in;				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		file = new File(filePath + ".tck");
		if(file.exists()){
			try {
				in = new BufferedReader(new FileReader(file));
				return in;			
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return in;
		
	}
	
	public Vector<String> getFilesInLns(File inFl){
		Vector<String> outLns = new Vector<String>();
		String line = null;
	
		BufferedReader in = getBufferedReaderIn(inFl.getPath());
		try {
			while((line = in.readLine()) != null){			
				outLns.add(line);
			}
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return outLns;
	}
	
	public Vector<String> getFilesInLnsSpltVals(File inFl, int col){
		Vector<String> outLns = new Vector<String>();
		String line = null;
		String[] lnSplt = null;
		
		BufferedReader in = getBufferedReaderIn(inFl.getPath());
		try {
			while((line = in.readLine()) != null){			
				lnSplt = line.split("\\|");
				outLns.add(lnSplt[col]);
			}
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return outLns;
	}
	
	public Vector<String> getFilesNamesFromFolder(File fldr){
	
		Vector<String> outNms = new Vector<String>();
		FileFinder flFndr = new utilities.FileFinder(fldr);
		List<File> files = flFndr.list();
		
	    for(File f: files){
	    	outNms.add(f.getName());
	    }
		return outNms;
	}
	
	public Vector<String> getFilesNamesFromFolder(File fldr, boolean withExt, String ext){
		Vector<String> outNms = new Vector<String>();
		
		Iterator<File> i;
		File fl = null;
		
		if(ext == null){			
			i = FileUtils.iterateFiles(fldr, null, true);
//			i = FileUtils.iterateFiles(fldr, new String[]{".txt", ".zip"}, true);
//			i = FileUtils.iterateFiles(fldr, new String[]{".txt", ".zip", ".tck", ".xlsx", ".xls"}, true);
		}else{
			i = FileUtils.iterateFiles(fldr, new String[]{ext}, true);
		}
		
		while(i.hasNext()){
			fl = (File) i.next();
			
			if(withExt){
				outNms.add(fl.getName());	
			}else{
				outNms.add(fl.getName().substring(0, fl.getName().lastIndexOf(".")));
			}
		}

		Collections.sort(outNms);
		return outNms;

	}

	public Vector<String> getFolderNamesFromFolder(File fldr){
//		Vector<String> outNms = new Vector<String>();
////		File[] fldrs = fldr.listFiles();
//		File[] fldrs = fldr.listFiles((FilenameFilter) FileFilterUtils.directoryFileFilter());
//		for(File fl: fldrs){
//			outNms.add(fl.getName());
//		}
//		
//		return outNms;
		
		Vector<String> outNms = new Vector<String>();
		FileFinder flFndr = new utilities.FileFinder(fldr);
		flFndr.yieldDirectories();
		List<File> files = flFndr.list();
		
	    for(File f: files){
	    	outNms.add(f.getName());
	    }
		
		return outNms;
		
	}
	
	public HashMap<String, String> getHashMapFromFileByFld(File file, int fldNum) throws IOException{
		
		lines.clear();
		
		in = new BufferedReader(new FileReader(file));
		while((line = in.readLine()) != null){
			lineSplit = line.split("\\|");
			
			if(!lines.containsKey(lineSplit[fldNum])){
				lines.put(lineSplit[fldNum], line);
			}		
		}
		in.close();
		
		keys = (TreeSet<String>) lines.keySet();
		i = keys.iterator();
		
		while(i.hasNext()){
			line = i.next().toString();
			srtdLines.put(line, lines.get(line));
		}
		
		return lines;
	}
	
	public String getNextMrktDay(String inDay){
		DateFormat dtf = new SimpleDateFormat("yyyyMMdd");
		Date inDate = new Date();

		try {
			inDate.setTime(dtf.parse(inDay).getTime());
			inDate.setDate(inDate.getDate() + 1);
			
			while(inDate.getDay() == 0 || inDate.getDay() == 6 || mcv.holidays.contains(inDate)){
				inDate.setDate(inDate.getDate() + 1);
			}
		}
		catch (ParseException e) { e.printStackTrace();}
		return dtf.format(inDate);
	}
	
	public String getPrevMrktDay(String inDay){
		DateFormat dtf = new SimpleDateFormat("yyyyMMdd");
		Date inDate = new Date();

		try {
			inDate.setTime(dtf.parse(inDay).getTime());
			inDate.setDate(inDate.getDate() - 1);
			
			while(inDate.getDay() == 0 || inDate.getDay() == 6 || mcv.holidays.contains(inDate)){
				inDate.setDate(inDate.getDate() - 1);
			}
		}
		catch (ParseException e) { e.printStackTrace();}
		return dtf.format(inDate);
	}
	
	public void removeDuplicateLines(Vector<String> inLns){
		Vector<String> tmp = new Vector<String>();
		for(String s: inLns){
			if(!tmp.contains(s)){
				tmp.add(s);
			}
		}
		inLns.clear();
		inLns.addAll(tmp);
		
	}
	
	public void removeDuplicateLines(File file){

		try {
//		in = new BufferedReader(new FileReader(file));
		in = this.getBufferedReaderIn(file.getPath());
		
int lnCntr = 0;
		
			while((line = in.readLine()) != null){			
				if(!linesSet.contains(line)){
					linesSet.add(line);
				}
lnCntr++;
			}		
		in.close();
		
//if(lnCntr > 1){
//	System.out.println("tits " + file.getPath());
//}
		
		out = new BufferedWriter(new FileWriter(file));
		
		i = linesSet.iterator();
		while(i.hasNext()){
			out.write(i.next().toString()); out.newLine();
		}
		out.close();
		
		linesSet.clear();
		in = null; out = null;
		
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void sortFileContentsbyFld(File file, int fldNum) throws IOException{
		
		in = new BufferedReader(new FileReader(file));
		while((line = in.readLine()) != null){
			lineSplit = line.split("\\|");
			
			if(!lines.containsKey(lineSplit[fldNum])){
				lines.put(lineSplit[fldNum], line);
			}		
		}
		in.close(); in = null;
		
		keys = (TreeSet<String>) lines.keySet();
		i = keys.iterator();
		out = new BufferedWriter(new FileWriter(file));
		
		while(i.hasNext()){
			out.write(lines.get(i.next()));
			out.newLine();
		}
		
		out.close(); out = null;
		lines.clear(); lines = null;
		keys.clear(); keys = null;
		line = null; lineSplit = null; 
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
		
		mcv.fm.addEntryToFileDirectory(outFl);
		
	}
	
}
