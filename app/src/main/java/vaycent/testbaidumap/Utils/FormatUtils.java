package vaycent.testbaidumap.Utils;

import com.baidu.mapapi.search.poi.PoiIndoorInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vaycent on 2017/6/9.
 */

public class FormatUtils {

//  在楼层列表中加入"全部"类型
    public ArrayList<String> floorsFormat(ArrayList<String> floors){
        ArrayList<String> resultFloors = new ArrayList<>();
        boolean hasAllType = false;
        for(int i=0;i<floors.size();i++){
            resultFloors.add(floors.get(i));
            if("全部".equals(floors.get(i)))
                hasAllType = true;
        }
        if(!hasAllType)
            resultFloors.add(0,"全部");
        return resultFloors;
    }

//  在PoiIndoorInfo列表中筛选出选择楼层的结果
    public List<PoiIndoorInfo> poiIndoorResultFormat(List<PoiIndoorInfo> soruce,String chooseFloor){
        List<PoiIndoorInfo> result = new ArrayList<>();
        for(int i=0;i<soruce.size();i++){
            if(soruce.get(i).floor.equals(chooseFloor)||chooseFloor.equals("全部")){
                result.add(soruce.get(i));
            }
        }
        return result;
    }
}
