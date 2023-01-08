package com.uberTim12.ihor.model.communication;

import com.uberTim12.ihor.model.ride.Ride;
import com.uberTim12.ihor.model.users.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "sender_id", referencedColumnName = "id")
    private User sender;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "receiver_id", referencedColumnName = "id")
    private User receiver;

    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "send_time", nullable = false)
    private LocalDateTime sendTime;

    @Enumerated
    @Column(name = "type", nullable = false)
    private MessageType type;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ride_id")
    private Ride ride;

    public Message(User sender, User receiver, String content, LocalDateTime sendTime, MessageType type, Ride ride) {
        this.sender = sender;
        this.receiver = receiver;
        this.content = content;
        this.sendTime = sendTime;
        this.type = type;
        this.ride = ride;
    }
    public Integer getRideId()
    {
        return this.ride.getId();
    }

    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", sender=" + sender.getId() +
                ", receiver=" + receiver.getId() +
                ", content='" + content + '\'' +
                ", sendTime=" + sendTime +
                ", type=" + type +
                ", ride=" + ride.getId() +
                '}';
    }
}
