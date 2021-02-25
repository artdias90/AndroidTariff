package com.oneunit.test.cj2;


import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.IntegerRes;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.DataPointInterface;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.OnDataPointTapListener;
import com.jjoe64.graphview.series.Series;
import com.oneunit.test.cj2.UI.Constants;

import java.io.IOException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Muhmmd on 21.04.2016.
 */
public class CostFragment extends Fragment {

    private GraphView graph;
    private Date selectedDisplayDate = new Date();
    private ImageButton imageButton, imageButton1;
    private TextView dateIndicator,yAxis;
    TextView dateText;
    private Button dailyGraphButton, monthlyGraphButton, monthlyCostPerConsumption;
    private Button changeParametersButton;
    private ImageView dailyButtonCheck, monthlyButtonCheck, monthlyCostPerConsumptionCheck, costFragmentCircle;
    private RelativeLayout change_view_layout;
    private boolean dailyButtonflag = false, monthlyButtonflag = false, monthlyCostFlag = true;
    private int displayPointsNum = 0;
    private static float CostPerMB = 0;
    private static int flatRateCost = 19, multiple = 20;
    private static float flatRate = 700;
    private float linearCost = 0, logCost = 0, sqrtCost = 0, flatCost = 0;
    private View view;
    private TextView totalConsumption;
    private double flatRateUsageLimit = 0;
    private Calendar cal;
    private EditText paramA,paramB;
    int param_a=1, param_b=1;


    public GraphView getGraph() {
        return graph;
    }

    public static float getFlatRate() {
        return flatRate;
    }

    public static int getMultiple() {
        return multiple;
    }

    public static float getCostPerMB() {
        return CostPerMB;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.cost_fragment, container, false);
        String timeStamp = new SimpleDateFormat("dd/MM/yyyy").format(Calendar.getInstance().getTime());
        dateIndicator = (TextView) view.findViewById(R.id.costOfDataConsumption);
        dateIndicator.setText(timeStamp);

        try {
            Config config = new Config(getActivity());
            float userPricing = Float.valueOf(config.getUserConfig("price"));
            float userPackage = Float.valueOf(config.getUserConfig("volume_data"));
            CostPerMB = userPricing / userPackage;
            flatRateUsageLimit = userPackage;
        } catch(IOException e) {
            e.printStackTrace();
        }

        configureGraph(view);

        changeGraph(view);

