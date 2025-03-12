package com.banson.healthtagram.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@EnableMongoRepositories(basePackages = "com.banson.healthtagram.repository.mongoRepository")
@EnableMongoAuditing
@Configuration
public class MongoConfig {
}
