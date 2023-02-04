package com.uberTim12.ihor.service.communication.interfaces;

import com.uberTim12.ihor.dto.communication.MessageDTO;
import com.uberTim12.ihor.model.communication.Message;
import com.uberTim12.ihor.dto.communication.SendingMessageDTO;
import com.uberTim12.ihor.model.communication.MessageType;
import com.uberTim12.ihor.model.users.User;
import com.uberTim12.ihor.service.base.interfaces.IJPAService;
import jakarta.persistence.EntityNotFoundException;

import java.util.List;

public interface IMessageService extends IJPAService<Message> {
    List<MessageDTO> getMessages(Integer id);

    Message sendMessage(Integer senderId, Integer receiverId, Integer rideId, String content,
                        MessageType type) throws EntityNotFoundException;
}
