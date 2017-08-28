package br.com.bravatec.webdesk.util;

import java.util.List;

public class GetGroupsResponse {
	private List<Group> getGroupsResponse;
	private String faultcode;


	public List<Group> getGetGroupsResponse() {
		return getGroupsResponse;
	}

	public void setGetGroupsResponse(List<Group> getGroupsResponse) {
		this.getGroupsResponse = getGroupsResponse;
	}

	public String getFaultcode() {
		return faultcode;
	}

	public void setFaultcode(String faultcode) {
		this.faultcode = faultcode;
	}
}
