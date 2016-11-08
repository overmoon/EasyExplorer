package fun.my.easyexplorer.ui.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import fun.my.easyexplorer.R;
import fun.my.easyexplorer.model.AppInfo;
import fun.my.easyexplorer.model.MountPoint;
import fun.my.easyexplorer.ui.adapter.MainRecyclerListAdapter;
import fun.my.easyexplorer.utils.MountPointUtils;
import fun.my.easyexplorer.utils.Utils;

public class MainActivity extends BaseActivity {
    private static final int READ_EXTERNAL_STORAGE_REQUEST_CODE = 0;
    private List adapterDataList;
    private List<MountPoint> mountPoints;
    private List<AppInfo> appInfos;
    private MainRecyclerListAdapter adapter;
    private RecyclerView recyclerView;

    protected void initVariables() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            //申请READ_EXTERNAL_STORAGE权限
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    READ_EXTERNAL_STORAGE_REQUEST_CODE);
        }
        adapterDataList = new ArrayList();
        adapter = new MainRecyclerListAdapter(this, adapterDataList);
        //设置单击事件
        adapter.setItemClickedListener(new MainRecyclerListAdapter.OnItemClickedListener() {
            @Override
            public void onItemClicked(View view, int positon) {
                Object object = adapterDataList.get(positon);
                int viewType = adapter.getItemViewType(positon);
                if (viewType == MainRecyclerListAdapter.MOUNT_POINT) {
                    MountPoint mountPoint = (MountPoint) object;
                    startFileExplorerActivity(mountPoint.getFile().getAbsolutePath());
                }
            }
        });

        appInfos = Utils.getAppInfoList5(this);

    }

    protected void initViews(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);
        recyclerView = (RecyclerView) findViewById(R.id.mount_recyclerView);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    protected void loadData() {
        adapterDataList.clear();
        adapterDataList.addAll(getMountedPoints(this));
        adapterDataList.addAll(appInfos);
        adapterDataList.add(new Object());
        adapter.notifyDataSetChanged();
    }

    private List getMountedPoints(Context context) {
        MountPointUtils mountPointUtils = MountPointUtils.GetMountPointInstance(context);
        return mountPointUtils.getMountedPoint();
    }


    private void startFileExplorerActivity(String path) {
        Intent intent = new Intent(MainActivity.this, FileExplorerActivity.class);
        intent.putExtra("path", path);
        startActivity(intent);
    }
}
