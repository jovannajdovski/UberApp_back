package com.uberTim12.ihor.service.communication.impl;

import com.uberTim12.ihor.dto.communication.MessageDTO;
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
import java.util.stream.Collectors;

@Service
public class MessageService implements IMessageService {
    @Autowired
    private IMessageRepository messageRepository;
    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private IRideRepository rideRepository;

    @Override
    public List<MessageDTO> getMessages(Integer id) {
        List<Message> messages= messageRepository.findAllBySenderIdOrReceiverId(id,id);
        return messages.stream().map(MessageDTO::new).collect(Collectors.toList());
    }

    @Override
    public MessageDTO sendMessage(Integer senderId, SendingMessageDTO sendingMessageDTO) {
        Message message=messageRepository.saveAndFlush(new Message(userRepository.findById(senderId).get(),
                userRepository.findById(sendingMessageDTO.getReceiverId()).get(),
                sendingMessageDTO.getMessage(),
                LocalDateTime.now(),
                sendingMessageDTO.getType(),
                rideRepository.findById(sendingMessageDTO.getRideId()).get()));
        return new MessageDTO(message);
    }
}
