/*
 * web: org.nrg.xnat.helpers.prearchive.handlers.AbstractPrearchiveOperationHandler
 * XNAT http://www.xnat.org
 * Copyright (c) 2005-2017, Washington University School of Medicine and Howard Hughes Medical Institute
 * All Rights Reserved
 *
 * Released under the Simplified BSD.
 */

package org.nrg.xnat.helpers.prearchive.handlers;

import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.security.helpers.Users;
import org.nrg.xft.security.UserI;
import org.nrg.xnat.helpers.prearchive.PrearcDatabase;
import org.nrg.xnat.helpers.prearchive.PrearcUtils;
import org.nrg.xnat.helpers.prearchive.SessionData;
import org.nrg.xnat.services.messaging.prearchive.PrearchiveOperationRequest;
import org.reflections.Reflections;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static lombok.AccessLevel.PROTECTED;

@Getter(PROTECTED)
@Accessors(prefix = "_")
@Slf4j
public abstract class AbstractPrearchiveOperationHandler implements PrearchiveOperationHandler {
    public AbstractPrearchiveOperationHandler(final PrearchiveOperationRequest request) throws Exception {
        _user = Users.getUser(request.getUser().getUsername());
        _sessionData = request.getSessionData();
        _sessionDir = request.getSessionDir();
        _parameters.putAll(request.getParameters());
    }

    public static AbstractPrearchiveOperationHandler getHandler(final PrearchiveOperationRequest request) {
        checkInit();
        final PrearchiveOperation                                       operation   = request.getOperation();
        final Constructor<? extends AbstractPrearchiveOperationHandler> constructor = _handlers.get(operation);
        if (constructor == null) {
            throw new RuntimeException("No handler found for operation " + operation + ". Please check your classpath.");
        }
        try {
            if (log.isDebugEnabled()) {
                log.debug("Found handler for operation {}, creating with request type {}", operation, request.getClass().getName());
            }
            return constructor.newInstance(request);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException("An error occurred trying to instantiate a prearchive operation handler.", e);
        }
    }

    public void execute() throws Exception {
        if (!shouldProceed()) {
            log.debug("Operation {} indicates that conditions aren't appropriate to proceed.", getOperation());
            return;
        }
        try {
            handle();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    abstract protected boolean shouldProceed() throws Exception;

    abstract protected void handle() throws Exception;

    protected boolean isReceiving() {
        return getSessionData().getStatus() != null && getSessionData().getStatus().equals(PrearcUtils.PrearcStatus.RECEIVING);
    }

    protected PrearchiveOperation getOperation() {
        return getClass().getAnnotation(Handles.class).value();
    }

    protected boolean validateSessionDir() {
        final File parentFile = getSessionDir().getParentFile();
        if (!parentFile.exists()) {
            try {
                log.info("The parent of the indicated session '{}' for a {} request could not be found at the indicated location: {}. Deleting the corresponding cache row.", getSessionData().getName(), getOperation(), parentFile.getAbsolutePath());
                PrearcDatabase.unsafeSetStatus(getSessionData().getFolderName(), getSessionData().getTimestamp(), getSessionData().getProject(), PrearcUtils.PrearcStatus._DELETING);
                PrearcDatabase.deleteCacheRow(getSessionData().getFolderName(), getSessionData().getTimestamp(), getSessionData().getProject());
            } catch (Exception e) {
                log.error("An error occurred attempting to clear the prearchive entry for the session '{}', which doesn't exist at the indicated location: {}", getSessionData().getName(), parentFile.getAbsolutePath());
            }
            return false;
        }
        return true;
    }

    private static void checkInit() {
        if (_handlers.isEmpty()) {
            synchronized (_handlers) {
                final Reflections                                              reflections = new Reflections(AbstractPrearchiveOperationHandler.class.getPackage().getName());
                final Set<Class<? extends AbstractPrearchiveOperationHandler>> handlers    = reflections.getSubTypesOf(AbstractPrearchiveOperationHandler.class);
                for (final Class<? extends AbstractPrearchiveOperationHandler> handler : handlers) {
                    try {
                        final PrearchiveOperation operation = handler.getAnnotation(Handles.class).value();
                        log.debug("Found handler for {} operation: {}", operation, handler.getName());
                        _handlers.put(operation, handler.getConstructor(PrearchiveOperationRequest.class));
                    } catch (NoSuchMethodException e) {
                        throw new RuntimeException("No proper constructor found for " + handler.getName() + " class. It must have a constructor that accepts a " + AbstractPrearchiveOperationHandler.class.getName() + " object.");
                    }
                }
            }
        }
    }

    private final static Map<PrearchiveOperation, Constructor<? extends AbstractPrearchiveOperationHandler>> _handlers = new HashMap<>();

    private final UserI               _user;
    private final SessionData         _sessionData;
    private final File                _sessionDir;
    private final Map<String, String> _parameters = new HashMap<>();
}
