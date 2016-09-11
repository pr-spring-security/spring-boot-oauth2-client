package com.simon.cient.domain;

import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Created by simon on 2016/9/11.
 */
public interface AskHelpRepository extends MongoRepository<AskHelp, String> {
    AskHelp findById(String id);
}
