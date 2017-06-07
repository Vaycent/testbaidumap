package vaycent.testbaidumap.Poi;

import android.content.Context;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.search.poi.PoiIndoorInfo;

import vaycent.testbaidumap.Utils.AlertUtils;

/**
 * Created by vaycent on 2017/6/6.
 */

public class CanClickPoiOverlay extends IndoorPoiOverlay {

    private AlertUtils mAlertUtils;

    public CanClickPoiOverlay(BaiduMap baiduMap, Context mContext) {
        super(baiduMap);
        mAlertUtils = new AlertUtils(mContext);
    }

    @Override
    /**
     * 响应点击室内POI点事件
     * @param i
     *            被点击的poi在
     *            {@link com.baidu.mapapi.search.poi.PoiIndoorResult#getmArrayPoiInfo()} } 中的索引
     * @return
     */
    public boolean onPoiClick(int i) {
        PoiIndoorInfo info =  getIndoorPoiResult().getmArrayPoiInfo().get(i);
        mAlertUtils.poiClickAlert(info);
        return false;
    }

}
