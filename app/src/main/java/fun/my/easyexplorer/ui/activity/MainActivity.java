package fun.my.easyexplorer.ui.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import fun.my.easyexplorer.R;
import fun.my.easyexplorer.model.AppInfo;
import fun.my.easyexplorer.model.MountPoint;
import fun.my.easyexplorer.ui.adapter.MainRecyclerListAdapter;
import fun.my.easyexplorer.utils.JsonUtils;
import fun.my.easyexplorer.utils.MountPointUtils;
import fun.my.easyexplorer.utils.Utils;

public class MainActivity extends BaseActivity {
    private static final int READ_EXTERNAL_STORAGE_REQUEST_CODE = 0;
    private static final int REQUEST_INSTALL_PACKAGES_REQUEST_CODE = 1;
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
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.REQUEST_INSTALL_PACKAGES)
                != PackageManager.PERMISSION_GRANTED) {
            //申请READ_EXTERNAL_STORAGE权限
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.REQUEST_INSTALL_PACKAGES},
                    REQUEST_INSTALL_PACKAGES_REQUEST_CODE);
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

    }

    protected void initViews(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);
        recyclerView = (RecyclerView) findViewById(R.id.mount_recyclerView);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void loadData() {

    }

    protected void loadDatas() {
        adapterDataList.clear();
        adapterDataList.addAll(getMountedPoints(this));
        adapterDataList.add(new Object());
        new AsyncTask<Context, Void, List>() {
            @Override
            protected List doInBackground(Context[] params) {
                List<AppInfo> infos = null;
                try {
                    infos = JsonUtils.getAppInfos(params[0], null);
                    infos = getAppDrawable(params[0], infos);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return infos;
            }

            @Override
            protected void onPostExecute(List appInfos) {
                if (appInfos != null) {
                    adapterDataList.addAll(adapterDataList.size() - 1, appInfos);
                    adapter.notifyDataSetChanged();
                }
            }
        }.execute(this);

        adapter.notifyDataSetChanged();
    }

    //获取appinfo的drawable
    private List<AppInfo> getAppDrawable(Context context, List<AppInfo> appInfos) {
        if (appInfos != null) {
            for (AppInfo info : appInfos) {
                String filePath = info.getDrawableFile();
                //从文件路径中获取drawable
                if (!TextUtils.isEmpty(info.getDrawableFile())) {
                    info.setDrawable(Utils.getDrawableFromFile(filePath));
                } else if (!TextUtils.isEmpty(info.getPackageName())) {
                    //从系统中获取应用drawable
                    info.setDrawable(Utils.getAppDrawableIcon(context, info.getPackageName()));
                }
            }
        }

        return appInfos;
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

    @Override
    protected void onResume() {
        super.onResume();
        loadDatas();
    }
}
