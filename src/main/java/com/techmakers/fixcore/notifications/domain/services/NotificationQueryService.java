package com.techmakers.fixcore.notifications.domain.services;

import com.techmakers.fixcore.notifications.domain.model.queries.GetAlertsQuery;
import com.techmakers.fixcore.notifications.domain.model.valueobjects.Alert;

import java.util.List;

public interface NotificationQueryService {

    List<Alert> handle(GetAlertsQuery query);
}
