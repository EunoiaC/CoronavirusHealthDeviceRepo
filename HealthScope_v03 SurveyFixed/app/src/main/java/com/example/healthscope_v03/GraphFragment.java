package com.example.healthscope_v03;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class GraphFragment extends Fragment {

    float[] temps;
    Date[] dates;
    Date tempDate;
    GraphView graphView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_graph, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tempDate = new Date(System.currentTimeMillis() - TimeUnit.HOURS.toMillis(72));
        Log.d("TAG", "startDate: " + tempDate);

        graphView = getView().findViewById(R.id.tempGraph);
        temps = new float[432];
        generateRandomTemps();
        DataPoint[] values = new DataPoint[temps.length];
        for (int i = 0; i < temps.length; i++) {
            values[i] = new DataPoint(i, temps[i]);
        }
        LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(values);
        graphView.getViewport().setScrollable(true);
        graphView.getViewport().setMaxX(temps.length/4);
        graphView.getViewport().setScalable(true);
        graphView.addSeries(series);
        //TODO: Rerender X values to cureent time - x and in for loop always change x - 10min. x starts as current time
//        graphView.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter() {
//            @Override
//            public String formatLabel(double value, boolean isValueX) {
//                if (isValueX) {
//                    Log.d("TAG", "formatLabel: " + value);
//                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("d HH:mm");
//                    tempDate = new Date(tempDate.getTime() + TimeUnit.MINUTES.toMillis(10));
//                    Log.d("TAG", "tempDate: " + tempDate);
//                    return simpleDateFormat.format(tempDate);
//                } else {
//                    // return y label as number
//                    return super.formatLabel(value, isValueX); // let the y-value be normal-formatted
//                }
//            }
//        });

    }

    void generateRandomTemps() {
        for (int i = 0; i < temps.length; i++) {
            temps[i] = (new Random().nextFloat() % 4) + 35;
            Log.d("TAG", "generateRandomTemps: " + temps[i]);
        }
    }
}