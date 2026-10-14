package com.atait.assignment.api.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ValidationErrorItem {

    @JsonProperty("field")
    private String field;

    @JsonProperty("message")
    private String message;

    public ValidationErrorItem() {
    }

    public ValidationErrorItem(String field, String message) {
        this.field = field;
        this.message = message;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "ValidationErrorItem{" +
                "field='" + field + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
