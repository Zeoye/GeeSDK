package com.gtdev5.geetolsdk.mylibrary.widget;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.gtdev5.geetolsdk.R;
import com.gtdev5.geetolsdk.mylibrary.util.DensityUtils;

/**
 * Created by ZL on 2019/10/30
 *
 * 居中的Dialog
 */

public class CenterDialog extends AlertDialog implements View.OnClickListener {
    private int[] listenedItems;
    private int layoutResID;
    private Context context;
    private OnCenterItemClickListener listener;
    private boolean mIsEdit = false;

    public interface OnCenterItemClickListener {
        void OnCenterItemClick(CenterDialog dialog, View view);
    }

    public CenterDialog(@NonNull Context context) {
        super(context);
    }

    public CenterDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    protected CenterDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    public CenterDialog(Context context, int layoutResID, int[] listenedItems, boolean isEdit) {
        super(context, R.style.dialog_center); //dialog的样式
        this.context = context;
        this.layoutResID = layoutResID;
        this.listenedItems = listenedItems;
        this.mIsEdit = isEdit;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        window.setGravity(Gravity.CENTER); // 此处可以设置dialog显示的位置为居中
        setContentView(layoutResID);
        WindowManager windowManager = ((Activity) context).getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.width = display.getWidth() * 4 / 5; // 设置dialog宽度为屏幕的4/5
        getWindow().setAttributes(lp);
        setCanceledOnTouchOutside(true);// 点击Dialog外部消失
        if (mIsEdit) {
            // 以下两行代码是对话框的EditText点击后不能显示输入法的
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                    WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        }
        //遍历控件id,添加点击事件
        if (listenedItems != null) for (int id : listenedItems) {
            findViewById(id).setOnClickListener(this);
        }
    }

    @Override
    public void show() {
        try {
            if (context != null) {
                Activity activity = (Activity) context;
                if (!activity.isFinishing()) {
                    super.show();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        if (listener != null) {
            listener.OnCenterItemClick(this, v);
        }
        dismiss();
    }

    public void setOnCenterClickListener(OnCenterItemClickListener listener) {
        this.listener = listener;
    }

    /**
     * 根据资源id设置数据
     */
    public void setText(int layoutResID, String s) {
        TextView textView = findViewById(layoutResID);
        if (textView != null) {
            textView.setText(s);
        }
    }

    /**
     * 获取EditText内容
     * @param layoutResID
     * @return
     */
    public String getEditText(int layoutResID) {
        EditText editText = findViewById(layoutResID);
        if (TextUtils.isEmpty(editText.getText().toString())) {
            return null;
        } else {
            return editText.getText().toString();
        }
    }

    /**
     * 设置输入框默认文字
     */
    public void setEditText(int layoutResID, String text) {
        EditText editText = findViewById(layoutResID);
        if (!TextUtils.isEmpty(text)) {
            editText.setText(text);
            editText.setSelection(text.length());
        }
    }

    /**
     * 设置文字顶部图片
     * @param resId 图片资源
     */
    public void setTextDrawableTop(int layoutResID, int resId) {
        TextView textView = findViewById(layoutResID);
        if (textView != null) {
            Drawable drawable = context.getResources().getDrawable(resId).mutate();
            textView.setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null);
            textView.setCompoundDrawablePadding(DensityUtils.Dp2Px(context, 8));
        }
    }

    /**
     * 设置hint
     */
    public void setEditHint(int layoutResID, String hint) {
        EditText editText = findViewById(layoutResID);
        if (TextUtils.isEmpty(hint)) {
            editText.setHint("请输入");
        } else {
            editText.setHint(hint);
        }
    }

    /**
     * 对于带list的dialog初始化
     */
    public <T extends BaseAdapter> void initListView(int listId, T adapter, OnDialogItemClickListener listener) {
        ListView list = findViewById(listId);
        list.setAdapter(adapter);
        list.setOnItemClickListener((parent, view, position, id) -> listener.onItemClick(position, id));
    }

    /**
     * Created by Administrator on 2018\1\26 0026.
     * 带listview的dialog实现回调监听
     */

    public interface OnDialogItemClickListener {
        public void onItemClick(int position, long id);
    }
}
