package com.example.a14422.demo1.fragment;


import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.a14422.demo1.R;
import com.example.a14422.demo1.db.DBHelper;
import com.example.a14422.demo1.entity.DataEntity;
import com.example.a14422.demo1.entity.StockItemEntity;
import com.example.a14422.demo1.util.NumberUtil;
import com.example.a14422.demo1.util.DateUtil;
import com.example.a14422.demo1.util.DynamicLineChartManager;
import com.example.a14422.demo1.view.LineChartMarkView;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChartFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChartFragment extends Fragment implements OnChartValueSelectedListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private int mCurrentDialogStyle = com.qmuiteam.qmui.R.style.QMUI_Dialog;

    private String scode = "";
    private String sname = "";

    private DynamicLineChartManager dynamicLineChartManager2;
    private List<Integer> list = new ArrayList<>(); //数据集合
    private List<String> names = new ArrayList<>(); //折线名字集合
    private List<Integer> colour = new ArrayList<>();//折线颜色集合

    private View view;

    private LineChart chart;
    private XAxis xAxis;
    private YAxis yAxis;
    ArrayList<String> dateList;

    ArrayList<ArrayList<String>> totalList = new ArrayList<>();
    ArrayList<String> value0List;
    ArrayList<String> value1List;

    Handler handler = new Handler();

    private final int[] colors = new int[]{
            ColorTemplate.VORDIPLOM_COLORS[0],
            ColorTemplate.VORDIPLOM_COLORS[1],
            ColorTemplate.VORDIPLOM_COLORS[2]
    };

    public ChartFragment() {
        // Required empty public constructor
    }

    public static ChartFragment newInstance(String scode, String sname) {
        ChartFragment fragment = new ChartFragment();
        fragment.scode = scode;
        fragment.sname = sname;
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, scode);
        args.putString(ARG_PARAM2, sname);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();
