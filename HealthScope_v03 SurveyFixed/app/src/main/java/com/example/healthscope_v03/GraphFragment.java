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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.LineChartView;

public class GraphFragment extends Fragment {

    float[] temps;
    Axis axisY;
    Axis axisX;
    Date[] dates;
    XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
    XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
    XYSeriesRenderer currentRenderer;
    XYSeries series;
    Date tempDate;
    GraphView graphView;
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

        //TODO: Recreate following code with graphview lib
        LineChartView lineChartView = new LineChartView(getActivity());
        LineChartData lineChartData = new LineChartData();
        lineChartView.setInteractive(true);

        graphContainer = getView().findViewById(R.id.tempGraph);

        tempDate = new Date(System.currentTimeMillis() - (TimeUnit.HOURS.toMillis(71) + TimeUnit.MINUTES.toMillis(50)));
        Log.d("TAG", "startDate: " + tempDate);

        temps = new float[432];
        generateRandomTemps();

        List<PointValue> values = new ArrayList<PointValue>();
        List<AxisValue> axisValuesX = new ArrayList<AxisValue>();
        List<AxisValue> axisValuesY = new ArrayList<AxisValue>();

        for (int i = 0; i < temps.length; i++) {
            SimpleDateFormat sf = new SimpleDateFormat("HH:mm");
            values.add(new PointValue(i, temps[i]));
            Log.d("TAG", "rerenderingX: " + i + " " + tempDate + " " + temps[i] + " " + sf.format(tempDate));
            axisValuesX.add(new AxisValue(i).setLabel(sf.format(tempDate)));
            axisValuesY.add(new AxisValue(temps[i]).setLabel(String.valueOf(temps[i]).substring(0, 4)));
            tempDate = new Date(tempDate.getTime() + TimeUnit.MINUTES.toMillis(10));
        }
        Line line = new Line(values).setColor(Color.parseColor("#FFCD41"));  //The color of the broken line (orange)
        List<Line> lines = new ArrayList<Line>();
        line.setShape(ValueShape.CIRCLE);//The shape of each data point on a broken line chart is circular here (there are three kinds: ValueShape. SQUARE ValueShape. CIRCLE ValueShape. DIAMOND)
        line.setCubic(false);//Whether the curve is smooth, that is, whether it is a curve or a broken line
        line.setFilled(false);//Whether or not to fill the area of the curve
        line.setHasLabels(false);//Whether to add notes to the data coordinates of curves
//      Line. setHasLabels OnlyForSelected (true); // Click on the data coordinates to prompt the data (set this line.setHasLabels(true); invalid)
        line.setHasLines(true);//Whether to display with line or not. If it is false, there is no curve but point display
        line.setHasPoints(true);//Whether to display a dot if it is false, there is no origin but only a dot (each data point is a large dot)
        lines.add(line);

        axisX = new Axis(axisValuesX);
        axisY = new Axis(axisValuesY);
        axisY.setMaxLabelChars(5);
        axisX.setMaxLabelChars(5);
        axisY.setTextSize(10);
        lineChartData.setLines(lines);
        lineChartData.setAxisYLeft(axisY);
        lineChartData.setAxisXBottom(axisX);
        float ymax = 4 + 10f;
        final Viewport v = new Viewport(lineChartView.getMaximumViewport());
        v.top = ymax; //max value
        v.bottom = 0f;  //min value
        lineChartView.setMaximumViewport(v);
        lineChartView.setCurrentViewport(v);
        lineChartView.setLineChartData(lineChartData);

        graphicalView = ChartFactory.getLineChartView(getActivity(), dataset, renderer);
        graphContainer.addView(lineChartView);
    }

    void generateRandomTemps() {
        for (int i = 0; i < temps.length; i++) {
            temps[i] = (new Random().nextFloat() % 4) + 35;
            Log.d("TAG", "generateRandomTemps: " + temps[i]);
        }
    }
}