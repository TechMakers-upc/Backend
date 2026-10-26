package com.techmakers.fixcore.serviceplanning.application.outboundservices.acl;

import java.util.Optional;

public interface TechnicianDirectory {

    boolean isTechnician(Long userId);

    Optional<Long> technicianIdByEmail(String email);
}
