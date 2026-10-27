package com.techmakers.fixcore.inventory.infrastructure.acl;

import com.techmakers.fixcore.iam.interfaces.acl.IamContextFacade;
import com.techmakers.fixcore.inventory.application.outboundservices.acl.UserDirectory;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UserDirectoryAdapter implements UserDirectory {

    private final IamContextFacade iamContextFacade;

    public UserDirectoryAdapter(IamContextFacade iamContextFacade) {
        this.iamContextFacade = iamContextFacade;
    }

    @Override
    public Optional<Long> userIdByEmail(String email) {
        return iamContextFacade.fetchUserIdByEmail(email);
    }
}
