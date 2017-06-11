package vaycent.testbaidumap;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import vaycent.testbaidumap.Objects.Indoor;
import vaycent.testbaidumap.Utils.MapUtils;
import vaycent.testbaidumap.Utils.NoMultiClickListener;



public class HotsPoiActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView tvSearchTx;
    private LinearLayout llytSearchLayout;

    private Indoor mIndoorData;

    private LinearLayout llytKeywordHots,llytKeywordService,llytKeywordDevice;

    private MapUtils mMapUtils = new MapUtils();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hots_poi);

        initData();

        initLayout();


        ArrayList<String> hotsDatas = initHotsData();
        initAutoLL(hotsDatas,llytKeywordHots);
        ArrayList<String> serviceDatas = initServiceData();
        initAutoLL(serviceDatas,llytKeywordService);
        ArrayList<String> deviceDatas = initDeviceData();
        initAutoLL(deviceDatas,llytKeywordDevice);
    }

    @Override
    public void onClick(View v) {
        try{
            TextView tvClicked = (TextView) v;
            String keyWord = tvClicked.getText().toString().trim();
            if (!"".equals(keyWord)) {
                tvSearchTx.setText(keyWord);
                Intent multipoimapIntent = new Intent();
                multipoimapIntent.setClass(HotsPoiActivity.this, MultiPoiMapActivity.class);
                multipoimapIntent.putExtra("keyWord",keyWord);
                Bundle mBundle = new Bundle();
                mBundle.putParcelable(Indoor.KEY_NAME, mIndoorData);
                multipoimapIntent.putExtras(mBundle);
                startActivity(multipoimapIntent);
            }
        }catch (Exception e){

        }

    }

    private void initData(){
        Intent fromIntent = getIntent();
        if (null != fromIntent && null != fromIntent.getParcelableExtra(Indoor.KEY_NAME))
            mIndoorData = (Indoor)fromIntent.getParcelableExtra(Indoor.KEY_NAME);
    }

    private void initLayout() {
        llytKeywordHots = (LinearLayout)findViewById(R.id.activity_hots_poi_llyt_hots);
        llytKeywordService = (LinearLayout)findViewById(R.id.activity_hots_poi_llyt_service);
        llytKeywordDevice = (LinearLayout)findViewById(R.id.activity_hots_poi_llyt_device);

        tvSearchTx = (TextView) findViewById(R.id.activity_hots_poi_tv_searchtext);


        llytSearchLayout = (LinearLayout) findViewById(R.id.activity_hots_poi_et_searchlayout);
        llytSearchLayout.setOnClickListener(new NoMultiClickListener() {
            @Override
            protected void onNoMultiClick(View v) {
                Intent startIntent = new Intent();
                startIntent.setClass(HotsPoiActivity.this, PoiInDoorSearchActivity.class);
                Bundle mBundle = new Bundle();
                mBundle.putParcelable(Indoor.KEY_NAME, mIndoorData);
                startIntent.putExtras(mBundle);
                startActivity(startIntent);
            }
        });
    }

    private ArrayList<String> initHotsData() {
        ArrayList<String> datas = new ArrayList<>();
        datas.add("南航");
        datas.add("贵宾休息室");
        datas.add("餐饮");
        datas.add("商户");
        datas.add("行李");
        datas.add("中转");
        datas.add("机场大厅进出口");
        datas.add("安检口");
        return datas;
    }

    private ArrayList<String> initServiceData() {
        ArrayList<String> datas = new ArrayList<>();
        datas.add("登机口");
        datas.add("值机口");
        datas.add("安全检查");
        datas.add("候机厅");
        datas.add("出发厅");
        datas.add("到达厅");
        datas.add("行李服务");
        datas.add("交通运输");
        return datas;
    }

    private ArrayList<String> initDeviceData() {
        ArrayList<String> datas = new ArrayList<>();
        datas.add("ATM");
        datas.add("卫生间");
        datas.add("保险");
        datas.add("停车场");
        datas.add("扶梯");
        datas.add("出入口");
        datas.add("问讯处");
        datas.add("直梯");
        return datas;
    }



    private void initAutoLL(ArrayList<String> datas,LinearLayout llytKeyword) {
//        每一行的布局，初始化第一行布局
        LinearLayout rowLL = new LinearLayout(this);
        LinearLayout.LayoutParams rowLP =
                new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
        float rowMargin = mMapUtils.dip2px(this,10);
        rowLP.setMargins(0, (int) rowMargin, 0, 0);
        rowLL.setLayoutParams(rowLP);
        boolean isNewLayout = false;
        float maxWidth = getScreenWidth() - mMapUtils.dip2px(this,30);
//        剩下的宽度
        float elseWidth = maxWidth;
        LinearLayout.LayoutParams textViewLP =
                new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
        textViewLP.setMargins((int) mMapUtils.dip2px(this,8), 0, 0, 0);
        for (int i = 0; i < datas.size(); i++) {
//            若当前为新起的一行，先添加旧的那行
//            然后重新创建布局对象，设置参数，将isNewLayout判断重置为false
            if (isNewLayout) {
                llytKeyword.addView(rowLL);
                rowLL = new LinearLayout(this);
                rowLL.setLayoutParams(rowLP);
                isNewLayout = false;
            }
//            计算是否需要换行
            final TextView textView = (TextView) getLayoutInflater().inflate(R.layout.tv_hots_poi_keyword, null);
            textView.setText(datas.get(i));
            textView.measure(0, 0);
//            若是一整行都放不下这个文本框，添加旧的那行，新起一行添加这个文本框
            if (maxWidth < textView.getMeasuredWidth()) {
                llytKeyword.addView(rowLL);
                rowLL = new LinearLayout(this);
                rowLL.setLayoutParams(rowLP);
                rowLL.addView(textView);
                isNewLayout = true;
                continue;
            }
//            若是剩下的宽度小于文本框的宽度（放不下了）
//            添加旧的那行，新起一行，但是i要-1，因为当前的文本框还未添加
            if (elseWidth < textView.getMeasuredWidth()) {
                isNewLayout = true;
                i--;
//                重置剩余宽度
                elseWidth = maxWidth;
                continue;
            } else {
//                剩余宽度减去文本框的宽度+间隔=新的剩余宽度
                elseWidth -= textView.getMeasuredWidth() + mMapUtils.dip2px(this,8);
                if (rowLL.getChildCount() == 0) {
                    rowLL.addView(textView);
                } else {
                    textView.setLayoutParams(textViewLP);
                    rowLL.addView(textView);
                }
            }

            textView.setOnClickListener(this);
        }
//        添加最后一行，但要防止重复添加
        llytKeyword.removeView(rowLL);
        llytKeyword.addView(rowLL);
    }

    private float getScreenWidth() {
        return this.getResources().getDisplayMetrics().widthPixels;
    }



}
