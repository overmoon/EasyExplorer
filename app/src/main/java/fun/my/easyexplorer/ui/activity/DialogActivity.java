package fun.my.easyexplorer.ui.activity;

import android.content.Context;
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
import fun.my.easyexplorer.ui.adapter.ListPopupAdapter;

/**
 * Created by admin on 2016/11/11.
 */

public class DialogActivity extends BaseActivity {
    private ListPopupWindow listPopupWindow;
    private ListPopupAdapter popupAdapter;
    private EditText appName_editText, tag_dialog_editTextView, path_dialog_editTextView;
    private ImageView dialog_imageView, popItem_dialog_imageView;
    private View dialog_divider1;
    private boolean isShowing;

    @Override
    protected void initVariables() {
        isShowing = false;
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
        //init dialog view
        dialog_imageView = (ImageView) findViewById(R.id.appIcon_dialog_imageView);
        popItem_dialog_imageView = (ImageView) findViewById(R.id.popItem_dialog_imageView);
        appName_editText = (EditText) findViewById(R.id.appName_dialog_editTextView);
        tag_dialog_editTextView = (EditText) findViewById(R.id.tag_dialog_editTextView);
        path_dialog_editTextView = (EditText) findViewById(R.id.path_dialog_editTextView);
        dialog_divider1 = findViewById(R.id.dialog_divider1);

        dialog_imageView.setImageResource(R.mipmap.ic_launcher);
        dialog_imageView.setTag(R.mipmap.ic_launcher);
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
        path_dialog_editTextView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

            }
        });

        popItem_dialog_imageView.setFocusable(true);
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

}
