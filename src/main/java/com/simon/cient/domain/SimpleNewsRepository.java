package com.simon.cient.domain;

import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Created by simon on 2016/8/21.
 */
public interface SimpleNewsRepository extends MongoRepository<SimpleNews,String> {

}
