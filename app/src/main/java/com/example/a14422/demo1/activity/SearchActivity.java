package com.example.a14422.demo1.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewStub;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.a14422.demo1.R;
import com.example.a14422.demo1.adapter.StockAdapter;
import com.example.a14422.demo1.entity.ScodeEntity;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.mysql.jdbc.StringUtils;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    private MaterialSearchView mSearchView;
    private ListView mListView;
    private List<ScodeEntity> mScodeList;
    StockAdapter mAdapter;
    ViewStub noContentViewStub;
    View noContentView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = findViewById(R.id.search_toolbar);
        toolbar.setTitle("Search");
        setSupportActionBar(toolbar);
        initSearchView();
    }

    private void initSearchView() {
        noContentViewStub = findViewById(R.id.noContentView);
        mSearchView = findViewById(R.id.search_search_view);
        mListView = findViewById(R.id.search_listview);

        mScodeList = new ArrayList<>();
       /* List<ScodeEntity> scodeEntities = LitePal.select("sname", "scode").limit(100).find(ScodeEntity.class);
        mScodeList.clear();
        mScodeList.addAll(scodeEntities);*/
        mAdapter = new StockAdapter(SearchActivity.this, R.layout.search_item, mScodeList);
        mListView.setAdapter(mAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ScodeEntity entity = mScodeList.get(position);
                Intent intent = new Intent(SearchActivity.this,ShowActivity.class);
                intent.putExtra("extra_code",entity.getScode());
                intent.putExtra("extra_name",entity.getSname());
                startActivity(intent);
                finish();
            }
        });
//        mSearchView.setSuggestions(getResources().getStringArray(R.array.query_suggestions));
        mSearchView.setHint("搜索股票代码或名称");
        mSearchView.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        mSearchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                Log.e("TextSubmit", "click");
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if (!StringUtils.isNullOrEmpty(s)){
                    List<ScodeEntity> scodeEntities = LitePal.select("sname", "scode").where("sname like ? or scode like ? ", "%" + s + "%", "%" + s + "%").limit(30).find(ScodeEntity.class);
                    mScodeList.clear();
                    if (scodeEntities.size() == 0){
                        if (noContentView == null){
                            noContentView = noContentViewStub.inflate();
                        }
                        noContentView.setVisibility(View.VISIBLE);
                    }else {
                        if (noContentView != null){
                            noContentView.setVisibility(View.GONE);
                        }
                    }
                    mScodeList.addAll(scodeEntities);
                    mAdapter.notifyDataSetChanged();
                }else {
                    if (noContentView != null){
                        noContentView.setVisibility(View.GONE);
                    }
                    mScodeList.clear();
                    mAdapter.notifyDataSetChanged();
                }
                return false;
            }
        });
        mSearchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {
                Log.e("onSearchViewShown", "show");
            }

            @Override
            public void onSearchViewClosed() {
                Log.e("onSearchViewClosed", "close");
            }
        });
        mSearchView.post(new Runnable() {
            @Override
            public void run() {
                mSearchView.showSearch();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_view_github, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        mSearchView.setMenuItem(searchItem);
        return super.onCreateOptionsMenu(menu);
    }
}
