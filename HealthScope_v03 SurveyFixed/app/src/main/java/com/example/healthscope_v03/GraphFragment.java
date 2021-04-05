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
import java.util.ArrayList;
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
        ArrayList<Date> dates = new ArrayList<>();
        for (int i = 0; i < temps.length; i++) {
            values[i] = new DataPoint(i, temps[i]);
            dates.add(tempDate);
            tempDate = new Date(tempDate.getTime() + TimeUnit.MINUTES.toMillis(10));
        }
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(values);
        graphView.getViewport().setScrollable(true);
        graphView.getViewport().setMaxX(temps.length/4);
        graphView.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter() {
            @Override
            public String formatLabel(double value, boolean isValueX) {
                if (isValueX) {
                    Log.d("TAG", "formatLabel: " + value);
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
                    Log.d("TAG", "tempDate: " + dates.get((int) value));
                    return simpleDateFormat.format(dates.get((int) value));
                } else {
                    // return y label as number
                    return super.formatLabel(value, isValueX) + "°C"; // let the y-value be normal-formatted
                }
            }
        });
        graphView.getViewport().setScalable(true);
        graphView.addSeries(series);


    }

    void generateRandomTemps() {
        for (int i = 0; i < temps.length; i++) {
            temps[i] = (new Random().nextFloat() % 4) + 35;
            Log.d("TAG", "generateRandomTemps: " + temps[i]);
        }
    }
}