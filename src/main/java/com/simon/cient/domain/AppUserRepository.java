package com.simon.cient.domain;

import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Created by simon on 2016/8/13.
 */
public interface AppUserRepository extends MongoRepository<AppUser, String> {
    public AppUser findByUsername(String username);
}
