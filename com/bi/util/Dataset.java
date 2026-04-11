package com.bi.util;

import java.time.LocalDateTime;

public class Dataset {
    private String id;
    private Object data;
    private LocalDateTime timestamp;
    private String description;

    public Dataset() {
    }

    public Dataset(String id, Object data, LocalDateTime timestamp, String description) {
        this.id = id;
        this.data = data;
        this.timestamp = timestamp;
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
