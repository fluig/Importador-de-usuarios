package br.com.bravatec.webdesk.imp;

import java.awt.Rectangle;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Pattern;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.text.BadLocationException;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.FormulaEvaluator;

import br.com.bravatec.webdesk.util.ColleagueResult;
import br.com.bravatec.webdesk.util.GetRolesResult;
import br.com.bravatec.webdesk.util.Group;
import br.com.bravatec.webdesk.util.Result;
import br.com.bravatec.webdesk.util.Role;
import br.com.bravatec.webdesk.util.RoleResult;
import br.com.bravatec.webdesk.util.SimpleResult;
import br.com.bravatec.webdesk.util.WebDeskWS;
import br.com.bravatec.webdesk.util.XmlBinder;
import br.com.mail.SendMail;

public final class WSUserImport {

	public static FormulaEvaluator fe;
	public static JTextArea jTextLog;
	public static JScrollPane scroll;
	public static boolean scrollActive = false;
	public static String value = "À Á Â Ã Ä Å Æ Ç È É Ê Ë Ì Í Î Ï Ð Ñ Ò Ó Ô Õ Ö Ø Ù Ú Û Ü Ý Þ ß à á â ã ä å æ ç è é ê ë ì í î ï ð ñ ò ó ô õ ö ø ù ú û ü ý þ ÿ ";
	
