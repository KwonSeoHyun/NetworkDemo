package com.farmstory.vo;

//식물통계 정보를 DB에 저장하기 위한 vo 객체
public class PlantVal {

	private int plvNo;
	private String regPotNo;
	private String plvDateTime;
	private String plvType;
	private int plvVal;
	
	
	public String getPlvDateTime() {
		return plvDateTime;
	}
	public void setPlvDateTime(String plvDateTime) {
		this.plvDateTime = plvDateTime;
	}
	public String getPlvType() {
		return plvType;
	}
	public void setPlvType(String plvType) {
		this.plvType = plvType;
	}
	public int getPlvNo() {
		return plvNo;
	}
	public void setPlvNo(int plvNo) {
		this.plvNo = plvNo;
	}
	public String getRegPotNo() {
		return regPotNo;
	}
	public void setRegPotNo(String regPotNo) {
		this.regPotNo = regPotNo;
	}
	public int getPlvVal() {
		return plvVal;
	}
	public void setPlvVal(int plvVal) {
		this.plvVal = plvVal;
	}
	
	
}
