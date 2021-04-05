package com.example.healthscope_v03;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

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
    XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
    XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
    XYSeriesRenderer currentRenderer;
    XYSeries series;
    Date tempDate;
    LinearLayout graphContainer;
    GraphicalView graphicalView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_graph, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        graphContainer = getView().findViewById(R.id.tempGraph);

        tempDate = new Date(System.currentTimeMillis() - (TimeUnit.HOURS.toMillis(71) + TimeUnit.MINUTES.toMillis(50)));
        Log.d("TAG", "startDate: " + tempDate);

        temps = new float[432];
        generateRandomTemps();

        series = new XYSeries("Temp Graph");
        currentRenderer = new XYSeriesRenderer();

        for (int i = 0; i < temps.length; i++) {
            series.add(i, temps[i]);
        }
        dataset.addSeries(series);

        currentRenderer.setLineWidth(4);

        renderer.addSeriesRenderer(currentRenderer);
        renderer.setShowGrid(true);
        renderer.setPanEnabled(true);
        renderer.setLabelsTextSize(50);
        renderer.setYLabelsColor(0, Color.RED);
        renderer.setYLabelsAlign(Paint.Align.CENTER);
        renderer.setMarginsColor(Color.WHITE);
        renderer.setMargins(new int[]{10, 50, 50, 10});
        renderer.setXLabelsPadding(50);

        for(int i = 0; i < temps.length; i++){
            SimpleDateFormat sf = new SimpleDateFormat("HH:mm");
            Log.d("TAG", "rerenderingX: " + i + " " + tempDate + " " + temps[i] + " " + sf.format(tempDate));
            renderer.addXTextLabel(i, sf.format(tempDate));
            tempDate = new Date(tempDate.getTime() + TimeUnit.MINUTES.toMillis(10));
        }

        renderer.zoom

        graphicalView = ChartFactory.getLineChartView(getActivity(), dataset, renderer);
        graphContainer.addView(graphicalView);
    }

    void generateRandomTemps() {
        for (int i = 0; i < temps.length; i++) {
            temps[i] = (new Random().nextFloat() % 4) + 35;
            Log.d("TAG", "generateRandomTemps: " + temps[i]);
        }
    }
}