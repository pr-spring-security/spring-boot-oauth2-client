package com.simon.cient.domain;

import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Created by simon on 2016/9/16.
 */
public interface AppVersionRepository extends MongoRepository<AppVersion, String> {
    AppVersion findById(String id);
}
