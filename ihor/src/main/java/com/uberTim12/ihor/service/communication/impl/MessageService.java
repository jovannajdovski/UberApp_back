package com.uberTim12.ihor.service.communication.impl;

import com.uberTim12.ihor.model.communication.Message;
import com.uberTim12.ihor.model.communication.SendingMessageDTO;
import com.uberTim12.ihor.repository.communication.IMessageRepository;
import com.uberTim12.ihor.repository.ride.IRideRepository;
import com.uberTim12.ihor.repository.users.IUserRepository;
import com.uberTim12.ihor.service.communication.interfaces.IMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MessageService implements IMessageService {
    @Autowired
    private IMessageRepository IMessageRepository;
    @Autowired
    private IUserRepository IUserRepository;

    @Autowired
    private IRideRepository IRideRepository;

    @Override
    public List<Message> getMessages(Integer id) {
        return IMessageRepository.findAllBySenderIdOrReceiverId(id,id);
    }

    @Override
    public Message sendMessage(Integer senderId, SendingMessageDTO sendingMessageDTO) {
        return IMessageRepository.saveAndFlush(new Message(IUserRepository.findById(senderId).get(),
                IUserRepository.findById(sendingMessageDTO.getReceiverId()).get(),
                sendingMessageDTO.getContent(),
                LocalDateTime.now(),
                sendingMessageDTO.getType(),
                IRideRepository.findById(sendingMessageDTO.getRideId()).get()));
    }
}
