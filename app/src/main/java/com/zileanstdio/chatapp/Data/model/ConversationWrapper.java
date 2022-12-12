package com.zileanstdio.chatapp.Data.model;

import androidx.annotation.NonNull;

import java.util.Objects;

// TODO: 12/11/2022
public class ConversationWrapper {

    private String documentId;
    private Conversation conversation;

    public ConversationWrapper(String documentId, Conversation conversation) {
        this.documentId = documentId;
        this.conversation = conversation;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public Conversation getConversation() {
        return conversation;
    }

    public void setConversation(Conversation conversation) {
        this.conversation = conversation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConversationWrapper that = (ConversationWrapper) o;
        return Objects.equals(documentId, that.documentId) && Objects.equals(conversation, that.conversation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(documentId, conversation);
    }

    @NonNull
    @Override
    public String toString() {
        return "ConversationWrapper{" +
                "documentId='" + documentId + '\'' +
                ", conversation=" + conversation +
                '}';
    }
}