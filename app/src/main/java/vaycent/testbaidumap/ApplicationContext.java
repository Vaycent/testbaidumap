package vaycent.testbaidumap;

import android.app.Application;
import android.util.Log;

import com.baidu.mapapi.SDKInitializer;

/**
 * Created by vaycent on 17/2/9.
 */

public class ApplicationContext extends Application {

    private static ApplicationContext application;

    public ApplicationContext() {
        application = this;
    }

    public static ApplicationContext getApplication() {
        return application;
    }

    @Override
    public void onCreate(){
        super.onCreate();
        //在使用SDK各组件之前初始化context信息，传入ApplicationContext
        //注意该方法要再setContentView方法之前实现
        SDKInitializer.initialize(this);
        Log.e("ApplicationContext","SDKInitializer finish");
    }
}
