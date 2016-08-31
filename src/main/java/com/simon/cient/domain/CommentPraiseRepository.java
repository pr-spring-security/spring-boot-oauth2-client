package com.simon.cient.domain;

import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Created by simon on 2016/8/31.
 */
public interface CommentPraiseRepository extends MongoRepository<CommentPraise, String> {
    CommentPraise findByNewsIdAndCommentIdAndUsername(String newsId, String commentId, String username);
}
