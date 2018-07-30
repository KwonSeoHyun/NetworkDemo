package com.farmstory.vo;


//다이어리 이미지 정보를 DB에 저장하기 위한 vo 객체
public class DiaryImg {

	private int diiNo;
	private int diaNo;
	private String diiImg;
	
	public int getDiiNo() {
		return diiNo;
	}
	public void setDiiNo(int diiNo) {
		this.diiNo = diiNo;
	}
	public int getDiaNo() {
		return diaNo;
	}
	public void setDiaNo(int diaNo) {
		this.diaNo = diaNo;
	}
	public String getDiiImg() {
		return diiImg;
	}
	public void setDiiImg(String diiImg) {
		this.diiImg = diiImg;
	}
	
	
}
