package vaycent.testbaidumap;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import vaycent.testbaidumap.databinding.MultiPoiMapActivityBinding;

public class MultiPoiMapActivity extends AppCompatActivity {

    private MultiPoiMapActivityBinding mBinding;

    private String keyWord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_multi_poi_map);

        initData();
    }

    private void initData(){
        Intent fromIntent = getIntent();
        keyWord = fromIntent.getStringExtra("keyWord");
        Log.e("Vaycent","keyWord:"+keyWord);
    }
}
