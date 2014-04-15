package com.att.api.immn.service;

public class GetContactParams {
	
	private String xFields;
	private PageParams pageParams;
	private SearchParams searchParams;
	
	public GetContactParams(String xFields, PageParams pageParams,
			SearchParams searchParams) {
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

	public SearchParams getSearchParams() {
		return searchParams;
	}

	public void setSearchParams(SearchParams searchParams) {
		this.searchParams = searchParams;
	}
	
	

}
