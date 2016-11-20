package fun.my.easyexplorer.model;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;

/**
 * Created by admin on 2016/11/6.
 */

public class AppInfo {
    @Expose
    private String appName;
    @Expose
    private String drawableFile;
    @Expose
    private String packageName;
    @Expose
    private ArrayList<ValuePair> valuePairList;
    private Drawable drawable;

    public AppInfo(String appName, String packageName) {
        this(appName, packageName, null);
    }

    public AppInfo(String appName, String packageName, Drawable drawable) {
        this.appName = appName;
        this.packageName = packageName;
        this.drawable = drawable;
    }

    public AppInfo(String appName, String drawableFile, String packageName, ArrayList<ValuePair> valuePairList) {
        this.appName = appName;
        this.drawableFile = drawableFile;
        this.packageName = packageName;
        this.valuePairList = valuePairList;
    }

    public AppInfo() {

    }

    public AppInfo(PackageInfo info, PackageManager pm) {
        packageName = info.packageName;
        appName = info.applicationInfo.loadLabel(pm).toString();
        drawable = info.applicationInfo.loadIcon(pm);
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

    public ArrayList<ValuePair> getValuePairList() {
        return valuePairList;
    }

    public void setValuePairList(ArrayList<ValuePair> valuePairList) {
        this.valuePairList = valuePairList;
    }

    public String getDrawableFile() {
        return drawableFile;
    }

    public void setDrawableFile(String drawableFile) {
        this.drawableFile = drawableFile;
    }

//    public AppInfo(Context context, AppInfoBean bean) {
//        this.appName = bean.getAppName();
//        this.packageName = bean.getPackageName();
//        this.valuePairList = bean.getValuePairList();
//
//        String drawableString = bean.getDrawable();
//        if (drawableString != null || !drawableString.equals("") || !drawableString.equals("null")) {
//            drawable = Utils.getDrawableFromFile(drawableString);
//        } else if (!TextUtils.isEmpty("packageName")) {
//            drawable = Utils.getAppDrawableIcon(context, packageName);
//        }
//
//    }
//
//    public static List<AppInfo> fromAppInfoBeans(Context context, List<AppInfoBean> appInfoBeans){
//        List<AppInfo> appInfos = new ArrayList<>();
//        for (AppInfoBean bean:appInfoBeans){
//            appInfos.add(new AppInfo(context, bean));
//        }
//
//        return appInfos;
//    }
//
//    public static AppInfoBean toAppInfoBean(){
//
//    }
}
