package vaycent.testbaidumap;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by vaycent on 2017/6/2.
 */

public class SharedPreferencesUtil {

    public static final String HASUPDATE = "HASUPDATE";
    public static final String ISSHOWDOWNLOADDIALOG = "ISSHOWDOWNLOADDIALOG";
    public static final String LOCATION_BAIDU = "LOCATION_BAIDU";

    /**
     * SharedPreferences文件名
     */
    private static final String PREFERENCE_NAME = "client_preferences";
    private static SharedPreferencesUtil instance;
    private SharedPreferences mSharedPreferences;

    private SharedPreferencesUtil() {
        mSharedPreferences = ApplicationContext.getApplication().getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
    }

    public synchronized static SharedPreferencesUtil getInstance() {
        if (instance == null) {
            instance = new SharedPreferencesUtil();
        }
        return instance;
    }

    /**
     * 保存字符串
     */
    public void saveString(String key, String value) {
        mSharedPreferences.edit().putString(key, value).apply();
    }

    /**
     * 获取字符串
     */
    public String getString(String key, String... defValue) {
        if (defValue.length > 0) {
            return mSharedPreferences.getString(key, defValue[0]);
        } else {
            return mSharedPreferences.getString(key, "");
        }
    }

    /**
     * 获取布尔值
     */
    public Boolean getBoolean(String key, Boolean... defValue) {
        if (defValue.length > 0) {
            return mSharedPreferences.getBoolean(key, defValue[0]);
        } else {
            return mSharedPreferences.getBoolean(key, false);
        }
    }

    /**
     * 保存布尔值
     */
    public void saveBoolean(String key, Boolean defValue) {
        mSharedPreferences.edit().putBoolean(key, defValue).apply();
    }

    /**
     * 保存 Integer
     */
    public void saveInteger(String key, Integer defValue) {
        mSharedPreferences.edit().putInt(key, defValue).apply();
    }

    /**
     * 取 Integer 值
     */
    public Integer getInteger(String key, Integer... defValue) {
        if (defValue.length > 0) {
            return mSharedPreferences.getInt(key, defValue[0]);
        }
        return mSharedPreferences.getInt(key, 0);
    }
}
