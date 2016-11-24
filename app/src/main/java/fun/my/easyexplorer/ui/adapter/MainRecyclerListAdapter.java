package fun.my.easyexplorer.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.ListPopupWindow;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import fun.my.easyexplorer.R;
import fun.my.easyexplorer.model.AppInfo;
import fun.my.easyexplorer.model.MountPoint;
import fun.my.easyexplorer.model.ValuePair;
import fun.my.easyexplorer.ui.activity.AddDialogActivity;
import fun.my.easyexplorer.ui.activity.EditDialogActivity;
import fun.my.easyexplorer.ui.activity.FileExplorerActivity;
import fun.my.easyexplorer.ui.view.CustomCircleView;
import fun.my.easyexplorer.utils.JsonUtils;
import fun.my.easyexplorer.utils.Utils;
import my.fun.asyncload.imageloader.utils.DensityUtils;

/**
 * Created by admin on 2016/11/6.
 */

public class MainRecyclerListAdapter extends RecyclerView.Adapter<RecyclerListViewHolder> {
    public final static int MOUNT_POINT = 0;
    public final static int NORMAL_FILE = 1;
    public final static int ADD_BUTTON = 2;
    // 手势识别
    GestureDetector detector;
    int downX, downY;
    boolean pressed = false;
    private Context context;
    private List objList;
    private CustomCircleView customCircleView;
    private TextView titleTextView, capacityTextView, pathTextView, appTextView, packageTextView;
    private ImageView iconImageView, addImageView, delImageView;
    private Button addButton;
    private LinearLayout list_item2_linearLayout;
    private OnItemClickedListener itemClickedListener;
    private OnItemLongClickedListener itemLongClickedListener;
    private AlertDialog dialog;
    private ListPopupWindow listPopupWindow;
    private List<AppInfo> appInfos;
    private ListPopupAdapter popupAdapter;
    private EditText appName_editText, tag_dialog_editTextView, path_dialog_editTextView;
    private ImageView dialog_imageView;
    private View dialog_divider1;
    private int margin;

    public MainRecyclerListAdapter(List objList) {
        this.objList = objList;
    }

    public MainRecyclerListAdapter(Context context, List objList) {
        this.context = context;
        this.objList = objList;

        //获取主题属性颜色
//        typedValue = new TypedValue();
//        context.getTheme().resolveAttribute(R.attr.myColorPrimaryLight, typedValue, true);
    }

