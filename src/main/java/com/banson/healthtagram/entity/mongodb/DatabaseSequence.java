package com.banson.healthtagram.entity.mongodb;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter //getters and setters omitted
@Document(collection = "database_sequences")//AUTO INCREMENT 연속성 정보 저장 컬렉션
public class DatabaseSequence {
    @Id
    private String id;
    private long seq;
}
