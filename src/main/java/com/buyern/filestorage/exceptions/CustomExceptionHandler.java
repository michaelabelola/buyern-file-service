package com.buyern.filestorage.exceptions;

import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.ArrayList;
import java.util.List;

@ControllerAdvice(basePackages = {"com.buyern.filestorage.controllers"})
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(Exception.class)
    public final ResponseEntity<Object> handleAllExceptions(Exception ex) {
        List<String> details = new ArrayList<>();
        details.add(ex.getMessage());
        logger.error(ex.getMessage());
        return new ResponseEntity<>(new ErrorResponse("91", "Error!", details), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(EntityAlreadyExistsException.class)
    public final ResponseEntity<Object> handleEntityAlreadyExistsException(EntityAlreadyExistsException ex) {
        logger.error(ex.getMessage());
        return new ResponseEntity<>(new ErrorResponse("04", ex.getMessage()), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(RecordNotFoundException.class)
    public final ResponseEntity<Object> handleRecordNotFoundException(RecordNotFoundException ex) {

        logger.error(ex.getMessage());
        return new ResponseEntity<>(new ErrorResponse("05", ex.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(LimitReachedException.class)
    public final ResponseEntity<Object> handleLimitReachedException(LimitReachedException ex) {

        logger.error(ex.getMessage());
        return new ResponseEntity<>(new ErrorResponse("06", ex.getMessage()), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(BadRequestException.class)
    public final ResponseEntity<Object> handleBadRequestException(BadRequestException ex) {
        logger.error(ex.getMessage());
        return new ResponseEntity<>(new ErrorResponse("07", ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(FileUploadException.class)
    public final ResponseEntity<Object> handleFileUploadException(FileUploadException ex) {
        logger.error(ex.getMessage());
        return new ResponseEntity<>(new ErrorResponse("08", ex.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        logger.error(ex.getMessage());
        List<String> details = new ArrayList<>();
        for (ObjectError error : ex.getBindingResult().getAllErrors()) {
            details.add(error.getDefaultMessage());
        }
        return new ResponseEntity<>(new ErrorResponse("03", "Bad Request", details), HttpStatus.BAD_REQUEST);
    }

}
