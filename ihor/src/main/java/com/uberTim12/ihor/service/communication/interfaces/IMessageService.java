package com.uberTim12.ihor.service.communication.interfaces;

import com.uberTim12.ihor.model.communication.Message;

import java.util.List;

public interface IMessageService {
    List<Message> getMessages(Integer id);
}
