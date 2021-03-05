package com.manju.moviecatalogservice.services;

import com.manju.moviecatalogservice.models.CatalogItem;
import com.manju.moviecatalogservice.models.Movie;
import com.manju.moviecatalogservice.models.Rating;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;

@Service
public class MovieInfo {

    @Autowired
    WebClient.Builder webClientBuilder;

    @CircuitBreaker(name = "default", fallbackMethod = "getFallbackCatalogItem")
    public Movie getCatalogItem(Rating rating) {
        return webClientBuilder.build()
                .get()
                .uri("http://localhost:8082/movies/" + rating.getMovieId())
                .retrieve()
                .bodyToMono(Movie.class)
                .block();
    }

    public CatalogItem getFallbackCatalogItem(Rating rating, Exception e) {
        return new CatalogItem("No Movie", "",0);
    }
}
