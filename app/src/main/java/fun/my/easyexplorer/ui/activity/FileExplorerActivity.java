package fun.my.easyexplorer.ui.activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
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

import fun.my.easyexplorer.R;
import fun.my.easyexplorer.model.Frame;
import fun.my.easyexplorer.ui.FileDividerItemDecoration;
import fun.my.easyexplorer.ui.adapter.FileListAdapter;
import fun.my.easyexplorer.ui.adapter.RecyclerListAdapter;
import fun.my.easyexplorer.utils.Utils;

/**
 * Created by admin on 2016/9/10.
 */
public class FileExplorerActivity extends BaseActivity {
    private static final int READ_EXTERNAL_STORAGE_REQUEST_CODE = 0;
    protected int scrollPosition, childTop;
    private ListView file_ListView;
    private RecyclerView recyclerView;
    private FileListAdapter fileListAdapter;
    private RecyclerListAdapter recyclerListAdapter;
    //当前文件列表
    private List<File> currentFileList;
    //打开文件顺序
    private ArrayList<Frame> cacheList;

    @Override
    protected void initVariables() {
        cacheList = new ArrayList();
        String path = getIntent().getStringExtra("file");
        File currentFile;
        if (path != null || !path.equals("")) {
            currentFile = new File(path);
            cacheList.addAll(getParentsFiles(currentFile));
        } else {
            currentFile = new File(File.separator);
        }

        cacheList.add(new Frame(currentFile, new Point()));
        currentFileList = new ArrayList<>();
        scrollPosition = childTop = 0;
        //file list adapter
        fileListAdapter = new FileListAdapter(FileExplorerActivity.this, currentFileList);

        //路径栏
        recyclerListAdapter = new RecyclerListAdapter(cacheList);
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
        //添加路径栏监听事件
        recyclerListAdapter.setOnItemClickListener(new RecyclerListAdapter.OnItemClickListener() {
                                                       @Override
                                                       public void onItemClick(View view, int position) {
                                                           int size = cacheList.size();
                                                           for (int i = size - 1; i > position; i--) {
                                                               cacheList.remove(i);
                                                           }
                                                           loadData();
                                                       }
                                                   }

        );
    }

    @Override
    protected void loadData() {
        int size = cacheList.size();
        //加载路径栏
        loadPathView(cacheList);
        //加载file list
        loadFileListView(cacheList.get(size - 1));

    }

    //获取父文件列表
    private ArrayList<Frame> getParentsFiles(File currentFile) {
        ArrayList<Frame> list = new ArrayList();
        File f = currentFile;
        while ((f = f.getParentFile()) != null) {
            list.add(0, new Frame(f, new Point()));
        }
        return list;
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
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, R.layout.main_recycler_item1, strings);
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
        lp.width = (int) (Utils.getWindowWidth(this) * 0.9);
        alertDialog.getWindow().setAttributes(lp);
    }

    private void openDir(File f) {
        //缓存列表位置
        cacheList.get(cacheList.size() - 1).getPoint().set(scrollPosition, childTop);
        //添加文件
        cacheList.add(new Frame(f, new Point()));
        loadData();
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

    private void loadPathView(List list) {
        recyclerListAdapter.notifyDataSetChanged();
        recyclerView.smoothScrollToPosition(list.size() - 1);
    }

    public void loadFileListView(Frame frame) {
        File currentFile = frame.getFile();
        Point p = frame.getPoint();
        currentFileList.clear();
        File[] files = currentFile.listFiles();
        if (files != null) {
            currentFileList.addAll(Arrays.asList(files));
        }
        fileListAdapter.notifyDataSetChanged();

        if (p == null) {
            scrollPosition = 0;
            childTop = 0;
        } else {
            scrollPosition = p.x;
            childTop = p.y;
        }
        file_ListView.setSelectionFromTop(scrollPosition, childTop);
    }

    @Override
    public void onBackPressed() {
        int size = cacheList.size();
        if (size == 0) {
            super.onBackPressed();
        } else {
            cacheList.remove(cacheList.size() - 1);
            loadData();
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
