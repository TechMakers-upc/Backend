package com.techmakers.fixcore.serviceexecution.application.outboundservices.acl;

import java.util.Optional;

public interface UserDirectory {

    Optional<Long> userIdByEmail(String email);

    boolean isTechnician(Long userId);
}
