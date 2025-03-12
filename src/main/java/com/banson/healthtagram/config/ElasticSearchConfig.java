package com.banson.healthtagram.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

@Configuration
@EnableElasticsearchRepositories(basePackages = "com.banson.healthtagram.repository.elasticsearch")
public class ElasticSearchConfig extends ElasticsearchConfiguration {

    @Value("${elastic.ip}")
    private String elasticIp;

    @Override
    public ClientConfiguration clientConfiguration() {
        return ClientConfiguration.builder()
                .connectedTo(elasticIp + ":9200")
                .build();
    }
}
