package vaycent.testbaidumap.Utils;

import android.content.Context;
import android.widget.Toast;

import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.PoiIndoorInfo;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.route.IndoorRouteResult;

/**
 * Created by vaycent on 2017/6/6.
 */

public class AlertUtils {

    private Context mContext;

    public AlertUtils(Context mContext){
        this.mContext = mContext;
    }

    public boolean routePlanAlert(IndoorRouteResult indoorRouteResult){
        if(null == indoorRouteResult || indoorRouteResult.error != SearchResult.ERRORNO.NO_ERROR){
            String errorStr = null==indoorRouteResult?"":indoorRouteResult.error.toString();
            Toast.makeText(mContext,"室内路线规划失败["+errorStr+"]",Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }

    public boolean getPoiAlert(PoiResult poiResult){
        if(null == poiResult || poiResult.error != SearchResult.ERRORNO.NO_ERROR){
            String errorStr = null==poiResult?"":poiResult.error.toString();
            Toast.makeText(mContext, "无Poi搜索结果["+errorStr+"]",Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }

    public boolean getPoiIndoorAlert(PoiIndoorResult poiIndoorResult){
        if(null == poiIndoorResult || poiIndoorResult.error != SearchResult.ERRORNO.NO_ERROR){
            String errorStr = null==poiIndoorResult?"":poiIndoorResult.error.toString();
            Toast.makeText(mContext, "无室内Poi搜索结果["+errorStr+"]" , Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }

    public void notIndoorModeAlert(){
        Toast.makeText(mContext,"请打开室内图或将室内图移入屏幕内",Toast.LENGTH_SHORT).show();
    }

    public void poiClickAlert(PoiIndoorInfo info){
        Toast.makeText(mContext, info.name + ",在" + info.floor + "层,坐标:"+info.latLng.latitude+","+info.latLng.longitude, Toast.LENGTH_SHORT).show();
    }

    public void incorrectPoiIndoorOption(){
        Toast.makeText(mContext, "搜索过滤条件出错", Toast.LENGTH_SHORT).show();
    }
}
