package com.simon.cient.domain;

import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Created by simon on 2016/9/8.
 */
public interface JoinEventRepository extends MongoRepository<JoinEvent, String> {
    JoinEvent findByEventIdAndPhone(String eventId, String phone);
}
