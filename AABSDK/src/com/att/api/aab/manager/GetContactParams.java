package com.att.api.aab.manager;

import com.att.api.aab.service.PageParams;


public class GetContactParams {
	
	private String xFields;
	private PageParams pageParams;
	private String searchParams;
	
	public GetContactParams(String xFields, PageParams pageParams,
			String searchParams) {
		super();
		this.xFields = xFields;
		this.pageParams = pageParams;
		this.searchParams = searchParams;
	}

	public String getxFields() {
		return xFields;
	}

	public void setxFields(String xFields) {
		this.xFields = xFields;
	}

	public PageParams getPageParams() {
		return pageParams;
	}

	public void setPageParams(PageParams pageParams) {
		this.pageParams = pageParams;
	}

	public String getSearchParams() {
		return searchParams;
	}

	public void setSearchParams(String searchParams) {
		this.searchParams = searchParams;
	}
	
	

}
