package com.uberTim12.ihor.service.communication.impl;

import com.uberTim12.ihor.dto.communication.MessageDTO;
import com.uberTim12.ihor.exception.NotFoundException;
import com.uberTim12.ihor.exception.UnauthorizedException;
import com.uberTim12.ihor.model.communication.Message;
import com.uberTim12.ihor.model.communication.MessageType;
import com.uberTim12.ihor.model.ride.Ride;
import com.uberTim12.ihor.model.users.User;
import com.uberTim12.ihor.repository.communication.IMessageRepository;
import com.uberTim12.ihor.service.base.impl.JPAService;
import com.uberTim12.ihor.service.communication.interfaces.IMessageService;
import com.uberTim12.ihor.service.ride.interfaces.IRideService;
import com.uberTim12.ihor.service.users.interfaces.IUserService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.boot.autoconfigure.neo4j.Neo4jProperties;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class MessageService extends JPAService<Message> implements IMessageService {
    private final IMessageRepository messageRepository;
    private final IUserService userService;
    private final IRideService rideService;

    public MessageService(IMessageRepository messageRepository, IUserService userService, IRideService rideService) {
        this.messageRepository = messageRepository;
        this.userService = userService;
        this.rideService = rideService;
    }

    @Override
    protected JpaRepository<Message, Integer> getEntityRepository() {
        return messageRepository;
    }

    @Override
    public List<MessageDTO> getMessages(Integer id) {
        //List<Message> messages= messageRepository.findAllBySenderIdOrReceiverId(id,id);
        List<Message> messages = sortMessagesToChatFormat(messageRepository.findAllBySenderIdOrReceiverId(id,id),id);
        return messages.stream().map(MessageDTO::new).collect(Collectors.toList());
    }

    @Override
    public List<MessageDTO> getMessagesOfSpecificRide(Integer id, Integer rideId) {
        List<Message> messages = sortMessagesToChatFormat(messageRepository.findAllByRideIdAndSenderIdOrReceiverIdAnd(id, rideId),id);
        return messages.stream().map(MessageDTO::new).collect(Collectors.toList());
    }

    @Override
    public Message sendMessage(Integer senderId, Integer receiverId, Integer rideId, String content,
                               MessageType type) throws EntityNotFoundException {
        User sender = null;
        User receiver = null;
        Ride ride = null;
        try {
            sender = userService.get(senderId);
            receiver = userService.get(receiverId);
            ride = rideService.get(rideId);
            LocalDateTime currentTime = LocalDateTime.now();
            Message message = new Message(sender, receiver, content, currentTime, type, ride);
            return save(message);
        } catch (EntityNotFoundException e) {
            if (sender == null) {
                throw new EntityNotFoundException("User does not exist!");
            }
            else if (receiver == null) {
                throw new EntityNotFoundException("Receiver does not exist!");
            }
            else if (ride == null) {
                throw new EntityNotFoundException("Ride does not exist!");
            }
        }
        return null;
    }

    private List<Message> sortMessagesToChatFormat(List<Message> messages, Integer userId)
    {
        List<Message> groupedMessages=messages.stream()
                .sorted(Comparator.comparing(Message::getRideId).thenComparing(Message::getSendTime))
                .toList();
        List<Message> sortedMessages=new ArrayList<>();

        List<Integer> startIndexes=new ArrayList<>();
        List<Integer> endIndexes=new ArrayList<>();
        if(groupedMessages.size()==0)  return new ArrayList<Message>();


        int currentRideId=groupedMessages.get(0).getRideId();
//        if(Integer.parseInt(jwtUtil.extractId(authHeader.substring(7)))!=id)
        int start=0, end=groupedMessages.size();
        for(int i=0;i<groupedMessages.size();i++)
        {
            if(groupedMessages.get(i).getRideId()!=currentRideId) {
                end=i; //indeks prve koja nije ta
                groupMessageByUser(groupedMessages, start, end, userId);
                start=i;
                currentRideId=groupedMessages.get(i).getRideId();
            }
        }


        currentRideId=groupedMessages.get(0).getRideId();
        int otherUserId, currentOtherUserId=groupedMessages.get(0).getSender().getId()+groupedMessages.get(0).getReceiver().getId()-userId;
        startIndexes.add(0);
        for(int i=0;i<groupedMessages.size();i++)
        {
            otherUserId=groupedMessages.get(i).getSender().getId()+groupedMessages.get(i).getReceiver().getId()-userId;
            if(groupedMessages.get(i).getRideId()!=currentRideId || otherUserId!=currentOtherUserId) {
                endIndexes.add(i - 1);
                startIndexes.add(i);
                currentRideId=groupedMessages.get(i).getRideId();
                currentOtherUserId=otherUserId;
            }
        }
        endIndexes.add(groupedMessages.size()-1);
        int temp;
        for(int i=0;i< endIndexes.size()-1; i++)  // 0 1, 0 2, 1 2
        {
            for(int j=i+1;j<endIndexes.size();j++)
            {
                if(groupedMessages.get(endIndexes.get(i)).getSendTime().isBefore(groupedMessages.get(endIndexes.get(j)).getSendTime())) {
                    temp = endIndexes.get(i);
                    endIndexes.set(i,endIndexes.get(j));
                    endIndexes.set(j,temp);

                    temp=startIndexes.get(i);
                    startIndexes.set(i,startIndexes.get(j));
                    startIndexes.set(j,temp);
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

    private void groupMessageByUser(List<Message> groupedMessages, int start, int end, int userId) {
        Map<Integer, Integer> firstIndexes=new HashMap<>();
        int otherUserId1, otherUserId2;
        for(int i=start;i<end;i++)
        {
            otherUserId1=groupedMessages.get(i).getReceiver().getId()+groupedMessages.get(i).getSender().getId()-userId;
            if(!firstIndexes.containsKey(otherUserId1))
                firstIndexes.put(otherUserId1,i);
        }
        Message message1, message2;
        for(int i=start;i<end-1;i++)
        {
            otherUserId1=groupedMessages.get(i).getReceiver().getId()+groupedMessages.get(i).getSender().getId()-userId;
            for(int j=i+1;j<end;j++)
            {
                otherUserId2=groupedMessages.get(j).getReceiver().getId()+groupedMessages.get(j).getSender().getId()-userId;
                if(firstIndexes.get(otherUserId1)>firstIndexes.get(otherUserId2) ||
                        (Objects.equals(firstIndexes.get(otherUserId1), firstIndexes.get(otherUserId2)) && groupedMessages.get(i).getSendTime().isAfter(groupedMessages.get(j).getSendTime())))
                {
                    message1=new Message();
                    message1.setMessage(groupedMessages.get(i));
                    groupedMessages.get(i).setMessage(groupedMessages.get(j));
                    groupedMessages.get(j).setMessage(message1);
//                    message2=groupedMessages.get(j);

//                    groupedMessages.set(i, groupedMessages.get(j));
//                    groupedMessages.set(j, temp);
                }
            }
        }

    }

}
