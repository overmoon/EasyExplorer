package fun.my.easyexplorer.model;

import java.util.ArrayList;

/**
 * Created by admin on 2016/11/13.
 */

public class AppInfoBean {

    private String appName;
    private String drawable;
    private String packageName;
    private ArrayList<ValuePair> valuePairList;

    public AppInfoBean(String appName, String drawable, String packageName, ArrayList<ValuePair> valuePairList) {
        this.appName = appName;
        this.drawable = drawable;
        this.packageName = packageName;
        this.valuePairList = valuePairList;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getDrawable() {
        return drawable;
    }

    public void setDrawable(String drawable) {
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

}
