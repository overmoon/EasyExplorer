package fun.my.easyexplorer.model;

import java.io.File;

/**
 * Created by admin on 2016/9/10.
 */
public class MountPoint {
    private File file;
    /**
     * 用于判断是否为内置存储卡，如果为true就是代表本挂载点可以移除，就是外置存储卡，否则反之
     */
    private boolean isRemovable;
    /**
     * 用于标示，这段代码执行的时候这个出处卡是否处于挂载状态，如果是为true，否则反之
     */
    private boolean isMounted;

    public MountPoint(File file, boolean isRemovable, boolean isMounted) {
        this.file = file;
        this.isMounted = isMounted;
        this.isRemovable = isRemovable;
    }

    public File getFile() {
        return file;
    }

    public boolean isRemovable() {
        return isRemovable;
    }

    public boolean isMounted() {
        return isMounted;
    }

    public boolean isSDcard(){
        return isMounted&&isRemovable;
    }

    public boolean isInternalMem(){
        return isMounted&&(!isRemovable);
    }
}
