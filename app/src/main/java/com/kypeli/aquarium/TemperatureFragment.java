package com.kypeli.aquarium;

import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.LineGraphView;
import com.kypeli.aquarium.models.AquariumReadings;

import java.util.ArrayList;

import rx.Observable;
import rx.functions.Action0;
import rx.functions.Action1;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TemperatureFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class TemperatureFragment extends Fragment {
    private ArrayList<GraphView.GraphViewData> mTemperatureData = new ArrayList<GraphView.GraphViewData>();
    //    private GraphViewSeries mTemperatureSeries = new GraphViewSeries();

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

        Observable<AquariumReadings.Reading> observableAllValues = valueReader.getAquariumReadingsObservable();
        observableAllValues
                .subscribe(
                        // onNext
                        new Action1<AquariumReadings.Reading>() {

                            @Override
                            public void call(AquariumReadings.Reading reading) {
                                Log.d("reading", "Got new reading - value: " + reading.temperature);
                                mTemperatureData.add(mTemperatureData.size(), new GraphView.GraphViewData(mTemperatureData.size(), reading.temperature));
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
                                GraphView graph = new LineGraphView(getActivity().getBaseContext(), "Temperature");
                                GraphView.GraphViewData[] data = new GraphView.GraphViewData[mTemperatureData.size()];
                                data = mTemperatureData.toArray(data);
                                GraphViewSeries series = new GraphViewSeries(data);
                                graph.addSeries(series);
                                graph.setManualMinY(true);
                                graph.setManualYAxisBounds(30.0, 22.0);

                                LinearLayout layout = (LinearLayout) getActivity().findViewById(R.id.temperature_graph_layout);
                                layout.addView(graph);
                            }
                        });


        Observable<AquariumReadings.Reading> observableLatestValues = valueReader.getLatestAquariumReadingObservable();
        observableLatestValues
                .subscribe(
                        // onNext
                        new Action1<AquariumReadings.Reading>() {

                            @Override
                            public void call(AquariumReadings.Reading reading) {
                                Log.d("reading", "Got latest value: " + reading.temperature);
                                TextView tempText = (TextView)getActivity().findViewById(R.id.temperature_text);
                                tempText.setText("Current: " + reading.temperature + " \u00B0C");
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
