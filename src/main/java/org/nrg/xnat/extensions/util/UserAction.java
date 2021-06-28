package org.nrg.xnat.extensions.util;

import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;

public enum UserAction {

    Reset,
    ResetEmailRequests;

    public static UserAction action(String action) {
        if (StringUtils.isBlank(action)) {
            return null;
        }
        if (_actions.isEmpty()) {
            synchronized (UserAction.class) {
                for (UserAction userAction : values()) {
                    _actions.put(userAction.toString(), userAction);
                }
            }
        }
        return _actions.get(action);
    }

    @Override
    public String toString() {
        return this.name();
    }

    private static Map<String, UserAction> _actions = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

}
