package com.techmakers.fixcore.inventory.application.outboundservices.acl;

import java.util.Optional;

public interface UserDirectory {

    Optional<Long> userIdByEmail(String email);
}
