package com.amigos.sindhusha.vo;

/**
 * Created by sindhusha on 15/5/17.
 */
public class ChatMessage {
    public boolean left;
    public String message;
    public String toId;
    public String fromId;
    public String time;

    public ChatMessage(boolean left, String message) {
        super();
        this.left = left;
        this.message = message;
    }
    public ChatMessage(boolean left, String message, String time) {
        super();
        this.left = left;
        this.message = message;
        this.time = time;
    }

    public ChatMessage(String toId,String fromId, String message) {
        super();
        this.toId = toId;
        this.fromId=fromId;
        this.message = message;
    }
}