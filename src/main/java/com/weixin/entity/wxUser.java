package com.weixin.entity;

public class wxUser {
	private Integer times;
	private String access_code;
	private char experience;
	private Integer code;
	private String wx_id;
	public wxUser() {
		// TODO Auto-generated constructor stub
	}
	public wxUser(Integer times, String access_code, char experience, Integer code, String wx_id) {
		this.times = times;
		this.access_code = access_code;
		this.experience = experience;
		this.code = code;
		this.wx_id = wx_id;
	}
	public Integer getTimes() {
		return times;
	}
	public void setTimes(Integer times) {
		this.times = times;
	}
	public String getAccess_code() {
		return access_code;
	}
	public void setAccess_code(String access_code) {
		this.access_code = access_code;
	}
	public char getExperience() {
		return experience;
	}
	public void setExperience(char experience) {
		this.experience = experience;
	}
	public Integer getCode() {
		return code;
	}
	public void setCode(Integer code) {
		this.code = code;
	}
	public String getWx_id() {
		return wx_id;
	}
	public void setWx_id(String wx_id) {
		this.wx_id = wx_id;
	}
	
	
	
}
