package vaycent.testbaidumap;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import vaycent.testbaidumap.databinding.MultiPoiMapActivityBinding;

public class MultiPoiMapActivity extends AppCompatActivity {

    private MultiPoiMapActivityBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_multi_poi_map);

    }
}
