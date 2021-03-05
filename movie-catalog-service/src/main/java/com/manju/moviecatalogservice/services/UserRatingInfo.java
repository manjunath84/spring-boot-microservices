package com.manju.moviecatalogservice.services;

import com.manju.moviecatalogservice.models.Rating;
import com.manju.moviecatalogservice.models.UserRating;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;

@Service
public class UserRatingInfo {

    @Autowired
    RestTemplate restTemplate;

    @CircuitBreaker(name = "default", fallbackMethod = "getFallbackUserRating")
    public UserRating getUserRating(String userId) {
        UserRating userRating = restTemplate.getForObject("http://ratings-data-service/ratingsdata/user/" + userId, UserRating.class);
        return userRating;
    }

    public UserRating getFallbackUserRating(String userId, Exception e) {
        UserRating userRating = new UserRating();
        userRating.setUserId(userId);
        userRating.setRatings(Arrays.asList(new Rating("0",0)));
        return userRating;
    }
}