    @Override
    public RecyclerListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View item;
        RecyclerListViewHolder viewHolder = null;
        if (viewType == MOUNT_POINT) {
            item = LayoutInflater.from(parent.getContext()).inflate(R.layout.main_recycler_item1, parent, false);
            viewHolder = new RecyclerListViewHolder(item);
            viewHolder.set(R.id.customCircleView);
            viewHolder.set(R.id.titleTextView);
            viewHolder.set(R.id.capacityTextView);
            viewHolder.set(R.id.pathTextView);
        } else if (viewType == NORMAL_FILE) {
            item = LayoutInflater.from(parent.getContext()).inflate(R.layout.main_recycler_item2, parent, false);
            viewHolder = new RecyclerListViewHolder(item);
            viewHolder.set(R.id.iconImageView);
            viewHolder.set(R.id.appTextView);
            viewHolder.set(R.id.packageTextView);
            viewHolder.set(R.id.delImageView);
            viewHolder.set(R.id.addImageView);
            viewHolder.set(R.id.list_item2_linearLayout);
        } else if (viewType == ADD_BUTTON) {
            item = LayoutInflater.from(parent.getContext()).inflate(R.layout.main_recycler_item3, parent, false);
            viewHolder = new RecyclerListViewHolder(item);
            viewHolder.set(R.id.addButton);
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerListViewHolder holder, final int position) {
        Object object = objList.get(position);
        int type = getItemViewType(position);
        if (type == MOUNT_POINT) {
            customCircleView = holder.get(R.id.customCircleView);
            titleTextView = holder.get(R.id.titleTextView);
            capacityTextView = holder.get(R.id.capacityTextView);
            pathTextView = holder.get(R.id.pathTextView);

            MountPoint mountPoint = (MountPoint) object;
            File f = mountPoint.getFile();
            titleTextView.setText(mountPoint.getDescription());
            long maxSize = f.getTotalSpace();
            long freeSize = f.getFreeSpace();
            long usedSize = maxSize - freeSize;
            double usedSizeGb = Utils.getNDegree(Utils.byteToGB(usedSize), 2);
            double maxSizeGb = Utils.getNDegree(Utils.byteToGB(maxSize), 2);
            capacityTextView.setText(usedSizeGb + " GB / " + maxSizeGb + " GB");
            pathTextView.setText(f.getAbsolutePath());
            double percent = Utils.getNDegree((usedSizeGb / maxSizeGb), 2);
            customCircleView.setmPercent((float) percent);

        } else if (type == NORMAL_FILE) {
            iconImageView = holder.get(R.id.iconImageView);
            appTextView = holder.get(R.id.appTextView);
            packageTextView = holder.get(R.id.packageTextView);
            list_item2_linearLayout = holder.get(R.id.list_item2_linearLayout);
            addImageView = holder.get(R.id.addImageView);
            delImageView = holder.get(R.id.delImageView);

            AppInfo appInfo = (AppInfo) object;
            //设置item2点击事件
            setOnclickListener(appInfo);
            Drawable drawable = appInfo.getDrawable();
            if (drawable == null) {
                iconImageView.setImageResource(R.mipmap.ic_launcher);
            } else {
                iconImageView.setImageDrawable(drawable);
            }
            appTextView.setText(appInfo.getAppName());
            packageTextView.setText(appInfo.getPackageName());
            //添加subItems
            addSubItems(list_item2_linearLayout, appInfo);

        } else {
            addButton = holder.get(R.id.addButton);
            addButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startAddDialogForResult(context);
                }
            });
        }

        holder.itemView.setOnClickListener(new View.OnClickListener()

                                           {
                                               @Override
                                               public void onClick(View v) {
                                                   if (itemClickedListener != null) {
                                                       itemClickedListener.onItemClicked(v, position);
                                                   }
                                               }
                                           }

        );

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener()

                                               {
                                                   @Override
                                                   public boolean onLongClick(View v) {
                                                       if (itemLongClickedListener != null) {
                                                           itemLongClickedListener.onItemLongClicked(v, position);
                                                       }
                                                       return true;
                                                   }
                                               }

        );
    }

    //设置item2中的点击事件
    private void setOnclickListener(final AppInfo appInfo) {
        //添加子项目按钮监听事件
        addImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startEditDialogForResult(context, v, appInfo, -1);
            }
        });
        //删除项目按钮监听事件
        delImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog alertDialog = new AlertDialog.Builder(context)
                        .setMessage("确认删除" + appInfo.getAppName() + "分类？")
                        .setPositiveButton(R.string.confirm_string, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(final DialogInterface dialog, int which) {
                                new AsyncTask<Void, Void, Boolean>() {
                                    @Override
                                    protected Boolean doInBackground(Void... params) {
                                        try {
                                            return JsonUtils.deleteAppInfo(context, appInfo);
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                        return false;
                                    }

                                    @Override
                                    protected void onPostExecute(Boolean aBoolean) {
                                        if (aBoolean) {
                                            objList.remove(appInfo);
                                            notifyDataSetChanged();
                                            Utils.messageShort(context, "删除成功");
                                        } else {
                                            Utils.messageShort(context, "删除失败");
                                        }
                                        dialog.dismiss();
                                    }
                                }.execute();
                            }
                        })
                        .setNegativeButton(R.string.cancel_string, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).create();
                alertDialog.show();
            }
        });
    }

    private void addSubItems(LinearLayout list_item2_linearLayout, final AppInfo appInfo) {
        final LayoutInflater layoutInflater = LayoutInflater.from(context);
        ArrayList<ValuePair> list = appInfo.getValuePairList();
        int size = list.size();
        //已有的child个数
        int childCount = list_item2_linearLayout.getChildCount();
        //如果子view的个数大于子项的个数，则清除末尾多余的view
        if (size < childCount) {
            for (int i = 0; i < childCount - size; i++)
                list_item2_linearLayout.removeViewAt(childCount - 1 - i);
        }
        //动态添加子view
        for (int i = 0; i < size; i++) {
            ValuePair valuePair = list.get(i);
            View v = list_item2_linearLayout.getChildAt(i);

            //判断是否需要重绘
            if (v == null || v.getTag() != null && !v.getTag().equals(valuePair)) {
                final View view = layoutInflater.inflate(R.layout.subitem, list_item2_linearLayout, false);
                TextView tag_textView = (TextView) view.findViewById(R.id.tag_textView);
                TextView easyPath_textView = (TextView) view.findViewById(R.id.easyPath_textView);

                tag_textView.setText((String) valuePair.getName());
                easyPath_textView.setText((String) valuePair.getValue());
                view.setTag(valuePair);
                view.setClickable(true);
                view.setFocusable(true);
                //设置子view的点击事件
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ValuePair valuePair = (ValuePair) v.getTag();
                        String easyPath = (String) valuePair.getValue();
                        if (new File(easyPath).isDirectory()) {
                            Intent intent = new Intent(context, FileExplorerActivity.class);
                            intent.putExtra("path", easyPath);
                            context.startActivity(intent);
                        } else {
                            Utils.messageShort(context, "路径不存在，请确保路径正确");
                        }
                    }
                });
                //设置子view的长按事件
                final int position = i;
                view.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        startEditDialogForResult(context, v, appInfo, position);

                        return true;
                    }
                });
                //设置子view的onTouch事件
