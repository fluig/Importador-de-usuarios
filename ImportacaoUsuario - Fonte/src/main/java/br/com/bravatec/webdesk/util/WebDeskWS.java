package br.com.bravatec.webdesk.util;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public final class WebDeskWS {
	
	public static InputStream call(String sUrl, String service,String action, String ns,String msg) throws Exception {
		String message =
			"<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ws=\""+ns+"\"><soapenv:Header/><soapenv:Body>"
	       +"<ws:" + action +">"
	       + msg 
	       +"</ws:" + action +">"
			+"</soapenv:Body></soapenv:Envelope>";
		HttpURLConnection uc = null;
		URL url = null;
		
		url = new URL(sUrl + "/" + service);

		uc = (HttpURLConnection) url.openConnection();

		uc.setDoInput(true);
		uc.setDoOutput(true);
		uc.setUseCaches(false);
		uc.setRequestProperty("Content-Length", " " + message.length());
		if(action.equals("getColleague")){
			uc.setRequestProperty("Content-Type", "text/xml; charset=UTF-8");
		}else if(action.equals("getGroups")){
			uc.setRequestProperty("SOAPAction", action);
		}else{
			uc.setRequestProperty("Content-Type", "text/xml; charset=ISO-8859-1");
		}
		
		uc.setRequestMethod("POST");
		uc.connect();
		
		uc.getOutputStream().write(message.getBytes());
		uc.getOutputStream().flush();
		uc.getOutputStream().close();
		System.out.println(uc.getOutputStream().toString());
		/*if(uc.getOutputStream().toString().contains("getColleagueResponse")){
			System.out.println("ha "+uc.getOutputStream().toString());
			InputStream decodedInput =new ByteArrayInputStream(((ByteArrayOutputStream) uc.getOutputStream()).toByteArray());
			//System.out.println("ha2 "+uc.getInputStream().toString());
			return decodedInput;
		}*/
		//System.out.println(uc.getInputStream().);
		
		return uc.getInputStream();
	}
}
