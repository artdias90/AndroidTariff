package com.oneunit.test.cj2;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.jjoe64.graphview.GraphView;

import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.DataPointInterface;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.OnDataPointTapListener;
import com.jjoe64.graphview.series.Series;
import com.oneunit.test.cj2.UI.Constants;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by Bishal on 10/16/2015.
 */
public class UsageFragment extends Fragment{

    Spinner spinner;
    ArrayAdapter<CharSequence> adapter;
    private int displayPointsNum = 0;
    private Date selectedDisplayDate = new Date();
    private TextView dateIndicator;
    private TextView totalIndicator;
    private SimpleDateFormat dateFormatDay = new SimpleDateFormat("dd MMM yyyy");
    private SimpleDateFormat dateFormatMonth = new SimpleDateFormat("MMM yyyy");
    GraphView graph;
    private  Config config;

    private long yAxisMin = 0;
    private long yAxisMaxDay = 1024;
    private long yAxisMaxMonthly = 31 * yAxisMaxDay;
    ImageButton dateChangeLeft, dateChangeRight,changeViewButton;
    Button  dailyButton, monthlyButton, accumulativeButton;
    boolean dailyButtonflag = true, montlyButtonflag, accumulativeFlag;

    // time offset to adjust the information display accordingly


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.usage_fragment, container, false);

        final RelativeLayout change_view_layout = (RelativeLayout)view.findViewById(R.id.change_view_menu);
        final RelativeLayout relativeLayout = (RelativeLayout)view.findViewById(R.id.layout2);
        final ImageView dailyButtonCheck = (ImageView)view.findViewById(R.id.daily_button_check);
        final ImageView monthlyButtonCheck = (ImageView)view.findViewById(R.id.monthly_button_check);
        final ImageView accumulativeButtonCheck = (ImageView)view.findViewById(R.id.accumulative_button_check);

        // as UTC offset changes around the year, it affects the display of the information in the chart



        try {
            this.config = new Config(this.getActivity());
            if(config.getUserConfig("volume_data") == null) {
                WelcomeDialog welcomeDialog = new WelcomeDialog(this.getActivity());
                welcomeDialog.show();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
            WelcomeDialog welcomeDialog = new WelcomeDialog(this.getActivity());
            welcomeDialog.show();
        }

        graph  = (GraphView)view.findViewById(R.id.graph);
        //----spinner = (Spinner)view.findViewById(R.id.usage_dropdown);
        //adapter = ArrayAdapter.createFromResource(getActivity(),R.array.usage,android.R.layout.simple_spinner_item);
        //adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //spinner.setAdapter(adapter);
        dateIndicator = (TextView) view.findViewById(R.id.dataDate);
        totalIndicator = (TextView) view.findViewById(R.id.totalVolumeNum);
        dateIndicator.setText(dateFormatDay.format(new Date()));

        changeViewButton = (ImageButton) view.findViewById(R.id.changeViewButton);
        dailyButton = (Button) view.findViewById(R.id.daily_button);
        monthlyButton = (Button) view.findViewById(R.id.monthly_button);
        accumulativeButton = (Button) view.findViewById(R.id.accumulative_button);
        dateChangeLeft = (ImageButton) view.findViewById(R.id.leftButton);
        dateChangeRight = (ImageButton) view.findViewById(R.id.rightButton);
        //----------------------------------------------------------------------
        dailyButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dailyButtonflag = true;
                montlyButtonflag = false;
                accumulativeFlag = false;
                Animation slideDown = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.slide_down);
                change_view_layout.startAnimation(slideDown);
                change_view_layout.setVisibility(View.GONE);
                dailyButtonCheck.setVisibility(View.VISIBLE);
                monthlyButtonCheck.setVisibility(View.INVISIBLE);
                accumulativeButtonCheck.setVisibility(View.INVISIBLE);
                updateChart();
            }


        });
        monthlyButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                montlyButtonflag = true;
                dailyButtonflag = false;
                accumulativeFlag = false;
                Animation slideDown = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.slide_down);
                change_view_layout.startAnimation(slideDown);
                change_view_layout.setVisibility(View.GONE);
                dailyButtonCheck.setVisibility(View.INVISIBLE);
                monthlyButtonCheck.setVisibility(View.VISIBLE);
                accumulativeButtonCheck.setVisibility(View.INVISIBLE);
                updateChart();
            }


        });
        accumulativeButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                montlyButtonflag = false;
                dailyButtonflag = false;
                accumulativeFlag = true;
                Animation slideDown = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.slide_down);
                change_view_layout.startAnimation(slideDown);
                change_view_layout.setVisibility(View.GONE);
                dailyButtonCheck.setVisibility(View.INVISIBLE);
                monthlyButtonCheck.setVisibility(View.INVISIBLE);
                accumulativeButtonCheck.setVisibility(View.VISIBLE);
                updateChart();
            }
        });

        dateChangeLeft.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                Calendar cal = Calendar.getInstance();
                cal.setTime(selectedDisplayDate);
                if(dailyButtonflag){
                    cal.add(Calendar.DAY_OF_MONTH, -1);
                } else if(montlyButtonflag || accumulativeFlag){
                    cal.add(Calendar.MONTH, -1);
                }
                selectedDisplayDate = cal.getTime();
                dateIndicator.setText(dateFormatDay.format(selectedDisplayDate));
                updateChart();
            }
        });

        dateChangeRight.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                Calendar cal = Calendar.getInstance();
                cal.setTime(selectedDisplayDate);
                if (dailyButtonflag){
                    cal.add(Calendar.DAY_OF_MONTH, 1);
                } else if(montlyButtonflag || accumulativeFlag){
                    cal.add(Calendar.MONTH, 1);
                }
                selectedDisplayDate = cal.getTime();
                dateIndicator.setText(dateFormatDay.format(selectedDisplayDate));
                updateChart();
            }
        });

        relativeLayout.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                if(change_view_layout.getVisibility()== View.VISIBLE) {
                    Animation slideDown = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.slide_down);
                    change_view_layout.startAnimation(slideDown);
                    change_view_layout.setVisibility(View.GONE);
                }
            }

        });
        changeViewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation slideUp = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.slide_up);
                Animation slideDown = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.slide_down);

                if (change_view_layout.getVisibility() == View.GONE) {
                    change_view_layout.startAnimation(slideUp);
                    change_view_layout.setVisibility(View.VISIBLE);
                } else {
                    change_view_layout.startAnimation(slideDown);
                    change_view_layout.setVisibility(View.GONE);

                }
            }
        });

        updateChart();
        NetworkAnalysisDbHelper dbAnalysis = new NetworkAnalysisDbHelper(this.getActivity().getApplicationContext());
        SQLiteDatabase dbSource = dbAnalysis.getReadableDatabase();




        return view;
    }
    private void updateChart(){
        int volume_data = 0;
       // GridLabelRenderer gridLabel = graph.getGridLabelRenderer();
        StaticLabelsFormatter staticLabelsFormatter = new StaticLabelsFormatter(graph);

        try {
            Config config = new Config(getActivity());
            if(config.getUserConfig("volume_data") != null) {
                volume_data = Integer.valueOf(config.getUserConfig("volume_data"));
            }

        } catch(IOException e) {
            e.printStackTrace();
        }


        //graph.getViewport().setMinY(yAxisMin);

        int offsetTime = 0;

        LineGraphSeries<DataPoint> seriesNetworkRX = new LineGraphSeries<DataPoint>();
        LineGraphSeries<DataPoint> seriesNetworkTX = new LineGraphSeries<DataPoint>();
        LineGraphSeries<DataPoint> userDataPackage = new LineGraphSeries<DataPoint>();

        graph.removeAllSeries();
       //---- int selectedDropdownOption = spinner.getSelectedItemPosition();
        double[][] measuredData = new double[2][];

        if(dailyButtonflag) {
            displayPointsNum = Constants.DATA_PER_DAY;
            measuredData = TrafficInfoManager.getDataPerDay(getActivity(), selectedDisplayDate);
            graph.setTitle("Consumption(MB)");
            graph.getGridLabelRenderer().setHorizontalAxisTitle("Hours");
            graph.getViewport().setXAxisBoundsManual(true);
            graph.getViewport().setScrollable(false);
            graph.getViewport().setScalable(false);
            graph.getViewport().setMinX(0);
            graph.getViewport().setMaxX(24);
            graph.getViewport().setYAxisBoundsManual(false);
        } else if(montlyButtonflag) {
            displayPointsNum = Constants.DATA_PER_MONTH;
            measuredData = TrafficInfoManager.getDataPerMonth(getActivity(), selectedDisplayDate);
            graph.getGridLabelRenderer().setHorizontalAxisTitle("Days");
            graph.getViewport().setScrollable(true);
            graph.getViewport().setScalable(true);
            graph.getViewport().setMinX(1);
            graph.getViewport().setMaxX(31);

        }else if(accumulativeFlag) {
            displayPointsNum = Constants.DATA_PER_MONTH;
            measuredData = TrafficInfoManager.getDataPerMonth(getActivity(), selectedDisplayDate);
            graph.getGridLabelRenderer().setHorizontalAxisTitle("Days");
            graph.getViewport().setScrollable(true);
            graph.getViewport().setScalable(true);
            graph.getViewport().setMinX(1);
            if(volume_data != 0) {
                String[] yAxis = new String[7];
                for (int i =0; i<7; i++) {
                    yAxis[i] = String.valueOf((i* volume_data)/6);
                }
                graph.getViewport().setYAxisBoundsManual(true);
                graph.getViewport().setMinY(0);
                graph.getViewport().setMaxY(volume_data);
                staticLabelsFormatter.setVerticalLabels(yAxis);
            } else {
                graph.getViewport().setYAxisBoundsManual(false);
            }
        }
        double[][] accumulativeData = new double[2][30];
        for (int j = 0; j < displayPointsNum; j++) {
            // set accumulative series
            if(accumulativeFlag) {
                accumulativeData[0][j] = 0;
                accumulativeData[1][j] = 0;
                for (int k =0; k <= j; k++) {
                    accumulativeData[0][j] += measuredData[0][k];
                    accumulativeData[1][j] += measuredData[1][k];
                }
                seriesNetworkRX.appendData(new DataPoint(j, accumulativeData[0][j]), true, displayPointsNum);
                seriesNetworkTX.appendData(new DataPoint(j, accumulativeData[1][j]), true, displayPointsNum);
            } else {
                seriesNetworkRX.appendData(new DataPoint(j, measuredData[0][j]), true, displayPointsNum);
                seriesNetworkTX.appendData(new DataPoint(j, measuredData[1][j]), true, displayPointsNum);
            }
            try {
                Config config = new Config(getActivity());
                if(config.getUserConfig("volume_data") != null) {
                    userDataPackage.appendData(new DataPoint(j, Integer.valueOf(config.getUserConfig("volume_data"))), true, displayPointsNum);
                }

            } catch(IOException e) {
                e.printStackTrace();
            }
        }
        graph.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter);
        seriesNetworkRX.setColor(Color.RED);
        seriesNetworkTX.setColor(Color.BLUE);
        userDataPackage.setColor(Color.GREEN);
        graph.addSeries(seriesNetworkRX);
        graph.addSeries(seriesNetworkTX);

        seriesNetworkRX.setOnDataPointTapListener(new OnDataPointTapListener() {
            @Override
            public void onTap(Series series, DataPointInterface dataPoint) {
                Toast.makeText(getActivity(), "At " + (int)dataPoint.getX()+ ":00, " + dataPoint.getY() + " MB downloaded", Toast.LENGTH_SHORT).show();
            }
        });
        seriesNetworkTX.setOnDataPointTapListener(new OnDataPointTapListener() {
            @Override
            public void onTap(Series series, DataPointInterface dataPoint) {
                Toast.makeText(getActivity(), "At " + (int)dataPoint.getX()+ ":00, " + dataPoint.getY() + " MB uploaded", Toast.LENGTH_SHORT).show();
            }
        });
        if(montlyButtonflag || accumulativeFlag) {
            graph.addSeries(userDataPackage);
            userDataPackage.setTitle("Plan Limit");
        }


        // legend
        seriesNetworkRX.setTitle("Download");
        seriesNetworkTX.setTitle("Upload");
        graph.getLegendRenderer().setVisible(true);
        graph.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);
        updateTotal(measuredData);

    }

    private void updateTotal(double[][] measuredData) {
        double sum = 0;
        for (int i = 0; i < measuredData[0].length; i++){
            sum += measuredData[0][i];
        }
        totalIndicator.setText(Integer.toString((int)sum));
    }

}

