package com.simon.cient.domain;

import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by simon on 2016/8/21.
 */
public interface SimpleNewsRepository extends PagingAndSortingRepository<SimpleNews,String> {
}
