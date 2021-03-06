package com.livos.appdemo.topmovies;

import com.livos.appdemo.network.MoreInfoApiService;
import com.livos.appdemo.network.MovieApiService;
import com.livos.appdemo.network.apimodel.OmdbApi;
import com.livos.appdemo.network.apimodel.Result;
import com.livos.appdemo.network.apimodel.TopRated;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;

public class TopMoviesRepository implements Repository {

    private MovieApiService movieApiService;
    private MoreInfoApiService moreInfoApiService;
    private List<String> countries;
    private List<Result> results;
    private long timestamp;

    private static final long STALE_MS = 20 * 1000; // Data is stale after 20 seconds

    public TopMoviesRepository(MovieApiService movieApiService, MoreInfoApiService moreInfoApiService) {
        this.moreInfoApiService = moreInfoApiService;
        this.timestamp = System.currentTimeMillis();
        this.movieApiService = movieApiService;
        countries = new ArrayList<>();
        results = new ArrayList<>();

    }

    public boolean isUpToDate() {
        return System.currentTimeMillis() - timestamp < STALE_MS;
    }

    @Override
    public Observable<Result> getResultsFromMemory() {

        if (isUpToDate()) {
            return Observable.from(results);
        } else {
            timestamp = System.currentTimeMillis();
            results.clear();
            return Observable.empty();
        }
    }

    @Override
    public Observable<Result> getResultsFromNetwork() {

        // Concat 3 results pages
        Observable<TopRated> topRatedObservable = movieApiService.getTopRatedMovies(1).concatWith(movieApiService.getTopRatedMovies(2)).concatWith(movieApiService.getTopRatedMovies(3));

        // concatMap operator preserve the objects order
        return topRatedObservable.concatMap(new Func1<TopRated, Observable<Result>>() {
            @Override
            public Observable<Result> call(TopRated topRated) {
                return Observable.from(topRated.results);
            }
        }).doOnNext(new Action1<Result>() { // doOnNewt will be called every time a new item is emitted on our observable stream
            @Override
            public void call(Result result) {
                results.add(result); // add results to the cache
            }
        });
    }

    @Override
    public Observable<String> getCountriesFromMemory() {

        if (isUpToDate()) {
            return Observable.from(countries);
        } else {
            timestamp = System.currentTimeMillis();
            countries.clear();
            return Observable.empty();
        }
    }

    @Override
    public Observable<String> getCountriesFromNetwork() {

        return getResultsFromNetwork().concatMap(new Func1<Result, Observable<OmdbApi>>() {
            @Override
            public Observable<OmdbApi> call(Result result) {
                return moreInfoApiService.getCountry(result.title);
            }
        }).concatMap(new Func1<OmdbApi, Observable<String>>() {
            @Override
            public Observable<String> call(OmdbApi omdbApi) {
                return Observable.just(omdbApi.getCountry());
            }
        }).doOnNext(new Action1<String>() {
            @Override
            public void call(String s) {
                countries.add(s);
            }
        });

    }

    @Override
    public Observable<String> getCountryData() {

        return getCountriesFromMemory().switchIfEmpty(getCountriesFromNetwork());

    }

    @Override
    public Observable<Result> getResultData() {
        return getResultsFromMemory().switchIfEmpty(getResultsFromNetwork());
    }
}
