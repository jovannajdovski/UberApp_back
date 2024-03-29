package com.uberTim12.ihor.repository.communication;

import com.uberTim12.ihor.model.communication.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface IMessageRepository extends JpaRepository<Message, Integer>{
    public List<Message> findAllBySenderIdOrReceiverId(Integer sender_id, Integer receiver_id);

}
