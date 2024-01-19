package com.spom.service.common;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class CommonResponse {
    private Map<String, Object> data = new HashMap<String, Object>(0);
    private Collection<Message> successMsg = new ArrayList<>(0);
    private Collection<Message> errorMsg = new ArrayList<>(0);

    /**
     * @return the data
     */
    public Map<String, Object> getData() {
        return data;
    }

    public void addData(String key, Object t) {
        if (null != data) {
            data.put(key, t);
        }
    }

    /**
     * @param data the data to set
     */
    public void setData(Map<String, Object> data) {
        this.data = data;
    }

    /**
     * @return the messageList
     */
    public Collection<Message> getSuccessMsgList() {
        return successMsg;
    }

    /**
     * @param msg
     */
    public void addSuccessMsg(Message msg) {
        if (null != successMsg) {
            successMsg.add(msg);
        }
    }

    /**
     * @param messageList the messageList to set
     */
    public void setSuccessMsgList(Collection<Message> messageList) {
        this.successMsg = messageList;
    }

    /**
     * @return the errorMsg
     */
    public Collection<Message> getErrorMsg() {
        return errorMsg;
    }

    /**
     * @param errorMsg the errorMsg to set
     */
    public void setErrorMsg(Collection<Message> errorMsg) {
        this.errorMsg = errorMsg;
    }

    /**
     * Add Error Message
     *
     * @param msg
     */
    public void addErrorMsg(Message msg) {
        if (null != successMsg) {
            errorMsg.add(msg);
        }
    }

    /**
     * Add Error Message
     *
     * @param msg
     */
    public void addErrorMsg(String msg) {
        if (null != successMsg) {
            Message message = new Message();
            message.setMessage(msg);
            message.setMessageType("Error");
            errorMsg.add(message);
        }
    }
}
