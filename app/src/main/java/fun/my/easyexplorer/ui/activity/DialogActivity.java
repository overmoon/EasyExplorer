package fun.my.easyexplorer.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.ListPopupWindow;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.PopupWindow;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import fun.my.easyexplorer.R;
import fun.my.easyexplorer.model.AppInfo;
import fun.my.easyexplorer.model.ValuePair;
import fun.my.easyexplorer.ui.adapter.ListPopupAdapter;
import fun.my.easyexplorer.utils.JsonUtils;
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
    private AppInfo appInfoGlobal;
    private ValuePair valuePairGlobal;
    private List<AppInfo> appInfos;
    private boolean trigger;

    @Override
    protected void initVariables() {
        appInfoGlobal = new AppInfo();
        valuePairGlobal = new ValuePair();
        appInfos = new ArrayList<>();
        trigger = true;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setContentView(R.layout.easypath_add_dialog);
        initDialog(this);
        initListPopupView(this);
    }

    @Override
    protected void loadData() {
        new AsyncTask<Object, Void, List>() {

            @Override
            protected List doInBackground(Object[] params) {
                Context context = (Context) params[0];
                return Utils.getAppInfoList(context, Utils.FILTER_THIRD_APP);
            }

            @Override
            protected void onPostExecute(List o) {
                appInfos.clear();
                appInfos.addAll(o);
                popupAdapter.notifyDataSetChanged();
            }
        }.execute(this);
    }

    //初始化popupwindow
    private void initListPopupView(Context context) {
        //init listpopup view
        listPopupWindow = new ListPopupWindow(context);

        //init adapter
        popupAdapter = new ListPopupAdapter(context, appInfos);

        listPopupWindow.setAdapter(popupAdapter);
        listPopupWindow.setHeight(400);
        listPopupWindow.setModal(false);
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
                appName_editText.setSelection(appInfo.getAppName().length());
                listPopupWindow.dismiss();
                appInfoGlobal.setAppName(appInfo.getAppName());
                appInfoGlobal.setPackageName(appInfo.getPackageName());
                appInfoGlobal.setDrawable(appInfo.getDrawable());
            }
        });
        listPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                popItem_dialog_imageView.setImageResource(R.drawable.popdown_selector);
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
                        if (count == 0 && listPopupWindow.isShowing()) {
                            listPopupWindowDismiss();
                        }
                        if (count != 0 && !listPopupWindow.isShowing()) {
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
                if (!listPopupWindow.isShowing() && trigger) {
                    listPopupWindowShow();
                } else {
                    trigger = true;
                }
            }
        });

        Button cancelButton = (Button) findViewById(R.id.cancelButton);
        Button confirmButton = (Button) findViewById(R.id.confirmButton);
        //取消按钮，返回之前
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });
        //确认按钮，保存数据
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                appInfoGlobal.setAppName(appName_editText.getText().toString());
                //获取<tag, path>
                valuePairGlobal.setName(tag_dialog_editTextView.getText().toString());
                valuePairGlobal.setValue(path_dialog_editTextView.getText().toString());
                //设置appInfo的list
                appInfoGlobal.setValuePairList(new ArrayList<ValuePair>() {
                    {
                        add(valuePairGlobal);
                    }
                });
                //异步保存
                new AsyncTask<Void, Void, Boolean>() {
                    @Override
                    protected Boolean doInBackground(Void... params) {
                        try {
                            return JsonUtils.saveAppInfo(DialogActivity.this, appInfoGlobal);
                        } catch (IOException e) {
                            e.printStackTrace();
                            return false;
                        }
                    }

                    @Override
                    protected void onPostExecute(Boolean aBoolean) {
                        Context ctx = DialogActivity.this.getApplicationContext();
                        if (aBoolean) {
                            Utils.messageShort(ctx, "保存成功");
                            finish();
                        } else {
                            Utils.messageShort(ctx, "保存失败");
                        }
                    }
                }.execute();

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
        if (listPopupWindow != null) {
            listPopupWindow.show();
            trigger = false;
            popItem_dialog_imageView.setImageResource(R.drawable.popup_selector);
        }
    }

    void listPopupWindowDismiss() {
        if (listPopupWindow != null) {
            listPopupWindow.dismiss();
            trigger = true;
            popItem_dialog_imageView.setImageResource(R.drawable.popdown_selector);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE) {
            if (data != null) {
                Uri uri = data.getData();
                String path = UriUtils.getPath(this, uri);
                dialog_imageView.setImageDrawable(Utils.getDrawableFromFile(path));
                appInfoGlobal.setDrawableFile(path);
            }
        } else if (requestCode == REQUEST_PATH) {
            if (resultCode == RESULT_OK) {
                String path = data.getStringExtra("path");
                path_dialog_editTextView.setText(path);
                path_dialog_editTextView.setSelection(path.length());
            }
        }
    }
}
