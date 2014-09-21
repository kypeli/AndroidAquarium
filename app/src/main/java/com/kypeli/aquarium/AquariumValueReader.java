package com.kypeli.aquarium;

import android.content.Context;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.kypeli.aquarium.models.AquariumReadings;
import com.kypeli.aquarium.volley.GsonRequest;

import java.util.ArrayList;

import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;

public class AquariumValueReader {
    private ArrayList<AquariumReadings.Reading> mAquariumReadings = null;
    private RequestQueue mVolleyQueue = null;

    public AquariumValueReader(Context context) {
        mVolleyQueue = Volley.newRequestQueue(context);
    }

    public Observable<AquariumReadings.Reading> getAquariumReadings() {
        return Observable.create(new Observable.OnSubscribe<AquariumReadings.Reading>() {
            @Override
            public void call(final Subscriber<? super AquariumReadings.Reading> subscriber) {
                if (mAquariumReadings == null) {
                    GsonRequest<AquariumReadings> getReadings =
                            new GsonRequest<AquariumReadings>("http://johan.paul.fi/aquarium/api/v1/measurements", AquariumReadings.class,
                                    new Response.Listener<AquariumReadings>() {
                                        @Override
                                        public void onResponse(AquariumReadings aquariumReadings) {
                                            for(AquariumReadings.Reading r : aquariumReadings.readings) {
                                                subscriber.onNext(r);
                                            }

                                            subscriber.onCompleted();
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
        }).subscribeOn(Schedulers.io());
    }
}
