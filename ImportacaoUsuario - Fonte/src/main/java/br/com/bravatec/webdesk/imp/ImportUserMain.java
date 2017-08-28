package br.com.bravatec.webdesk.imp;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.PopupMenu;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JPopupMenu;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.filechooser.FileFilter;
import javax.swing.text.DefaultCaret;

import org.apache.poi.ss.usermodel.VerticalAlignment;

import br.com.bravatec.webdesk.util.ColleagueResult;
import br.com.bravatec.webdesk.util.WebDeskWS;
import br.com.bravatec.webdesk.util.XmlBinder;

@SuppressWarnings("serial")
public class ImportUserMain extends JFrame implements ActionListener {

	private JPanel jpnFields, jpnToolbar,jpnFields2,jpnFields3;
	private JTextField jtxtUrl, jtxtUser, jtxtEmpresa ,jtxtFile, jtxtHostSMTP, jtxtPortSMTP, jtxtUserSMTP, jtxtLogin;
	private JPasswordField jtxtPasswd, jtxtPassSMTP;
	private JButton jbtnFind, jbtnImport;
	private JLabel jlblUrl, jlblUser, jlblPasswd, jlblFile, jlblLogo, jlblEmpresa, jlblHostSMTP, jlblPortSMTP, jlblAuthSMTP, jlblUserSMTP, jlblPassSMTP, jlblLog, jlblLogin;
	private JCheckBox jchkAuthSMTP;
	public JTextArea jTextLog;
	public JScrollPane scroll;
	
	public ImportUserMain() {
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setTitle(ImportUserConstants.APPLICATION_NAME + ImportUserConstants.VERSION);
		setResizable(false);
		setSize(475,570);
		int x = (getToolkit().getScreenSize().width / 2) - (getWidth() / 2);
		int y = (getToolkit().getScreenSize().height / 2) - (getHeight() / 2);
		setLocation(x, y);

		buildScreen();

		this.jbtnFind.setActionCommand("browse");
		this.jbtnFind.addActionListener(this);
		this.jbtnImport.setActionCommand("importar");
		this.jbtnImport.addActionListener(this);
		this.jchkAuthSMTP.setActionCommand("auth");
		this.jchkAuthSMTP.addActionListener(this);
		//this.scroll.setActionCommand("auth");
		
		final PopUpDemo menu = new PopUpDemo();
		
		this.jTextLog.addMouseListener(new MouseAdapter() {
			 @Override
		     public void mousePressed(MouseEvent e) {
		        if(e.getButton() == MouseEvent.BUTTON3){
		        	
		            menu.show(e.getComponent(), e.getX(), e.getY());
		        	//jTextLog.setText("");
		        }
		     }
		});
		menu.anItem.addActionListener(this);
		menu.anItem.setActionCommand("clearlog");
		

		this.jtxtUrl.setText(ImportUserConstants.URL_DEFAULT);

		setVisible(true);
	}

