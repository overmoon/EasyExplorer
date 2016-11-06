package fun.my.easyexplorer.model;

import android.graphics.drawable.Drawable;

/**
 * Created by admin on 2016/11/6.
 */

public class AppInfo {

    private String appName;
    private Drawable drawable;
    private String packageName;
    private String title;
    private String path;

    public AppInfo( String appName, String packageName, Drawable drawable){
        this.appName = appName;
        this.packageName=packageName;
        this.drawable = drawable;
    }
}
