package br.com.bravatec.webdesk.util;

import java.util.List;

public class GetRolesResult {
	private List<Role> getWorkflowRolesResult;
	private String faultcode;


	public List<Role> getGetWorkflowRolesResult() {
		return getWorkflowRolesResult;
	}

	public void setGetWorkflowRolesResult(List<Role> getWorkflowRolesResult) {
		this.getWorkflowRolesResult = getWorkflowRolesResult;
	}

	public String getFaultcode() {
		return faultcode;
	}

	public void setFaultcode(String faultcode) {
		this.faultcode = faultcode;
	}
}
