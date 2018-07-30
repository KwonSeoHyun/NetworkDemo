package com.farmstory.vo;

import java.util.Date;

//공지사항을 DB에 저장하기 위한 vo 객체
public class Ann {

	private int annNo;
	private String annTitle;
	private String annContent;
	private Date annDate;
	
	public int getAnnNo() {
		return annNo;
	}
	public void setAnnNo(int annNo) {
		this.annNo = annNo;
	}
	public String getAnnTitle() {
		return annTitle;
	}
	public void setAnnTitle(String annTitle) {
		this.annTitle = annTitle;
	}
	public String getAnnContent() {
		return annContent;
	}
	public void setAnnContent(String annContent) {
		this.annContent = annContent;
	}
	public Date getAnnDate() {
		return annDate;
	}
	public void setAnnDate(Date annDate) {
		this.annDate = annDate;
	}
}
