package com.simon.cient.domain;

import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Created by simon on 2016/9/13.
 */
public interface OrganizationRepository extends MongoRepository<Organization, String> {
    Organization findById(String id);
}
