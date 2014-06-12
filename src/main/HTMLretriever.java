package main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

public class HTMLretriever {

	main.MasterControlVariables mcv = null;
	Vector<String> v_inLns = new Vector<String>();
	Vector<String> v_outLns = new Vector<String>();

	URL url = null;
	HttpURLConnection huc = null;
	BufferedReader in = null;
	String line = null;

	Iterator<String> i = null;

	Vector<String> rmvLns = new Vector<String>();
	
	Vector<String> v_rmvTags = new Vector<String>();
	
	public HTMLretriever(main.MasterControlVariables mcv){
		this.mcv = mcv;		
		v_rmvTags.add("</a>");
		v_rmvTags.add("<b>");
		v_rmvTags.add("</b>");
		v_rmvTags.add("<p>");
		v_rmvTags.add("</p>");
		v_rmvTags.add("<i>");
		v_rmvTags.add("</i>");
		v_rmvTags.add("<span>");
		v_rmvTags.add("</span>");
		v_rmvTags.add("</sup>");
		
	}

	public Vector<String> getParsedParagraphLines(String urlAddress){
		Vector<String> outLns = new Vector<String>();
		outLns.addAll(getHTMLinLinesFromWebPage(urlAddress));
		removeNonBodyNonHeadersNonParagraphLines(outLns);
		parseHTMLlines(outLns);
		setHeaderLinesinBold(outLns);
		
		return outLns;
	}
	
	public void parseHTMLlines(Vector<String> v_htmlLines){
		
		v_outLns.clear();
		int aStrt = 0;
		int aEnd = 0;
		String rmvSnppt = null;
		
		for(String hmtlLine: v_htmlLines){

			line = hmtlLine.trim();
			
			for(String s: v_rmvTags){
				if(line.contains(s)){
					line = line.replace(s, "");
				}
			}
			
			while(line.contains("<a href=")){				
				aStrt = line.indexOf("<a href=");
				for(int iii = aStrt; iii < line.length(); iii++){
					if(line.charAt(iii) == '>'){
						aEnd = iii;
						break;
					}		
				}
				rmvSnppt = line.substring(aStrt, aEnd+1);
				line = line.replace(rmvSnppt, "");
			}
			
			while(line.contains("<sup")){				
				aStrt = line.indexOf("<sup");
				for(int iii = aStrt; iii < line.length(); iii++){
					if(line.charAt(iii) == '>'){
						aEnd = iii;
						break;
					}		
				}
				rmvSnppt = line.substring(aStrt, aEnd+1);
				line = line.replace(rmvSnppt, "");
			}
			
			while(line.contains("<span")){				
				aStrt = line.indexOf("<span");
				for(int iii = aStrt; iii < line.length(); iii++){
					if(line.charAt(iii) == '>'){
						aEnd = iii;
						break;
					}		
				}
				rmvSnppt = line.substring(aStrt, aEnd+1);
				line = line.replace(rmvSnppt, "");
			}
			
			
			v_outLns.add(line);
		}

		v_htmlLines.clear();
		v_htmlLines.addAll(v_outLns);
		
	}

	public Vector<String> getHTMLinLinesFromWebPage(String urlAddress){

		v_outLns.clear();

		try {
			url = new URL(urlAddress);

			huc = (HttpURLConnection)  url.openConnection();
			huc.setRequestMethod("GET");
			huc.connect();

			if(huc.getResponseCode() == 400){
				System.out.println("400 Error");
			}else if(huc.getResponseCode() == 404){
				System.out.println("404 Error");
			}else{
				in = new BufferedReader(new InputStreamReader(url.openStream()));
				while ((line = in.readLine()) != null) {
					v_outLns.add(line);
				}
				in.close();										
			}

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return v_outLns;
	}
	
	public void removeNonBodyNonHeadersNonParagraphLines(Vector<String> htmlLines){
		
//		boolean bodyStrt = false;
//		boolean bodyStop = false;
//		rmvLns.clear();
//		for(String s: htmlLines){
//			if(s.contains("<body")){
//				bodyStrt = true;
//			}
//			if(s.contains("</body")){
//				bodyStop = true;
//			}
//			
//			if(!bodyStrt){
//				rmvLns.add(s);
//			}
//			if(bodyStop){
//				rmvLns.add(s);
//			}						
//		}						
//		htmlLines.removeAll(rmvLns);
		
		rmvLns.clear();
		for(String s: htmlLines){
			if(!s.contains("<p>") && !s.contains("</h")){
				rmvLns.add(s);
			}
		}						
		htmlLines.removeAll(rmvLns);
		
		rmvLns.clear();
		for(String s: htmlLines){
			if(s.contains("if lt IE") || s.contains("<br")){
				rmvLns.add(s);
			}
		}
		htmlLines.removeAll(rmvLns);
	}
	
	public void setHeaderLinesinBold(Vector<String> htmlLns){
						
		int aStrt = 0;
		int aEnd = 0;
		String rmvSnppt = null;
		
		v_outLns.clear();
		for(String line: htmlLns){
		
			while(line.contains("<h")){								
				aStrt = line.indexOf("<h");
				for(int iii = aStrt; iii < line.length(); iii++){
					if(line.charAt(iii) == '>'){
						aEnd = iii;
						break;
					}		
				}
				rmvSnppt = line.substring(aStrt, aEnd+1);
				line = line.replace(rmvSnppt, "<b>");
			}
			
			while(line.contains("</h")){				
				aStrt = line.indexOf("</h");
				for(int iii = aStrt; iii < line.length(); iii++){
					if(line.charAt(iii) == '>'){
						aEnd = iii;
						break;
					}		
				}
				rmvSnppt = line.substring(aStrt, aEnd+1);
				line = line.replace(rmvSnppt, "</b>");
			}
			
			line = line.replace("<b>", "<html><b>");
			line = line.replace("</b>", "</b></html>");
			
			v_outLns.add(line);
		}
		htmlLns.clear();
		htmlLns.addAll(v_outLns);
	}
	
}
