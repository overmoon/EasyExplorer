package fun.my.easyexplorer.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import fun.my.easyexplorer.R;
import fun.my.easyexplorer.model.AppInfo;
import fun.my.easyexplorer.utils.Utils;

/**
 * Created by admin on 2016/11/9.
 */

public class ListPopupAdapter extends BaseAdapter implements Filterable {
    private AppFilter appFilter;
    private List<AppInfo> appInfos;
    private List<AppInfo> originalInfos;
    private Context context;
    private LayoutInflater inflater;

    public ListPopupAdapter(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        appInfos = new ArrayList<>();
        originalInfos = new ArrayList<>();
    }

    public ListPopupAdapter(Context context, List appInfos) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.appInfos = appInfos;
        originalInfos = new ArrayList<>(appInfos);
    }

    @Override
    public int getCount() {
        return appInfos.size();
    }

    @Override
    public Object getItem(int position) {
        return appInfos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.listpop_item, parent, false);
        }
        ImageView imageView = ListViewHolder.get(convertView, R.id.popup_imageView);
        TextView textView = ListViewHolder.get(convertView, R.id.popup_textView);

        AppInfo info = appInfos.get(position);
        imageView.setImageDrawable(info.getDrawable());
        textView.setText(info.getAppName());

        return convertView;
    }

    @Override
    public Filter getFilter() {
        if (appFilter == null) {
            appFilter = new AppFilter();
        }
        return appFilter;
    }

    public void setFilter(AppFilter filter) {
        this.appFilter = filter;
    }

    class AppFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            if (originalInfos.size() == 0 && context != null) {
                originalInfos = Utils.getAppInfoList(context);
            }
            FilterResults results = new FilterResults();
            if (constraint == null) {
                results.values = originalInfos;
                results.count = getCount();
            } else {
                ArrayList tmpList = new ArrayList();
                for (AppInfo appInfo : originalInfos) {
                    if (appInfo.getAppName().toLowerCase().contains(constraint.toString().toLowerCase())) {
                        tmpList.add(appInfo);
                    }
                }
                results.values = tmpList;
                results.count = tmpList.size();
            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            appInfos = (List<AppInfo>) results.values;
            notifyDataSetChanged();
        }
    }
}
