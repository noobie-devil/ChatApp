package com.zileanstdio.chatapp.Data.model;

import androidx.annotation.NonNull;

import java.util.Date;
import java.util.List;

// TODO: 12/11/2022  
public class Conversation {
    private List<String> userJoined;
    private Date createdAt;
    private String lastMessage;
    private String typeMessage;
    private String lastSender;
    private Date lastUpdated;

    public Conversation() {
    }

    public Conversation(List<String> userJoined, Date createdAt, String lastMessage, String typeMessage, String lastSender, Date lastUpdated) {
        this.userJoined = userJoined;
        this.createdAt = createdAt;
        this.lastMessage = lastMessage;
        this.typeMessage = typeMessage;
        this.lastSender = lastSender;
        this.lastUpdated = lastUpdated;
    }

    public List<String> getUserJoined() {
        return userJoined;
    }

    public void setUserJoined(List<String> userJoined) {
        this.userJoined = userJoined;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getTypeMessage() {
        return typeMessage;
    }

    public void setTypeMessage(String typeMessage) {
        this.typeMessage = typeMessage;
    }

    public String getLastSender() {
        return lastSender;
    }

    public void setLastSender(String lastSender) {
        this.lastSender = lastSender;
    }

    public Date getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    @NonNull
    @Override
    public String toString() {
        return "Conversation{" +
                "userJoined=" + userJoined +
                ", createdAt=" + createdAt +
                ", lastMessage='" + lastMessage + '\'' +
                ", typeMessage='" + typeMessage + '\'' +
                ", lastSender='" + lastSender + '\'' +
                ", lastUpdated=" + lastUpdated +
                '}';
    }
}
