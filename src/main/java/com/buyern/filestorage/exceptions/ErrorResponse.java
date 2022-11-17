package com.buyern.filestorage.exceptions;


import com.buyern.filestorage.dtos.ResponseDTO;

public class ErrorResponse extends ResponseDTO<Object> {

    public ErrorResponse(String code, String message, Object data) {
        super(code, message, data);
    }

    public ErrorResponse(String code, String message) {
        super(code, message);
    }
}
