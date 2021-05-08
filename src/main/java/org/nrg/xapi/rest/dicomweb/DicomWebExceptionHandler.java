package org.nrg.xapi.rest.dicomweb;

import org.nrg.xapi.model.dicomweb.TransCoderException;
import org.nrg.xapi.model.dicomweb.UnsupportedTransferSyntaxException;
import org.nrg.xapi.rest.dicomweb.search.SearchException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class DicomWebExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = { SearchException.class })
    protected ResponseEntity<Object> handleSearchException( SearchException ex, WebRequest request) {
        String responseBody = "";
        HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        switch (ex.getType()) {
            case STUDY_INSTANCE_UID_CONFLICT:
                httpStatus = HttpStatus.CONFLICT;
                responseBody = getResponseBody( "Status 409 - Conflict: Same Study Instance UID(s) in Multiple Projects.",
                        "The query results are not unique. The same study instance UID occurs in multiple studies.");
                break;
            case UNEXPECTED:
            default:
                httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
                responseBody = getResponseBody( "Status 500 - Internal Error.",
                        "Here is a clue: " + ex.getMessage());
                break;
        }
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setContentType(MediaType.TEXT_HTML);
        return handleExceptionInternal(ex, responseBody, responseHeaders, httpStatus, request);
    }

    @ExceptionHandler( UnsupportedTransferSyntaxException.class)
    public ResponseEntity<Object> handleUnsupportedTSUIDException(UnsupportedTransferSyntaxException ex, WebRequest request) {
        HttpStatus httpStatus = HttpStatus.NOT_ACCEPTABLE;
        String responseBody = getResponseBody( "Unsupported TransferSyntax.", ex.getMessage());

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setContentType(MediaType.TEXT_HTML);
        return handleExceptionInternal(ex, responseBody, responseHeaders, httpStatus, request);
    }

    @ExceptionHandler( TransCoderException.class)
    public ResponseEntity<Object> handleTransCoderException(TransCoderException ex, WebRequest request) {
        HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        String responseBody = getResponseBody( "Transcoder Exception.", ex.getMessage());

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setContentType(MediaType.TEXT_HTML);
        return handleExceptionInternal(ex, responseBody, responseHeaders, httpStatus, request);
    }

    // Override parent method but don't annotate.
    // @ExceptionHandler( HttpMessageNotWritableException.class)
    protected ResponseEntity<Object> handleHttpMessageNotWritable(HttpMessageNotWritableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        String responseBody = getResponseBody( "HttpMessageNotWritableException.", ex.getMessage());

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setContentType(MediaType.TEXT_HTML);
//        return this.handleExceptionInternal(ex, responseBody, responseHeaders, httpStatus, request);
        return new ResponseEntity<Object>( responseBody, responseHeaders, httpStatus);
    }

    public String getResponseBody( String title, String body) {
        String responseFormat = "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <title>%s</title>\n" +
                "</head>\n" +
                "<body>\n" +
                "%s\n" +
                "<p><a href=\"https://ibm.com\">IBM</a></p>\n" +
                "</body>\n" +
                "</html>";
        return String.format( responseFormat, title, body);
    }

}
