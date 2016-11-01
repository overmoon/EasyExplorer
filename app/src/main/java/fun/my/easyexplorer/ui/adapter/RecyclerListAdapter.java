package fun.my.easyexplorer.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.File;
import java.util.List;

import fun.my.easyexplorer.R;

/**
 * Created by admin on 2016/9/13.
 */
public class RecyclerListAdapter extends RecyclerView.Adapter<RecyclerListViewHolder> {
    private static int TEXT_VIEW_ID = R.id.recycler_textView;
    private List<File> list;
    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener{
        void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        onItemClickListener = listener;
    }

    public RecyclerListAdapter( List list) {
        this.list = list;
    }

    @Override
    public RecyclerListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_listitem, parent, false);
        RecyclerListViewHolder recyclerListViewHolder = new RecyclerListViewHolder(itemView);
        recyclerListViewHolder.set(TEXT_VIEW_ID);
        return recyclerListViewHolder;
    }

    @Override
    public void onBindViewHolder(final RecyclerListViewHolder holder, final int position) {
        TextView textView = holder.get(TEXT_VIEW_ID);
        File f = list.get(position);
        String fileName = f.getName();
        if(fileName==null || fileName.trim().equals("")){
            fileName=f.getPath();
        }
        textView.setText(fileName);

        if (onItemClickListener != null){
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onItemClick(holder.itemView, holder.getLayoutPosition());
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
