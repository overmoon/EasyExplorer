package fun.my.easyexplorer.utils;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import fun.my.easyexplorer.model.AppInfo;
import fun.my.easyexplorer.model.ValuePair;
import my.fun.asyncload.imageloader.utils.DiskCacheUtils;

/**
 * Created by admin on 2016/11/13.
 */

public class JsonUtils {

    private static final String DEFAULT_FILE_NAME = "appInfos.json";
    private static final String DEFAULT_DIR_NAME = "data";

    //保存appInfo
    public static boolean saveAppInfo(Context context, AppInfo appInfoGlobal) throws IOException {
        if (appInfoGlobal == null)
            return true;

        List<AppInfo> appInfos = getAppInfos(context, DEFAULT_FILE_NAME);
        // 如果appInfos不为空
        if (appInfos != null) {
            String appName = appInfoGlobal.getAppName();
            String packageName = appInfoGlobal.getPackageName();
            String drawableFile = appInfoGlobal.getDrawableFile();
            ArrayList<ValuePair> valuePairs = appInfoGlobal.getValuePairList();
            //用来判断是否被添加
            boolean added = false;
            for (AppInfo info : appInfos) {
                //判断是否已经有相同的应用
                if (appName.equals(info.getAppName()) && packageName.equals(info.getPackageName())) {
                    //替换图标
//                    info.setDrawableFile(drawableFile);
                    //添加valuePair
                    List<ValuePair> list = info.getValuePairList();
                    if (list == null) {
                        info.setValuePairList(valuePairs);
                    } else {
                        list.addAll(valuePairs);
                    }
                    added = true;
                    break;
                }
            }
            //如果没有被添加，则直接加到appInfo list中
            if (!added) {
                appInfos.add(appInfoGlobal);
            }
        } else {
            //如果nappInfos为空，则新建list并添加
            appInfos = new ArrayList<>();
            appInfos.add(appInfoGlobal);
        }

        saveAppInfos(context, appInfos);
        return true;
    }

    //修改appInfo
    public static boolean changeAppInfo(Context context, AppInfo appInfo, int position) throws IOException {
        List<AppInfo> appInfos = getAppInfos(context, DEFAULT_FILE_NAME);
        AppInfo info = null;
        //查找appName, packageName对应的appInfo
        boolean hasItem = false; //判断是否存在
        for (AppInfo tmpInfo : appInfos) {
            if (tmpInfo.getAppName().equals(appInfo.getAppName())) {
                if (tmpInfo.getPackageName() != null && tmpInfo.getPackageName().equals(appInfo.getPackageName())
                        || tmpInfo.getPackageName() == appInfo.getPackageName()) {
                    info = tmpInfo;
                    hasItem = true;
                    break;
                }
            }
        }
        // 不存在此appInfo
        if (!hasItem) {
            return false;
        }
        //对appInfo的valuePair处理
        //valuePair被删除，且size大于删除的位置
        if (appInfo.getValuePairList().size() == 0 && position != -1 && info.getValuePairList().size() > position) {
            info.getValuePairList().remove(position);
            return saveAppInfos(context, appInfos);
        } else if (appInfo.getValuePairList().size() > 0) {
            if (position != -1) {
                //valuePair被修改
                info.getValuePairList().remove(position);
                info.getValuePairList().add(position, appInfo.getValuePairList().get(0));
            } else {
                //position=-1，则添加valuePair
                info.getValuePairList().add(appInfo.getValuePairList().get(0));
            }
            return saveAppInfos(context, appInfos);
        }
        return false;
    }

    public static boolean deleteAppInfo(Context context, AppInfo appInfo) throws IOException {
        List<AppInfo> appInfos = getAppInfos(context, DEFAULT_FILE_NAME);
        if (appInfos != null) {
            String appName = appInfo.getAppName();
            String packageName = appInfo.getPackageName();

            for (AppInfo tmpInfo : appInfos) {
                //判断是否已经有相同的应用
                if (tmpInfo.getAppName().equals(appName)) {
                    if (packageName != null && packageName.equals(tmpInfo.getPackageName())
                            || tmpInfo.getPackageName() == packageName) {
                        appInfos.remove(tmpInfo);
                        break;
                    }
                }
            }
            saveAppInfos(context, appInfos);
            return true;
        }
        return false;
    }

    //从文件中获取appInfo list
    public static List getAppInfos(Context context, String file) throws IOException {
        if (file == null) {
            file = DEFAULT_FILE_NAME;
        }
        File f = getDataFile(context, file);
        if (!f.exists())
            return null;

        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        List<AppInfo> appInfos = null;
        JsonReader jsonReader = null;
        try {
            jsonReader = new JsonReader(new FileReader(f));
            appInfos = gson.fromJson(jsonReader, new TypeToken<List<AppInfo>>() {
            }.getType());
        } finally {
            jsonReader.close();
        }

        return appInfos;
    }

    //保存AppInfo列表到Json文件
    private static boolean saveAppInfos(Context context, List<AppInfo> appInfos) {
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        String json = gson.toJson(appInfos);
        //json写入文件
        File file = null;
        try {
            file = getDataFile(context, DEFAULT_FILE_NAME);
            writeToFile(json, file);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    //写入数据
    public static void writeToFile(String json, File file) throws IOException {
        //覆盖源文件
        FileOutputStream out = out = new FileOutputStream(file, false);
        out.write(json.getBytes());
        if (out != null) {
            out.close();
        }
    }

    //获取json文件
    public static File getDataFile(Context context, String fileName) throws IOException {
        File fDir = DiskCacheUtils.getDiskCacheDir(context, DEFAULT_DIR_NAME);
        if (!fDir.exists()) {
            fDir.mkdirs();
        }

        File f = new File(fDir, fileName);
        if (!f.exists()) {
            f.createNewFile();
        }
        return f;
    }
}
