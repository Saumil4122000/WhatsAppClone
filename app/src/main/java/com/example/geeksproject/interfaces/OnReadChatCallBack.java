package com.example.geeksproject.interfaces;

import com.example.geeksproject.model.chat.Chats;

import java.util.List;

public interface OnReadChatCallBack {
    void onReadSuccess(List<Chats> list);
    void onFailure();
}
