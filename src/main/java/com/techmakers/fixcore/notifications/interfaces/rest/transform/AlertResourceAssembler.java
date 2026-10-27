package com.techmakers.fixcore.notifications.interfaces.rest.transform;

import com.techmakers.fixcore.notifications.domain.model.valueobjects.Alert;
import com.techmakers.fixcore.notifications.interfaces.rest.resources.AlertResource;
import com.techmakers.fixcore.shared.interfaces.rest.transform.EnumValueAssembler;

public final class AlertResourceAssembler {

    private AlertResourceAssembler() {
    }

    public static AlertResource toResource(Alert alert) {
        return new AlertResource(alert.id(), EnumValueAssembler.toValue(alert.kind()),
                EnumValueAssembler.toValue(alert.severity()), alert.params(), alert.link(), alert.at());
    }
}
