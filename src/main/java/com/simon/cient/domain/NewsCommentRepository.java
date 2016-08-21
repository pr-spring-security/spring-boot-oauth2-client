package com.simon.cient.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by simon on 2016/8/18.
 */
public interface NewsCommentRepository extends PagingAndSortingRepository<NewsComment, String>{
    Page<NewsComment> findByNewsId(String newsId, Pageable pageable);
}
