package cn.com.dataasyncload.domain;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Contact implements Serializable {
	private String date;// 日期
	private String dayPictureUrl;// 白天的图片
	private String nightPictureUrl;// 晚上的图片

	public Contact() {
		super();
	}

	public Contact(String date, String dayPictureUrl, String nightPictureUrl) {
		super();
		this.date = date;
		this.dayPictureUrl = dayPictureUrl;
		this.nightPictureUrl = nightPictureUrl;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getDayPictureUrl() {
		return dayPictureUrl;
	}

	public void setDayPictureUrl(String dayPictureUrl) {
		this.dayPictureUrl = dayPictureUrl;
	}

	public String getNightPictureUrl() {
		return nightPictureUrl;
	}

	public void setNightPictureUrl(String nightPictureUrl) {
		this.nightPictureUrl = nightPictureUrl;
	}

}
