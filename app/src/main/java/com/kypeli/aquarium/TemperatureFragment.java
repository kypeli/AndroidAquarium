package com.kypeli.aquarium;

import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kypeli.aquarium.models.AquariumReadings;

import rx.Observable;
import rx.functions.Action1;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TemperatureFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class TemperatureFragment extends Fragment {

    public static TemperatureFragment newInstance() {
        TemperatureFragment fragment = new TemperatureFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public TemperatureFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AquariumValueReader valueReader = new AquariumValueReader(getActivity().getBaseContext());
        Observable<AquariumReadings.Reading> observable = valueReader.getAquariumReadingsObservable();
        observable.subscribe(new Action1<AquariumReadings.Reading>() {

            @Override
            public void call(AquariumReadings.Reading reading) {
                Log.d("reading", "Got new reading - value: " + reading.temperature + ", date: " + reading.timestamp.toString());
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_temperature, container, false);
    }
}
