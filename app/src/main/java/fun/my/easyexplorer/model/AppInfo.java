package fun.my.easyexplorer.model;

import android.graphics.drawable.Drawable;

import java.util.HashMap;

/**
 * Created by admin on 2016/11/6.
 */

public class AppInfo {

    private String appName;
    private Drawable drawable;
    private String packageName;
    private HashMap<String, String> easyPathMap;

    public AppInfo(String appName, String packageName) {
        this(appName, packageName, null);
    }

    public AppInfo(String appName, String packageName, Drawable drawable) {
        this.appName = appName;
        this.packageName = packageName;
        this.drawable = drawable;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public Drawable getDrawable() {
        return drawable;
    }

    public void setDrawable(Drawable drawable) {
        this.drawable = drawable;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public HashMap<String, String> getEasyPathMap() {
        return easyPathMap;
    }

    public void setEasyPathMap(HashMap<String, String> easyPathMap) {
        this.easyPathMap = easyPathMap;
    }
}