	public void buildScreen(){

		jpnFields = new JPanel(null);
		jpnFields.setBorder(BorderFactory.createTitledBorder(ImportUserConstants.LABEL));
		jpnFields.setPreferredSize(new Dimension(307,125));
		jpnFields.setBounds(0, 0, 307, 125);
		
		jpnFields2 = new JPanel(null);
		jpnFields2.setBorder(BorderFactory.createTitledBorder(ImportUserConstants.SMTP));
		jpnFields2.setPreferredSize(new Dimension(307,125));
		jpnFields2.setBounds(0, 125, 307, 125);
		
		jpnFields3 = new JPanel(null);
		jpnFields3.setBorder(BorderFactory.createTitledBorder(ImportUserConstants.PROC));
		jpnFields3.setPreferredSize(new Dimension(307,270));
		jpnFields3.setBounds(0, 250, 307, 270);

		jpnToolbar = new JPanel(new FlowLayout(FlowLayout.CENTER));

		jlblUrl = new JLabel(ImportUserConstants.URL_ECM);
		jlblUrl.setBounds(65,30, 50,25);
		jtxtUrl = new JTextField(41);
		jtxtUrl.setBounds(110, 30, 210, 25);

		jlblUser = new JLabel(ImportUserConstants.USER);
		jlblUser.setBounds(48,55, 50,25);
		jtxtUser = new JTextField(18);
		jtxtUser.setBounds(110, 55, 80, 25);
		
		jlblLogin = new JLabel(ImportUserConstants.LOGIN);
		jlblLogin.setBounds(195,55, 50,25);
		jtxtLogin = new JTextField(18);
		jtxtLogin.setBounds(240, 55, 80, 25);

		jlblPasswd = new JLabel(ImportUserConstants.USER_PASSWORD);
		//jlblPasswd.setBounds(195,50, 50,25);
		jlblPasswd.setBounds(325,55, 50,25);
		jtxtPasswd = new JPasswordField(18);
		//jtxtPasswd.setBounds(240, 50, 80, 25);
		jtxtPasswd.setBounds(380, 55, 80, 25);

		jlblEmpresa = new JLabel(ImportUserConstants.COMPANY_CODE);
		jlblEmpresa.setBounds(325,30, 60,25);
		jtxtEmpresa= new JTextField(18);
		jtxtEmpresa.setBounds(380, 30, 80, 25);
		jtxtEmpresa.setText("1");

		jlblFile = new JLabel(ImportUserConstants.SPREADSHEET);
		jlblFile.setBounds(20,80, 100,25);
		jtxtFile = new JTextField(33);
		jtxtFile.setBounds(110, 80, 210, 25);
		jtxtFile.setEnabled(false);
		jtxtFile.setForeground(Color.BLACK);
		
		jlblHostSMTP = new JLabel(ImportUserConstants.SMTP_HOST);
		jlblHostSMTP.setBounds(20,30, 100,25);
		jtxtHostSMTP = new JTextField(18);
		jtxtHostSMTP.setBounds(110, 30, 210, 25);
		
		jlblPortSMTP = new JLabel(ImportUserConstants.SMTP_PORT);
		jlblPortSMTP.setBounds(325,30, 60,25);
		jtxtPortSMTP = new JTextField(18);
		jtxtPortSMTP.setBounds(380, 30, 80, 25);
		
		jlblAuthSMTP = new JLabel(ImportUserConstants.SMTP_AUTH);
		jlblAuthSMTP.setBounds(10,55, 100,25);
		jchkAuthSMTP = new JCheckBox();
		jchkAuthSMTP.setBounds(110, 55, 40, 25);
		
		jlblUserSMTP = new JLabel(ImportUserConstants.SMTP_USER);
		jlblUserSMTP.setBounds(38,80, 60,25);
		jtxtUserSMTP = new JTextField(33);
		jtxtUserSMTP.setBounds(110, 80, 210, 25);
		jtxtUserSMTP.setEnabled(false);
		
		jlblPassSMTP = new JLabel(ImportUserConstants.SMTP_PASS);
		jlblPassSMTP.setBounds(38,105, 60,25);
		jtxtPassSMTP = new JPasswordField(33);
		jtxtPassSMTP.setBounds(110, 105, 210, 25);
		jtxtPassSMTP.setEnabled(false);
		
		//jlblLog = new JLabel(ImportUserConstants.LOG);
		//jlblLog.setBounds(38,25, 60,25);
		jTextLog = new JTextArea();
		jTextLog.setBounds(38, 30, 420, 200);
		jTextLog.setEditable(false);
		
		scroll = new JScrollPane (jTextLog);
		scroll.setBounds(25, 30, 420, 200);
		
	    //JScrollBar verticalScrollBar = new JScrollBar();
	    //verticalScrollBar.setVe
		scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
	    scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
	    
	   // scroll.getVerticalScrollBar().addAdjustmentListener(l);
	    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	    /*ComponentListener l = new ComponentListener() {
			
			@Override
			public void componentShown(ComponentEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void componentResized(ComponentEvent e) {
				// TODO Auto-generated method stub
				System.out.println("]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]");
			}
			
			@Override
			public void componentMoved(ComponentEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void componentHidden(ComponentEvent e) {
				// TODO Auto-generated method stub
				
			}
		};
		scroll.getVerticalScrollBar();
	    
	    
	    scroll.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {  
	        public void adjustmentValueChanged(AdjustmentEvent e) {  
	        	System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>ha<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
	            e.getAdjustable().setValue(e.getAdjustable().getMaximum());  
	        }
	    });*/

	    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		jbtnFind = new JButton(ImportUserConstants.SEARCH_FILE);
		jbtnFind.setBounds(320, 80, 140, 25);

		jbtnImport = new JButton(ImportUserConstants.IMPORT);
		jbtnImport.setBounds(185, 230, 105, 25);

		jlblLogo = new JLabel();

		try{
			this.setIconImage(getToolkit().createImage(ClassLoader.getSystemResource("images/fluig_icon.png")));
		}catch (Exception e) {

		}
		
		try {
			jlblLogo.setIcon(new ImageIcon(ClassLoader.getSystemResource("images/fluig_icon.png")));
		}catch (Exception e) {

		}

		jpnFields.add(jlblUrl);
		jpnFields.add(jtxtUrl);
		jpnFields.add(jlblUrl);
		jpnFields.add(jtxtUrl);
		jpnFields.add(jlblUser);
		jpnFields.add(jtxtUser);
		jpnFields.add(jlblLogin);
		jpnFields.add(jtxtLogin);
		jpnFields.add(jlblPasswd);
		jpnFields.add(jtxtPasswd);
		jpnFields.add(jlblEmpresa);
		jpnFields.add(jtxtEmpresa);

		jpnFields.add(jlblFile);
		jpnFields.add(jtxtFile);
		jpnFields.add(jbtnFind);
		//jpnFields.add(jpnToolbar);
		jpnFields2.add(jlblHostSMTP);
		jpnFields2.add(jtxtHostSMTP);
		jpnFields2.add(jlblPortSMTP);
		jpnFields2.add(jtxtPortSMTP);
		jpnFields2.add(jlblAuthSMTP);
		jpnFields2.add(jchkAuthSMTP);
		jpnFields2.add(jlblUserSMTP);
		jpnFields2.add(jtxtUserSMTP);
		jpnFields2.add(jlblPassSMTP);
		jpnFields2.add(jtxtPassSMTP);
		//jpnFields3.add(jlblLog);
		jpnFields3.add(scroll);
		
		jpnFields3.add(jbtnImport);

		//add("North", jlblLogo);
		add("North",jpnFields);
		add("Center",jpnFields2);
		add("South",jpnFields3);
		//add("South", jpnToolbar);
	}