//        showChart();//绘制曲线图
//        showData(200);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_chart, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        showData(200);
    }

    private void initChart() {
        chart = view.findViewById(R.id.frg_lineChart);
        chart.setOnChartValueSelectedListener(this);

        chart.setDrawGridBackground(false);
        chart.getDescription().setEnabled(false);
        chart.setDrawBorders(false);

        chart.getAxisRight().setEnabled(false);
        chart.getAxisRight().setDrawAxisLine(false);
        chart.getAxisRight().setDrawGridLines(false);
        chart.getXAxis().setDrawAxisLine(false);
        chart.getXAxis().setDrawGridLines(false);

        // enable touch gestures
        chart.setTouchEnabled(true);

        // enable scaling and dragging
        chart.setDragEnabled(false);
        chart.setScaleEnabled(false);

        // if disabled, scaling can be done on x- and y-axis separately
        chart.setPinchZoom(false);

        chart.animateXY(1500, 1500);

        xAxis = chart.getXAxis();
        xAxis.setLabelCount(dateList.size());
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                String date = dateList.get((int) value % dateList.size());
                return DateUtil.formatDate(date);
            }
        });
        xAxis.setEnabled(false);

        Legend l = chart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(true);

        setMarkerView();
    }

    private void setData(int lines, int progress) {
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        for (int z = 0; z < lines; z++) {
            ArrayList<Entry> values = new ArrayList<>();
            for (int i = 0; i < progress; i++) {
                String val = totalList.get(z).get(i);
                values.add(new Entry(i, Float.valueOf(val), getResources().getDrawable(R.drawable.pic)));
            }

            LineDataSet d = new LineDataSet(values, "DataSet " + (z + 1));
            d.setLineWidth(2.5f);
            d.setCircleRadius(4f);

            int color = colors[z % colors.length];
            d.setColor(color);
            d.setCircleColor(color);
            d.setDrawIcons(true);
            d.setMode(LineDataSet.Mode.CUBIC_BEZIER);

            d.setDrawIcons(true);
            d.setDrawValues(false);
            d.setDrawCircleHole(false);
            d.enableDashedHighlightLine(10f, 5f, 0f);
            d.setHighlightEnabled(true);

            dataSets.add(d);
        }
        // make the first DataSet dashed
        ((LineDataSet) dataSets.get(0)).enableDashedLine(10, 10, 0);
        ((LineDataSet) dataSets.get(0)).setColors(ColorTemplate.VORDIPLOM_COLORS);
        ((LineDataSet) dataSets.get(0)).setCircleColors(ColorTemplate.VORDIPLOM_COLORS);

        Drawable drawable = ContextCompat.getDrawable(getActivity(), R.drawable.fade_red);
        ((LineDataSet) dataSets.get(0)).setFillDrawable(drawable);
        ((LineDataSet) dataSets.get(0)).setDrawFilled(false);
//        ((LineDataSet) dataSets.get(0)).enableDashedHighlightLine(10f, 5f, 0f);

        LineData data = new LineData(dataSets);
        chart.setData(data);
    }

    private void showData(int limit) {
        totalList.clear();
        dateList = new ArrayList<>();
        value0List = new ArrayList<>();
        value1List = new ArrayList<>();
        if (scode.length() == 0 || scode.equals("")) {
            int totalCount = LitePal.findAll(DataEntity.class).size();
            if (limit > totalCount) {
                limit = totalCount;
            }
            List<DataEntity> findList = LitePal.select("tdate", "rzrqye", "rzye", "rqye", "close").order("tdate asc").limit(limit).offset(totalCount - limit).find(DataEntity.class);
            int findCount = findList.size();
            Log.e("loadAxisData", String.valueOf(findCount) + "/" + String.valueOf(totalCount));
            for (DataEntity entity : findList) {
                String tDate = entity.getTdate();
                dateList.add(tDate);
                value0List.add(NumberUtil.formatNum(entity.getRzrqye()).replaceAll("[^0-9]", ""));
                value1List.add(NumberUtil.formatNum(entity.getClose()).replaceAll("[^0-9]", ""));
            }
            totalList.add(value0List);
            totalList.add(value1List);

            initChart();
            setData(totalList.size(), dateList.size());
            chart.invalidate();

        } else {
            final int finalLimit = limit;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    if (DBHelper.checkIsTableExist(scode)) {
                        List<StockItemEntity> entities = DBHelper.getStockItemSet(scode, finalLimit);
                        for (StockItemEntity entity : entities) {
                            dateList.add(entity.getTdate());
                            value0List.add(NumberUtil.formatNum(entity.getRzrqye()).replaceAll("[^0-9]", ""));
                            value1List.add(NumberUtil.formatNum(entity.getClose()).replaceAll("[^0-9]", ""));
                        }
                        totalList.add(value0List);
                        totalList.add(value1List);
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                initChart();
                                setData(totalList.size(), dateList.size());
                                chart.invalidate();
                            }
                        });
                    }else {
//                        new AlertFragment().show(getFragmentManager(),"Error");
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                showMessagePositiveDialog();
                            }
                        });
                    }
                }
            }).start();
        }

    }


    private void showMessagePositiveDialog() {
        new QMUIDialog.MessageDialogBuilder(getContext())
                .setTitle("Error " + sname)
                .setMessage("目标数据不存在")
                .addAction("确定", new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        dialog.dismiss();
                        getActivity().finish();
                    }
                })
                .addAction("取消", new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        dialog.dismiss();
                        getActivity().finish();
                    }
                })
                .create(mCurrentDialogStyle).show();
    }

    /**
     * 设置 可以显示X Y 轴自定义值的 MarkerView
     */
    public void setMarkerView() {
        LineChartMarkView mv = new LineChartMarkView(getContext(), xAxis.getValueFormatter());
        mv.setChartView(chart);
        chart.setMarker(mv);
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        Log.e("VAL SELECTED",
                "Value: " + e.getY() + ", xIndex: " + e.getX()
                        + ", DataSet index: " + h.getDataSetIndex());
    }

    @Override
    public void onNothingSelected() {

    }

    @Override
    public void onStop() {
        Log.e("onStop", "Chart碎片停止");
        super.onStop();
    }

    @Override
    public void onDestroy() {
        Log.e("onDestory", "Chart碎片销毁");
        super.onDestroy();
    }

    public void showChart() {
        LineChart mChart2 = view.findViewById(R.id.frg_lineChart);
        //折线名字
        names.add("温度");
        names.add("压强");
        names.add("其他");
        //折线颜色
        colour.add(Color.CYAN);
        colour.add(Color.GREEN);
        colour.add(Color.BLUE);

        dynamicLineChartManager2 = new DynamicLineChartManager(mChart2, names, colour);

        dynamicLineChartManager2.setYAxis(100, 0, 10);

        dynamicLineChartManager2.setDescription("Five");

        for (int i = 0; i < 5; i++) {
            list.add((int) (Math.random() * 50) + 10);
            list.add((int) (Math.random() * 80) + 10);
            list.add((int) (Math.random() * 100));
            dynamicLineChartManager2.addEntry(list);
            list.clear();
        }
    }
}
