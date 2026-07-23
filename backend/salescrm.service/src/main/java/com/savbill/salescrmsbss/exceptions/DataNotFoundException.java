package com.savbill.salescrmsbss.exceptions;

import java.util.Objects;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import lombok.Data;

@ResponseStatus(code = HttpStatus.NOT_FOUND)
@Data
public class DataNotFoundException extends RuntimeException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String errorMessage = "Data Not found";
	
	private Integer statusCode = 400;

	public DataNotFoundException(String errorMessage) {
		super(errorMessage);
		this.errorMessage = errorMessage;
	}

	public DataNotFoundException(String errorMessage, Integer statusCode) {
		super(errorMessage);
		this.statusCode = statusCode;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DataNotFoundException other = (DataNotFoundException) obj;
		return Objects.equals(errorMessage, other.errorMessage) && Objects.equals(statusCode, other.statusCode);
	}

	@Override
	public int hashCode() {
		return Objects.hash(errorMessage, statusCode);
	}
}
