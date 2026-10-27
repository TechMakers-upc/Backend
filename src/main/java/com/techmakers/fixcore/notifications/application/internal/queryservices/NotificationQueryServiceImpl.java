package com.techmakers.fixcore.notifications.application.internal.queryservices;

import com.techmakers.fixcore.notifications.application.outboundservices.acl.AlertSourcesProvider;
import com.techmakers.fixcore.notifications.domain.model.queries.GetAlertsQuery;
import com.techmakers.fixcore.notifications.domain.model.valueobjects.Alert;
import com.techmakers.fixcore.notifications.domain.model.valueobjects.Recipient;
import com.techmakers.fixcore.notifications.domain.model.valueobjects.RecipientRole;
import com.techmakers.fixcore.notifications.domain.services.AlertComposer;
import com.techmakers.fixcore.notifications.domain.services.NotificationQueryService;
import com.techmakers.fixcore.shared.domain.exceptions.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class NotificationQueryServiceImpl implements NotificationQueryService {

    private final AlertSourcesProvider sourcesProvider;

    public NotificationQueryServiceImpl(AlertSourcesProvider sourcesProvider) {
        this.sourcesProvider = sourcesProvider;
    }

    @Override
    public List<Alert> handle(GetAlertsQuery query) {
        var recipient = sourcesProvider.recipientByEmail(query.recipientEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User", query.recipientEmail()));
        var sources = sourcesProvider.sourcesOf(plantsOf(recipient));
        return AlertComposer.compose(recipient, sources, LocalDate.now(), ZoneId.systemDefault());
    }

    private List<Long> plantsOf(Recipient recipient) {
        if (recipient.role() == RecipientRole.OPERATIONS_MANAGER) {
            return sourcesProvider.allPlantIds();
        }
        return recipient.plantId() == null ? List.of() : List.of(recipient.plantId());
    }
}
