package com.uberTim12.ihor.dto.communication;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MultipleSendingMessageDTO {
    SendingMessageDTO message;
    List<Integer> userIds;
}
