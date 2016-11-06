package fun.my.easyexplorer.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;

/**
 * Created by admin on 2016/9/13.
 */
public class RecyclerListViewHolder extends RecyclerView.ViewHolder {
    SparseArray<View> sparseArray;

    public RecyclerListViewHolder(View itemView) {
        super(itemView);
        sparseArray = new SparseArray<>();
    }

    // I added a generic return type to reduce the casting noise in client code
    @SuppressWarnings("unchecked")
    public <T extends View> T get(int id) {
        return (T) sparseArray.get(id);
    }

    public void set(int id) {
        View childView = itemView.findViewById(id);
        sparseArray.put(id, childView);
    }

}
