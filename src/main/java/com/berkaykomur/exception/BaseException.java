package com.berkaykomur.exception;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class BaseException extends RuntimeException {
    private HttpStatus status;
	public BaseException(ErrorMessage errorMessage) {
		super(errorMessage.prepearMessage());
        this.status = errorMessage.getMessagesType().getHttpStatus();
		
	}
    public HttpStatus getStatus() {
        return status;
    }

}
