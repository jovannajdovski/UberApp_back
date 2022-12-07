package com.uberTim12.ihor.service.communication.impl;

import com.uberTim12.ihor.model.communication.Message;
import com.uberTim12.ihor.dto.communication.SendingMessageDTO;
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
    private IMessageRepository messageRepository;
    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private IRideRepository rideRepository;

    @Override
    public List<Message> getMessages(Integer id) {
        return messageRepository.findAllBySenderIdOrReceiverId(id,id);
    }

    @Override
    public Message sendMessage(Integer senderId, SendingMessageDTO sendingMessageDTO) {
        return messageRepository.saveAndFlush(new Message(userRepository.findById(senderId).get(),
                userRepository.findById(sendingMessageDTO.getReceiverId()).get(),
                sendingMessageDTO.getContent(),
                LocalDateTime.now(),
                sendingMessageDTO.getType(),
                rideRepository.findById(sendingMessageDTO.getRideId()).get()));
    }
}
