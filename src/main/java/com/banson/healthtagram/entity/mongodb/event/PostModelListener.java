package com.banson.healthtagram.entity.mongodb.event;

import com.banson.healthtagram.entity.mongodb.Post;
import com.banson.healthtagram.service.SequenceGeneratorService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertEvent;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class PostModelListener extends AbstractMongoEventListener<Post> {
    private final SequenceGeneratorService sequenceGenerator;

    @Override
    public void onBeforeConvert(BeforeConvertEvent<Post> event) {
        // if (event.getSource().getId() < 1) {// null point error
        event.getSource().setId(sequenceGenerator.generateSequence(Post.SEQUENCE_NAME));
        // }
    }
}