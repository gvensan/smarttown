package com.eap.scs.asyncapi.alertaggregator;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;


@JsonInclude(JsonInclude.Include.NON_NULL)
public class AggregateAlert {
	private int count;
	private String severity;
	private String alertType;
	private String city;
	private String timeStamp;
	private int duration; 
	private java.math.BigDecimal temperature;
	private java.math.BigDecimal lat;
	@JsonProperty("long")
	private java.math.BigDecimal _long;

	public static final String ISO_8601_24H_FULL_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX";
	final SimpleDateFormat sdf = new SimpleDateFormat(ISO_8601_24H_FULL_FORMAT);


	public AggregateAlert () {
		this.count = 0;
		this.timeStamp = sdf.format(new Date());
		this.duration = 0;
		this.severity = "Unknown";
		this.alertType = "Unknown";
		this.city = "Unknown";
		this.temperature = BigDecimal.ZERO;
		this.lat = BigDecimal.ZERO;
		this._long = BigDecimal.ZERO;
	}
	public AggregateAlert (
		int count,
		int duration,
		String severity, 
		String alertType, 
		String city, 
		String timeStamp,
		java.math.BigDecimal temperature, 
		java.math.BigDecimal lat, 
		java.math.BigDecimal _long) {
		this.count = count;
		this.duration = duration;
		this.severity = severity;
		this.alertType = alertType;
		this.city = city;
		this.timeStamp = timeStamp;
		this.temperature = temperature;
		this.lat = lat;
		this._long = _long;
	}


	public int getCount() {
		return count;
	}
	
	public void setCount(int count) {
		this.count = count;
	}

	public String getSeverity() {
		return severity;
	}

	public AggregateAlert setSeverity(String severity) {
		this.severity = severity;
		return this;
	}


	public String getAlertType() {
		return alertType;
	}

	public AggregateAlert setAlertType(String alertType) {
		this.alertType = alertType;
		return this;
	}


	public String getCity() {
		return city;
	}

	public AggregateAlert setCity(String city) {
		this.city = city;
		return this;
	}


	public String getTimeStamp() {
		return timeStamp;
	}
	public void setTimeStamp(String timeStamp) {
		this.timeStamp = timeStamp;
	}
	public int getDurationSec() {
		return duration;
	}
	public void setDurationSec(int durationSec) {
		this.duration = durationSec;
	}
	public java.math.BigDecimal getTemperature() {
		return temperature;
	}

	public AggregateAlert setTemperature(java.math.BigDecimal temperature) {
		this.temperature = temperature;
		return this;
	}


	public java.math.BigDecimal getLat() {
		return lat;
	}

	public AggregateAlert setLat(java.math.BigDecimal lat) {
		this.lat = lat;
		return this;
	}


	public java.math.BigDecimal get_long() {
		return _long;
	}

	public AggregateAlert set_long(java.math.BigDecimal _long) {
		this._long = _long;
		return this;
	}


	@Override
	public String toString() {
		return "AggregateAlert [count=" + count + ", severity=" + severity + ", alertType=" + alertType + ", city="
				+ city + ", timeStamp=" + timeStamp + ", temperature=" + temperature + ", lat=" + lat + ", _long="
				+ _long + "]";
	}
}

