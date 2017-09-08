package com.zt.tz.becelview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity{
    private FrameLayout frameLayout;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        frameLayout= (FrameLayout) findViewById(R.id.root_view);
        searchView= (SearchView) findViewById(R.id.searchview);
        frameLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                searchView.obtainVelocity(event);
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:searchView.eventDown(event);break;
                    case MotionEvent.ACTION_MOVE:searchView.eventMove(event);break;
                    case MotionEvent.ACTION_UP:searchView.eventUp(event);break;
                }
                return true;
            }
        });
        searchView.setOnScrollListener(new SearchView.OnScrollListener() {
            @Override
            public void scrollDown() {
            }

            @Override
            public void scrollUp() {
            }
        });
    }
}
