package com.simon.cient.domain;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * Created by simon on 2016/9/8.
 */
public interface JoinEventRepository extends MongoRepository<JoinEvent, String> {
    JoinEvent findByEventIdAndPhone(String eventId, String phone);
    Integer countByPhone(String phone);
    List<JoinEvent> findByPhoneAndStatus(String phone, Integer status, Pageable pageable);
}
