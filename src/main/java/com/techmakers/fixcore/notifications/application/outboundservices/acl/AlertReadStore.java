package com.techmakers.fixcore.notifications.application.outboundservices.acl;

import java.util.Set;

public interface AlertReadStore {

    Set<String> readIds(String email);

    void markRead(String email, String alertId);
}
