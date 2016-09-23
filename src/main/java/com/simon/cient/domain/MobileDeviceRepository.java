package com.simon.cient.domain;

import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Created by simon on 2016/9/23.
 */
public interface MobileDeviceRepository extends MongoRepository<MobileDevice, String> {
    MobileDevice findById(String id);
    MobileDevice findByUserId(String userId);
}
