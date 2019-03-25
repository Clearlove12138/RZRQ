package com.example.a14422.demo1.fragment;


import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;

import com.example.a14422.demo1.R;
import com.example.a14422.demo1.db.DBHelper;
import com.example.a14422.demo1.entity.ColumnEntity;
import com.example.a14422.demo1.entity.DataEntity;
import com.example.a14422.demo1.util.NumberUtil;
import com.rmondjone.locktableview.DisplayUtil;
import com.rmondjone.locktableview.LockTableView;
import com.rmondjone.xrecyclerview.ProgressStyle;
import com.rmondjone.xrecyclerview.XRecyclerView;

import org.litepal.LitePal;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TableFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TableFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private View view;

    private ArrayList<ArrayList<String>> mTableDatas;
    private LockTableView mLockTableView;
    private ViewGroup mContentView;
    ArrayList<String> mfristData;
    private static Handler handler = new Handler();

    private final static String filename = "rzrq_data";
    RandomAccessFile rf;
    private long filePointer = 0;

    private final static int LIMIT = 20;
    private  int offset = 0;
    private int totalCount = 0;

    public TableFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TableFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TableFragment newInstance(String param1, String param2) {
        TableFragment fragment = new TableFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
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
    public void onStart() {
        super.onStart();
//        mContentView = view.findViewById(R.id.frg_Locktable);
//        initDisplayOpinion();
//        mTableDatas = new ArrayList<>();
//        initHeaderDatas();
//        initLockTableView();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_table, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mContentView = view.findViewById(R.id.frg_Locktable);
        initDisplayOpinion();
        mTableDatas = new ArrayList<>();
        initHeaderDatas();
        initLockTableView();
    }

    private void initDisplayOpinion() {
        DisplayMetrics dm = getResources().getDisplayMetrics();
        DisplayUtil.density = dm.density;
        DisplayUtil.densityDPI = dm.densityDpi;
        DisplayUtil.screenWidthPx = dm.widthPixels;
        DisplayUtil.screenhightPx = dm.heightPixels;
        DisplayUtil.screenWidthDip = DisplayUtil.px2dip(getActivity().getApplicationContext(), dm.widthPixels);
        DisplayUtil.screenHightDip = DisplayUtil.px2dip(getActivity().getApplicationContext(), dm.heightPixels);
    }

    private void initHeaderDatas() {
        mTableDatas.clear();
        //表头
        mfristData = new ArrayList<>();
        List<ColumnEntity> columnEntities = LitePal.select("comment").find(ColumnEntity.class);
        Log.e("initHeaderDatas","Column:"+columnEntities.size());
        for (ColumnEntity entity:columnEntities){
            mfristData.add(entity.getComment());
        }
        mTableDatas.add(mfristData);
    }

    private void setLockTableView(ArrayList<ArrayList<String>> mTableDatas) {
        mLockTableView = new LockTableView(getActivity(), mContentView, mTableDatas);
        Log.e("表格加载开始", "当前线程：" + Thread.currentThread());
        mLockTableView.setLockFristColumn(true) //是否锁定第一列
                .setLockFristRow(true) //是否锁定第一行
                .setMaxColumnWidth(500) //列最大宽度
                .setMinColumnWidth(60) //列最小宽度
//                .setColumnWidth(1,30) //设置指定列文本宽度
//                .setColumnWidth(2,20)
                .setMinRowHeight(30)//行最小高度
                .setMaxRowHeight(50)//行最大高度
                .setTextViewSize(16) //单元格字体大小
                .setFristRowBackGroudColor(R.color.deep_gray)//表头背景色
                .setTableHeadTextColor(R.color.comment_text)//表头字体颜色
                .setTableContentTextColor(R.color.border_color)//单元格字体颜色
                .setCellPadding(8)//设置单元格内边距(dp)
                .setNullableString("N/A") //空值替换值
                .setTableViewListener(new LockTableView.OnTableViewListener() {
                    @Override
                    public void onTableViewScrollChange(int x, int y) {
//                        Log.e("滚动值","["+x+"]"+"["+y+"]");
                    }
                })
                .setTableViewRangeListener(new LockTableView.OnTableViewRangeListener() {
                    @Override
                    public void onLeft(HorizontalScrollView view) {
//                        Log.e("滚动边界","滚动到最左边");
                    }

                    @Override
                    public void onRight(HorizontalScrollView view) {
//                        Log.e("滚动边界","滚动到最右边");
                    }
                })//设置横向滚动边界监听
                .setOnLoadingListener(new LockTableView.OnLoadingListener() {
                    @Override
                    public void onRefresh(final XRecyclerView mXRecyclerView, final ArrayList<ArrayList<String>> mTableDatas) {
                        Log.e("refresh", "正在刷新");
                        try {
                            mTableDatas.clear();
                            mTableDatas.add(mfristData);
                            File fileDB  = getContext().getDatabasePath("demo.db");
                            if (fileDB.exists()){
                                if(LitePal.deleteAll(DataEntity.class) == totalCount){
                                    Log.e("ReFresh","数据库数据已删除");
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            offset = 0;
                                            NumberUtil.CreateSQLite(0,400);
                                            Log.e("ReFresh","数据库数据创建完成");
                                            loadRzrqDataFromSQLite(LIMIT,offset);
                                            handler.post(new Runnable() {
                                                @Override
                                                public void run() {
                                                    mLockTableView.setTableDatas(mTableDatas);
                                                    mXRecyclerView.refreshComplete();
                                                    return;
                                                }
                                            });
                                        }
                                    }).start();
                                }
                            }
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onLoadMore(final XRecyclerView mXRecyclerView, final ArrayList<ArrayList<String>> mTableDatas) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
//                                final boolean isLoadMore = loadRzrqDataFromFile();
                                offset += LIMIT;
                                if (offset + LIMIT > totalCount){
                                    loadRzrqDataFromSQLite(totalCount - offset,offset);
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            mLockTableView.setTableDatas(mTableDatas);
                                            mXRecyclerView.loadMoreComplete();
                                            mXRecyclerView.setNoMore(true);
                                            Log.e("LoadMore","无更多数据");
                                        }
                                    });
                                }else {
                                    loadRzrqDataFromSQLite(LIMIT,offset);
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            mLockTableView.setTableDatas(mTableDatas);
                                            mXRecyclerView.loadMoreComplete();
                                            Log.e("loadMore", "正在加载更多数据");
                                        }
                                    });
                                }
                            }

                        }).start();
                    }
                })
                .setOnItemClickListenter(new LockTableView.OnItemClickListenter() {
                    @Override
                    public void onItemClick(View item, int position) {
                        Log.e("点击事件", position + "");
                    }
                })
                .setOnItemLongClickListenter(new LockTableView.OnItemLongClickListenter() {
                    @Override
                    public void onItemLongClick(View item, int position) {
                        Log.e("长按事件", position + "");
                    }
                })
                .setOnItemSeletor(R.color.green)//设置Item被选中颜色
                .show(); //显示表格,此方法必须调用
        mLockTableView.getTableScrollView().setPullRefreshEnabled(true);
        mLockTableView.getTableScrollView().setLoadingMoreEnabled(true);
        mLockTableView.getTableScrollView().setRefreshProgressStyle(ProgressStyle.SquareSpin);
    }

    private void initLockTableView() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                loadRzrqDataFromSQLite(LIMIT,offset);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            setLockTableView(mTableDatas);
                        }
                    });
            }
        }).start();
    }

    private void writeRzRqToFile() {
        FileOutputStream out = null;
        BufferedWriter writer = null;
        ResultSet resultSet = DBHelper.getResultSet(0,20);
        if (resultSet != null) {
            try {
                ResultSetMetaData rsm = resultSet.getMetaData();
                int rowCount = rsm.getColumnCount();
                out = getContext().openFileOutput(filename, Context.MODE_PRIVATE);
                writer = new BufferedWriter(new OutputStreamWriter(out));
                while (resultSet.next()) {
                    StringBuffer stringBuffer = new StringBuffer();
                    for (int i = 0; i < rowCount; i++) {
                        stringBuffer.append(resultSet.getString(i + 1) + " ");
                    }
                    writer.write(stringBuffer.toString());
                    writer.newLine();
                }
                Log.e("文件写入", "完成");
            } catch (IOException e) {
                e.printStackTrace();
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (writer != null) {
                        writer.close();
                    }
                    if (out != null) {
                        out.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        /*try {
            if ( DBHelper.getConnect() != null) {
                DBHelper.getConnect().close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }*/
    }

    private boolean loadRzrqDataFromFile() {
        Log.e("loadRzrqDataFromFile", "文件加载数据");
        FileInputStream in = null;
        BufferedReader reader = null;
        try {
            if (rf == null) {
                rf = new RandomAccessFile(getContext().getFileStreamPath(filename), "r");
            }
            rf.seek(filePointer);
            for (int i = 0; i < 3; i++) {
                String line = rf.readLine();
                if (!TextUtils.isEmpty(line)) {
                    String[] lineItem = line.split(" ");
                    ArrayList<String> dataList = new ArrayList<>();
                    for (int j = 0; j < lineItem.length; j++) {
                        dataList.add(lineItem[j]);
                    }
                    mTableDatas.add(dataList);
                } else {
                    rf.close();
                    rf = null;
                    filePointer = 0;
                    Log.e("loadRzrqData", "无更多数据，跳出");
                    return false;
                }
            }
            filePointer = rf.getFilePointer();
            Log.e("---filePointer---", String.valueOf(filePointer));
        } catch (FileNotFoundException e) {
            Log.e("writeRzRqToFile", "本地缓存不存在，重新下载");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    writeRzRqToFile();
                    loadRzrqDataFromFile();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            mLockTableView.setTableDatas(mTableDatas);
                        }
                    });
//                    loadRzrqData();
                }
            }).start();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    private boolean loadRzrqDataFromSQLite(int limit,int offset){
        Log.e("loadRzrqDataFromSQLite", "视图加载数据开始");
        totalCount = LitePal.findAll(DataEntity.class).size();
        Log.e("数据库数据数量",String.valueOf(totalCount));
        List<DataEntity> totalList = LitePal.limit(limit).offset(offset).find(DataEntity.class);
        List<ColumnEntity> columnEntities = LitePal.findAll(ColumnEntity.class);
        for (DataEntity entity : totalList){
            try{
                ArrayList<String> listTemp = new ArrayList<>();
                for (int i = 0;i<columnEntities.size();i++){
                    String name = columnEntities.get(i).getColumn();
                    name = name.replaceFirst(name.substring(0, 1), name.substring(0, 1).toUpperCase());
                    Method m = entity.getClass().getMethod("get"+name);
                    String value = (String)m.invoke(entity);
                    if (!name.contains("Zdf")){
                        value = NumberUtil.formatNum(value);
                    }else {
                        DecimalFormat df = new DecimalFormat();
                        df.applyPattern("#0.00");
                        value = df.format(Double.valueOf(value));
                    }
                    listTemp.add(value);
                }
                mTableDatas.add(listTemp);
            }catch (NoSuchMethodException e){
                e.printStackTrace();
            }catch (IllegalAccessException e){
                e.printStackTrace();
            }catch (InvocationTargetException e){
                e.printStackTrace();
            }
        }
        Log.e("loadRzrqDataFromSQLite", "视图加载数据完成");
        return true;
    }

    @Override
    public void onDestroy() {
        Log.e("onDestory", "Table碎片销毁");
        super.onDestroy();
    }

    @Override
    public void onStop() {
        Log.e("onStop", "Table碎片停止");
        super.onStop();
    }
}
