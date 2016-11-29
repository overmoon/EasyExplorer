package fun.my.easyexplorer.utils;

import java.io.File;
import java.io.IOException;
import java.util.Comparator;

/**
 * Created by admin on 2016/11/29.
 */

public class FileComparator {


    public static Comparator getComparator(String type) {
        switch (type) {
            case "date":
                return new FileDateComparator();
            case "size":
                return new FileSizeComparator();
            case "type":
                return new FileTypeComparator();
            case "name":
            default:
                return new FileNameComparator();
        }
    }

    /**
     * 时间比较：
     * 1.如果都是文件夹，或都是文件，则按文件的时间顺序排列
     * 2.文件夹小于文件
     */
    public static class FileDateComparator implements Comparator<File> {
        @Override
        public int compare(File lhs, File rhs) {
            if (lhs.isDirectory() && rhs.isDirectory() || !lhs.isDirectory() && !rhs.isDirectory()) {
                return lhs.lastModified() > rhs.lastModified() ? 1 : -1;
            } else if (lhs.isDirectory() && !rhs.isDirectory()) {
                return -1;
            } else if (!lhs.isDirectory() && rhs.isDirectory()) {
                return 1;
            } else {
                return 0;
            }

        }
    }

    /**
     * 大小比较：
     * 1.如果都是文件夹，则按名字排序
     * 2.文件夹小于文件
     * 3.同时文件类型，则比较文件大小
     */
    public static class FileSizeComparator implements Comparator<File> {
        @Override
        public int compare(File lhs, File rhs) {
            if (lhs.isDirectory() && rhs.isDirectory()) {
                return lhs.getName().compareToIgnoreCase(rhs.getName());
            } else if (lhs.isDirectory() && !rhs.isDirectory()) {
                return -1;
            } else if (!lhs.isDirectory() && rhs.isDirectory()) {
                return 1;
            } else if (!lhs.isDirectory() && !rhs.isDirectory()) {
                try {
                    return Utils.getFileSize(lhs) > Utils.getFileSize(rhs) ? 1 : -1;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return 0;
        }
    }

    /**
     * 类型比较：
     * 1.如果都是文件夹，则按名字排序
     * 2.文件夹大于文件
     * 3.都是文件时，则比较扩展名的字母顺序
     */
    public static class FileTypeComparator implements Comparator<File> {
        @Override
        public int compare(File lhs, File rhs) {
            if (lhs.isDirectory() && rhs.isDirectory()) {
                return lhs.getName().compareToIgnoreCase(rhs.getName());
            } else if (lhs.isDirectory() && !rhs.isDirectory()) {
                return -1;
            } else if (!lhs.isDirectory() && rhs.isDirectory()) {
                return 1;
            } else if (!lhs.isDirectory() && !rhs.isDirectory()) {
                return Utils.getFileExtension(lhs.getName()).compareToIgnoreCase(Utils.getFileExtension(rhs.getName()));
            } else {
                return 0;
            }
        }
    }

    /**
     * 名字比较：
     * 1.如果都是文件夹， 或都是文件类型，则按名字排序
     * 2.文件夹小于文件
     * 3.都不是则返回0
     */
    public static class FileNameComparator implements Comparator<File> {
        @Override
        public int compare(File lhs, File rhs) {
            if (lhs.isDirectory() && rhs.isDirectory() || !lhs.isDirectory() && !rhs.isDirectory()) {
                return lhs.getName().compareToIgnoreCase(rhs.getName());
            } else if (lhs.isDirectory() && !rhs.isDirectory()) {
                return -1;
            } else if (!lhs.isDirectory() && rhs.isDirectory()) {
                return 1;
            } else {
                return 0;
            }
        }
    }
}