        return view;
    }

    private void configureGraph(View view) {
        graph = (GraphView) view.findViewById(R.id.graph);
        graph.setBackgroundColor(Color.LTGRAY);
        graph.getLegendRenderer().setVisible(true);
        graph.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);
        changeParametersButton = (Button) view.findViewById(R.id.confirm_parameters);
        paramA = (EditText) view.findViewById(R.id.parameter_a);
        paramB = (EditText) view.findViewById(R.id.parameter_b);
        //----------------------------------------------------------------------
        updateChart();
    }


    public void changeGraph(View view) {
        dailyGraphButton = (Button) view.findViewById(R.id.daily_button);
        monthlyGraphButton = (Button) view.findViewById(R.id.monthly_button);
        monthlyCostPerConsumption = (Button) view.findViewById(R.id.monthlyCostPerConsumption);
        dailyButtonCheck = (ImageView) view.findViewById(R.id.daily_button_check);
        monthlyButtonCheck = (ImageView) view.findViewById(R.id.monthly_button_check);
        monthlyCostPerConsumptionCheck = (ImageView) view.findViewById(R.id.monthlyCostPerConsumptionCheck);
        change_view_layout = (RelativeLayout) view.findViewById(R.id.change_view_menu);
        dateIndicator = (TextView) view.findViewById(R.id.costOfDataConsumption);
        costFragmentCircle = (ImageView) view.findViewById(R.id.costFragmentCircle);
        yAxis = (TextView)view.findViewById(R.id.costDollar);
        dailyButtonCheck.setVisibility(View.INVISIBLE);
        monthlyButtonCheck.setVisibility(View.INVISIBLE);
        monthlyCostPerConsumptionCheck.setVisibility(View.INVISIBLE);
        imageButton = (ImageButton) view.findViewById(R.id.changeViewButton);
        imageButton.setOnClickListener(new View.OnClickListener() {
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
        dailyGraphButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Animation slideDown = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.slide_down);
                change_view_layout.startAnimation(slideDown);
                change_view_layout.setVisibility(View.GONE);
                dailyButtonCheck.setVisibility(View.VISIBLE);
                monthlyButtonCheck.setVisibility(View.INVISIBLE);
                monthlyCostPerConsumptionCheck.setVisibility(View.INVISIBLE);
                totalConsumption.setVisibility(View.VISIBLE);
                costFragmentCircle.setVisibility(View.VISIBLE);
                yAxis.setVisibility(View.INVISIBLE);
                dailyButtonflag = true;
                monthlyButtonflag = false;
                monthlyCostFlag = false;
                updateChart();
            }

        });
        monthlyGraphButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Animation slideDown = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.slide_down);
                change_view_layout.startAnimation(slideDown);
                change_view_layout.setVisibility(View.GONE);
                monthlyButtonCheck.setVisibility(View.VISIBLE);
                dailyButtonCheck.setVisibility(View.INVISIBLE);
                monthlyCostPerConsumptionCheck.setVisibility(View.INVISIBLE);
                totalConsumption.setVisibility(View.VISIBLE);
                costFragmentCircle.setVisibility(View.VISIBLE);
                yAxis.setVisibility(View.INVISIBLE);
                dailyButtonflag = false;
                monthlyButtonflag = true;
                monthlyCostFlag = false;
                updateChart();
            }

        });
        monthlyCostPerConsumption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation slideDown = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.slide_down);
                change_view_layout.startAnimation(slideDown);
                change_view_layout.setVisibility(View.GONE);
                monthlyButtonCheck.setVisibility(View.INVISIBLE);
                dailyButtonCheck.setVisibility(View.INVISIBLE);
                monthlyCostPerConsumptionCheck.setVisibility(View.VISIBLE);
                totalConsumption.setVisibility(View.INVISIBLE);
                costFragmentCircle.setVisibility(View.INVISIBLE);
                yAxis.setVisibility(View.VISIBLE);
                dailyButtonflag = false;
                monthlyButtonflag = false;
                monthlyCostFlag = true;
                updateChart();
            }

        });
        imageButton = (ImageButton) view.findViewById(R.id.rightButton);
        imageButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                cal = Calendar.getInstance();
                cal.setTime(selectedDisplayDate);
                if (dailyButtonflag) {
                    cal.add(Calendar.DAY_OF_MONTH, 1);
                } else if (monthlyButtonflag) {
                    cal.add(Calendar.MONTH, 1);
                } else if (monthlyCostFlag) {
                    cal.add(Calendar.MONTH, 1);
                }
                selectedDisplayDate = cal.getTime();
                String timeStamp = new SimpleDateFormat("dd/MM/yyyy").format(selectedDisplayDate);
                dateIndicator.setText(timeStamp);
                updateChart();
            }

        });
        imageButton1 = (ImageButton) view.findViewById(R.id.leftButton);
        imageButton1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                cal = Calendar.getInstance();
                cal.setTime(selectedDisplayDate);
                if (dailyButtonflag) {
                    cal.add(Calendar.DAY_OF_MONTH, -1);
                } else if (monthlyButtonflag) {
                    cal.add(Calendar.MONTH, -1);
                } else if (monthlyCostFlag) {
                    cal.add(Calendar.MONTH, -1);
                }
                selectedDisplayDate = cal.getTime();
                String timeStamp = new SimpleDateFormat("dd/MM/yyyy").format(selectedDisplayDate);
                dateIndicator.setText(timeStamp);
                updateChart();
            }

        });

        changeParametersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                param_a = Integer.valueOf(paramA.getText().toString());
                param_b = Integer.valueOf(paramB.getText().toString());
                updateChart();
            }

        });


    }

    private void updateChart() {
        // GridLabelRenderer gridLabel = graph.getGridLabelRenderer();
        StaticLabelsFormatter staticLabelsFormatter = new StaticLabelsFormatter(graph);
//---------Dotted Line Boundary---------------

        Viewport viewport = graph.getViewport();
        viewport.setXAxisBoundsManual(true);
        viewport.setMinX(1);
        graph.getViewport().setScalable(true);
        graph.getViewport().setScrollable(true);
        int offsetTime = 0;
        double boundaryLine = 0;

        LineGraphSeries<DataPoint> linearNetwork = new LineGraphSeries<DataPoint>();
        LineGraphSeries<DataPoint> linearWifi = new LineGraphSeries<DataPoint>();
        LineGraphSeries<DataPoint> logarithmicNetwork = new LineGraphSeries<DataPoint>();
        LineGraphSeries<DataPoint> logarithmicWifi = new LineGraphSeries<DataPoint>();
        LineGraphSeries<DataPoint> squareRootNetwork = new LineGraphSeries<DataPoint>();
        LineGraphSeries<DataPoint> squareRootWifi = new LineGraphSeries<DataPoint>();
        LineGraphSeries<DataPoint> flatRateWifi = new LineGraphSeries<DataPoint>();
        LineGraphSeries<DataPoint> flatRateNet = new LineGraphSeries<DataPoint>();
        LineGraphSeries<DataPoint> consumptionBoundary = new LineGraphSeries<DataPoint>();
        LineGraphSeries<DataPoint> networkConsumption = new LineGraphSeries<DataPoint>();
        LineGraphSeries<DataPoint> wifiConsumption = new LineGraphSeries<DataPoint>();


        graph.removeAllSeries();
        //---- int selectedDropdownOption = spinner.getSelectedItemPosition();
        double[][] measuredData = new double[2][];
        if (dailyButtonflag) {
            boundaryLine = 1;
            displayPointsNum = Constants.DATA_PER_DAY;
            measuredData = TrafficInfoManager.getDataPerDay(getActivity(), selectedDisplayDate);
            graph.getGridLabelRenderer().setHorizontalAxisTitle("Hours");
            // gridLabel.setHorizontalAxisTitle("Hours");
            graph.setTitle("Usage Per Day");
            // set manual y bounds to have nice steps
            GridLabelRenderer gridLabel = graph.getGridLabelRenderer();
            gridLabel.setHorizontalAxisTitle("Hours");
            // set manual X bounds
            graph.getViewport().setXAxisBoundsManual(true);
            graph.getViewport().setMinX(0);
            graph.getViewport().setMaxX(24);
            // set manual Y bounds
            graph.getViewport().setYAxisBoundsManual(true);
            graph.getViewport().setMinY(scaleAxis(measuredData)[0]);
            graph.getViewport().setMaxY(scaleAxis(measuredData)[1]);
            changeParametersButton.setVisibility(View.GONE);
            paramA.setVisibility(View.GONE);
            paramB.setVisibility(View.GONE);
            graph.removeSeries(consumptionBoundary);
            graph.getLegendRenderer().resetStyles();
            graph.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);
            //  checkConsumptionPerDay(measuredData);
            // staticLabelsFormatter.setHorizontalLabels(new String[]{"00", "02", "04", "06", "08", "10", "12", "14", "16", "18", "20", "22"});
        } else if (monthlyButtonflag) {
            displayPointsNum = Constants.DATA_PER_MONTH;
            measuredData = TrafficInfoManager.getDataPerMonth(getActivity(), selectedDisplayDate);
            graph.setTitle("Usage Per Month");
            graph.getGridLabelRenderer().setHorizontalAxisTitle("Days");
            graph.getViewport().setXAxisBoundsManual(false);
            graph.getViewport().setXAxisBoundsManual(true);
            graph.getViewport().setMinX(1);
            graph.getViewport().setMaxX(31);
            // set manual Y bounds
            graph.getViewport().setYAxisBoundsManual(true);
            graph.getViewport().setMinY(scaleAxis(measuredData)[0]);
            graph.getViewport().setMaxY(flatRateUsageLimit + flatRateUsageLimit * 0.01);
            changeParametersButton.setVisibility(View.GONE);
            paramA.setVisibility(View.GONE);
            paramB.setVisibility(View.GONE);
            consumptionBoundary.setTitle("Flat Rate Usage Limit");

        } else if (monthlyCostFlag) {
            boundaryLine = 1;
            displayPointsNum = Constants.DATA_PER_MONTH;
            measuredData = TrafficInfoManager.getDataPerMonth(getActivity(), selectedDisplayDate);
            graph.setTitle("Cost of Usage Per Month");
            // set manual y bounds to have nice steps
            GridLabelRenderer gridLabel = graph.getGridLabelRenderer();
            gridLabel.setHorizontalAxisTitle("Days");
            graph.getViewport().setXAxisBoundsManual(false);
            graph.getViewport().setXAxisBoundsManual(true);
            graph.getViewport().setMinX(1);
            graph.getViewport().setMaxX(31);
            // set manual Y bounds
            graph.getViewport().setYAxisBoundsManual(true);
            graph.getViewport().setMinY(-1);
            graph.getViewport().setMaxY(20);
            changeParametersButton.setVisibility(View.VISIBLE);
            paramA.setVisibility(View.VISIBLE);
            paramB.setVisibility(View.VISIBLE);
            graph.getLegendRenderer().resetStyles();
            graph.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);
        }


        consumptionBoundary = new LineGraphSeries<DataPoint>(generateBoundary());
        for (int i = 0; i < displayPointsNum; i++) {
            if (i != 0) {
                measuredData[0][i] = measuredData[0][i] + measuredData[0][i - 1];
                measuredData[1][i] = measuredData[1][i] + measuredData[1][i - 1];
                // } else {
                // measuredData[0][i] = 0;
                // measuredData[1][i] = 0;
                //}
            }
        }

        for (int j = 0; j < displayPointsNum; j++) {
            if (!monthlyCostFlag) {
                wifiConsumption.appendData(new DataPoint(j + 1, measuredData[1][j]), true, displayPointsNum);
                networkConsumption.appendData(new DataPoint(j + 1, measuredData[0][j]), true, displayPointsNum);
            }else if(monthlyCostFlag) {
                linearWifi.appendData(new DataPoint(j, param_a * measuredData[1][j]/param_b), true, displayPointsNum);
                logarithmicWifi.appendData(new DataPoint(j+1, param_a*Math.log(measuredData[1][j]/param_b)/Math.log(2)), true, displayPointsNum);
                squareRootWifi.appendData(new DataPoint(j + 1, param_a * Math.sqrt(measuredData[1][j]/param_b)), true, displayPointsNum);
            }
        }

        //----------------------------------------------

        if(monthlyCostFlag) {
            graph.removeAllSeries();
            graph.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter);
            //----------DOTTED LINE------------------------------
            linearWifi.setTitle("Linear Cost");
            logarithmicWifi.setTitle("Logarithmic Cost");
            squareRootWifi.setTitle("Square Root Cost");
            linearWifi.setColor(Color.RED);
            logarithmicWifi.setColor(Color.MAGENTA);
            squareRootWifi.setColor(Color.BLUE);
            linearWifi.setOnDataPointTapListener(new OnDataPointTapListener() {
                @Override
                public void onTap(Series series, DataPointInterface dataPoint) {
                    Toast.makeText(getActivity(), "Linear metric: day " + (int)dataPoint.getX() + " / " + (int)dataPoint.getY() + " euro", Toast.LENGTH_SHORT).show();
                }
            });
            logarithmicWifi.setOnDataPointTapListener(new OnDataPointTapListener() {
                @Override
                public void onTap(Series series, DataPointInterface dataPoint) {
                    Toast.makeText(getActivity(), "Logarithmic metric: day " + (int)dataPoint.getX() + " / " + (int)dataPoint.getY() + " euro", Toast.LENGTH_SHORT).show();
                }
            });
            squareRootWifi.setOnDataPointTapListener(new OnDataPointTapListener() {
                @Override
                public void onTap(Series series, DataPointInterface dataPoint) {
                    Toast.makeText(getActivity(), "Square Root metric: day " + (int)dataPoint.getX() + " / " + (int)dataPoint.getY() + " euro", Toast.LENGTH_SHORT).show();
                }
            });
            //--------------------------------------
            graph.addSeries(linearWifi);
            graph.addSeries(logarithmicWifi);
            graph.addSeries(squareRootWifi);
            updateTotal(measuredData);
        }
        else {
            graph.removeAllSeries();
            graph.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter);
            //----------DOTTED LINE------------------------------
            networkConsumption.setTitle("Download");
            wifiConsumption.setTitle("Upload");
            wifiConsumption.setColor(Color.RED);
            networkConsumption.setColor(Color.BLUE);
            consumptionBoundary.setColor(Color.MAGENTA);
            networkConsumption.setOnDataPointTapListener(new OnDataPointTapListener() {
                @Override
                public void onTap(Series series, DataPointInterface dataPoint) {
                    Toast.makeText(getActivity(), "At " + (int)dataPoint.getX()+ ":00, " + dataPoint.getY() + " MB downloaded", Toast.LENGTH_SHORT).show();
                }
            });
            wifiConsumption.setOnDataPointTapListener(new OnDataPointTapListener() {
                @Override
                public void onTap(Series series, DataPointInterface dataPoint) {
                    Toast.makeText(getActivity(), "At " + (int)dataPoint.getX()+ ":00, " + dataPoint.getY() + " MB uploaded", Toast.LENGTH_SHORT).show();
                }
            });


            //--------------------------------------
            graph.addSeries(wifiConsumption);
            graph.addSeries(networkConsumption);
            if(monthlyButtonflag){
                graph.getLegendRenderer().resetStyles();
                graph.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);
                consumptionBoundary.setTitle("Flat Rate Usage Limit");
                graph.addSeries(consumptionBoundary);
            }
            updateTotal(measuredData);
        }

    }

    private void updateTotal(double[][] measuredData) {
        totalConsumption = (TextView)view.findViewById(R.id.totalConsumption);
        double sum = 0,sum1=0;
        int len = measuredData[0].length-1;
        sum = measuredData[0][len];
        sum1 = measuredData[1][len];
        if(!monthlyCostFlag) {
            totalConsumption.setText("Download:" + Integer.toString((int) sum)
                    + "MB" + "\nUpload:"
                    + Integer.toString((int) sum1) + "MB");
        }
    }

    private DataPoint[] generateBoundary() {
        int count=0;
        double boundary = 0;
        if(monthlyButtonflag) {
            count = 31;
            boundary = flatRateUsageLimit;
        }

        DataPoint[] values = new DataPoint[count];
        for (int i=0; i<count; i++) {

            double x = i;
            double y = boundary;
            DataPoint v = new DataPoint(x,Math.abs(y));
            values[i] = v;
        }

        return values;
    }
    private double[] scaleAxis(double[][] data){
        double sum = 0,sum1=0;
        double min=0,min1=0;
        double[] num={0,0};
        for(int j=0;j<data[1].length;j++){
            sum += data[0][j];
            sum1 += data[1][j];
            if(min>data[0][j]){
                min = data[0][j];
            }
            if(min1>data[1][j]){
                min1 = data[1][j];
            }
        }
        if(sum>sum1){
            num[1] = sum;
        }
        else{
            num[1] = sum1;
        }
        if(min>min1){
            num[0] = min1;
        }
        else{
            num[0] = min;
        }
        return num;
    }


}
