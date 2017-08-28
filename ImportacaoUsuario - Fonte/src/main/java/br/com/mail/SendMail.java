package br.com.mail;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

import javax.mail.*;
import javax.mail.internet.*;
import javax.activation.*;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Workbook;

import br.com.bravatec.webdesk.imp.WSUserImport;

public class SendMail {
	
	private String auser;
	private String apass;
	private String host;
	private String port;
	private boolean auth;
	
	public SendMail(String host, String port, String auser, String apass, boolean auth){
		this.auser = auser;
		this.apass = apass;
		this.host = host;
		this.port = port;
		this.auth = auth;
	}
	
	public boolean isAuth() {
		return auth;
	}

	public void setAuth(boolean auth) {
		this.auth = auth;
	}

	public String getAuser() {
		return auser;
	}
	public void setAuser(String auser) {
		this.auser = auser;
	}
	public String getApass() {
		return apass;
	}
	public void setApass(String apass) {
		this.apass = apass;
	}
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public String getPort() {
		return port;
	}
	public void setPort(String port) {
		this.port = port;
	}
	
	public void sendInfo (String login, String pass, String to) throws FileNotFoundException, IOException{
		// Recipient's email ID needs to be mentioned.
	      ///////////////////////////////////////////////////
		  //String to = "";
	      //String login = "";
	      //String pass = "";
	      ///////////////////////////////////////////////////
	      //final String auser2 = auser;
	      //final String apass2 = apass;

	      // Sender's email ID needs to be mentioned
	      String from = "no-reply@fluig.com";

	      // Assuming you are sending email from localhost
	      //String host = "mail.totvs.com.br";
	      
	      //String port = "587";

	      // Get system properties
	      Properties properties = System.getProperties();

	      // Setup mail server
	      properties.setProperty("mail.smtp.host", host);
	      properties.setProperty("mail.smtp.port", port);
	      

	      // Get the default Session object.
	      Session session;
	      if(this.auth){
	    	  properties.setProperty("mail.smtp.auth", "true");
	    	  session = Session.getInstance(properties,
	              new javax.mail.Authenticator() {
	                protected PasswordAuthentication getPasswordAuthentication() {
	                    return new PasswordAuthentication(auser, apass);
	                }
	              });
	      }else{
	    	  properties.setProperty("mail.smtp.auth", "false");
	    	  session = Session.getInstance(properties);
	      }
	      try{
	         // Create a default MimeMessage object.
	         MimeMessage message = new MimeMessage(session);

	         // Set From: header field of the header.
	         message.setFrom(new InternetAddress(from));

	         // Set To: header field of the header.
	         

	         // Set Subject: header field
	         //////////////////////////////////////////////////////////////////////////
	         /*POIFSFileSystem fs = new POIFSFileSystem(new FileInputStream(file));
	         HSSFWorkbook wb = new HSSFWorkbook(fs);
	         HSSFSheet sheet = wb.getSheetAt(0);
	         HSSFRow row;*/
	         //////////////////////////////////////////////////////////////////////////
	         //String email = "";
	
	         /////////////////////////////////////////////////////////////////////////
	         //FormulaEvaluator evaluator = wb.getCreationHelper().createFormulaEvaluator();
	         /////////////////////////////////////////////////////////////////////////
	         
	         //int rows; // No of rows
	         //rows = sheet.getPhysicalNumberOfRows();
	
	         //int cols = 0; // No of columns
	         //int tmp = 0;
	
	         // This trick ensures that we get the data properly even if it
	         // doesn't start from first few rows
	         /*for (int i = 0; i < 10 || i < rows; i++) {
	        	row = sheet.getRow(i);
	        	if (row != null) {
	        	tmp = sheet.getRow(i).getPhysicalNumberOfCells();
					if (tmp > cols)
						cols = tmp;
				}
	         }*/
	
			//for (int r = 1; r < rows; r++) {
				//row = sheet.getRow(this.lineN);
				//if (row != null) {
					//to = "";
					//login = "";
					//pass = "";
					//if(r > 1){
						//message.setRecipient(Message.RecipientType.TO, null);
					//}
					if(to != null && to != ""){
					//to = row.getCell(3).getStringCellValue();
					
						System.out.println(to);
						message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
					
						//login = row.getCell(4).toString();
						//pass = cellValue(row.getCell(6).getCachedFormulaResultType(), row.getCell(6), evaluator);
						//pass = row.getCell(6).toString();
					
						message.setSubject("Acesso Fluig");

			        	// Now set the actual message
						message.setContent("<html><head></head><body>Seu usu&aacute;rio foi criado!<br /><b>Login:</b> "+login+"<br /><b>Senha:</b> "+pass+"</body></html>", "text/html; charset=utf-8");
			        	//message.setText("login "+login+" senha "+pass);
	
			        	// Send message
			        	Transport.send(message);
			        	System.out.println("Sent message successfully ("+to+") login "+login+" senha "+pass);
			        	WSUserImport.jTextLog.append(" E-mail enviado com sucesso para "+to+".\n");
						WSUserImport.jTextLog.update(WSUserImport.jTextLog.getGraphics());
					}
					
				//}
			//}
			
	         
	         
	      }catch (MessagingException mex) {
	         mex.printStackTrace();
	         WSUserImport.jTextLog.append(" Falha ao enviar e-mail para "+to+".\n Mensagem de erro: "+mex.getMessage()+"\n");
	         WSUserImport.jTextLog.update(WSUserImport.jTextLog.getGraphics());
	      }
	   
	}
	
	public String cellValue(int type, HSSFCell cell, FormulaEvaluator evaluator){
		
		
         if (cell.getCellType() == HSSFCell.CELL_TYPE_STRING) {
              return cell.getStringCellValue();
         } else  {
        	 CellValue cell2 = evaluator.evaluate(cell);
        	 //System.out.println(cell2.formatAsString());
        	 //System.out.println();
        	 return cell2.formatAsString();
        	 //cellValue(cell.getCachedFormulaResultType(), cell, evaluator);
         }

	}
}
