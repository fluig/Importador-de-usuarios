package br.com.bravatec.webdesk.imp;

public interface ImportUserConstants {
	
	public static final String VERSION = "versão 1.4.0";
	public static final String APPLICATION_NAME = "Importador de Usuários - Fluig - ";
	public static final String LABEL = "Dados para a importação";
	public static final String LABEL_SMTP = "Servidor de e-mails";
	public static final String URL_ECM = "URL";
	public static final String USER = "Matrícula";
	public static final String LOGIN = "Login";
	public static final String USER_PASSWORD = "Senha";
	public static final String COMPANY_CODE = "Empresa";
	public static final String SPREADSHEET = "Arquivo Excel";
	public static final String SEARCH_FILE = "Buscar";
	public static final String IMPORT = "Importar";
	public static final String IMPORT_SUCCESS = "Relatório de importação salvo em: "; 
	public static final String EMPTY_URL = "Favor informe a url de acesso ao ECM TOTVS";
	public static final String INVALID_URL = "Favor insira uma URL válida.\nEx.: http://<servidor>:<porta>/webdesk";
	public static final String EMPTY_FIELD_FILE = "Favor selecione o arquivo Excel";
	public static final String INVALID_EXCEL_FILE = "Favor selecione somente arquivo Excel (.xls)";
	public static final String EXCEL_FILE = "Planilha do Excel 97-2003 (*.xls)";
	public static final String INVALID_FILE = "Arquivo não encontrado";
	public static final String EMPTY_USER = "Favor informe o usuário";
	public static final String EMPTY_PASSWORD = "Favor informe a senha";
	public static final String EMPTY_COMPANY = "Favor informe o código da empresa";
	public static final String WS_ERROR_ACCESS = "Erro ao invocar o webservice.\nVerifique os dados informados e tente novamente.";
	public static final String DISABLED_USER = "Usuário informado não esta ativo.";
	public static final String NOT_ADMNISTRATOR = "Usuário informado deve ser administrador do Fluig.";
	public static final String IMPORT_STATUS = "Status Importação";
	public static final String GROUPI_STATUS = "Status inclusão grupos";
	public static final String GROUPR_STATUS = "Status remoção grupos";
	public static final String ERROR = "Erro";
	public static final String URL_DEFAULT = "http://spod3792/webdesk";
	public static final String INFO = "Importação realizada com sucesso";
	public static final String NOT_FOUND = "Arquivo não encontrado: ";
	public static final String SMTP_HOST = "Host smtp";
	public static final String SMTP_PORT = "Porta";
	public static final String SMTP_AUTH = "Req. autenticação";
	public static final String SMTP_USER = "Usuário";
	public static final String SMTP_PASS = "Senha";
	public static final String LOG = "Log";
	public static final String SMTP = "Servidor de E-mail";
	public static final String PROC = "Execução";
}
