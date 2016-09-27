package com.simon.cient.domain;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * Created by simon on 2016/9/13.
 */
public interface OrganizationRepository extends MongoRepository<Organization, String> {
    Organization findById(String id);
    List<Organization> findByOrgNameLike(String orgName, Pageable pageable);
}
