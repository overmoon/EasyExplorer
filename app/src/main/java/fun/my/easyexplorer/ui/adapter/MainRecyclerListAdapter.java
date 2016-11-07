package fun.my.easyexplorer.ui.adapter;

import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.List;

import fun.my.easyexplorer.R;
import fun.my.easyexplorer.model.AppInfo;
import fun.my.easyexplorer.model.MountPoint;
import fun.my.easyexplorer.ui.view.CustomCircleView;
import fun.my.easyexplorer.utils.Utils;

/**
 * Created by admin on 2016/11/6.
 */

public class MainRecyclerListAdapter extends RecyclerView.Adapter<RecyclerListViewHolder> {
    public final static int MOUNT_POINT = 0;
    public final static int NORMAL_FILE = 1;
    public final static int ADD_BUTTON = 2;

    private List objList;
    private CustomCircleView customCircleView;
    private TextView titleTextView, capacityTextView, pathTextView, appTextView, packageTextView;
    private ImageView iconImageView;
    private Button addButton;
    private OnItemClickedListener itemClickedListener;
    private OnItemLongClickedListener itemLongClickedListener;

    public MainRecyclerListAdapter( List objList) {
        this.objList = objList;
    }

    @Override
    public RecyclerListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View item;
        RecyclerListViewHolder viewHolder;
        if (viewType == MOUNT_POINT) {
            item = LayoutInflater.from(parent.getContext()).inflate(R.layout.main_recycler_item1, parent, false);
            viewHolder = new RecyclerListViewHolder(item);
            viewHolder.set(R.id.customCircleView);
            viewHolder.set(R.id.titleTextView);
            viewHolder.set(R.id.capacityTextView);
            viewHolder.set(R.id.pathTextView);
        } else if (viewType == NORMAL_FILE){
            item = LayoutInflater.from(parent.getContext()).inflate(R.layout.main_recycler_item2, parent, false);
            viewHolder = new RecyclerListViewHolder(item);
            viewHolder.set(R.id.iconImageView);
            viewHolder.set(R.id.appTextView);
            viewHolder.set(R.id.packageTextView);
        } else {
            item = LayoutInflater.from(parent.getContext()).inflate(R.layout.main_recycler_item3, parent, false);
            viewHolder = new RecyclerListViewHolder(item);
            viewHolder.set(R.id.addButton);
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerListViewHolder holder, final int position) {
        Object object = objList.get(position);
        int type = getItemViewType(position);
        if (type == MOUNT_POINT) {
            customCircleView = holder.get(R.id.customCircleView);
            titleTextView = holder.get(R.id.titleTextView);
            capacityTextView = holder.get(R.id.capacityTextView);
            pathTextView = holder.get(R.id.pathTextView);

            MountPoint mountPoint = (MountPoint) object;
            File f = mountPoint.getFile();
            titleTextView.setText(mountPoint.getDescription());
            long maxSize = f.getTotalSpace();
            long freeSize = f.getFreeSpace();
            long usedSize = maxSize - freeSize;
            double usedSizeGb = Utils.getNDegree( Utils.byteToGB(usedSize), 2);
            double maxSizeGb = Utils.getNDegree(Utils.byteToGB(maxSize) , 2);
            capacityTextView.setText(usedSizeGb + " GB / " + maxSizeGb + " GB");
            pathTextView.setText(f.getAbsolutePath());
            double percent = Utils.getNDegree ((usedSizeGb / maxSizeGb), 2);
            customCircleView.setmPercent((float) percent);


        } else  if (type == NORMAL_FILE){
            iconImageView = holder.get(R.id.iconImageView);
            appTextView = holder.get(R.id.appTextView);
            packageTextView = holder.get(R.id.packageTextView);

            AppInfo appInfo = (AppInfo) object;
            Drawable drawable = appInfo.getDrawable();
            if (drawable == null) {
                iconImageView.setImageResource(R.mipmap.ic_launcher);
            } else {
                iconImageView.setImageDrawable(drawable);
            }
            appTextView.setText(appInfo.getAppName());
            packageTextView.setText(appInfo.getPackageName());


        } else {
            addButton = holder.get(R.id.addButton);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickedListener.onItemClicked(v, position);
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                itemLongClickedListener.onItemLongClicked(v, position);
                return true;
            }
        });
    }


    @Override
    public int getItemCount() {
        return objList.size();
    }

    @Override
    public int getItemViewType(int position) {
        Object object = objList.get(position);
        if (object instanceof MountPoint) {
            return MOUNT_POINT;
        } else if (object instanceof AppInfo){
            return NORMAL_FILE;
        } else {
            return ADD_BUTTON;
        }
    }

    public void setItemClickedListener(OnItemClickedListener itemClickedListener) {
        this.itemClickedListener = itemClickedListener;
    }

    public void setItemLongClickedListener(OnItemLongClickedListener itemLongClickedListener) {
        this.itemLongClickedListener = itemLongClickedListener;
    }

    public interface OnItemClickedListener {
        void onItemClicked(View view, int positon);
    }

    public interface OnItemLongClickedListener {
        void onItemLongClicked(View v, int position);
    }

}
