package vaycent.testbaidumap;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import vaycent.testbaidumap.Utils.NoMultiClickListener;

public class HotsPoiActivity extends AppCompatActivity implements View.OnClickListener{

    private TextView tvSearchTx;
    private LinearLayout llytSearchLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hots_poi);

        initLayout();
    }

    @Override
    public void onClick(View v) {
        String keyWord;
        if(v.getId()==R.id.activity_hots_poi_btn_search){
            keyWord = tvSearchTx.getText().toString().trim();
        }else{
            Button btnClicked = (Button)v;
            keyWord = btnClicked.getText().toString().trim();
        }

        if(!"".equals(keyWord)){
            tvSearchTx.setText(keyWord);
            Intent multipoimapIntent = new Intent();
            multipoimapIntent.setClass(HotsPoiActivity.this,MultiPoiMapActivity.class);
            multipoimapIntent.putExtra("keyWord",keyWord);
            startActivity(multipoimapIntent);
        }
    }

    private void initLayout(){
        tvSearchTx = (TextView) findViewById(R.id.activity_hots_poi_tv_searchtext);

        findViewById(R.id.activity_hots_poi_btn_search).setOnClickListener(this);

        findViewById(R.id.activity_hots_poi_btn_service01).setOnClickListener(this);
        findViewById(R.id.activity_hots_poi_btn_service02).setOnClickListener(this);
        findViewById(R.id.activity_hots_poi_btn_service03).setOnClickListener(this);
        findViewById(R.id.activity_hots_poi_btn_service04).setOnClickListener(this);
        findViewById(R.id.activity_hots_poi_btn_service05).setOnClickListener(this);
        findViewById(R.id.activity_hots_poi_btn_service06).setOnClickListener(this);
        findViewById(R.id.activity_hots_poi_btn_service07).setOnClickListener(this);
        findViewById(R.id.activity_hots_poi_btn_service08).setOnClickListener(this);

        findViewById(R.id.activity_hots_poi_btn_deveice01).setOnClickListener(this);
        findViewById(R.id.activity_hots_poi_btn_deveice02).setOnClickListener(this);
        findViewById(R.id.activity_hots_poi_btn_deveice03).setOnClickListener(this);
        findViewById(R.id.activity_hots_poi_btn_deveice04).setOnClickListener(this);
        findViewById(R.id.activity_hots_poi_btn_deveice05).setOnClickListener(this);
        findViewById(R.id.activity_hots_poi_btn_deveice06).setOnClickListener(this);
        findViewById(R.id.activity_hots_poi_btn_deveice07).setOnClickListener(this);
        findViewById(R.id.activity_hots_poi_btn_deveice08).setOnClickListener(this);

        llytSearchLayout = (LinearLayout)findViewById(R.id.activity_hots_poi_et_searchlayout);
        llytSearchLayout.setOnClickListener(new NoMultiClickListener() {
            @Override
            protected void onNoMultiClick(View v) {
                Intent startIntent = new Intent();
                startIntent.setClass(HotsPoiActivity.this,PoiInDoorSearchActivity.class);
//                startIntent.putExtra("callbackLocation",callbackLocation);
//                startIntent.putExtra("indoorId",indoorId);
//                startIntent.putExtra("requestcode","");
                startActivity(startIntent);
            }
        });
    }

}
