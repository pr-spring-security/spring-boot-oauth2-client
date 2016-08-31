package com.simon.cient.domain;

import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Created by simon on 2016/8/31.
 */
public interface CarouselRepository extends MongoRepository<Carousel, String> {
}