	public void actionPerformed(ActionEvent e) {
		
		if("browse".equals(e.getActionCommand())) {
					
			JFileChooser chooser = new JFileChooser();
			chooser.setFileFilter(new FileFilter(){
				@Override
				public boolean accept(File f) {
					if(f.isDirectory()){
						return true;
					}
					return f.getName().toLowerCase().endsWith(".xls");
				}
				@Override
				public String getDescription() {
					return ImportUserConstants.EXCEL_FILE;
				}
			});
			chooser.showOpenDialog(this);

			if(chooser.getSelectedFile() != null && chooser.getSelectedFile().exists()) {
				this.jtxtFile.setText(chooser.getSelectedFile().getAbsolutePath());
			}
		} else if("importar".equals(e.getActionCommand())){
			this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			try {
				if(! validateFields()){
					this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
					return;
				}

				byte[] report = WSUserImport.execute(this.jtxtFile.getText(), this.jtxtUrl.getText(), this.jtxtLogin.getText(), this.jtxtEmpresa.getText() ,new String(this.jtxtPasswd.getPassword()),this.jtxtHostSMTP.getText(),this.jtxtPortSMTP.getText(),this.jtxtUserSMTP.getText(),new String (this.jtxtPassSMTP.getPassword()),this.jchkAuthSMTP.isSelected(),this.jTextLog,this.scroll);

				String f = new File(this.jtxtFile.getText()).getParent() + "/Relatório de Importação de Usuários.xls";
				FileOutputStream out = new FileOutputStream(f);
				out.write(report);
				out.flush();
				out.close();
				JOptionPane.showMessageDialog(this, ImportUserConstants.IMPORT_SUCCESS + f, ImportUserConstants.INFO, JOptionPane.INFORMATION_MESSAGE);
			} catch (Exception e1) {
				JOptionPane.showMessageDialog(this, e1.getMessage(), ImportUserConstants.ERROR, JOptionPane.ERROR_MESSAGE);
			}
			this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		} else if ("auth".equals(e.getActionCommand())){
			if(jtxtUserSMTP.isEnabled()){
				jtxtUserSMTP.setEnabled(false);
				jtxtUserSMTP.setForeground(Color.BLACK);
				jtxtPassSMTP.setEnabled(false);
				jtxtPassSMTP.setForeground(Color.BLACK);
			}else{
				jtxtUserSMTP.setEnabled(true);
				jtxtUserSMTP.setForeground(jtxtUser.getForeground());
				jtxtPassSMTP.setEnabled(true);
				jtxtPassSMTP.setForeground(jtxtUser.getForeground());
			}
		} else if ("clearlog".equals(e.getActionCommand())){
			System.out.println("emptyup");
			jTextLog.setText("");
			scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		}
	}

