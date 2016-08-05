package com.example.admin.webrtccall.model;

/**
 * Created by Admin on 7/25/2016.
 */
public class RtcItem {

    private String username;
    private String status;
    private String userId;
    private String opponentView;

    public RtcItem(String status, String userId, String opponent) {
        this.status = status;
        this.userId = userId;
        this.opponentView = opponent;
    }

    public String getOpponentView() {
        return opponentView;
    }

    public void setOpponentView(String opponentView) {
        this.opponentView = opponentView;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


}
