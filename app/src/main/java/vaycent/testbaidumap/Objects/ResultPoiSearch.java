package vaycent.testbaidumap.Objects;

import com.baidu.mapapi.model.LatLng;

/**
 * Created by vaycent on 2017/6/1.
 */

public class ResultPoiSearch {
    public String name;
    public LatLng latlng;
    public String floor;

    public ResultPoiSearch(String name,LatLng latlng,String floor){
        this.name = name;
        this.latlng = latlng;
        this.floor = floor;
    }
}