	public static byte[] execute(String file, String url, String user, String empresa, String passwd, String host, String port, String auser, String apass, boolean auth, JTextArea jtl, JScrollPane scroll) throws Exception {
		WSUserImport.jTextLog = jtl;
		WSUserImport.jTextLog.setText("");
		
		WSUserImport.scroll = scroll;
		//WSUserImport.jTextLog.append("------------------------------\n");
		//WSUserImport.jTextLog.update(WSUserImport.jTextLog.getGraphics());
		try {
			HSSFWorkbook book = new HSSFWorkbook(new FileInputStream(file));
			HSSFSheet sheet = book.getSheetAt(0);
			HSSFCellStyle headerStyle = sheet.getRow(0).getCell(0).getCellStyle();
			HSSFCell cell =  sheet.getRow(0).createCell(13);
			cell.setCellStyle(headerStyle);
			cell.setCellValue(ImportUserConstants.IMPORT_STATUS);
			
			HSSFCell cell2 =  sheet.getRow(0).createCell(14);
			cell2.setCellStyle(headerStyle);
			cell2.setCellValue(ImportUserConstants.GROUPI_STATUS);
			
			HSSFCell cell3 =  sheet.getRow(0).createCell(15);
			cell3.setCellStyle(headerStyle);
			cell3.setCellValue(ImportUserConstants.GROUPR_STATUS);
			
			WSUserImport.fe = book.getCreationHelper().createFormulaEvaluator();
			//Map<String, Integer> cachePapeis = cachePapeis(url, user, passwd, empresa, sheet);

			HSSFRow row;
			int rowIndex = 1;
			//TreeSet<String> grupos = new TreeSet<String>();
			TreeSet<String> grupos = cacheGrupos(url, user, passwd, empresa);
			ArrayList<String> papeis = cachePapeis(url, user, passwd, empresa, sheet);
			
			String lineStatus;
			if(sheet.getLastRowNum() == 1){
				WSUserImport.jTextLog.append("Existe "+(sheet.getLastRowNum())+" linha.\n");
			}else if(sheet.getLastRowNum() > 1){
				WSUserImport.jTextLog.append("Existem "+(sheet.getLastRowNum())+" linhas.\n");
			}
			while( (row = sheet.getRow(rowIndex++)) != null) {
				lineStatus = "";
				if(row.getCell(0) == null || getStringValue(row.getCell(0)).equals("")) {
					break;
				}
				lineStatus = callWSUser(url, user, passwd, empresa,row, grupos, papeis, host, port, auser, apass, file, auth);
				for(int i = 0; i < lineStatus.split(" ").length; i++){
					row.createCell(i+13).setCellValue(lineStatus.split(" ")[i]);
				}
			}
			//ultimo item a ser comentado
			/*for(String g:grupos){
				row = sheet.createRow(rowIndex++);
				row.createCell(0).setCellValue("Grupo " + g);
				row.createCell(1).setCellValue(callWSGroup(url, user, passwd,empresa, g));
			}*/

			/*for(String p:papeis){
				row = sheet.createRow(rowIndex++);
				row.createCell(0).setCellValue("Papel " + p);
				row.createCell(1).setCellValue(cachePapeis.get(p));
			}			*/

			ByteArrayOutputStream out = new ByteArrayOutputStream();
			book.write(out);

			//ByteArrayOutputStream logOut = new ByteArrayOutputStream();
			//String log = new St
			
			return out.toByteArray();
		}catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	private static String callWSUser(String url, String user, String passwd, String empresa,HSSFRow row, Set<String> gruposList, List<String> papeisList, String host, String port, String auser, String apass, String file, boolean auth) throws Exception {
		try{
			
			WSUserImport.jTextLog.append("----------------------------------------------- x -----------------------------------------------\n");
			
			StringBuffer xml = new StringBuffer();
			String matricula = semAcento(getStringValue(row.getCell(0)));
			String nome      = getStringValue(row.getCell(1));
			String ramal     = getStringValue(row.getCell(2));
			String funcao    = getStringValue(row.getCell(3));
			String area      = getStringValue(row.getCell(4));
			String email     = getStringValue(row.getCell(5));
			String login     = semAcento(getStringValue(row.getCell(6)));
			String grupos    = getStringValue(row.getCell(7));
			String senha     = criptografaSenha(getStringValue(row.getCell(8)));
			String grupoWF   = getStringValue(row.getCell(9));
			String papeis    = getStringValue(row.getCell(10));
			String admin     = getStringValue(row.getCell(11));
			String rGrupos    = getStringValue(row.getCell(12));
			String rPapeis    = getStringValue(row.getCell(13));
			String atualizarInfo = getStringValue(row.getCell(14));
			String enviarEmail = getStringValue(row.getCell(15));
			
			System.out.println("matricula >>"+matricula);
			WSUserImport.jTextLog.append(" Dados do usuï¿½rio:\n");
			WSUserImport.jTextLog.append("  matricula: "+matricula+"\n");
			WSUserImport.jTextLog.append("  nome: "+nome+"\n");
			WSUserImport.jTextLog.append("  login: "+login+"\n");
			WSUserImport.jTextLog.append("  email: "+email+"\n");
			WSUserImport.jTextLog.append("  admin: "+admin+"\n");
			WSUserImport.jTextLog.append("  senha: "+senha+"\n");
			WSUserImport.jTextLog.append("  empresa: "+empresa+"\n");
			WSUserImport.jTextLog.append("  ramal: "+ramal+"\n");
			WSUserImport.jTextLog.append("  funcao: "+funcao+"\n");
			WSUserImport.jTextLog.append("  area: "+area+"\n");
			WSUserImport.jTextLog.update(WSUserImport.jTextLog.getGraphics());
			moveLog(WSUserImport.jTextLog.getCaretPosition());
			
			xml.append("<username>").append(user).append("</username>")
			.append("<password>").append(passwd).append("</password>")
			.append("<companyId>").append(empresa).append("</companyId>")
			.append("<colleagues>")
			.append("<item>")
			.append("<colleagueId>").append(matricula).append("</colleagueId>")
			.append("<colleagueName>").append(nome).append("</colleagueName>")
			.append("<login>").append(login).append("</login>")
			.append("<mail>").append(email).append("</mail>")
			.append("<adminUser>").append("s".equals(admin)).append("</adminUser>")
			.append("<passwd>").append(senha).append("</passwd>")
			.append("<companyId>").append(empresa).append("</companyId>")
			.append("<defaultLanguage>PT_BR</defaultLanguage>")
			.append("<dialectId>PT_BR</dialectId>")
			.append("<emailHtml>true</emailHtml>")
			.append("<extensionNr>").append(ramal).append("</extensionNr>")
			.append("<especializationArea>").append(funcao).append("</especializationArea>")
			.append("<currentProject>").append(area).append("</currentProject>")
			.append("<nominalUser>true</nominalUser>")
			.append("<gedUser>true</gedUser>")
			.append("<active>true</active>")
			.append("</item>")
			.append("</colleagues>");
			//.append("<groups>");
			///////////////////////////////////////////////////////////////////////////////////////////
			TreeSet<String> gruposCorrentes = new TreeSet<String>();
			
			if(grupos != null && grupos.trim().length() >0 ) {
				for (String g: grupos.split("\\|")){
					if(g.trim().length() > 0) {
						/*xml.append("<item>")
						.append("<companyId>").append(empresa).append("</companyId>")
						.append("<groupId>").append(g).append("</groupId>")
						.append("</item>");   */     		
						if(! gruposList.contains(g.trim())) {
							gruposCorrentes.add(g);
							gruposList.add(g);
						}
					}
				}
			}
			if(!gruposCorrentes.isEmpty()){
				WSUserImport.jTextLog.append(" Criando grupos\n");
				WSUserImport.jTextLog.update(WSUserImport.jTextLog.getGraphics());
				moveLog(WSUserImport.jTextLog.getCaretPosition());
			}
			for(String g:gruposCorrentes){
				
				WSUserImport.jTextLog.append("  " + g + " ("+callWSGroup(url, user, passwd,empresa, semAcento(g)) + ")\n");
				WSUserImport.jTextLog.update(WSUserImport.jTextLog.getGraphics());
				moveLog(WSUserImport.jTextLog.getCaretPosition());
			}
			
			List<String> papeisCorrentes = new ArrayList<String>();
			
			if(papeis != null && papeis.trim().length() >0 ) {
				for (String p: papeis.split("\\|")){
					if(p.trim().length() > 0) {
						if(! papeisList.contains(p.trim())) {
							papeisCorrentes.add(p);
							papeisList.add(p);
						}
					}
				}
			}
			if(!papeisCorrentes.isEmpty()){
				WSUserImport.jTextLog.append(" Criando grupos\n");
				WSUserImport.jTextLog.update(WSUserImport.jTextLog.getGraphics());
				moveLog(WSUserImport.jTextLog.getCaretPosition());
			}
			for(String p:papeisCorrentes){
				
				WSUserImport.jTextLog.append("  " + p + " ("+callWSRole(url, user, passwd, empresa, semAcento(p), semAcento(p)) + ")\n");
				WSUserImport.jTextLog.update(WSUserImport.jTextLog.getGraphics());
				moveLog(WSUserImport.jTextLog.getCaretPosition());
			}

			StringBuffer xmlCheck = new StringBuffer();
			
			xmlCheck.append("<username>").append(user).append("</username>")
			.append("<password>").append(passwd).append("</password>")
			.append("<companyId>").append(empresa).append("</companyId>")
			.append("<colleagueId>").append(matricula).append("</colleagueId>");
			
			XmlBinder binder = new XmlBinder();
			System.out.println(xmlCheck.toString());
			//WSUserImport.jTextLog.append(xmlCheck.toString()+"\n");
			//WSUserImport.jTextLog.update(WSUserImport.jTextLog.getGraphics());
			ColleagueResult exists = binder.bind(ColleagueResult.class, WebDeskWS.call(url, "ColleagueService", "getColleague","http://ws.foundation.webdesk.technology.datasul.com/", xmlCheck.toString()));
			System.out.println(">> "+exists.getColab().size());
			//WSUserImport.jTextLog.append("Usuï¿½rio ja existe? "+exists.getColab().size()+"\n");
			//WSUserImport.jTextLog.update(WSUserImport.jTextLog.getGraphics());
			SimpleResult result = new SimpleResult();
			String sresult = new String();
			if(exists.getColab() == null || exists.getColab().size() == 0|| exists.getColab().get(0) == null || exists.getColab().get(0).getCompanyId() == null ||exists.getColab().get(0).getCompanyId().equals("0")){
				//System.out.println(exists.getColab().get(0).getCompanyId());
				//WSUserImport.jTextLog.append("CompanyId "+exists.getColab().get(0).getCompanyId()+"\n");
				//WSUserImport.jTextLog.update(WSUserImport.jTextLog.getGraphics());
				result = binder.bind(SimpleResult.class, WebDeskWS.call(url, "ColleagueService", "createColleague","http://ws.foundation.webdesk.technology.datasul.com/", xml.toString()));
				System.out.println(">>>new user "+result.getResultXML());
				WSUserImport.jTextLog.append(" Usuï¿½rio inexistente.\n");
				WSUserImport.jTextLog.append(" Resultado importaï¿½ï¿½o: "+result.getResultXML()+"\n");
				WSUserImport.jTextLog.update(WSUserImport.jTextLog.getGraphics());
				moveLog(WSUserImport.jTextLog.getCaretPosition());
				if(enviarEmail.equals("s")){
					SendMail mail = new SendMail(host, port, auser, apass, auth);
					mail.sendInfo(login, senha, email);
				}
				
				if(result.getResultXML() == null){
					result.setResultXML("");
				}
				
				sresult = "UC"+result.getResultXML().toUpperCase();
			}else if(atualizarInfo.equals("s")){
				System.out.println(exists.getColab().get(0).getCompanyId());
				/*WSUserImport.jTextLog.append("CompanyId "+exists.getColab().get(0).getCompanyId()+"\n");
				WSUserImport.jTextLog.update(WSUserImport.jTextLog.getGraphics());*/
				result = binder.bind(SimpleResult.class, WebDeskWS.call(url, "ColleagueService", "updateColleague","http://ws.foundation.webdesk.technology.datasul.com/", xml.toString()));
				System.out.println(">>>update user "+result.getResultXML());
				WSUserImport.jTextLog.append(" Usuï¿½rio jï¿½ cadastrado na empresa "+exists.getColab().get(0).getCompanyId()+"\n");
				WSUserImport.jTextLog.append(" Resultado atualizaï¿½ï¿½o: "+result.getResultXML()+"\n");
				WSUserImport.jTextLog.update(WSUserImport.jTextLog.getGraphics());
				moveLog(WSUserImport.jTextLog.getCaretPosition());
				
				if(result.getResultXML() == null){
					result.setResultXML("");
				}
				
				sresult = "UU" + result.getResultXML().toUpperCase();
			}else{
				System.out.println(" Usuï¿½rio jï¿½ existe e optou-se por nï¿½o atualizï¿½-lo.");
				WSUserImport.jTextLog.append(" Usuï¿½rio jï¿½ existe e optou-se por nï¿½o atualizï¿½-lo.\n");
				WSUserImport.jTextLog.update(WSUserImport.jTextLog.getGraphics());
				moveLog(WSUserImport.jTextLog.getCaretPosition());
				//WSUserImport.jTextLog.firePropertyChang
				sresult = "UENU";
			}
			
			//========================================================================
			if((grupos != null && grupos.trim().length() >0) || (papeis != null && papeis.trim().length() >0)) 
			{
				SimpleResult resultPG = new SimpleResult();
				
				StringBuffer xmlPG = new StringBuffer();
				
				xmlPG = new StringBuffer();
				xmlPG.append("<username>").append(user).append("</username>")
				.append("<password>").append(passwd).append("</password>")
				.append("<companyId>").append(empresa).append("</companyId>");
				
				xmlPG.append("<colleagues>")
	            .append("<item>")
	               .append("<active>true</active>")
	               .append("<adminUser>").append("s".equals(admin)).append("</adminUser>")
	    		   .append("<colleagueId>").append(matricula).append("</colleagueId>")
					   .append("<colleagueName>").append(nome).append("</colleagueName>")
					   .append("<companyId>").append(empresa).append("</companyId>")
	               .append("<login>").append(login).append("</login>")
	    		   .append("<mail>").append(email).append("</mail>")
	    		   .append("<passwd>").append(senha).append("</passwd>")
	           .append("</item>")
	         .append("</colleagues>");
				
				xmlPG.append("<groups>");
				
				if(grupos != null && grupos.trim().length() >0 ) 
				{
					for (String g: grupos.split("\\|")){
						if(g.trim().length() > 0) {
							
							xmlPG.append("<item>")
							.append("<companyId>").append(empresa).append("</companyId>")
							.append("<groupId>").append(semAcento(g)).append("</groupId>")
							.append("</item>");     
						}
					}
				}
				else{
					WSUserImport.jTextLog.append(" Nenhum grupo especificado para inclusï¿½o.\n");
					WSUserImport.jTextLog.update(WSUserImport.jTextLog.getGraphics());
					moveLog(WSUserImport.jTextLog.getCaretPosition());
				}
				
				xmlPG.append("</groups>");

				String idPapel = papeis.replace(" ", "");
				
				xmlPG.append("<workflowRoles>");
				
				if(papeis != null && papeis.trim().length() >0 ) 
				{
					for (String p: papeis.split("\\|")){
						if(p.trim().length() > 0) {
							
							xmlPG.append("<item>")
							.append("<companyId>").append(empresa).append("</companyId>")
							.append("<roleId>").append(semAcento(idPapel).trim()).append("</roleId>")
							.append("</item>");     
						}
					}
				}
				else{
					WSUserImport.jTextLog.append(" Nenhum papel especificado para inclusï¿½o.\n");
					WSUserImport.jTextLog.update(WSUserImport.jTextLog.getGraphics());
					moveLog(WSUserImport.jTextLog.getCaretPosition());
				}
				
				xmlPG.append("</workflowRoles>");
				
				resultPG = binder.bind(SimpleResult.class, WebDeskWS.call(url, "ECMColleagueService", "updateColleaguewithDependencies","http://ws.foundation.ecm.technology.totvs.com/", xmlPG.toString()));
			}
			else
			{
			
			}
			
			return sresult;
			
		}catch (Exception e) {
			e.printStackTrace();
			return e.getMessage();
		}
	}

	private static String getStringValue(HSSFCell cell) {
		if(cell == null){
			return "";
		}
		switch (cell.getCellType()) {
		case HSSFCell.CELL_TYPE_STRING: return cell.getStringCellValue();
		case HSSFCell.CELL_TYPE_NUMERIC: return new Double(cell.getNumericCellValue()).intValue() + "";
		case HSSFCell.CELL_TYPE_BOOLEAN: return Boolean.toString(cell.getBooleanCellValue());
		case HSSFCell.CELL_TYPE_FORMULA: return WSUserImport.fe.evaluate(cell).formatAsString();
		}
		return "";
	}

	private static String callWSGroup(String url, String user, String passwd,String empresa, String grupo) throws Exception {

		try{
			StringBuffer xml = new StringBuffer();

			xml.append("<username>").append(user).append("</username>")
			.append("<password>").append(passwd).append("</password>")
			.append("<companyId>").append(empresa).append("</companyId>")
			.append("<groups>")
			.append("<item>")
			.append("<companyId>").append(empresa).append("</companyId>")
			.append("<groupId>").append(grupo).append("</groupId>")
			.append("<foo/>")
			.append("<groupDescription>").append(grupo).append("</groupDescription>")
			.append("</item>")        		
			.append("</groups>");

			XmlBinder binder = new XmlBinder();
			SimpleResult result = binder.bind(SimpleResult.class, WebDeskWS.call(url, "GroupService", "createGroup","http://ws.foundation.webdesk.technology.datasul.com/" ,xml.toString()));
			return result.getResultXML();
		}catch (Exception e) {
			return e.getMessage();
		}
	}	

	private static String callWSRole(String url, String user, String passwd, String empresa, String id, String role) throws Exception {

		try{
			StringBuffer xml = new StringBuffer();
			
			id = id.replace(" ", "");

			xml.append("<username>").append(user).append("</username>")
			.append("<password>").append(passwd).append("</password>")
			.append("<companyId>").append(empresa).append("</companyId>")
			.append("<workflowRoleDto>")
			.append("<companyId>").append(empresa).append("</companyId>")
			.append("<foo/>")
			.append("<roleDescription>").append(role).append("</roleDescription>")
			.append("<roleId>").append(id).append("</roleId>")
			.append("</workflowRoleDto>");

			XmlBinder binder = new XmlBinder();

			RoleResult result = binder.bind(RoleResult.class, WebDeskWS.call(url, "WorkflowRoleService", "createWorkflowRole","http://ws.workflow.webdesk.technology.datasul.com/" ,xml.toString()));
			return result.getFaultcode() + result.getCreateWorkflowRoleResult();
		}catch (Exception e) {
			return e.getMessage();
		}
	}	

	private static ArrayList<String> cachePapeis(String url, String user, String passwd,String empresa, HSSFSheet sheet) throws Exception {

		StringBuffer xml = new StringBuffer();

		xml.append("<username>").append(user).append("</username>")
		.append("<password>").append(passwd).append("</password>")
		.append("<companyId>").append(empresa).append("</companyId>");

		XmlBinder binder = new XmlBinder();
		GetRolesResult result = binder.bind(GetRolesResult.class, WebDeskWS.call(url, "WorkflowRoleService", "getWorkflowRoles","http://ws.workflow.webdesk.technology.datasul.com/" ,xml.toString()));

		if(result.getFaultcode() != null  && result.getFaultcode().trim().length() > 0 ) {
			throw new Exception(result.getFaultcode());
		}

		ArrayList<String> cachePapeis = new ArrayList<String>();
		//Integer maiorId = 0;

		for(Role r:result.getGetWorkflowRolesResult()) {
			System.out.println(r.getRoleId());
			WSUserImport.jTextLog.append(r.getRoleDescription()+"\n");
			WSUserImport.jTextLog.update(WSUserImport.jTextLog.getGraphics());
			moveLog(WSUserImport.jTextLog.getCaretPosition());
			//Integer id = new Integer(r.getRoleId());
			cachePapeis.add(r.getRoleDescription() == null ? "" : r.getRoleDescription());

			/*if(id > maiorId) {
				maiorId = id;
			}*/
		}

		/*HSSFRow row;
		int rowIndex = 1;
		
		while( (row = sheet.getRow(rowIndex++)) != null) {
			String papeis    = getStringValue(row.getCell(10));
			if(papeis != null && papeis.trim().length() > 0) {
				for (String p: papeis.split("\\|")){
					if(! cachePapeis.contains(p)) {
						//maiorId++;
						callWSRole(url, user, passwd, empresa, String.valueOf(p), p);
						cachePapeis.add(p);
					}
				}
			}
		}*/
		return cachePapeis;
	}
	
	private static TreeSet<String> cacheGrupos(String url, String user, String passwd,String empresa) throws Exception {
		
		StringBuffer xml = new StringBuffer();

		xml.append("<username>").append(user).append("</username>")
		.append("<password>").append(passwd).append("</password>")
		.append("<companyId>").append(empresa).append("</companyId>");

		XmlBinder binder = new XmlBinder();
		Result result = binder.bind(Result.class, WebDeskWS.call(url, "GroupService", "getGroups","http://ws.workflow.webdesk.technology.datasul.com/" ,xml.toString()));
		
		TreeSet<String> cacheGrupos = new TreeSet<String>();
		System.out.println("numero de grupos "+result.getResult().size());
		for(Group r:result.getResult()) {
			System.out.println(r.getGroupId());
			/*Grupos que ja existem
			WSUserImport.jTextLog.append(r.getGroupId()+"\n");
			WSUserImport.jTextLog.update(WSUserImport.jTextLog.getGraphics());
			*/
			//String id = new String();
			if(r.getGroupId() != null){
				cacheGrupos.add(r.getGroupId());
				System.out.println("grupo existente: "+r.getGroupId());
			}
		}

		/*HSSFRow row;
		int rowIndex = 1;
		
		while( (row = sheet.getRow(rowIndex++)) != null) {
			String papeis    = getStringValue(row.getCell(8));
			if(papeis != null && papeis.trim().length() > 0) {
				for (String p: papeis.split("\\|")){
					if(! cacheGrupos.containsKey(p)) {
						maiorId++;
						callWSRole(url, user, passwd, empresa, maiorId, p);
						cacheGrupos.put(p, maiorId);
					}
				}
			}
		}*/
		return cacheGrupos;
	
	}
	
	private static boolean moveLog(int position){
		
		Rectangle rect;
		try {
			rect = WSUserImport.jTextLog.modelToView(position);
			WSUserImport.jTextLog.scrollRectToVisible(rect);
			if(position > WSUserImport.scroll.getBounds().height && !scrollActive){
				scrollActive = true;
				System.out.println("position "+position);
				System.out.println("WSUserImport.scroll.getBounds().height "+WSUserImport.scroll.getBounds().height);
				WSUserImport.scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			}
			WSUserImport.scroll.update(WSUserImport.scroll.getGraphics());
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;
		
	}
	
	public static String semAcento(String str) {
        String nfdNormalizedString = Normalizer.normalize(str, Normalizer.Form.NFD); 
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(nfdNormalizedString).replaceAll("");
    }
	
	public static String criptografaSenha(String senha) {
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			
		}

		BigInteger hash = new BigInteger(1, md.digest(senha.getBytes()));
		String s = hash.toString(16);

		if (s.length() % 2 != 0)
			s = "0" + s;

		return s;
	}
}
