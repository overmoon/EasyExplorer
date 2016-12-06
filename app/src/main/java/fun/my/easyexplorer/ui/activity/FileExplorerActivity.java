package fun.my.easyexplorer.ui.activity;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

import fun.my.easyexplorer.R;
import fun.my.easyexplorer.model.Frame;
import fun.my.easyexplorer.ui.FileDividerItemDecoration;
import fun.my.easyexplorer.ui.adapter.FileListAdapter;
import fun.my.easyexplorer.ui.adapter.RecyclerListAdapter;
import fun.my.easyexplorer.utils.FileComparator;
import fun.my.easyexplorer.utils.Utils;

/**
 * Created by admin on 2016/9/10.
 */
public class FileExplorerActivity extends BaseActivity {
    private static final int READ_EXTERNAL_STORAGE_REQUEST_CODE = 0;
    private static final String DEFAULT_FILE_SORT_TYPE = "name";
    protected int scrollPosition, childTop;
    private ListView file_ListView;
    private RecyclerView recyclerView;
    private LinearLayout edit_linearLayout, edit2_linearLayout;
    private FileListAdapter fileListAdapter;
    private RecyclerListAdapter recyclerListAdapter;
    //当前文件列表
    private ArrayList<File> currentFileList;
    //打开文件顺序
    private ArrayList<File> cacheList;
    private Stack<Frame> fileStack;
    private MODE mode = MODE.NORMAL;
    private LinearLayout buttonLayout;

