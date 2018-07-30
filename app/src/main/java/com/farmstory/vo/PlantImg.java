package com.farmstory.vo;


//식물 이미지 정보를 DB에 저장하기 위한 vo 객체
public class PlantImg {

	private int pliNo;
	private String pliImg;
	private int plaNo;
	private int imgIdx;
	
	public int getPliNo() {
		return pliNo;
	}
	public void setPliNo(int pliNo) {
		this.pliNo = pliNo;
	}
	public String getPliImg() {
		return pliImg;
	}
	public void setPliImg(String pliImg) {
		this.pliImg = pliImg;
	}
	public int getPlaNo() {
		return plaNo;
	}
	public void setPlaNo(int plaNo) {
		this.plaNo = plaNo;
	}
	public int getImgIdx() {
		return imgIdx;
	}
	public void setImgIdx(int imgIdx) {
		this.imgIdx = imgIdx;
	}
	
	
}
