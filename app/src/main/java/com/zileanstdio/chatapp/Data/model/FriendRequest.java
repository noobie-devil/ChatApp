package com.zileanstdio.chatapp.Data.model;

import java.util.Date;

public class FriendRequest {
    private String documentId;

    private RequestBody requestBody;

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public RequestBody getRequestBody() {
        return requestBody;
    }

    public void setRequestBody(RequestBody requestBody) {
        this.requestBody = requestBody;
    }

    @Override
    public String toString() {
        return "FriendRequest{" +
                "documentId='" + documentId + '\'' +
                ", requestBody=" + requestBody +
                '}';
    }

    public static class RequestBody {
        private String sender;
        private Date sendAt;

        public RequestBody(String sender, Date sendAt) {
            this.sender = sender;
            this.sendAt = sendAt;
        }

        public String getSender() {
            return sender;
        }

        public void setSender(String sender) {
            this.sender = sender;
        }

        public Date getSendAt() {
            return sendAt;
        }

        public void setSendAt(Date sendAt) {
            this.sendAt = sendAt;
        }

        @Override
        public String toString() {
            return "RequestBody{" +
                    "sender='" + sender + '\'' +
                    ", sendAt=" + sendAt +
                    '}';
        }
    }

}
