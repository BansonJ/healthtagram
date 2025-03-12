package com.banson.healthtagram.entity.mongodb.event;

import com.banson.healthtagram.entity.mongodb.Reply;
import com.banson.healthtagram.service.SequenceGeneratorService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertEvent;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class ReplyModelListener extends AbstractMongoEventListener<Reply> {
    private final SequenceGeneratorService sequenceGenerator;

    @Override
    public void onBeforeConvert(BeforeConvertEvent<Reply> event) {
        // if (event.getSource().getId() < 1) {// null point error
        event.getSource().setId(sequenceGenerator.generateSequence(Reply.SEQUENCE_NAME));
        // }
    }
}