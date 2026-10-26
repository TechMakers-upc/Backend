package com.techmakers.fixcore.serviceplanning.infrastructure.acl;

import com.techmakers.fixcore.iam.interfaces.acl.IamContextFacade;
import com.techmakers.fixcore.serviceplanning.application.outboundservices.acl.TechnicianDirectory;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class TechnicianDirectoryAdapter implements TechnicianDirectory {

    private final IamContextFacade iamContextFacade;

    public TechnicianDirectoryAdapter(IamContextFacade iamContextFacade) {
        this.iamContextFacade = iamContextFacade;
    }

    @Override
    public boolean isTechnician(Long userId) {
        return iamContextFacade.isTechnician(userId);
    }

    @Override
    public Optional<Long> technicianIdByEmail(String email) {
        return iamContextFacade.fetchUserIdByEmail(email).filter(iamContextFacade::isTechnician);
    }
}
