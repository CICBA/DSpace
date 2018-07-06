package ar.edu.unlp.sedici.dspace.xmlui.util;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.dspace.app.util.Util;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.xpath.NodeSet;
import org.w3c.dom.Document;

import org.dspace.services.factory.DSpaceServicesFactory;

public class XSLTHelper {
	
	private final static Logger log = Logger.getLogger(XSLTHelper.class);
	
	public static String replaceAll(String source, String regex, String replacement) {
		return source.replaceAll(regex, replacement);
	}
	
	public static String getLugarDesarrollo(String source) {
 		int first = source.indexOf("(");
 		int last = source.indexOf(")");
 		if (first != -1 && last != -1) {
 			return source.substring(first + 1, last);
 		} else {
 			return source;
 		}
 	}
	
	public static String stripDash(String source) {
		if (source.endsWith("-")) {
			return source.substring(0, source.length() - 1);
		} else {
			return source;
		}
	}
	
	/**
	 * Returns an encoded URL using all allowed ASCII chars, encoding all non allowed
	 * @param uriString		The uri string to encode
	 * @return encoded uri
	 * @throws NullPointerException if no value is passed
	 */
	public static String escapeURI(String uriString) throws NullPointerException{
		if (uriString == null){
			try{
				throw new NullPointerException();
			}catch (Exception e) {
				log.error("escapeURL: Se recibe null como url", e);
			}
		}
		try {
			//is better use the URI.toASCIIString() than the URLEncoder.encode(). Read https://stackoverflow.com/a/4571518 about this.
			URI uri = new URI(uriString);
			//If URL can be encoded, then returns succesfully
			return uri.toASCIIString();
		} catch (URISyntaxException e) {
			 log.error(
	                    "Error while encoding uri '"+uriString+"'", e);
		}
		//else, log exception in the log file and returns empty...
		return "";
	}
	

	public static String getFileExtension(String filename) {
		return filename.substring(filename.lastIndexOf(".") + 1).toUpperCase();
	}
		

	public static String formatearFecha(String date,String language){
		//Si la fecha viene con el formato aaaa-mm-ddTminutos:segundos:milesimasZ me quedo solo con la fecha
		 String parsedDate=date.split("T")[0];
		 Locale locale=null;
		 String toReplace="";
		 DateTime dt = new DateTime();
		 DateTimeFormatter fmt; 
		 String resul,finalDate;
		 String day="";
		 String month="";
		 String[] formats = {"yyyy-MM-dd","yyyy-MM","yyyy"};
		 String[] finalFormats = {"dd-MMMM-yyyy","MMMM-yyyy","yyyy"};
		 for(int i=0;i<formats.length;i++)
		 {
			 try
			 {
				 fmt = DateTimeFormat.forPattern(formats[i]);
				 fmt.parseDateTime(parsedDate);
				 if(formats[i]!="yyyy")
				 {
					 month=fmt.parseDateTime(parsedDate).monthOfYear().getAsText()+"-";
				 }
				 if(formats[i]=="yyyy-MM-dd")
				 {
					 day=fmt.parseDateTime(parsedDate).getDayOfMonth()+"-";
				 }				 
				 finalDate=day+month+parsedDate.split("-")[0];
			  	 fmt = DateTimeFormat.forPattern(finalFormats[i]);			  
			 	 switch (language)
			 	 {
				 case "en":
					 	locale=Locale.US;
						toReplace=" of ";
						break;
				
		 		 default:
		 			locale=Locale.getDefault();
					toReplace=" de ";
					break;
				 }
			 	resul= fmt.parseDateTime(finalDate).toString(finalFormats[i],locale);
				resul=resul.replace("-",toReplace);
			 	return resul;
				 
			 }
			 catch (java.lang.IllegalArgumentException e)
			 {
				
			 }
		 }
		 return "";
		

	}
	
    /*
     * Retorna un conjunto de property values desde el dspace.cfg dada una property key.
     */
    public static String getPropertyValuesAsString(String property){

        String[] properties= DSpaceServicesFactory.getInstance().getConfigurationService().getArrayProperty(property);
        return String.join(",",properties);
    }

	/*
	 * Retorna un conjunto de property keys desde el dspace.cfg cuyo prefijo coincida.
	 */
	public static NodeSet getPropertyKeys(String prefix){
		
		 java.util.List<String> keys = DSpaceServicesFactory.getInstance().getConfigurationService().getPropertyKeys(prefix);
		 return collectionToNodeSet(keys);
	
	}
	
	/*
	 * Crea un conjunto de nodos texto a partir de una colección de Objetos
	 */
	private static NodeSet collectionToNodeSet(List<String> list){
		NodeSet ns = new NodeSet();
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance() ;
            DocumentBuilder dBuilder;
            dBuilder = dbf.newDocumentBuilder();
            Document doc = dBuilder.newDocument();

            for(int i=0; i < list.size(); i++){
            	ns.addNode( doc.createTextNode(list.get(i).toString())) ;
            }

        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        return ns ;
	}
	
}

