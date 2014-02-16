package com.funkyquest.app.dto.util;

public class MessageBody<T> {

    private T body;
    private Class<T> messageBodyClass;
    private EventType eventType;

    public MessageBody() {
    }

    public MessageBody(T body, Class<T> messageBodyClass, EventType eventType) {
        this.body = body;
        this.messageBodyClass = messageBodyClass;
        this.eventType = eventType;
    }

    public Class<T> getMessageBodyClass() {
        return messageBodyClass;
    }

    public T getBody() {
        return body;
    }

    public EventType getEventType() {
        return eventType;
    }

}