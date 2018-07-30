package com.farmstory.vo;

//�쎒 �냼媛쒗럹�씠吏�瑜� DB�뿉 ���옣�븯湲� �쐞�븳 vo 媛앹껜
public class Cover {

	private int covNo;
	private String covTitle;
	private String covLcontent;
	private String covRcontent;
	public String getCovRcontent() {
		return covRcontent;
	}
	public void setCovRcontent(String covRcontent) {
		this.covRcontent = covRcontent;
	}
	private String covImg;
	
	public int getCovNo() {
		return covNo;
	}
	public void setCovNo(int covNo) {
		this.covNo = covNo;
	}
	public String getCovTitle() {
		return covTitle;
	}
	public void setCovTitle(String covTitle) {
		this.covTitle = covTitle;
	}
	public String getCovLcontent() {
		return covLcontent;
	}
	public void setCovLcontent(String covLcontent) {
		this.covLcontent = covLcontent;
	}
	
	public String getCovImg() {
		return covImg;
	}
	public void setCovImg(String covImg) {
		this.covImg = covImg;
	}
}
