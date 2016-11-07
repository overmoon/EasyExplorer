package fun.my.easyexplorer.model;

import android.graphics.Point;

import java.io.File;

/**
 * Created by admin on 2016/11/7.
 */

public class Frame {
    Point point;
    File file;

    public Frame(File currentFile, Point point) {
        this.file = currentFile;
        this.point = point;
    }

    public Point getPoint() {
        return point;
    }

    public void setPoint(Point point) {
        this.point = point;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }
}
