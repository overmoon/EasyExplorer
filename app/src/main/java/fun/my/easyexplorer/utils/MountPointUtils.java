package fun.my.easyexplorer.utils;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.support.annotation.NonNull;
import android.util.Log;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import fun.my.easyexplorer.model.MountPoint;

/**
 * Created by admin on 2016/9/10.
 */
public class MountPointUtils {
    private Context context;
    private final static String tag = "MountPointUtils";

    /**
     * 构造方法
     */
    private MountPointUtils(Context context) {
        this.context = context;
    }

    /**
     * 之所以用这种方法初始化时为了防止使用的时候没有检查SDK版本导致出错
     */
    public static MountPointUtils GetMountPointInstance(Context context) {
        if (17 <= Build.VERSION.SDK_INT) {
            return new MountPointUtils(context);
        } else {
            Log.e(tag, "本类不支持当前SDK版本");
            return null;
        }
    }

    /**
     * 核心操作-获取所有挂载点信息。
     */
    public List<MountPoint> getMountPoint() {
        try{
            Class class_StorageManager = StorageManager.class;
            Method method_getVolumeList = class_StorageManager.getMethod("getVolumeList");
            Method method_getVolumeState = class_StorageManager.getMethod("getVolumeState", String.class);
            StorageManager sm = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
            Class class_StorageVolume = Class.forName("android.os.storage.StorageVolume");
            Method method_isRemovable = class_StorageVolume.getMethod("isRemovable");
            Method method_getPath = class_StorageVolume.getMethod("getPath");
            Method method_getUserLabel = class_StorageVolume.getMethod("getUserLabel");
//           if Build.VERSION.SDK_INT >= 17, StorageVolume has the Method getPathFile;
//            if (Build.VERSION.SDK_INT >= 17) {
//                // api17以下的版本在StorageVolume方法中没有getPathFile
//                method_getPathFile = class_StorageVolume.getMethod("getPathFile");
//            }
            //region 所有挂载点File---附带是内置存储卡还是外置存储卡的标志
            Object[] objArray = (Object[]) method_getVolumeList.invoke(sm);
            List<MountPoint> result = new ArrayList<>();
            for (Object value:objArray){
                String path = (String) method_getPath.invoke(value);
                File file;
                // if(Build.VERSION.SDK_INT >=17), file = (File) method_getPathFile.invoke(value);
                file = new File(path);
                boolean isRemovable = (boolean) method_isRemovable.invoke(value);
                boolean isMounted;
                //获取挂载状态。
                String getVolumeState = (String) method_getVolumeState.invoke(sm, path);
                if (getVolumeState.equals(Environment.MEDIA_MOUNTED)) {
                    isMounted = true;
                } else {
                    isMounted = false;
                }
                //获取description
                String description = (String) method_getUserLabel.invoke(value);
                result.add(new MountPoint(file, isRemovable, isMounted, description));
            }
            return result;
            //endregion
        }catch(NoSuchMethodException e){
            e.printStackTrace();
        }
        catch(InvocationTargetException e){
            e.printStackTrace();
        }
        catch(IllegalAccessException e){
            e.printStackTrace();
        }
        catch(ClassNotFoundException e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取处于挂载状态的挂载点的信息
     */
    public List<MountPoint> getMountedPoint() {
        List<MountPoint> result = getMountPoint();
        for(MountPoint value: result){
            if(!value.isMounted()) {
                result.remove(value);
            }
        }
        return result;
    }
}