package com.app.exceldatatodb.response;

public class JsonResponse {

	public String message;
	
	public JsonResponse() {
		super();
	}

	public JsonResponse(String message) {
		super();
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
