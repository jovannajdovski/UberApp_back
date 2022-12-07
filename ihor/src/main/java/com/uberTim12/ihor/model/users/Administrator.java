package com.uberTim12.ihor.model.users;

import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@PrimaryKeyJoinColumn(name = "id")
public class Administrator extends  User{

}
