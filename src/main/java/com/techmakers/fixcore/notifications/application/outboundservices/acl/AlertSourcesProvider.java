package com.techmakers.fixcore.notifications.application.outboundservices.acl;

import com.techmakers.fixcore.notifications.domain.model.valueobjects.AlertSources;
import com.techmakers.fixcore.notifications.domain.model.valueobjects.Recipient;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface AlertSourcesProvider {

    Optional<Recipient> recipientByEmail(String email);

    List<Long> allPlantIds();

    AlertSources sourcesOf(Collection<Long> plantIds);
}
