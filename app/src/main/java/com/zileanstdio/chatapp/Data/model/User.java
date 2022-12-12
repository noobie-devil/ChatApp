package com.zileanstdio.chatapp.Data.model;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class User implements Serializable {
    private String fullName;
    private String userName;
    private boolean onlineStatus;
    private String phoneNumber;
    private String gender;
    private String birthDate;
    private String avatarImageUrl;
    private Date createdAt;
    private Date lastOnline;
    private List<String> conversationList;

    public User(String fullName, String userName, boolean onlineStatus, String phoneNumber, String gender, String birthDate, String avatarImageUrl, Date createdAt, Date lastOnline, List<String> conversationList) {
        this.fullName = fullName;
        this.userName = userName;
        this.onlineStatus = onlineStatus;
        this.phoneNumber = phoneNumber;
        this.gender = gender;
        this.birthDate = birthDate;
        this.avatarImageUrl = avatarImageUrl;
        this.createdAt = createdAt;
        this.lastOnline = lastOnline;
        this.conversationList = conversationList;
    }

    public List<String> getConversationList() {
        return conversationList;
    }

    public void setConversationList(List<String> conversationList) {
        this.conversationList = conversationList;
    }


    public User() {}

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public boolean isOnlineStatus() {
        return onlineStatus;
    }

    public void setOnlineStatus(boolean onlineStatus) {
        this.onlineStatus = onlineStatus;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public String getAvatarImageUrl() {
        return avatarImageUrl;
    }

    public void setAvatarImageUrl(String avatarImageUrl) {
        this.avatarImageUrl = avatarImageUrl;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getLastOnline() {
        return lastOnline;
    }

    public void setLastOnline(Date lastOnline) {
        this.lastOnline = lastOnline;
    }

    @NonNull
    @Override
    public String toString() {
        return "User{" +
                "fullName='" + fullName + '\'' +
                ", userName='" + userName + '\'' +
                ", onlineStatus=" + onlineStatus +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", gender='" + gender + '\'' +
                ", birthDate='" + birthDate + '\'' +
                ", avatarImageUrl='" + avatarImageUrl + '\'' +
                ", createdAt=" + createdAt +
                ", lastOnline=" + lastOnline +
                ", conversationList=" + conversationList +
                '}';
    }
}
