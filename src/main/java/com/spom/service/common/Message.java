package com.spom.service.common;

import java.io.Serializable;

public class Message implements Serializable {

	private static final long serialVersionUID = 1L;	
	private String messageType=null;
	private String message=null;

	public void setMessageType(String messageType) {
		this.messageType = messageType;
	}
	public String getMessageType() {
		return messageType;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getMessage() {
		return message;
	}
}
