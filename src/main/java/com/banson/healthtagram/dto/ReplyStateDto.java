package com.banson.healthtagram.dto;

import com.banson.healthtagram.entity.mongodb.Reply;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ReplyStateDto {
    private Reply reply;
    private boolean state;
}
