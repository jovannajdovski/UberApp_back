package com.uberTim12.ihor.model.users;

import com.uberTim12.ihor.model.users.User;
import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
public class Suggestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @OneToOne
    private User user;

    @Column(name = "message", nullable = false)
    private String message;
}
