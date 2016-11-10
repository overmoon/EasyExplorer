package fun.my.easyexplorer.ui.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.List;

import fun.my.easyexplorer.R;
import fun.my.easyexplorer.model.FileType;
import fun.my.easyexplorer.utils.Utils;
import my.fun.asyncload.imageloader.core.DisplayOption;
import my.fun.asyncload.imageloader.core.ImageLoader;

/**
 * Created by admin on 2016/9/10.
 */
public class FileListAdapter extends BaseAdapter {
    protected LayoutInflater inflater;
    protected List<File> files;
    protected int layout_ID;
    private Context context;

    public FileListAdapter(Context context, List<File> files) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.files = files;
        layout_ID = R.layout.fileexplorer_listitem;
    }

    public FileListAdapter(Context context, List<File> files, int layout_ID) {
        context = context.getApplicationContext();
        inflater = LayoutInflater.from(context);
        this.files = files;
        this.layout_ID = layout_ID;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        FileViewHolder fileViewHolder;
        if (convertView != null) {
            fileViewHolder = (FileViewHolder) convertView.getTag();
        } else {
            fileViewHolder = new FileViewHolder();
            convertView = inflater.inflate(layout_ID, null);
            fileViewHolder.fileIcon_ImageView = (ImageView) convertView.findViewById(R.id.file_icon);
            fileViewHolder.fileName_TextView = (TextView) convertView.findViewById(R.id.file_name);
            convertView.setTag(fileViewHolder);
        }

        File file = (File) getItem(position);
        // set file icon
        if (file.isDirectory()) {
            setFolderIcon(fileViewHolder.fileIcon_ImageView, file);
        } else {
            setFileIcon(fileViewHolder.fileIcon_ImageView, file);
        }

        //set file name
        fileViewHolder.fileName_TextView.setText(file.getName());

        return convertView;
    }

    private void setFileIcon(ImageView fileIcon_imageView, File file) {
        String mimeType = Utils.getMimeType(file);
        FileType f = FileType.mimeTypeOf(mimeType);
        String uri = Uri.fromFile(file).toString();

        DisplayOption.Builder builder = new DisplayOption.Builder();
        builder.setImageView(fileIcon_imageView)
                .setResources(context.getResources())
                .setData(uri);
        switch (f) {
            case UNKNOW:
                fileIcon_imageView.setImageResource(R.mipmap.ic_unknown);
                break;
            case TXT:
                fileIcon_imageView.setImageResource(R.mipmap.ic_text);
                break;
            case AUDIO:
                fileIcon_imageView.setImageResource(R.mipmap.ic_audio);
                break;
            case VIDEO:
                builder.setImageHolder(R.mipmap.ic_video);
                ImageLoader.getInstance().loadBitmap(builder.build());
                break;
            case IMAGE:
                builder.setImageHolder(R.mipmap.ic_pic);
                ImageLoader.getInstance().loadBitmap(builder.build());
                break;

        }
    }

    private void setFolderIcon(ImageView fileIcon_imageView, File file) {
        int icon_id = 0;
        if (file.list() == null || file.list().length == 0) {
            icon_id = R.mipmap.ic_folder_empty;
        } else {
            icon_id = R.mipmap.ic_folder;
        }
        fileIcon_imageView.setImageResource(icon_id);
    }

    @Override
    public int getCount() {
        return files.size();
    }

    @Override
    public Object getItem(int position) {
        return files.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public int getLayout_ID() {
        return layout_ID;
    }

    public void setLayout_ID(int layout_ID) {
        this.layout_ID = layout_ID;
    }

    private class FileViewHolder {
        TextView fileName_TextView;
        ImageView fileIcon_ImageView;
    }
}
