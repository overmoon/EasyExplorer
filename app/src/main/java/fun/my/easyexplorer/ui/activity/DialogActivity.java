package fun.my.easyexplorer.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.ListPopupWindow;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.PopupWindow;

import fun.my.easyexplorer.R;
import fun.my.easyexplorer.model.AppInfo;
import fun.my.easyexplorer.model.ValuePair;
import fun.my.easyexplorer.ui.adapter.ListPopupAdapter;
import fun.my.easyexplorer.utils.UriUtils;
import fun.my.easyexplorer.utils.Utils;

/**
 * Created by admin on 2016/11/11.
 */

public class DialogActivity extends BaseActivity {
    private final static int REQUEST_IMAGE = 0;
    private final static int REQUEST_PATH = 1;

    private ListPopupWindow listPopupWindow;
    private ListPopupAdapter popupAdapter;
    private EditText appName_editText, tag_dialog_editTextView, path_dialog_editTextView;
    private ImageView dialog_imageView, popItem_dialog_imageView, path_dialog_imageView;
    private View dialog_divider1;
    private boolean isShowing;
    private AppInfo appInfoGlobal;
    private ValuePair valuePairGlobal;

    @Override
    protected void initVariables() {
        isShowing = false;
        appInfoGlobal = new AppInfo();
        valuePairGlobal = new ValuePair();
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setContentView(R.layout.easypath_add_dialog);
        initDialog(this);
        initListPopupView(this);
    }

    @Override
    protected void loadData() {

    }

    //初始化popupwindow
    private void initListPopupView(Context context) {
        //init listpopup view
        listPopupWindow = new ListPopupWindow(context);

        //init adapter
        popupAdapter = new ListPopupAdapter(context);

        listPopupWindow.setAdapter(popupAdapter);
        listPopupWindow.setHeight(400);
        listPopupWindow.setModal(true);
        listPopupWindow.setAnchorView(dialog_divider1);
        listPopupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                AppInfo appInfo = (AppInfo) parent.getItemAtPosition(position);
                Object object = dialog_imageView.getTag();
                int i = object == null ? 0 : (int) object;
                if (i == R.mipmap.ic_launcher) {
                    dialog_imageView.setImageDrawable(appInfo.getDrawable());
                }
                //标记被点击
                appName_editText.setTag(true);
                appName_editText.setText(appInfo.getAppName());
                listPopupWindow.dismiss();
                appInfoGlobal.setAppName(appInfo.getAppName());
                appInfoGlobal.setPackageName(appInfo.getPackageName());
                appInfoGlobal.setDrawable(appInfo.getDrawable());
            }
        });
        listPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                listPopupWindowDismiss();
            }
        });

    }

    //初始化dialog
    private void initDialog(final Context context) {
        //init dialog view component
        dialog_imageView = (ImageView) findViewById(R.id.appIcon_dialog_imageView);
        popItem_dialog_imageView = (ImageView) findViewById(R.id.popItem_dialog_imageView);
        path_dialog_imageView = (ImageView) findViewById(R.id.path_dialog_imageView);
        appName_editText = (EditText) findViewById(R.id.appName_dialog_editTextView);
        tag_dialog_editTextView = (EditText) findViewById(R.id.tag_dialog_editTextView);
        path_dialog_editTextView = (EditText) findViewById(R.id.path_dialog_editTextView);
        dialog_divider1 = findViewById(R.id.dialog_divider1);
        //类别图片按钮
        dialog_imageView.setImageResource(R.mipmap.ic_launcher);
        dialog_imageView.setTag(R.mipmap.ic_launcher);
        dialog_imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImageSelector();
            }
        });
        //路径图片按钮
        path_dialog_imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DialogActivity.this, FileExplorerActivity.class);
                intent.putExtra("isDir", true);
                startActivityForResult(intent, REQUEST_PATH);
            }
        });
        //auto text监听
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Object object = appName_editText.getTag();
                boolean bool = object != null && (boolean) object;
                //判断是否通过popupWindow选择
                if (bool) {
                    appName_editText.setTag(false);
                    return;
                }
                popupAdapter.getFilter().filter(s, new Filter.FilterListener() {
                    @Override
                    public void onFilterComplete(int count) {
                        //if adapter data not empty and popup window not shown
                        if (count == 0 && isShowing) {
                            listPopupWindowDismiss();
                        }
                        if (count != 0 && !isShowing) {
                            listPopupWindowShow();
                        }
                    }
                });
            }

            @Override
            public void afterTextChanged(Editable s) {

            }

        };
        appName_editText.addTextChangedListener(textWatcher);
        //下拉框按钮
        popItem_dialog_imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isShowing) {
                    listPopupWindowShow();
                } else {
                    listPopupWindowDismiss();
                }
            }
        });
    }

    //打开图片选择窗口
    private void openImageSelector() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);//ACTION_OPEN_DOCUMENT
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/jpeg");
        startActivityForResult(intent, REQUEST_IMAGE);
    }

    void listPopupWindowShow() {
        listPopupWindow.show();
        isShowing = true;
        popItem_dialog_imageView.setImageResource(R.drawable.popup_selector);
    }

    void listPopupWindowDismiss() {
        listPopupWindow.dismiss();
        isShowing = false;
        popItem_dialog_imageView.setImageResource(R.drawable.popdown_selector);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE) {
            Uri uri = data.getData();
            String path = UriUtils.getPath(this, uri);
            dialog_imageView.setImageDrawable(Utils.getDrawableFromFile(path));
            appInfoGlobal.setDrawableFile(path);
        } else if (requestCode == REQUEST_PATH) {
            if (resultCode == RESULT_OK) {
                String path = data.getStringExtra("path");
                path_dialog_editTextView.setText(path);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
