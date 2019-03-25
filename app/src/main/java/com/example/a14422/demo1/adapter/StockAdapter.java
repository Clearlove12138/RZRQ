package com.example.a14422.demo1.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.a14422.demo1.R;
import com.example.a14422.demo1.entity.ScodeEntity;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class StockAdapter extends ArrayAdapter<ScodeEntity> {

    private int resourceId;

    public StockAdapter(@NonNull Context context, int resource, @NonNull List<ScodeEntity> objects) {
        super(context, resource, objects);
        resourceId = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ScodeEntity entity = getItem(position);
        View view;
        StockHoder stockHoder;
        if (convertView == null){
            view = LayoutInflater.from(getContext()).inflate(resourceId,parent,false);
            stockHoder = new StockHoder();
            stockHoder.stockImage = view.findViewById(R.id.stock_image);
            stockHoder.stockName = view.findViewById(R.id.stock_name);
            stockHoder.stockCode = view.findViewById(R.id.stock_code);
            view.setTag(stockHoder);
        }else {
            view = convertView;
            stockHoder = (StockHoder)view.getTag();
        }
        stockHoder.stockImage.setImageResource(R.mipmap.candy);
        if (entity.getScode() == null || entity.getScode().length() == 0){
            stockHoder.stockName.setText(entity.getSname());
            stockHoder.stockCode.setText("");
        }else {
            stockHoder.stockName.setText("股票名称:" + entity.getSname());
            stockHoder.stockCode.setText("股票代码:" + entity.getScode());
//            stockHoder.stockName.setText(entity.getSname() + "(" + entity.getScode() + ")");
        }
        return view;
//        return super.getView(position, convertView, parent);
    }
    class StockHoder{
        ImageView stockImage;
        TextView stockName;
        TextView stockCode;
    }
}
