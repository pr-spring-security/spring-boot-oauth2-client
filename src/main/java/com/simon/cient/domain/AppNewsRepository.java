package com.simon.cient.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Created by simon on 2016/8/17.
 */
public interface AppNewsRepository extends MongoRepository<AppNews, String> {
    AppNews findById(String id);
}
