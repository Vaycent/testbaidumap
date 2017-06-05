package vaycent.testbaidumap.Utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vaycent on 2017/6/2.
 */

public class HistroySharePreference {

    private final String POI_SEARCH_HISTROY="PoiSearchHistroy";

    public void save(String keyWord){
        List<String> mKeyWords = read();
        if(mKeyWords.size()==20){
            mKeyWords.remove(mKeyWords.size()-1);
        }
        mKeyWords.add(keyWord);
        Gson gson = new Gson();
        String gsonStr = gson.toJson(mKeyWords);
        SharedPreferencesUtil.getInstance().saveString(POI_SEARCH_HISTROY, gsonStr);
    }

    public List<String> read(){
        String gsonStr = SharedPreferencesUtil.getInstance().getString(POI_SEARCH_HISTROY, "");
        Gson gson = new Gson();
        ArrayList<String> mKeyWords = gson.fromJson(gsonStr,new TypeToken<List<String>>(){}.getType());
        if(null==mKeyWords)
            mKeyWords = new ArrayList<String>();
        return mKeyWords;
    }


}
