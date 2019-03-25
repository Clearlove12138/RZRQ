package com.example.a14422.demo1.util;

import android.util.Log;

import com.example.a14422.demo1.db.DBHelper;
import com.example.a14422.demo1.entity.ColumnEntity;
import com.example.a14422.demo1.entity.DataEntity;
import com.example.a14422.demo1.entity.ScodeEntity;

import org.litepal.LitePal;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NumberUtil {
    public static void CreateSQLite(int start, int end){
        Log.e("数据库写入", "文件不存在或数据为空，开始写入");
        List<ColumnEntity> columnList = DBHelper.getColumnsSet("rzrq_down");
        ResultSet resultSet = DBHelper.getResultSet(start,end);
        LitePal.getDatabase();
        try{
            while (resultSet.next()) {
                DataEntity dataEntity = new DataEntity();
                for (int i = 0; i < columnList.size(); i++) {
                    String name = columnList.get(i).getColumn();
                    name = name.replaceFirst(name.substring(0, 1), name.substring(0, 1).toUpperCase());
                    Method m = dataEntity.getClass().getMethod("set" + name, String.class);
                    m.invoke(dataEntity, resultSet.getString(i + 1));
                }
                dataEntity.save();
            }
            Log.e("数据库写入", "完成");
            Log.e("数据库字段写入","开始,"+columnList.size());
            for (ColumnEntity column:columnList){
                ColumnEntity columnEntity = new ColumnEntity();
                columnEntity.setColumn(column.getColumn());
                columnEntity.setComment(column.getComment());
                columnEntity.save();
            }
            Log.e("数据库字段写入", "完成");
        }catch (SQLException e){
            e.printStackTrace();
        }catch (NoSuchMethodException e){
            e.printStackTrace();
        }catch (IllegalAccessException e){
            e.printStackTrace();
        }catch (InvocationTargetException e){
            e.printStackTrace();
        }finally {
            try{
                if (resultSet != null){
                    resultSet.close();
                }
                /*if (conn!=null){
                    conn.close();
                }*/
            }catch (SQLException e){
                e.printStackTrace();
            }
        }
    }

    public static void CreateSQLStock(){
        Log.e("Stock数据库写入", "文件不存在或数据为空，开始写入");
        LitePal.getDatabase();
        List<ScodeEntity> scodeEntities = DBHelper.getCodeSet();
        for (ScodeEntity entity: scodeEntities){
            ScodeEntity scode = new ScodeEntity();
            scode.setScode(entity.getScode());
            scode.setSname(entity.getSname());
            scode.save();
        }
        Log.e("Stock数据库写入","Stock数据库写入完成");
    }

    public static String formatNum(String num) {
        if (!isNumeric(num)){
            return DateUtil.formatDate(num);
        }

        final int scale = 2;

        StringBuffer sb = new StringBuffer();

        BigDecimal b0 = new BigDecimal("10");
        BigDecimal b1 = new BigDecimal("10000");
        BigDecimal b2 = new BigDecimal("100000000");
        BigDecimal b3 = new BigDecimal(num);
        BigDecimal b4 = b3.abs();

        String formatNumStr = "";
        String nuit = "";

        // 以千为单位处理
        if (b4.compareTo(b0) == -1){
            DecimalFormat df = new DecimalFormat();
            String style = "##.##%";
            df.applyPattern(style);
            sb.append(df.format(Double.valueOf(num)));
        }else if (b4.compareTo(b1) == -1) {
            b3 = b3.setScale(scale,RoundingMode.HALF_UP);
            sb.append(b3.toString());
        } else if ((b4.compareTo(b1) == 0 && b4.compareTo(b1) == 1)
                || b4.compareTo(b2) == -1) {
            formatNumStr = b3.divide(b1,scale, RoundingMode.HALF_UP).toString();
            nuit = "万";
        } else if (b4.compareTo(b2) == 0 || b4.compareTo(b2) == 1) {
            formatNumStr = b3.divide(b2,scale, RoundingMode.HALF_UP).toString();
            nuit = "亿";
        }
        /*if (!"".equals(formatNumStr)) {
            int i = formatNumStr.indexOf(".");
            if (i == -1) {
                sb.append(formatNumStr).append(nuit);
            } else {
                i = i + 1;
                String v = formatNumStr.substring(i, i + 1);
                if (!v.equals("0")) {
                    sb.append(formatNumStr.substring(0, i + 1)).append(nuit);
                } else {
                    sb.append(formatNumStr.substring(0, i - 1)).append(nuit);
                }
            }
        }*/
        sb.append(formatNumStr).append(nuit);
        if (sb.length() == 0)
            return "0";
        return sb.toString();
    }

    private static boolean isNumeric(String str){
        Pattern pattern = Pattern.compile("-?[0-9]+.?[0-9]+");
        Matcher isNum = pattern.matcher(str);
        if( !isNum.matches()){
            return false;
        }
        return true;
    }

   /* public static void main(String[] args) {
        System.out.println(DataUtil.formatNum("12.345678"));
    }*/

}
