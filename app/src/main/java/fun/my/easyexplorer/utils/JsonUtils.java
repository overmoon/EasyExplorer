package fun.my.easyexplorer.utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import fun.my.easyexplorer.model.AppInfo;
import fun.my.easyexplorer.model.ValuePair;

/**
 * Created by admin on 2016/11/13.
 */

public class JsonUtils {

    private static final String DEFAULT_FILE = "";

    //保存appInfo
    public static void saveAppInfo(AppInfo appInfoGlobal) {
        if (appInfoGlobal == null)
            return;

        List<AppInfo> appInfos = getAppInfos(DEFAULT_FILE);
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
        Gson gson = new Gson();
        String json = gson.toJson(appInfos);
        //写入文件
        try {
            writeToFile(json, DEFAULT_FILE);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //从文件中获取appInfo list
    public static List getAppInfos(String file) {
        if (file == null) {
            file = DEFAULT_FILE;
        }
        Gson gson = new Gson();
        List<AppInfo> appInfos = null;
        try {
            JsonReader jsonReader = new JsonReader(new FileReader(file));
            appInfos = gson.fromJson(jsonReader, new TypeToken<List<AppInfo>>() {
            }.getType());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return appInfos;
    }

    public static void writeToFile(String json, String file) throws IOException {
        FileOutputStream out = null;
        out = new FileOutputStream(file);
        out.write(json.getBytes());
        if (out != null) {
            out.close();
        }
    }
}
