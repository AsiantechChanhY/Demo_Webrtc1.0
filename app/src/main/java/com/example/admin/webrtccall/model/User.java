package com.example.admin.webrtccall.model;

/**
 * Created by Admin on 7/25/2016.
 */
public class User {
    private String userId;
    private String username;

    public User(String username, String userId) {
        this.username = username;
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof HistoryItem)) return false;
        HistoryItem cu = (HistoryItem)o;

        return this.userId.equals(((HistoryItem) o).getUserId());

    }

    @Override
    public int hashCode() {
      return this.getUserId().hashCode();
    }
}
