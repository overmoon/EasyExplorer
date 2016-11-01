package fun.my.easyexplorer.utils;

import android.app.Application;
import android.content.Context;

import my.fun.asyncload.imageloader.core.ImageLoader;
import my.fun.asyncload.imageloader.core.ImageLoaderConfiguration;

/**
 * Created by admin on 2016/10/21.
 */
public class MyApplication extends Application {


    @Override
    public void onCreate() {
        super.onCreate();

        ImageLoaderConfiguration configuration = initImageLoaderConf(getApplicationContext());
        ImageLoader.getInstance().init(configuration);
    }

    private ImageLoaderConfiguration initImageLoaderConf(Context context) {
        return new ImageLoaderConfiguration.Builder()
                .setContext(context).setExecutor(ImageLoaderConfiguration.CONCURRENT_EXCUTOR).build();
    }
}
