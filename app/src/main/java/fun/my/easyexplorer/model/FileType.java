package fun.my.easyexplorer.model;

import java.io.File;

/**
 * Created by admin on 2016/10/22.
 */
public enum FileType {
    IMAGE("image"), VIDEO("video"), AUDIO("audio"), TXT("text"), UNKNOW("");

    public String mimeType_prefix;

    FileType(String prefix) {
        if (prefix==null)
            prefix = "";
        mimeType_prefix = prefix;
    }

    public static FileType mimeTypeOf(String mimeType){
        if (mimeType == null){
            mimeType = "";
        }
        for (FileType f : FileType.values()){
            if(f.belongTo(mimeType)){
                return f;
            }
        }
        return UNKNOW;
    }

    boolean belongTo(String mimeType){
        return mimeType.startsWith(mimeType_prefix);
    }
}
