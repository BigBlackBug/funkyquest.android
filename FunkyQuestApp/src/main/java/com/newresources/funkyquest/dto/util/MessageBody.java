package com.newresources.funkyquest.dto.util;

public class MessageBody<T> {

    private T body;
    private String messageBodyClass;
    private EventType eventType;

    public MessageBody() {
    }

    public MessageBody(T body, String messageBodyClass, EventType eventType) {
        this.body = body;
        this.messageBodyClass = messageBodyClass;
        this.eventType = eventType;
    }

    public String getMessageBodyClass() {
        return messageBodyClass;
    }

    public T getBody() {
        return body;
    }

    public EventType getEventType() {
        return eventType;
    }

}