package com.example.healthscope_v03;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.DataPointInterface;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.OnDataPointTapListener;
import com.jjoe64.graphview.series.Series;

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

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");

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
        graphView.setTitle("Body Temperature for Last 3 Days");
        graphView.setTitleTextSize(50);
        graphView.setTitleColor(Color.RED);
        series.setOnDataPointTapListener(new OnDataPointTapListener() {
            @Override
            public void onTap(Series series, DataPointInterface dataPoint) {
                Toast.makeText(getActivity(), "Clicked on: temp = " + dataPoint.getY() + " time = " + simpleDateFormat.format(dates.get((int) dataPoint.getX())), Toast.LENGTH_LONG).show();
            }
        });
        graphView.getViewport().setYAxisBoundsManual(true);
        graphView.getViewport().setMaxY(50);
        graphView.getViewport().setMinY(25);
        graphView.getViewport().setMaxX(temps.length);
        graphView.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter() {
            @Override
            public String formatLabel(double value, boolean isValueX) {
                if (isValueX) {
                    Log.d("TAG", "formatLabel: " + value);
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
            double randomNum = 35 + new Random().nextDouble() * (40 - 35);
            temps[i] = (float) randomNum;
            Log.d("TAG", "generateRandomTemps: " + temps[i] + " " + randomNum);
        }
    }
}