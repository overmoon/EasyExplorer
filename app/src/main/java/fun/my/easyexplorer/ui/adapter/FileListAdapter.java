package fun.my.easyexplorer.ui.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
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
    private List<Boolean> isCheckList;
    private Context context;
    private boolean isEdit;
    private Animation animation;

    public FileListAdapter(Context context, List<File> files) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.files = files;
        initCheckList(files);
        layout_ID = R.layout.fileexplorer_listitem;
        isEdit = false;
        initCheckBoxAnimation();
    }

    public FileListAdapter(Context context, List<File> files, int layout_ID) {
        this(context, files);
        this.layout_ID = layout_ID;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        FileViewHolder fileViewHolder;
        if (convertView != null) {
            fileViewHolder = (FileViewHolder) convertView.getTag();
        } else {
            fileViewHolder = new FileViewHolder();
            convertView = inflater.inflate(layout_ID, null);
            fileViewHolder.fileIcon_ImageView = (ImageView) convertView.findViewById(R.id.file_icon);
            fileViewHolder.fileName_TextView = (TextView) convertView.findViewById(R.id.file_name);
            fileViewHolder.file_checkBox = (CheckBox) convertView.findViewById(R.id.file_checkbox);
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

        if (!isEdit) {
            fileViewHolder.file_checkBox.setVisibility(View.GONE);
        } else {
            fileViewHolder.file_checkBox.setVisibility(View.VISIBLE);
            fileViewHolder.file_checkBox.isChecked();
//            fileViewHolder.file_checkBox.startAnimation(animation);
            fileViewHolder.file_checkBox.setChecked(isCheckList.get(position));
        }
        return convertView;
    }

    //设置文件图标
    private void setFileIcon(ImageView fileIcon_imageView, File file) {
        String uri = Uri.fromFile(file).toString();
        String tag = getTag(fileIcon_imageView);
        //如果图片相同则返回
        if (uri.equals(tag))
            return;

        fileIcon_imageView.setTag(R.id.file_icon, uri);
        String mimeType = Utils.getMimeType(file);
        FileType f = FileType.mimeTypeOf(mimeType);

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

    //设置文件夹图标
    private void setFolderIcon(ImageView fileIcon_imageView, File file) {
        int icon_id;
        if (file.list() == null || file.list().length == 0) {
            icon_id = R.mipmap.ic_folder_empty;
        } else {
            icon_id = R.mipmap.ic_folder;
        }
        String resId = getTag(fileIcon_imageView);

        if (!resId.equals(String.valueOf(icon_id))) {
            fileIcon_imageView.setImageResource(icon_id);
            fileIcon_imageView.setTag(R.id.file_icon, icon_id);
        }
    }

    private String getTag(ImageView imageView) {
        Object object = imageView.getTag(R.id.file_icon);
        String resId = "";
        if (object != null) {
            resId = String.valueOf(object);
        }
        return resId;
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

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    public void notifyDataSetChangedInitList() {
        initCheckList(files);
        notifyDataSetChanged();
    }

    private void initCheckBoxAnimation() {
        //淡入动画
        animation = new AlphaAnimation(0f, 1f);
        //持续时间
        animation.setDuration(500);
    }

    private void initCheckList(List<File> files) {
        isCheckList = new ArrayList();
        for (int i = 0; i < files.size(); i++) {
            isCheckList.add(false);
        }
    }

    public boolean getIsEdit() {
        return isEdit;
    }

    public void setIsEdit(boolean bool) {
        isEdit = bool;
    }

    public void setIsChecked(int position, boolean isChecked) {
        if (isCheckList != null && isCheckList.size() > position) {
            isCheckList.set(position, isChecked);
        }
    }

    //item的点击点击转化为点击checkbox
    public void setCheckBoxClicked(View view, int position) {
        FileViewHolder viewHolder = (FileViewHolder) view.getTag();
        if (viewHolder != null) {
            viewHolder.file_checkBox.performClick();
            isCheckList.set(position, viewHolder.file_checkBox.isChecked());
        }
    }

    public ArrayList<File> getSelectedFiles() {
        int size = files.size();
        ArrayList selectedFiles = new ArrayList();
        for (int i = 0; i < size; i++) {
            if (isCheckList.get(i)) {
                selectedFiles.add(files.get(i));
            }
        }
        return selectedFiles;
    }

    private class FileViewHolder {
        TextView fileName_TextView;
        ImageView fileIcon_ImageView;
        CheckBox file_checkBox;
    }


}
