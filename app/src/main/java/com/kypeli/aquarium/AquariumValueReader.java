package com.kypeli.aquarium;

import android.content.Context;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.kypeli.aquarium.models.AquariumReadings;
import com.kypeli.aquarium.volley.GsonRequest;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subjects.ReplaySubject;

public class AquariumValueReader {
    private RequestQueue mVolleyQueue = null;
    private final ReplaySubject<AquariumReadings.Reading> readingsReplaySubject = ReplaySubject.create();

    public AquariumValueReader(Context context) {
        mVolleyQueue = Volley.newRequestQueue(context);
        fetchDataToSubject(readingsReplaySubject);
    }

    public Observable<AquariumReadings.Reading> getAquariumReadingsObservable() {
        return Observable.create(new Observable.OnSubscribe<AquariumReadings.Reading>() {
            @Override
            public void call(final Subscriber<? super AquariumReadings.Reading> subscriber) {
                readingsReplaySubject.subscribe(
                        // onNext
                        new Action1<AquariumReadings.Reading>() {
                            @Override
                            public void call(AquariumReadings.Reading reading) {
                                subscriber.onNext(reading);
                            }
                        },
                        // onError
                        new Action1<Throwable>() {
                            @Override
                            public void call(Throwable throwable) {
                            }
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

    public Observable<AquariumReadings.Reading> getLatestAquariumReadingObservable() {
        return Observable.create(new Observable.OnSubscribe<AquariumReadings.Reading>() {
            @Override
            public void call(final Subscriber<? super AquariumReadings.Reading> subscriber) {
                readingsReplaySubject
                        .last()
                        .subscribe(new Action1<AquariumReadings.Reading>() {
                            // onNext
                            @Override
                            public void call(AquariumReadings.Reading reading) {
                                subscriber.onNext(reading);
                                subscriber.onCompleted();
                            }
                        });
            }
        }).subscribeOn(Schedulers.io());
    }

    private void fetchDataToSubject(final ReplaySubject<AquariumReadings.Reading> readingsReplaySubject) {
        GsonRequest<AquariumReadings> getReadings =
                new GsonRequest<AquariumReadings>("http://johan.paul.fi/aquarium/api/v1/measurements", AquariumReadings.class,
                        new Response.Listener<AquariumReadings>() {
                            @Override
                            public void onResponse(AquariumReadings aquariumReadings) {
                                for (AquariumReadings.Reading r : aquariumReadings.readings) {
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
