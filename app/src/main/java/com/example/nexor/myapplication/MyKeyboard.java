package com.example.nexor.myapplication;

import android.content.Context;
import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
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

public class MyKeyboard extends LinearLayout implements View.OnClickListener {

    private Button button1, button2, button3, button4, button5, button6, button7, button8, button9, button0, buttonDelete, buttonEnter;
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
        button1 = (Button) findViewById(R.id.button_1);
        button1.setOnClickListener(this);
        button2 = (Button) findViewById(R.id.button_2);
        button2.setOnClickListener(this);
        button3 = (Button) findViewById(R.id.button_3);
        button3.setOnClickListener(this);
        button4 = (Button) findViewById(R.id.button_4);
        button4.setOnClickListener(this);
        button5 = (Button) findViewById(R.id.button_5);
        button5.setOnClickListener(this);
        button6 = (Button) findViewById(R.id.button_6);
        button6.setOnClickListener(this);
        button7 = (Button) findViewById(R.id.button_7);
        button7.setOnClickListener(this);
        button8 = (Button) findViewById(R.id.button_8);
        button8.setOnClickListener(this);
        button9 = (Button) findViewById(R.id.button_9);
        button9.setOnClickListener(this);
        button0 = (Button) findViewById(R.id.button_0);
        button0.setOnClickListener(this);
        buttonDelete = (Button) findViewById(R.id.button_delete);
        buttonDelete.setOnClickListener(this);
        buttonEnter = (Button) findViewById(R.id.button_enter);
        buttonEnter.setOnClickListener(this);


        keyValues.put(R.id.button_1, "1");
        keyValues.put(R.id.button_2, "2");
        keyValues.put(R.id.button_3, "3");
        keyValues.put(R.id.button_4, "4");
        keyValues.put(R.id.button_5, "5");
        keyValues.put(R.id.button_6, "6");
        keyValues.put(R.id.button_7, "7");
        keyValues.put(R.id.button_8, "8");
        keyValues.put(R.id.button_9, "9");
        keyValues.put(R.id.button_0, "0");
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
        if (getRootView().getId() == R.id.button_delete) {
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
        Log.e("kek", ""+ v.getId());
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