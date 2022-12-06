package com.uberTim12.ihor.service.communication.impl;

import com.uberTim12.ihor.model.communication.Message;
import com.uberTim12.ihor.repository.communication.IMessageRepository;
import com.uberTim12.ihor.service.communication.interfaces.IMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageService implements IMessageService {
    @Autowired
    private IMessageRepository IMessageRepository;

    @Override
    public List<Message> getMessages(Integer id) {
        return IMessageRepository.findAllBySenderIdOrReceiverId(id,id);
    }
}
