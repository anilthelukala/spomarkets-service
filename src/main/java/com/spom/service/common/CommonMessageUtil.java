package com.spom.service.common;

public class CommonMessageUtil {
    public static Message getExceptionMessage(String message) {
        Message msg = new Message();
        msg.setMessage(message);
        msg.setMessageType(MessageType.Error.toString());
        return msg;
    }

    public static Message getSuccessMessage(String message) {
        Message msg = new Message();
        msg.setMessage(message);
        msg.setMessageType(MessageType.Info.toString());
        return msg;
    }
}
