package com.simon.cient.domain;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * Created by simon on 2016/8/30.
 */
public interface OrgEventRepository extends MongoRepository<OrgEvent, String>{
    OrgEvent findById(String id);
    List<OrgEvent> findByPublisherId(String publisherId, Pageable pageable);
    List<OrgEvent> findByThemeLike(String theme, Pageable pageable);
}
