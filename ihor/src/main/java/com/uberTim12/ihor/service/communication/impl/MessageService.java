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
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

        List<Message> messages= sortMessagesToChatFormat(messageRepository.findAllBySenderIdOrReceiverId(id,id),id);
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
    private List<Message> sortMessagesToChatFormat(List<Message> messages, Integer userId)
    {
        List<Message> groupedMessages=messages.stream()
                .sorted(Comparator.comparing(Message::getRideId).thenComparing(Message::getSendTime))
                .toList();
        List<Message> sortedMessages=new ArrayList<>();

        List<Integer> startIndexes=new ArrayList<>();
        List<Integer> endIndexes=new ArrayList<>();
        int currentRideId=groupedMessages.get(0).getRideId();
        startIndexes.add(0);
        for(int i=0;i<groupedMessages.size();i++)
        {
            if(groupedMessages.get(i).getRideId()!=currentRideId) {
                endIndexes.add(i - 1);
                startIndexes.add(i);
            }
        }
        endIndexes.add(groupedMessages.size()-1);
        int pom;
        for(int i=0;i< endIndexes.size()-1; i++)  // 0 1, 0 2, 1 2
        {
            for(int j=i+1;j<endIndexes.size();j++)
            {
                if(groupedMessages.get(endIndexes.get(i)).getSendTime().isAfter(groupedMessages.get(endIndexes.get(j)).getSendTime())) {
                    pom = endIndexes.get(i);
                    endIndexes.set(i,endIndexes.get(j));
                    endIndexes.set(j,pom);

                    pom=startIndexes.get(i);
                    startIndexes.set(i,startIndexes.get(j));
                    startIndexes.set(j,pom);
                }
            }
        }

        // sort po sender/receiverId, pa po vremenu,
        // sortirati po poslednjoj za svaku konverzaciju
        /* primer KORISNIK SA ID=1

            id senderId reciverId content sendTime type rideId
                3           1               6.12.           1
                1           3               8.12.           1
                1           3               2.12.           2
                5           1               4.12.           3
                startIndexes=[2,3,0]
                endIndexes=[2,3,1]
         */
        for(int i=0;i< endIndexes.size(); i++)
        {
            sortedMessages= Stream.concat(sortedMessages.stream(),groupedMessages.subList(startIndexes.get(i),endIndexes.get(i)+1).stream()).toList();
        }

        return sortedMessages;
    }
}
