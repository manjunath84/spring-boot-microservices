package com.manju.moviecatalogservice.resources;

import com.manju.moviecatalogservice.models.CatalogItem;
import com.manju.moviecatalogservice.models.Movie;
import com.manju.moviecatalogservice.models.Rating;
import com.manju.moviecatalogservice.models.UserRating;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/catalog")
public class MovieCatalogResource {

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    WebClient.Builder webClientBuilder;

    @RequestMapping("/{userId}")
    //@Retry(name="default", fallbackMethod = "h")
    @CircuitBreaker(name = "default", fallbackMethod = "getFallbackCatalog")
    public List<CatalogItem> getCatalog(@PathVariable("userId") String userId) {

        UserRating userRating = restTemplate.getForObject("http://ratings-data-service/ratingsdata/user/" + userId, UserRating.class);

        //ParameterizedType<List<Rating>>
        return userRating.getRatings().stream().map(rating -> {
            //Movie movie = restTemplate.getForObject("http://movie-info-service/movies/" + rating.getMovieId(), Movie.class);


            //Alternative WebClient way
            Movie movie = webClientBuilder.build()
                    .get()
                    .uri("http://localhost:8082/movies/" + rating.getMovieId())
                    .retrieve()
                    .bodyToMono(Movie.class)
                    .block();

            return new CatalogItem(movie.getName(), "test", rating.getRating());
        }).collect(Collectors.toList());

    }

    public List<CatalogItem> getFallbackCatalog(@PathVariable("userId") String userId, Exception e) {
        return Arrays.asList((new CatalogItem("No Movie", "",0)));
    }
}
