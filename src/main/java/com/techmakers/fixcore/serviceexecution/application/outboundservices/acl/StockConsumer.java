package com.techmakers.fixcore.serviceexecution.application.outboundservices.acl;

public interface StockConsumer {

    void consume(Long partId, int quantity, Long workOrderId, String reason, String performedBy);
}
