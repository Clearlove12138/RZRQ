package com.example.a14422.demo1.db;

import android.content.Context;
import android.util.Log;

import com.example.a14422.demo1.MyApplication;
import com.example.a14422.demo1.entity.ColumnEntity;
import com.example.a14422.demo1.entity.ScodeEntity;
import com.example.a14422.demo1.entity.StockItemEntity;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;


public class DBHelper {

    private static Connection conn = null;

    static {
        conn = initConnection(new MyApplication().getContext());
    }

    public static List<ArrayList<String>> SelectSql(Connection conn) {
        ResultSet result;
        List<ArrayList<String>> resultList = new ArrayList<>();
        try {
            Connection cn = conn;
            String sql = "select * from `rzrq_down` limit 0,100";
            Statement st = cn.createStatement();
            result = st.executeQuery(sql);
            ResultSetMetaData rsm = result.getMetaData();
            int rowCount = rsm.getColumnCount();
            while (result.next()) {
                ArrayList<String> dataList = new ArrayList<>();
                for (int i = 0; i < rowCount; i++) {
                    dataList.add(result.getString(i + 1));
                }
                resultList.add(dataList);
            }
            /*String sqlpic = "insert into `rzrq_pic` (pic) values (?)";
            PreparedStatement preparedStat = cn.prepareStatement(sqlpic);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(),R.drawable.test);
            bitmap.compress(Bitmap.CompressFormat.PNG,100,out);
            Log.e("out大小:",Integer.toString(out.toByteArray().length));
            InputStream in = new ByteArrayInputStream(out.toByteArray());
            preparedStat.setBlob(1,in);
            int count = preparedStat.executeUpdate();
            Log.e("受影响行数:",Integer.toString(count));*/
//            cn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultList;
    }

    public static ResultSet getResultSet(int start,int end){
        String sql = "select * from `rzrq_down` order by tdate desc limit ?,?";
        try {
            PreparedStatement stat =  conn.prepareStatement(sql,ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
            stat.setInt(1,start);
            stat.setInt(2,end);
            return stat.executeQuery();
        }catch (SQLException e){
            e.printStackTrace();
        }
       return null;
    }

    public static List<ColumnEntity> getColumnsSet(String tableName){
        String sql = "SELECT COLUMN_NAME,column_comment FROM information_schema.columns WHERE table_name=?";
        try {
            PreparedStatement stat =  conn.prepareStatement(sql);
            stat.setString(1,tableName);
            ResultSet resultSet = stat.executeQuery();
            List<ColumnEntity> columnList = new ArrayList<>();
            while (resultSet.next()){
                ColumnEntity columnEntity = new ColumnEntity();
                columnEntity.setColumn(resultSet.getString(1));
                columnEntity.setComment(resultSet.getString(2));
                columnList.add(columnEntity);
            }
            return columnList;
        }catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    public static List<ScodeEntity> getCodeSet(){
//        String sql = "select sname,scode from rzrq_stocklist where scode like ? or sname like ? limit 0,10";
        String sql = "select sname,scode from rzrq_stocklist";
        try{
//            PreparedStatement stat = conn.prepareStatement(sql);
            Statement stat = conn.createStatement();
            ResultSet resultSet = stat.executeQuery(sql);
            List<ScodeEntity> scodeEntities = new ArrayList<>();
            while (resultSet.next()){
                ScodeEntity scodeEntity = new ScodeEntity();
                scodeEntity.setSname(resultSet.getString(1));
                scodeEntity.setScode(resultSet.getString(2));
                scodeEntities.add(scodeEntity);
            }
            return scodeEntities;
        }catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    public static boolean checkIsTableExist(String scode){
        String tableName = "rzrq_detail_" + scode;
        String sql = "show tables like ?";
        try{
            PreparedStatement stat = conn.prepareStatement(sql);
            stat.setString(1,tableName);
            ResultSet resultSet = stat.executeQuery();
            if (resultSet.next()){
                Log.e("checkIsTableExist",tableName + "存在");
                return true;
            }else {
                Log.e("checkIsTableExist",tableName + "不存在");
                return false;
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return false;
    }

    public static List<StockItemEntity> getStockItemSet(String scode,int count){
        String tableName = "rzrq_detail_" + scode;
        int offset = 1426 - count - 1;
        int limit = count;
        String sql = "select * from "+ tableName +" order by tdate asc limit ?,?";
        try{
            PreparedStatement stat = conn.prepareStatement(sql);
            stat.setInt(1,offset);
            stat.setInt(2,limit);
            ResultSet resultSet = stat.executeQuery();
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();
            List<StockItemEntity> entities = new ArrayList<>();
            while (resultSet.next()){
                StockItemEntity itemEntity = new StockItemEntity();
                for (int i=0;i<columnCount;i++){
                    String name = metaData.getColumnName(i+1);
                    name = name.replaceFirst(name.substring(0, 1), name.substring(0, 1).toUpperCase());
                    Method m = itemEntity.getClass().getMethod("set" + name,String.class);
                    m.invoke(itemEntity,resultSet.getString(i+1));
                }
                entities.add(itemEntity);
            }
            return entities;
        }catch (SQLException e){
            e.printStackTrace();
        }catch (NoSuchMethodException e){
            e.printStackTrace();
        }catch (IllegalAccessException e){
            e.printStackTrace();
        }catch (InvocationTargetException e){
            e.printStackTrace();
        }
        return null;
    }

    private static Connection initConnection(Context context) {
        Properties prop = new Properties();
        Connection cn = null;
        try {
            String config_file = "sqlconfig.properties";
            InputStream in = context.getAssets().open(config_file);
            prop.load(in);
            String url = prop.getProperty("url");
            String username = prop.getProperty("username");
            String password = prop.getProperty("password");
            String driverClass = prop.getProperty("DriverClass");
            Class.forName(driverClass);
            cn = DriverManager.getConnection(url, username, password);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return cn;
    }

    public static Connection getConnect(){
        return conn;
    }

}


