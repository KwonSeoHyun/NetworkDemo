package com.farmstory.vo;

//식물등록테이블 정보를 DB에 저장하기 위한 vo 객체
public class RegistPlant {

	private int regPotNo;
	private String memId;
	private int plaNo;
	
	public int getRegPotNo() {
		return regPotNo;
	}
	public void setRegPotNo(int regPotNo) {
		this.regPotNo = regPotNo;
	}
	public String getMemId() {
		return memId;
	}
	public void setMemId(String memId) {
		this.memId = memId;
	}
	public int getPlaNo() {
		return plaNo;
	}
	public void setPlaNo(int plaNo) {
		this.plaNo = plaNo;
	}
	
}
