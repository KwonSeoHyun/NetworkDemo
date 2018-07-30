package com.farmstory.vo;

import java.util.List;

//식물정보를 DB에 저장하기 위한 vo 객체
public class Plant extends PlantImg {

	private String plaName;
	private String plaContent;
	private String plaSimple;
	private int plaMaxTem;
	private int plaMinTem;
	private int plaMaxHum;
	private int plaMinHum;
	private int plaMaxLux;
	private int plaMinLux;
	private String plaType;

	// Plant 테이블과 PlantImg 테이블의 관계를 1:Many의 관계를 만들기 위한 필드
	private List<PlantImg> attachments;
	
	public String getPlaName() {
		return plaName;
	}
	public void setPlaName(String plaName) {
		this.plaName = plaName;
	}
	public String getPlaContent() {
		return plaContent;
	}
	public void setPlaContent(String plaContent) {
		this.plaContent = plaContent;
	}
	public String getPlaSimple() {
		return plaSimple;
	}
	public void setPlaSimple(String plaSimple) {
		this.plaSimple = plaSimple;
	}
	public int getPlaMaxTem() {
		return plaMaxTem;
	}
	public void setPlaMaxTem(int plaMaxTem) {
		this.plaMaxTem = plaMaxTem;
	}
	public int getPlaMinTem() {
		return plaMinTem;
	}
	public void setPlaMinTem(int plaMinTem) {
		this.plaMinTem = plaMinTem;
	}
	public int getPlaMaxHum() {
		return plaMaxHum;
	}
	public void setPlaMaxHum(int plaMaxHum) {
		this.plaMaxHum = plaMaxHum;
	}
	public int getPlaMinHum() {
		return plaMinHum;
	}
	public void setPlaMinHum(int plaMinHum) {
		this.plaMinHum = plaMinHum;
	}
	public int getPlaMaxLux() {
		return plaMaxLux;
	}
	public void setPlaMaxLux(int plaMaxLux) {
		this.plaMaxLux = plaMaxLux;
	}
	public int getPlaMinLux() {
		return plaMinLux;
	}
	public void setPlaMinLux(int plaMinLux) {
		this.plaMinLux = plaMinLux;
	}
	public List<PlantImg> getAttachments() {
		return attachments;
	}
	public void setAttachments(List<PlantImg> attachments) {
		this.attachments = attachments;
	}
	public String getPlaType() {
		return plaType;
	}
	public void setPlaType(String plaType) {
		this.plaType = plaType;
	}



}
