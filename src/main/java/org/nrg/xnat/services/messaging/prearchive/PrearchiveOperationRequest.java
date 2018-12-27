/*
 * web: org.nrg.xnat.services.messaging.prearchive.PrearchiveOperationRequest
 * XNAT http://www.xnat.org
 * Copyright (c) 2005-2017, Washington University School of Medicine and Howard Hughes Medical Institute
 * All Rights Reserved
 *
 * Released under the Simplified BSD.
 */

package org.nrg.xnat.services.messaging.prearchive;

import org.nrg.xft.security.UserI;
import org.nrg.xnat.helpers.prearchive.SessionData;
import org.nrg.xnat.helpers.prearchive.handlers.PrearchiveOperation;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class PrearchiveOperationRequest implements Serializable {
    /**
     * Used to store the project ID of the destination project for move operations.
     */
    public static final String PARAM_DESTINATION = "destination";

    /**
     * Creates a prearchive operation request.
     *
     * @param user        The user requesting the prearchive operation.
     * @param sessionData Data for the session on which the operation should be performed.
     * @param sessionDir  The location of the session files.
     * @param operation   The operation to be performed.
     *
     * @deprecated This method is replaced by {@link #PrearchiveOperationRequest(UserI, SessionData, File, PrearchiveOperation)}.
     */
    @Deprecated
    public PrearchiveOperationRequest(final UserI user, final SessionData sessionData, final File sessionDir, final String operation) {
        this(user, sessionData, sessionDir, PrearchiveOperation.valueOf(operation), null);
    }

    /**
     * Creates a prearchive operation request.
     *
     * @param user        The user requesting the prearchive operation.
     * @param sessionData Data for the session on which the operation should be performed.
     * @param sessionDir  The location of the session files.
     * @param operation   The operation to be performed.
     * @param parameters  Parameters to be passed to the prearchive operation request handler.
     *
     * @deprecated This method is replaced by {@link #PrearchiveOperationRequest(UserI, SessionData, File, PrearchiveOperation)}.
     */
    @Deprecated
    public PrearchiveOperationRequest(final UserI user, final SessionData sessionData, final File sessionDir, final String operation, final Map<String, String> parameters) {
        this(user, sessionData, sessionDir, PrearchiveOperation.valueOf(operation), parameters);
    }

    /**
     * Creates a prearchive operation request.
     *
     * @param user        The user requesting the prearchive operation.
     * @param sessionData Data for the session on which the operation should be performed.
     * @param sessionDir  The location of the session files.
     * @param operation   The operation to be performed.
     */
    public PrearchiveOperationRequest(final UserI user, final SessionData sessionData, final File sessionDir, final PrearchiveOperation operation) {
        this(user, sessionData, sessionDir, operation, null);
    }

    /**
     * Creates a prearchive operation request.
     *
     * @param user        The user requesting the prearchive operation.
     * @param sessionData Data for the session on which the operation should be performed.
     * @param sessionDir  The location of the session files.
     * @param operation   The operation to be performed.
     * @param parameters  Parameters to be passed to the prearchive operation request handler.
     */
    public PrearchiveOperationRequest(final UserI user, final SessionData sessionData, final File sessionDir, final PrearchiveOperation operation, final Map<String, String> parameters) {
        _user = user;
        _sessionData = sessionData;
        _sessionDir = sessionDir;
        _operation = operation;
        _parameters = parameters == null ? new HashMap<String, String>() : new HashMap<>(parameters);
    }

    public UserI getUser() {
        return _user;
    }

    public SessionData getSessionData() {
        return _sessionData;
    }

    public File getSessionDir() {
        return _sessionDir;
    }

    public PrearchiveOperation getOperation() {
        return _operation;
    }

    public Map<String, String> getParameters() {
        return new HashMap<>(_parameters);
    }

    private final UserI               _user;
    private final SessionData         _sessionData;
    private final File                _sessionDir;
    private final PrearchiveOperation _operation;
    private final Map<String, String> _parameters;
}
