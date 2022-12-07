package com.uberTim12.ihor.service.communication.interfaces;

import com.uberTim12.ihor.dto.communication.MessageDTO;
import com.uberTim12.ihor.model.communication.Message;
import com.uberTim12.ihor.dto.communication.SendingMessageDTO;

import java.util.List;

public interface IMessageService {
    List<MessageDTO> getMessages(Integer id);

    MessageDTO sendMessage(Integer senderId, SendingMessageDTO sendingMessageDTO);
}
