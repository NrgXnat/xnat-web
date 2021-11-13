/*
 * web: org.nrg.xapi.rest.XapiRestControllerAdvice
 * XNAT http://www.xnat.org
 * Copyright (c) 2005-2017, Washington University School of Medicine and Howard Hughes Medical Institute
 * All Rights Reserved
 *
 * Released under the Simplified BSD.
 */

package org.nrg.xapi.rest.dicom;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.nrg.anonscriptprovider.entities.AnonScript;
import org.nrg.anonscriptprovider.exceptions.AnonScriptProviderServiceException;
import org.nrg.anonscriptprovider.exceptions.DuplicateScriptException;
import org.nrg.anonscriptprovider.exceptions.NoSuchScriptException;
import org.nrg.xapi.XapiUtils;
import org.nrg.xdat.XDAT;
import org.nrg.xdat.preferences.SiteConfigPreferences;
import org.nrg.xft.security.UserI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.annotation.Nonnull;
import javax.servlet.http.HttpServletRequest;

import static org.apache.commons.lang3.StringUtils.defaultIfBlank;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.springframework.http.HttpStatus.*;

/**
 * Where in we define the ExceptionHandlers for the exceptions thrown by the Anonymize REST API.
 *
 * The default exception handlers are in XapiRestControllerAdvice. Note the Order precedence had to be defined
 * despite targeting the advice to AnonymizeApi base package.
 */
@ControllerAdvice(basePackageClasses = AnonymizeApi.class)
@Order(Ordered.HIGHEST_PRECEDENCE)
@Slf4j
public class AnonymizeRestControllerAdvice {
    @Autowired
    public AnonymizeRestControllerAdvice(final SiteConfigPreferences preferences) {
        _realm = XapiUtils.getWwwAuthenticateBasicHeaders(preferences.getSiteId());
    }

    @ExceptionHandler( DuplicateScriptException.class)
    public ResponseEntity<?> handleDuplicateScriptException(final HttpServletRequest request, final DuplicateScriptException exception) {
        String msg;
        if( exception.getScripts().isEmpty()) {
            msg = "Duplicate Script Exception.";
        }
        else {
            AnonScript script = exception.getScripts().get(0);
            msg = String.format("Script with label '%s' and version '%s' already exists.", script.getLabel(), script.getVersion());
        }
        return getExceptionResponseEntity(request, CONFLICT, exception, msg);
    }

    @ExceptionHandler( NoSuchScriptException.class)
    public ResponseEntity<?> handleNoSuchScriptException(final HttpServletRequest request, final NoSuchScriptException exception) {
        String msg = null;
        if( ! exception.getScripts().isEmpty()) {
            AnonScript script = exception.getScripts().get(0);
            msg = String.format("No such script with label '%s' and version '%s'.", script.getLabel(), script.getVersion());
        }
        return getExceptionResponseEntity(request, NO_CONTENT, exception, msg);
    }

    @ExceptionHandler( AnonScriptProviderServiceException.class)
    public ResponseEntity<?> handleAnonScriptProviderScriptException(final HttpServletRequest request, final AnonScriptProviderServiceException exception) {
        String msg = null;
        if( ! exception.getScripts().isEmpty()) {
            AnonScript script = exception.getScripts().get(0);
            msg = String.format("Script with label '%s' and version '%s'.", script.getLabel(), script.getVersion());
        }
        return getExceptionResponseEntity(request, BAD_REQUEST, exception, msg);
    }

    @NotNull
    private ResponseEntity<?> getExceptionResponseEntity(@Nonnull final HttpServletRequest request, final HttpStatus status, final Exception exception, final String message) {
        final String resolvedMessage;
        if (message == null && exception == null) {
            resolvedMessage = null;
        } else if (message == null) {
            resolvedMessage = exception.getMessage();
        } else if (exception == null) {
            resolvedMessage = message;
        } else {
            resolvedMessage = message + ": " + exception.getMessage();
        }
        // If there's an explicit status, use that. Otherwise try to get it off of the exception and default to 500 if not available.
        final HttpStatus resolvedStatus = status != null ? status : exception != null ? getExceptionResponseStatus(exception) : DEFAULT_ERROR_STATUS;

        // Log 500s as errors, other statuses can just be logged as info messages.
        final UserI  userDetails = XDAT.getUserDetails();
        final String username    = userDetails != null ? userDetails.getUsername() : "unauthenticated user";
        final String requestUri  = request.getServletPath() + request.getPathInfo();

        if (resolvedStatus == INTERNAL_SERVER_ERROR) {
            log.error("HTTP status 500: Request by user {} to URL {} caused an internal server error", username, requestUri, exception);
        } else if (exception != null) {
            log.info("HTTP status {}: Request by user {} to URL {} caused an exception of type {}{}", resolvedStatus, username, requestUri, exception.getClass().getName(), defaultIfBlank(resolvedMessage, ""));
        }

        final ResponseEntity.BodyBuilder builder = ResponseEntity.status(resolvedStatus);
        if (status == UNAUTHORIZED) {
            builder.headers(_realm);
        }
        return isBlank(resolvedMessage) ? builder.contentLength(0).build() : builder.contentType(MediaType.TEXT_PLAIN).contentLength(resolvedMessage.length()).body(resolvedMessage);
    }

    private HttpStatus getExceptionResponseStatus(final Exception exception) {
        return AnnotationUtils.findAnnotation(exception.getClass(), ResponseStatus.class).value();
    }

    private static final HttpStatus DEFAULT_ERROR_STATUS = INTERNAL_SERVER_ERROR;

    private final HttpHeaders _realm;
}
