package fun.my.easyexplorer.ui.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

import fun.my.easyexplorer.R;
import fun.my.easyexplorer.model.AppInfo;
import fun.my.easyexplorer.model.MountPoint;
import fun.my.easyexplorer.ui.adapter.MainRecyclerListAdapter;
import fun.my.easyexplorer.ui.view.CustomCircleView;
import fun.my.easyexplorer.utils.MountPointUtils;

public class MainActivity extends Activity {
    private static final int READ_EXTERNAL_STORAGE_REQUEST_CODE = 0;
    private List adapterDataList;
    private List<MountPoint> mountPoints;
    private MainRecyclerListAdapter adapter;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initVariables();
        initViews(savedInstanceState);
        loadData();
    }

    protected void initVariables() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            //申请READ_EXTERNAL_STORAGE权限
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    READ_EXTERNAL_STORAGE_REQUEST_CODE);
        }
        adapterDataList = new ArrayList();
        adapter = new MainRecyclerListAdapter(adapterDataList);

    }

    protected void initViews(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);
        recyclerView = (RecyclerView) findViewById(R.id.mount_recyclerView);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    protected void loadData() {
        adapterDataList.addAll(getMountedPoints(this));
        adapterDataList.add(new Object());
        adapter.notifyDataSetChanged();
    }

    private List getMountedPoints(Context context){
        MountPointUtils mountPointUtils = MountPointUtils.GetMountPointInstance(context);
        return mountPointUtils.getMountedPoint();
    }

}
