package org.nrg.xnat.helpers.prearchive.handlers;

import lombok.Getter;
import lombok.experimental.Accessors;
import org.nrg.xft.security.UserI;
import org.nrg.xnat.helpers.prearchive.SessionData;

import java.io.File;
import java.util.Map;

@Getter
@Accessors(prefix = "_")
public class PrearchiveOperationException extends RuntimeException {
    public PrearchiveOperationException(final UserI user, final PrearchiveOperation operation, final SessionData sessionData, final File sessionDir) {
        this(user, operation, sessionData, sessionDir, null);
    }

    public PrearchiveOperationException(final UserI user, final PrearchiveOperation operation, final SessionData sessionData, final File sessionDir, final Map<String, String> parameters) {
        _user = user;
        _operation = operation;
        _sessionData = sessionData;
        _sessionDir = sessionDir;
        _parameters = parameters;
    }

    private final UserI               _user;
    private final PrearchiveOperation _operation;
    private final SessionData         _sessionData;
    private final File                _sessionDir;
    private final Map<String, String> _parameters;
}
