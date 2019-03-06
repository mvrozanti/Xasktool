package com.example.nexor.myapplication;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputConnection;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ToggleButton;

public class MyKeyboard extends LinearLayout implements View.OnClickListener {

    private Button buttonEscape;
    private Button buttonEnter;
    private Button buttonCtrl;
    private Button buttonAlt;
    private Button buttonSuper;
    private Button buttonTab;
    private Button buttonLeft;
    private Button buttonDown;
    private Button buttonUp;
    private Button buttonRight;
    private SparseArray<String> keyValues = new SparseArray<>();
    private InputConnection inputConnection;
    private OnClickListener ock;

    public MyKeyboard(Context context) {
        this(context, null, 0);
    }

    public MyKeyboard(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyKeyboard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public void init(Context context, AttributeSet attrs) {
        LayoutInflater.from(context).inflate(R.layout.keyboard, this, true);
        buttonEscape = (Button) findViewById(R.id.button_escape);
        buttonEscape.setOnClickListener(this);
        buttonCtrl = (Button) findViewById(R.id.button_ctrl);
        buttonCtrl.setOnClickListener(this);
        buttonSuper = (Button) findViewById(R.id.button_super);
        buttonSuper.setOnClickListener(this);
        buttonAlt = (Button) findViewById(R.id.button_alt);
        buttonAlt.setOnClickListener(this);
        buttonEnter = (Button) findViewById(R.id.button_enter);
        buttonEnter.setOnClickListener(this);
        buttonTab = (Button) findViewById(R.id.button_tab);
        buttonTab.setOnClickListener(this);
        buttonLeft = (Button) findViewById(R.id.button_left);
        buttonLeft.setOnClickListener(this);
        buttonDown = (Button) findViewById(R.id.button_down);
        buttonDown.setOnClickListener(this);
        buttonUp = (Button) findViewById(R.id.button_up);
        buttonUp.setOnClickListener(this);
        buttonRight = (Button) findViewById(R.id.button_right);
        buttonRight.setOnClickListener(this);
        keyValues.put(R.id.button_enter, "\n");
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        Log.e("kek", "" + keyCode);
        return super.onKeyUp(keyCode, event);
    }

    @Override
    public void onClick(View v) {
        if (inputConnection == null) {
            return;
        }
        if (getRootView().getId() == R.id.button_escape) {
            CharSequence selectedText = inputConnection.getSelectedText(0);
            if (TextUtils.isEmpty(selectedText)) {
                inputConnection.deleteSurroundingText(1, 0);
            } else {
                inputConnection.commitText("", 1);
            }
        } else {
            String value = keyValues.get(getRootView().getId());
            if (value != null)
                inputConnection.commitText(value, 1);
        }
        if (ock != null) {
            ock.onClick(v);
        }
    }

    public void setOnClickListener(OnClickListener ock) {
        this.ock = ock;
    }

    public void setInputConnection(InputConnection inputConnection) {
        this.inputConnection = inputConnection;
    }
}