package com.simon.cient.domain;

import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Created by simon on 2016/8/30.
 */
public interface OrgEventRepository extends MongoRepository<OrgEvent, String>{
    OrgEvent findById(String id);
}