//                    detector = new GestureDetector(context, new MyOnGestureListener(view));
//                    view.setOnTouchListener(new View.OnTouchListener() {
//                        @Override
//                        public boolean onTouch(View v, MotionEvent event) {
//                            return detector.onTouchEvent(event);
//                        }
//                    });
                //如果有子View，则删除
                if (childCount > i) {
                    list_item2_linearLayout.removeViewAt(i);
                }
                list_item2_linearLayout.addView(view, i);
            }
        }
    }


    // subitem的touch事件，判断是否滑动
    private boolean onTouchSubItem(View v, MotionEvent event) {
        int action = event.getAction();
        int x = (int) event.getX();
        int y = (int) event.getY();
        if (action == MotionEvent.ACTION_DOWN) {
            downX = x;
            downY = y;
            System.out.println("action down at: x=" + downX + " y=" + downY);
            return true;
        } else if (action == MotionEvent.ACTION_MOVE) {
            int i = event.getHistorySize();
            if (i > 1) {
                int tx = (int) event.getHistoricalX(i - 1);
                int ty = (int) event.getHistoricalY(i - 1);
                System.out.println("action move : delatX=" + (x - tx) + " y=" + (y - ty));
            }
            return true;
        } else if (action == MotionEvent.ACTION_UP) {

            downX = downY = 0;
            pressed = false;
        }
        return false;
    }

    @Override
    public int getItemCount() {
        return objList.size();
    }

    @Override
    public int getItemViewType(int position) {
        Object object = objList.get(position);
        if (object instanceof MountPoint) {
            return MOUNT_POINT;
        } else if (object instanceof AppInfo) {
            return NORMAL_FILE;
        } else {
            return ADD_BUTTON;
        }
    }

    public void setItemClickedListener(OnItemClickedListener itemClickedListener) {
        this.itemClickedListener = itemClickedListener;
    }

    public void setItemLongClickedListener(OnItemLongClickedListener itemLongClickedListener) {
        this.itemLongClickedListener = itemLongClickedListener;
    }

    private void startAddDialogForResult(Context context) {
        Intent intent = new Intent(context, AddDialogActivity.class);
        ((Activity) context).startActivityForResult(intent, Utils.REQUEST_ADD);
    }

    private void startEditDialogForResult(Context context, View v, AppInfo appInfo, int position) {
        ValuePair valuePair = (ValuePair) v.getTag();
        Intent intent = new Intent(context, EditDialogActivity.class);
        intent.putExtra("appName", appInfo.getAppName());
        intent.putExtra("packageName", appInfo.getPackageName());
        intent.putExtra("valuePair", valuePair);
        intent.putExtra("position", position);
        ((Activity) context).startActivityForResult(intent, Utils.REQUEST_MODIFY);
    }

    private void initViews(Context context) {
        LayoutInflater inflator = LayoutInflater.from(context);
        initDialog(context);
        initListPopupView(inflator);
    }

    //初始化popupwindow
    private void initListPopupView(LayoutInflater inflator) {
        //init listpopup view
        listPopupWindow = new ListPopupWindow(context);

        //init adapter
        popupAdapter = new ListPopupAdapter(context);

        listPopupWindow.setAdapter(popupAdapter);
        listPopupWindow.setHeight(400);
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
    }

    //初始化dialog
    private void initDialog(final Context context) {
        if (dialog == null) {
            //init dialog view
            View view = LayoutInflater.from(context).inflate(R.layout.easypath_add_dialog, null);
            dialog_imageView = (ImageView) view.findViewById(R.id.appIcon_dialog_imageView);
            appName_editText = (EditText) view.findViewById(R.id.appName_dialog_editTextView);
            tag_dialog_editTextView = (EditText) view.findViewById(R.id.tag_dialog_editTextView);
            path_dialog_editTextView = (EditText) view.findViewById(R.id.path_dialog_editTextView);
            dialog_divider1 = view.findViewById(R.id.dialog_divider1);

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
                    //判断是否被点击
                    if (bool) {
                        appName_editText.setTag(false);
                        return;
                    }
                    popupAdapter.getFilter().filter(s, new Filter.FilterListener() {
                        @Override
                        public void onFilterComplete(int count) {
                            //if adapter data not empty and popup window not shown
                            if (count == 0 && listPopupWindow.isShowing()) {
                                listPopupWindow.dismiss();
                            }
                            if (count != 0 && !listPopupWindow.isShowing()) {
                                listPopupWindow.show();
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
                    if (hasFocus) {
//                        ((Activity)context).startActivityForResult();
                    }
                }
            });
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder((context));
            dialogBuilder.setView(view);
            dialog = dialogBuilder.create();
        }
    }

    private enum STATE {
        OPENED, OPENING, CLOSING, CLOSED
    }

    public interface OnItemClickedListener {
        void onItemClicked(View view, int positon);
    }

    public interface OnItemLongClickedListener {
        void onItemLongClicked(View v, int position);
    }

    // 手势识别listener
    class MyOnGestureListener extends GestureDetector.SimpleOnGestureListener {
        private View view;
        private View edit_linearLayout;
        private int width;
        private float startLeft, minLeft;
        private float minVelocity;
        private STATE state;

        public MyOnGestureListener(View view) {
            this.view = view;
            width = DensityUtils.dip2px(context, 60);
            state = STATE.CLOSED;
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            ValuePair valuePair = (ValuePair) view.getTag();
            String easyPath = (String) valuePair.getValue();
            if (new File(easyPath).isDirectory()) {
                Intent intent = new Intent(context, FileExplorerActivity.class);
                intent.putExtra("path", easyPath);
                context.startActivity(intent);
            } else {
                Utils.messageShort(context, "路径不存在，请确保路径正确");
            }
            return true;
        }

        @Override
        public boolean onDown(MotionEvent e) {
            startLeft = view.getRight() - view.getLeft();
            minLeft = startLeft - width;
//            editBarShow();
            return true;
        }

        //快速滑动并抬手
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            System.out.println("onFling");
            System.out.println("velocityX: " + velocityX);
            float x1 = e1.getX();
            float y1 = e1.getY();
            float x2 = e2.getX();
            float y2 = e2.getY();
            float deltaY = y2 - y1;
            //左划,且未打开,且加速度大于固定值
            if (x1 - x2 > 2 * (Math.abs(deltaY))) {
                smoothAnimateEditBar(edit_linearLayout.getLeft(), minLeft, 0);
//                //如果已经打开
//                if (edit_linearLayout.getLeft() == minLeft) {
//                    state=STATE.OPENED;
//                }else if (edit_linearLayout.getLeft() > minLeft && x1 - x2 > width / 3  && state!=STATE.OPENED) {
//                    //如果还没打开,且已经打开了一定宽度，且加速度大于固定值，则继续打开
//                    state=STATE.OPENING;
//                    smoothAnimateEditBar(edit_linearLayout.getLeft(), minLeft, 0);
//
//                }
            } else if (x2 - x1 > 2 * (Math.abs(deltaY)) && x2 - x1 > width / 4) {
                //右划关闭
//                animateEditBar(edit_linearLayout.getLeft(), startLeft);
                return true;
            }
            return true;
        }

        private void smoothAnimateEditBar(final int left, final float minLeft, float velocityX) {
            int tmp = left;
            float deltaX = (minLeft - tmp) / 50;
            int time = 500;
            int deltaT = time / 50;
            for (int i = 0; i < 500; i += deltaT) {

                try {
                    Thread.sleep(deltaT);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        //在屏幕上滑动
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            System.out.println("onScroll");

            float tmpLeft = edit_linearLayout.getLeft() - distanceX;
            if (tmpLeft < minLeft) {
                tmpLeft = minLeft;
            } else if (tmpLeft > startLeft) {
                tmpLeft = startLeft;
            }
            edit_linearLayout.setLeft((int) tmpLeft);
            return true;
        }

        void animateEditBar(float fromX, final float toX) {
            Animation animation = new TranslateAnimation(fromX, toX, edit_linearLayout.getY(), edit_linearLayout.getY());
            animation.setDuration(500);
            animation.setFillAfter(true);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            edit_linearLayout.startAnimation(animation);
        }
    }
}
