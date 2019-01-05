package net.fp.backBook.configuration;

import net.fp.backBook.exceptions.AddException;
import net.fp.backBook.exceptions.DeleteException;
import net.fp.backBook.exceptions.GetException;
import net.fp.backBook.exceptions.ModifyException;
import net.fp.backBook.model.ErrorDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class RestResponseExceptionHandler extends ResponseEntityExceptionHandler {

    private HttpHeaders httpHeaders;

    @Autowired
    public RestResponseExceptionHandler(final HttpHeaders httpHeaders) {
        this.httpHeaders = httpHeaders;
    }

    @ExceptionHandler( value = {GetException.class})
    protected ResponseEntity<Object> handleGetConflict(RuntimeException e, WebRequest request) {
        Object responseString = new ErrorDto("Error during retrieving of data (GET METHOD). " + e.getMessage());
        return handleExceptionInternal(e, responseString, httpHeaders, HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler( value = {AddException.class})
    protected ResponseEntity<Object> handleAddConflict(RuntimeException e, WebRequest request) {
        Object responseString = new ErrorDto("Error during data persistence (POST METHOD). " + e.getMessage());
        return handleExceptionInternal(e, responseString, httpHeaders, HttpStatus.NOT_ACCEPTABLE, request);
    }

    @ExceptionHandler( value = {DeleteException.class})
    protected ResponseEntity<Object> handleDeleteConflict(RuntimeException e, WebRequest request) {
        Object responseString = new ErrorDto("Error during deletion process (DELETE METHOD). " + e.getMessage());
        return handleExceptionInternal(e, responseString, httpHeaders, HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler( value = {ModifyException.class})
    protected ResponseEntity<Object> handleModifyConflict(RuntimeException e, WebRequest request) {
        Object responseString = new ErrorDto("Error during data persistence (PUT METHOD). " + e.getMessage());
        return handleExceptionInternal(e, responseString, httpHeaders, HttpStatus.CONFLICT, request);
    }
}
