package com.techmakers.fixcore.serviceexecution.infrastructure.acl;

import com.techmakers.fixcore.iam.interfaces.acl.IamContextFacade;
import com.techmakers.fixcore.serviceexecution.application.outboundservices.acl.UserDirectory;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component("executionUserDirectory")
public class UserDirectoryAdapter implements UserDirectory {

    private final IamContextFacade iamContextFacade;

    public UserDirectoryAdapter(IamContextFacade iamContextFacade) {
        this.iamContextFacade = iamContextFacade;
    }

    @Override
    public Optional<Long> userIdByEmail(String email) {
        return iamContextFacade.fetchUserIdByEmail(email);
    }

    @Override
    public boolean isTechnician(Long userId) {
        return iamContextFacade.isTechnician(userId);
    }
}
