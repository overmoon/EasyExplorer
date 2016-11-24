package fun.my.easyexplorer.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import fun.my.easyexplorer.R;
import fun.my.easyexplorer.model.AppInfo;
import fun.my.easyexplorer.model.ValuePair;
import fun.my.easyexplorer.utils.JsonUtils;
import fun.my.easyexplorer.utils.MountPointUtils;
import fun.my.easyexplorer.utils.Utils;

/**
 * Created by admin on 2016/11/11.
 */

public class EditDialogActivity extends BaseActivity {


    private EditText path_dialog_editTextView;
    private ValuePair<String, String> valuePair;
    private int position;
    private AppInfo appInfo;

    @Override
    protected void initVariables() {
        Intent intent = getIntent();
        String appName = intent.getStringExtra("appName");
        String packageName = intent.getStringExtra("packageName");
        valuePair = (ValuePair<String, String>) intent.getSerializableExtra("valuePair");
        if (valuePair == null) {
            valuePair = new ValuePair<>();
        }
        position = intent.getIntExtra("position", -1);
        appInfo = new AppInfo(appName, packageName);
        //构建appinfo对象半成品
        appInfo.setValuePairList(new ArrayList<ValuePair>() {
            {
                add(valuePair);
            }
        });
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setContentView(R.layout.tag_edit_dialog);
        initDialog(this);
    }

    @Override
    protected void loadData() {
    }

    //初始化dialog
    private void initDialog(final Context context) {
        //init dialog view component
        final ImageView path_dialog_imageView = (ImageView) findViewById(R.id.path_dialog_imageView);
        final EditText tag_dialog_editTextView = (EditText) findViewById(R.id.tag_dialog_editTextView);
        path_dialog_editTextView = (EditText) findViewById(R.id.path_dialog_editTextView);
        String tagString = valuePair.getName();
        if (!TextUtils.isEmpty(tagString)) {
            tag_dialog_editTextView.setText(tagString);
            tag_dialog_editTextView.setSelection(tagString.length());
        }
        if (!TextUtils.isEmpty(valuePair.getValue())) {
            path_dialog_editTextView.setText(valuePair.getValue());
        }
        //路径选择按钮
        path_dialog_imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startFileActivityForResult(context);
            }
        });
        Button cancelButton = (Button) findViewById(R.id.cancelButton);
        Button confirmButton = (Button) findViewById(R.id.confirmButton);
        Button delButton = (Button) findViewById(R.id.delButton);
        //取消按钮，返回之前activity
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
                valuePair.setName(tag_dialog_editTextView.getText().toString());
                valuePair.setValue(path_dialog_editTextView.getText().toString());
                //异步处理
                new AsyncTask<Void, Void, Boolean>() {
                    @Override
                    protected Boolean doInBackground(Void... params) {
                        try {
                            return JsonUtils.changeAppInfo(EditDialogActivity.this, appInfo, position);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return false;
                    }

                    @Override
                    protected void onPostExecute(Boolean aBoolean) {
                        Context ctx = EditDialogActivity.this.getApplicationContext();
                        if (aBoolean) {
                            Utils.messageShort(ctx, "修改成功");
                            setResult(RESULT_OK);
                            finish();
                        } else {
                            Utils.messageShort(ctx, "修改失败");
                        }
                    }
                }.execute();

            }
        });
        delButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                appInfo.getValuePairList().clear();
                //异步删除
                new AsyncTask<Void, Void, Boolean>() {
                    @Override
                    protected Boolean doInBackground(Void... params) {
                        try {
                            return JsonUtils.changeAppInfo(EditDialogActivity.this, appInfo, position);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return false;
                    }

                    @Override
                    protected void onPostExecute(Boolean aBoolean) {
                        Context ctx = EditDialogActivity.this.getApplicationContext();
                        if (aBoolean) {
                            Utils.messageShort(ctx, "删除成功");
                            setResult(RESULT_OK);
                            finish();
                        } else {
                            Utils.messageShort(ctx, "删除成功");
                        }
                    }
                }.execute();
            }
        });

    }

    //打开路径选择activity
    private void startFileActivityForResult(Context context) {
        //如果有路径且存在，则打开原来设置的路径
        String path = valuePair.getValue();
        if (TextUtils.isEmpty(path) || !new File(path).isDirectory()) {
            path = MountPointUtils.GetMountPointInstance(context).getMemPath();
        }
        Intent intent = new Intent(context, FileExplorerActivity.class);
        intent.putExtra("isDir", true);
        intent.putExtra("path", path);
        startActivityForResult(intent, Utils.REQUEST_PATH);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Utils.REQUEST_PATH) {
            if (resultCode == RESULT_OK) {
                String path = data.getStringExtra("path");
                path_dialog_editTextView.setText(path);
                path_dialog_editTextView.setSelection(path.length());
                valuePair.setValue(path);
            }
        }
    }
}
