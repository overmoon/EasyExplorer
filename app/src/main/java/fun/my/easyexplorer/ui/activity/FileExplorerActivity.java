package fun.my.easyexplorer.ui.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;

import fun.my.easyexplorer.R;
import fun.my.easyexplorer.model.MountPoint;
import fun.my.easyexplorer.ui.FileDividerItemDecoration;
import fun.my.easyexplorer.ui.adapter.FileListAdapter;
import fun.my.easyexplorer.ui.adapter.RecyclerListAdapter;
import fun.my.easyexplorer.utils.MountPointUtils;
import fun.my.easyexplorer.utils.Utils;

/**
 * Created by admin on 2016/9/10.
 */
public class FileExplorerActivity extends BaseActivity {
    private static final int READ_EXTERNAL_STORAGE_REQUEST_CODE = 0;
    private ListView file_ListView;
    private RecyclerView recyclerView;
    private FileListAdapter fileListAdapter;
    private RecyclerListAdapter recyclerListAdapter;
    private List<File> files;
    private List<File> currentFileList;
    private List<MountPoint> mountPoints;
    private File currentFile;
    protected int scrollPosition, childTop;
    private Stack<Point> scrollPosStack;

    @Override
    protected void initVariables() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            //申请READ_EXTERNAL_STORAGE权限
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    READ_EXTERNAL_STORAGE_REQUEST_CODE);
        }
        MountPointUtils mountPointUtils = MountPointUtils.GetMountPointInstance(this);
        currentFileList = new ArrayList<>();
        mountPoints = mountPointUtils.getMountedPoint();
        for (MountPoint p : mountPoints) {
            currentFileList.add(p.getFile());
        }
        scrollPosition = childTop = 0;
        scrollPosStack = new Stack<>();
        //file list
        fileListAdapter = new FileListAdapter(FileExplorerActivity.this, currentFileList);

        //guide view
        currentFile = new File("/storage");
        files = new ArrayList<>();
        files.add(new File("/"));
        files.add(currentFile);
//        List parseList = parsePath(currentFile);
//        if (parseList != null) {
//            files.addAll(parseList);
//        }
        recyclerListAdapter = new RecyclerListAdapter(files);
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setContentView(R.layout.activity_fileexplorer);

        //filelist init
        file_ListView = (ListView) findViewById(R.id.file_listView);
        file_ListView.setAdapter(fileListAdapter);
        file_ListView.setItemsCanFocus(true);
        file_ListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                openItem(position);
            }
        });
        file_ListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                listOnScrollChanged(view, scrollState);
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
        //recyclerView init
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView_horizontal);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(recyclerListAdapter);
        //设置Item增加、移除动画
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        //添加分割线
        FileDividerItemDecoration itemDecoration = new FileDividerItemDecoration(this, LinearLayoutManager.HORIZONTAL, 25);
        recyclerView.addItemDecoration(itemDecoration);
        recyclerListAdapter.setOnItemClickListener(new RecyclerListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (position != files.size() - 1) {
                    Point p = null;
                    for (int i = files.size() - 1; i > position; i--) {
                        files.remove(i);
                        if (scrollPosStack.empty())
                            p = null;
                        else
                            p = scrollPosStack.pop();
                    }
                    refreshGuideView();
                    refreshFileListView(files.get(position), p);
                }
            }
        });
    }

    @Override
    protected void loadData() {

    }

    private void listOnScrollChanged(AbsListView view, int scrollState) {
        //if the scroll is stop
        if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
            scrollPosition = file_ListView.getFirstVisiblePosition();
            View v = file_ListView.getChildAt(0);
            if (v != null) {
                childTop = v.getTop();
            }
        }
    }

    private void openItem(int position) {
        File f = (File) (fileListAdapter.getItem(position));
        if (f.canRead()) {
            if (f.isDirectory()) {
                openDir(f);
            } else {
                openFile(f);
            }
        } else {
            Toast.makeText(this, "File: " + f.getAbsolutePath() + " can not be read", Toast.LENGTH_SHORT).show();
        }
    }

    private void openFile(File f) {
        String mimeType = Utils.getMimeType(f);
        if (mimeType == null) {
//            Utils.messageShort(this, "未找到合适的应用打开文件，请从列表中选择");
            showChooseDialog(f);
            return;
        }
        openMimeType(f, mimeType);
    }

    void openMimeType(File f, String mimeType) {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(f), mimeType);
        startActivity(intent);
    }

    void showChooseDialog(final File f) {
        LayoutInflater layoutInflater = getLayoutInflater();
        View dialog = layoutInflater.inflate(R.layout.dialog, (ViewGroup) findViewById(R.id.dialog));
        ListView listView = (ListView) dialog.findViewById(R.id.dialog_listView);
        final String[] strings = getResources().getStringArray(R.array.mimeType_array);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, R.layout.mount_recycler_item, strings);
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String mimeType = Utils.type_map.get(strings[position]);
                openMimeType(f, mimeType);
            }
        });

        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle("打开为")
                .setView(dialog)
                .create();

        alertDialog.show();
        WindowManager.LayoutParams lp = alertDialog.getWindow().getAttributes();
        lp.width= (int) (Utils.getWindowWidth(this)*0.9);
        alertDialog.getWindow().setAttributes(lp);
    }

    private void openDir(File f) {
        scrollPosStack.add(new Point(scrollPosition, childTop));
        addGuideView(f);
        refreshFileListView(f, null);
    }

    private List<String> parsePath(File file) {
        if (null != file) {
            String path = file.getAbsolutePath();
            String[] paths = path.split(File.separator);
            paths[0] = File.separator;
            return Arrays.asList(paths);
        } else {
            return null;
        }
    }

    private void addGuideView(File f) {
        files.add(f);
        refreshGuideView();
    }

    private void removeGuideView() {
        files.remove(files.size() - 1);
        refreshGuideView();
    }

    private void refreshGuideView() {
        recyclerListAdapter.notifyDataSetChanged();
        recyclerView.smoothScrollToPosition(files.size() - 1);
    }

    public void refreshFileListView(File f, Point p) {
        currentFile = f;
        currentFileList.clear();
        currentFileList.addAll(Arrays.asList(f.listFiles()));
        fileListAdapter.notifyDataSetChanged();

        if (p == null) {
            scrollPosition = 0;
            childTop = 0;
        }else{
            scrollPosition = p.x;
            childTop = p.y;
        }
        file_ListView.setSelectionFromTop(scrollPosition, childTop);
    }

    @Override
    public void onBackPressed() {
        if (currentFile == null || currentFile.getParentFile() == null) {
            super.onBackPressed();
        } else {
            Point p = null;
            if (!scrollPosStack.empty()) {
                p = scrollPosStack.pop();
            }
            removeGuideView();
            refreshFileListView(currentFile.getParentFile(), p);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        switch (requestCode) {
            case READ_EXTERNAL_STORAGE_REQUEST_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Get Permission", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Get Permission failed", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }


}