	private boolean validateFields() {

		if(this.jtxtUrl.getText().trim().length() == 0) {
			JOptionPane.showMessageDialog(this, ImportUserConstants.EMPTY_URL, ImportUserConstants.ERROR, JOptionPane.WARNING_MESSAGE);
			return false;
		}

		try {
			new URL(jtxtUrl.getText());
		}catch (Exception e) {
			JOptionPane.showMessageDialog(this, ImportUserConstants.INVALID_URL, ImportUserConstants.ERROR, JOptionPane.WARNING_MESSAGE);
			return false;
		}

		if(this.jtxtFile.getText().trim().length() == 0) {
			JOptionPane.showMessageDialog(this, ImportUserConstants.EMPTY_FIELD_FILE, ImportUserConstants.ERROR, JOptionPane.WARNING_MESSAGE);
			return false;
		}		

		if(this.jtxtFile.getText().toLowerCase().indexOf(".xls") == -1) {
			JOptionPane.showMessageDialog(this, ImportUserConstants.INVALID_EXCEL_FILE, ImportUserConstants.ERROR, JOptionPane.WARNING_MESSAGE);
			return false;
		}

		File f= new File(this.jtxtFile.getText());

		if(!f.exists()) {
			JOptionPane.showMessageDialog(this, ImportUserConstants.NOT_FOUND + "\n" + f.getAbsolutePath(), ImportUserConstants.ERROR, JOptionPane.WARNING_MESSAGE);
			return false;
		}

		if(this.jtxtUser.getText().trim().length() == 0) {
			JOptionPane.showMessageDialog(this, ImportUserConstants.EMPTY_USER, ImportUserConstants.ERROR, JOptionPane.WARNING_MESSAGE);
			return false;
		}
		if(this.jtxtPasswd.getPassword().length == 0) {
			JOptionPane.showMessageDialog(this, ImportUserConstants.EMPTY_PASSWORD, ImportUserConstants.ERROR, JOptionPane.WARNING_MESSAGE);
			return false;
		}

		if(this.jtxtEmpresa.getText().trim().length() == 0) {
			JOptionPane.showMessageDialog(this, ImportUserConstants.EMPTY_COMPANY, ImportUserConstants.ERROR, JOptionPane.WARNING_MESSAGE);
			return false;
		}

		try{
			validateUser();
		}catch (Exception e) {
			JOptionPane.showMessageDialog(this, ImportUserConstants.WS_ERROR_ACCESS, ImportUserConstants.ERROR, JOptionPane.WARNING_MESSAGE);
			e.printStackTrace();
			return false;
		}

		return true;
	}

	private void validateUser() throws Exception {

		StringBuffer xml = new StringBuffer();

		xml.append("<username>").append(jtxtLogin.getText()).append("</username>")
		.append("<password>").append(jtxtPasswd.getPassword()).append("</password>")
		.append("<companyId>").append(jtxtEmpresa.getText()).append("</companyId>")
		.append("<colleagueId>").append(jtxtUser.getText()).append("</colleagueId>") ;


		XmlBinder binder = new XmlBinder();

		ColleagueResult result = binder.bind(ColleagueResult.class, WebDeskWS.call(jtxtUrl.getText(), "ECMColleagueService", "getColleague","http://ws.foundation.ecm.technology.totvs.com/" ,xml.toString()));

		if(result.getColab() == null || result.getColab().size() ==0){
			throw new Exception (ImportUserConstants.WS_ERROR_ACCESS);
		}
		if(! "true".equals(result.getColab().get(0).getActive())) {
			throw new Exception (ImportUserConstants.DISABLED_USER);
		}
		if(! "true".equals(result.getColab().get(0).getAdminUser())) {
			throw new Exception (ImportUserConstants.NOT_ADMNISTRATOR);
		}
	}	

	public static void main(String[] args) {

		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
		} catch (Exception e) {

		}
		
		new ImportUserMain();
	}
}

class PopUpDemo extends JPopupMenu {
    JMenuItem anItem;
    public PopUpDemo(){
        anItem = new JMenuItem("Limpar log");
        add(anItem);
    }
}
