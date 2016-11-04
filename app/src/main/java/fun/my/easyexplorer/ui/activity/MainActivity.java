package fun.my.easyexplorer.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import fun.my.easyexplorer.R;
import fun.my.easyexplorer.ui.view.CustomCircleView;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initVariables();
        initViews(savedInstanceState);
        loadData();
    }

    protected void initVariables() {

    }

//    protected void initViews(Bundle savedInstanceState) {
//
//    }


     protected void initViews(Bundle savedInstanceState) {
           setContentView(R.layout.main_recycler_item1);
           final CustomCircleView customCircleView = (CustomCircleView) findViewById(R.id.customCircleView);
           customCircleView.setmPercent(0.8f);


       }

    protected void loadData() {

    }

}
