package com.simon.cient.domain;

import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Created by simon on 2016/8/31.
 */
public interface NewsPraiseRepository extends MongoRepository<NewsPraise, String>{
    NewsPraise findByNewsIdAndUsername(String newsId, String username);
}