    @Override
    protected void initVariables() {
        //缓存文件点击
        fileStack = new Stack<>();
        cacheList = new ArrayList<>();
        String path = getIntent().getStringExtra("path");
        mode = (MODE) getIntent().getSerializableExtra("mode");
        File currentFile;
        if (!TextUtils.isEmpty(path)) {
            currentFile = new File(path);
        } else {
            currentFile = new File(File.separator);
        }
        // 添加当前文件
        fileStack.add(new Frame(currentFile, new Point()));
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
        //底部按钮栏
        buttonLayout = (LinearLayout) findViewById(R.id.buttonLinearLayout);
        //判断是否显示按钮栏
        if (mode == MODE.DIR) {
            buttonLayout.setVisibility(View.VISIBLE);
        }
        //取消按钮
        Button cancelButton = (Button) findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });
        //确认按钮
        Button confirmButton = (Button) findViewById(R.id.confirm_button);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                //获取当前路径
                String path = getCurrentPath();
                intent.putExtra("path", path);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        //fileList init
        file_ListView = (ListView) findViewById(R.id.file_listView);
        file_ListView.setAdapter(fileListAdapter);
        file_ListView.setItemsCanFocus(true);
        file_ListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //编辑模式下，转化为点击checkbox
                if (mode == MODE.EDIT) {
                    fileListAdapter.setCheckBoxClicked(view, position);
                } else {
                    openItem(position);
                }
            }
        });
        file_ListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                intoEditMode(position);
                return true;
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
                                                           File f = cacheList.get(position);
                                                           //压入栈中
                                                           fileStack.push(new Frame(f, new Point()));
                                                           loadData();
                                                       }
                                                   }
        );

        //编辑菜单栏
        initEditBars();

    }

    @Override
    protected void loadData() {
        cacheList.clear();
        cacheList.addAll(getParentFilesIncludingSelf(fileStack.peek().getFile()));

        //加载路径栏
        loadPathView(cacheList);
        //加载file list
        loadFileListView(fileStack.peek());
    }

    //初始化colorStateList
    private ColorStateList initButtonStateColorList(int normal, int pressed, int focused, int unable) {
        int[] colors = new int[]{pressed, focused, normal, focused, unable, normal};
        int[][] stateList = new int[6][];
        stateList[0] = new int[]{android.R.attr.state_pressed, android.R.attr.state_enabled};
        stateList[1] = new int[]{android.R.attr.state_enabled, android.R.attr.state_focused};
        stateList[2] = new int[]{android.R.attr.state_enabled};
        stateList[3] = new int[]{android.R.attr.state_focused};
        stateList[4] = new int[]{android.R.attr.state_window_focused};
        stateList[5] = new int[]{};
        return new ColorStateList(stateList, colors);
    }

    //初始化编辑栏
    private void initEditBars() {
        //初始化layout
        edit_linearLayout = (LinearLayout) findViewById(R.id.editBar_linearLayout);
        edit2_linearLayout = (LinearLayout) findViewById(R.id.editBar2_linearLayout);
        //初始化colorStateList
        int colorPressed = Utils.getThemeAttrColor(this, R.attr.myColorAccent);
        int colorNormal = getResources().getColor(R.color.colorGrayWhite);
        ColorStateList stateList = initButtonStateColorList(colorNormal, colorPressed, colorNormal, colorNormal);

        //初始化编辑栏1
        initEditBar1(stateList);
        //初始化编辑栏2
        initEditBar2(stateList);

    }

    // 初始化编辑栏1
    private void initEditBar1(ColorStateList stateList) {
        Drawable drawable = Utils.tintDrawable(this, R.mipmap.edit, stateList);
        TextView newTextView = (TextView) findViewById(R.id.newFolder_textView);
        //新建文件夹
        newTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(FileExplorerActivity.this);
                View view = getLayoutInflater().inflate(R.layout.new_folder_dialog, null);
                builder.setView(view);
                final Dialog dialog = builder.create();
                final EditText editText = (EditText) view.findViewById(R.id.folderName_editText);
                editText.setText(R.string.new_folder);
                editText.setSelection(0, editText.getText().length());
                // 取消按钮
                Button cancelButton = (Button) view.findViewById(R.id.cancel_button);
                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                // 确定按钮
                Button confirmButton = (Button) view.findViewById(R.id.confirm_button);
                confirmButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String folderName = editText.getText().toString();
                        if (TextUtils.isEmpty(folderName)) {
                            Utils.messageShort(FileExplorerActivity.this, "文件夹不能为空");
                        } else {
                            File f = new File(getCurrentPath() + File.separator + folderName);
                            if (f.exists()) {
                                Utils.messageShort(FileExplorerActivity.this, "文件夹已存在，请换个文件夹名");
                            } else {
                                f.mkdir();
                                loadData();
                                Utils.messageShort(FileExplorerActivity.this, "文件夹创建成功");
                                dialog.dismiss();
                            }
                        }
                    }
                });
                dialog.show();
            }
        });
        TextView searchTextView = (TextView) findViewById(R.id.search_textView);
        TextView sortTextView = (TextView) findViewById(R.id.sort_textView);
        TextView menuTextView = (TextView) findViewById(R.id.menu_textView);

        setColorStateList(newTextView, stateList, null, drawable, null, null);
        setColorStateList(searchTextView, stateList, null, drawable, null, null);
        setColorStateList(sortTextView, stateList, null, drawable, null, null);
        setColorStateList(menuTextView, stateList, null, drawable, null, null);
    }

    //初始化编辑栏2
    private void initEditBar2(ColorStateList stateList) {
        Drawable drawable = Utils.tintDrawable(this, R.mipmap.edit, stateList);
        TextView copyTextView = (TextView) findViewById(R.id.copy_textView);
        TextView delTextView = (TextView) findViewById(R.id.del_textView);
        TextView moveTextView = (TextView) findViewById(R.id.move_textView);
        TextView moreTextView = (TextView) findViewById(R.id.more_textView);

        setColorStateList(copyTextView, stateList, null, drawable, null, null);
        setColorStateList(delTextView, stateList, null, drawable, null, null);
        setColorStateList(moveTextView, stateList, null, drawable, null, null);
        setColorStateList(moreTextView, stateList, null, drawable, null, null);
    }

    private void setColorStateList(TextView view, ColorStateList colorStateList, Drawable left, Drawable top, Drawable right, Drawable bottom) {
        view.setTextColor(colorStateList);
        view.setCompoundDrawablesWithIntrinsicBounds(left, top, right, bottom);
    }

    private String getCurrentPath() {
        return fileStack.peek().getFile().getAbsolutePath();
    }

    public void sortFileBy(String type, ArrayList<File> files) {
        Collections.sort(files, FileComparator.getComparator(type));
    }

    //获取父文件列表包括自己
    private ArrayList<File> getParentFilesIncludingSelf(File currentFile) {
        ArrayList<File> list = new ArrayList<File>();
        list.add(currentFile);
        File f = currentFile;
        while ((f = f.getParentFile()) != null) {
            list.add(0, f);
        }
        return list;
    }

    //判断滚动条状态
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
            } else if (mode == MODE.NORMAL) { //正常模式下打开文件
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

    private void openDir(File f) {
        //缓存列表位置
        fileStack.peek().getPoint().set(scrollPosition, childTop);
        //添加文件
        fileStack.push(new Frame(f, new Point()));
        loadData();
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

    private void loadPathView(List<File> list) {
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
        //默认排序
        sortFileBy(DEFAULT_FILE_SORT_TYPE, currentFileList);
        fileListAdapter.notifyDataSetChangedInitList();

        if (p == null) {
            scrollPosition = 0;
            childTop = 0;
        } else {
            scrollPosition = p.x;
            childTop = p.y;
        }
        file_ListView.setSelectionFromTop(scrollPosition, childTop);
    }

    private ArrayList<File> getDirFiles(File[] files) {
        ArrayList<File> dirFileList = new ArrayList<>();
        for (File f : files) {
            if (f.isDirectory()) {
                dirFileList.add(f);
            }
        }
        return dirFileList;
    }

    @Override
    public void onBackPressed() {
        if (isInEditMode()) {
            outEditMode();
            return;
        }
        int size = fileStack.size();
        if (size <= 1) {
            super.onBackPressed();
        } else {
            fileStack.pop();
            loadData();
        }
    }

    //判断是否在编辑
    private boolean isInEditMode() {
        return fileListAdapter.getIsEdit();
    }

    //进入编辑模式
    private void intoEditMode(int position) {
        mode = MODE.EDIT;
        fileListAdapter.setIsEdit(true);
        fileListAdapter.setIsChecked(position, true);
        fileListAdapter.notifyDataSetChanged();
        edit_linearLayout.setVisibility(View.INVISIBLE);
        edit2_linearLayout.setVisibility(View.VISIBLE);
    }

    //退出编辑模式
    private void outEditMode() {
        mode = MODE.NORMAL;
        fileListAdapter.setIsEdit(false);
        fileListAdapter.notifyDataSetChangedInitList();
        edit2_linearLayout.setVisibility(View.GONE);
        edit_linearLayout.setVisibility(View.VISIBLE);
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

    public enum MODE {
        DIR, NORMAL, EDIT, MOVE, COPY, SEARCH
    }

}
