package com.example.nexor.myapplication;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputType;
import android.text.Layout;
import android.text.TextWatcher;
import android.util.Log;
import android.view.DragEvent;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.widget.EditText;
import android.widget.ToggleButton;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private InputConnection ic;

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        final EditText editText = findViewById(R.id.editText);
        MyKeyboard keyboard = findViewById(R.id.keyboard);
        editText.setRawInputType(InputType.TYPE_CLASS_TEXT);
//        editText.setOnDragListener(new View.OnDragListener() {
//            @Override
//            public boolean onDrag(View v, DragEvent event) {
//                event.g
//                return false;
//            }
//        });
        editText.setOnTouchListener(new View.OnTouchListener() {
            private float l_x = -1;
            private float l_y = -1;
            private long l_read = -1;
            private GestureDetector gestureDetector = new GestureDetector(MainActivity.this, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onDoubleTap(MotionEvent e) {
                    Log.d("TEST", "onDoubleTap");
                    return super.onDoubleTap(e);
                }
            });
            boolean firstTouch = false;
            long time = -1;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (firstTouch && (System.currentTimeMillis() - time) < 300) {
                            new SendKeys().execute("click 1");
//                            new SendKeys().execute("click --repeat 2 1");
                            firstTouch = false;
                        } else {
                            firstTouch = true;
                            time = System.currentTimeMillis();
                        }
                        break;
                    case MotionEvent.ACTION_MOVE:
                        Layout layout = ((EditText) v).getLayout();
                        float x = event.getX() + editText.getScrollX();
                        float y = event.getY() + editText.getScrollY();
                        if (Math.abs(x - l_x) > 100 || Math.abs(y - l_y) > 100) {
                            l_x = x;
                            l_y = y;
                        }
                        if (l_x != -1 && l_y != -1) {
                            int d_x = (int) (x - l_x);
                            int d_y = (int) (y - l_y);
                            d_x = Math.min(100, d_x);
                            d_y = Math.min(100, d_y);
                            new SendKeys().execute("x:" + d_x + ",y:" + d_y);
                            l_read = System.currentTimeMillis();
                        }
                        l_x = x;
                        l_y = y;
                        break;
                    default:
                        Log.e("kek", "editText#onTouch#event.getAction()=" + event.getAction());
                }
                gestureDetector.onTouchEvent(event);
                return true;
            }
        });
        editText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                new SendKeys().execute("BackSpace");
                return false;
            }
        });
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() != 0) {
                    new SendKeys().execute(s.toString());
                    editText.setText("");
                    Log.e("kekao4", s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        editText.setTextIsSelectable(true);
        ic = editText.onCreateInputConnection(new EditorInfo());
        keyboard.setInputConnection(ic);
        keyboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // send keycodes to port
                if (v instanceof AppCompatButton) {
                    AppCompatButton acb = (AppCompatButton) v;
                    new SendKeys().execute(acb.getText());
                    Log.e("kekao2", acb.getText().toString());
                } else if (v instanceof ToggleButton) {
                    ToggleButton tb = (ToggleButton) v;
                    new SendKeys().execute(tb.getText());
                    Log.e("kekao3", tb.getText().toString());
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        if (id == R.id.nav_camera) {
            Intent menuIntent = new Intent(this, FullscreenActivity.class);
            startActivity(menuIntent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            Intent myIntent = new Intent(MainActivity.this, FullscreenActivity.class);
            myIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(myIntent);
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        }
        Log.e("kekao", id + "");

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
