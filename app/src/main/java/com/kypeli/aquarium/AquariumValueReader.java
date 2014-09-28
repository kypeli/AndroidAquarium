package com.kypeli.aquarium;

import android.content.Context;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.kypeli.aquarium.models.AquariumReadings;
import com.kypeli.aquarium.models.AquariumReadingsJackson;
import com.kypeli.aquarium.volley.GsonRequest;
import com.kypeli.aquarium.volley.JacksonRequest;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subjects.ReplaySubject;

public class AquariumValueReader {
    private RequestQueue mVolleyQueue = null;
    private final ReplaySubject<AquariumReadingsJackson.Reading> readingsReplaySubject = ReplaySubject.create();

    public AquariumValueReader(Context context) {
        mVolleyQueue = Volley.newRequestQueue(context);
        fetchDataToSubject(readingsReplaySubject);
    }

    public Observable<AquariumReadingsJackson.Reading> getAquariumReadingsObservable() {
        return Observable.create(new Observable.OnSubscribe<AquariumReadingsJackson.Reading>() {
            @Override
            public void call(final Subscriber<? super AquariumReadingsJackson.Reading> subscriber) {
                readingsReplaySubject.subscribe(
                        // onNext
                        new Action1<AquariumReadingsJackson.Reading>() {
                            @Override
                            public void call(AquariumReadingsJackson.Reading reading) {
                                subscriber.onNext(reading);
                            }
                        },
                        // onError
                        new Action1<Throwable>() {
                            @Override
                            public void call(Throwable throwable) {}
                        },
                        // onCompleted
                        new Action0() {
                            @Override
                            public void call() {
                                subscriber.onCompleted();
                            }
                        });
            }
        }).subscribeOn(Schedulers.io());
    }

    public Observable<AquariumReadingsJackson.Reading> getLatestAquariumReadingObservable() {
        return Observable.create(new Observable.OnSubscribe<AquariumReadingsJackson.Reading>() {
            @Override
            public void call(final Subscriber<? super AquariumReadingsJackson.Reading> subscriber) {
                readingsReplaySubject
                        .last()
                        .subscribe(new Action1<AquariumReadingsJackson.Reading>() {
                            // onNext
                            @Override
                            public void call(AquariumReadingsJackson.Reading reading) {
                                subscriber.onNext(reading);
                                subscriber.onCompleted();
                            }
                        });
            }
        }).subscribeOn(Schedulers.io());
    }

    private void fetchDataToSubject(final ReplaySubject<AquariumReadingsJackson.Reading> readingsReplaySubject) {
        JacksonRequest<AquariumReadingsJackson> getReadings =
                new JacksonRequest<AquariumReadingsJackson>("http://johan.paul.fi/aquarium/api/v1/measurements", AquariumReadingsJackson.class,
                        new Response.Listener<AquariumReadingsJackson>() {
                            @Override
                            public void onResponse(AquariumReadingsJackson aquariumReadings) {
                                for (AquariumReadingsJackson.Reading r : aquariumReadings.readings) {
                                    readingsReplaySubject.onNext(r);
                                }
                                readingsReplaySubject.onCompleted();
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError volleyError) {
                                Log.d("json", "ERROR: " + volleyError.toString());
                            }
                        }
                );

        mVolleyQueue.add(getReadings);
    }
}
