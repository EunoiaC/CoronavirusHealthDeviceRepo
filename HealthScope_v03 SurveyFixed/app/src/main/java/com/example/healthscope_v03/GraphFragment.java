package com.example.healthscope_v03;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
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

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tempDate = new Date(System.currentTimeMillis() - (TimeUnit.HOURS.toMillis(71) + TimeUnit.MINUTES.toMillis(50)));
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
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(values);
        graphView.getViewport().setScrollable(true);
//        graphView.getViewport().setMaxXAxisSize(432);
        graphView.getViewport().setMaxX(temps.length);
        series.setOnDataPointTapListener(new OnDataPointTapListener() {
            @Override
            public void onTap(Series series, DataPointInterface dataPoint) {
                Toast.makeText(getActivity(), "Clicked on: temp = " + dataPoint.getY() + " time = " + simpleDateFormat.format(dates.get((int) dataPoint.getX())), Toast.LENGTH_LONG).show();
            }
        });
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
        graphView.getViewport().setYAxisBoundsManual(true);
        graphView.getViewport().setMaxY(40);
        graphView.getViewport().setMinY(25);
        graphView.addSeries(series);


    }

    void generateRandomTemps() {
        for (int i = 0; i < temps.length; i++) {
            double randomNum = 25 + new Random().nextDouble() * (40 - 25);
            temps[i] = (float) ((randomNum % 4) + 35);
            Log.d("TAG", "generateRandomTemps: " + temps[i] + " " + randomNum);
        }
    }
}