package fun.my.easyexplorer.ui.activity;

import android.app.Activity;
import android.os.Bundle;

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

    protected void initViews(Bundle savedInstanceState) {

    }

    /*
     protected void initViews(Bundle savedInstanceState) {
           setContentView(R.layout.mount_recycler_item);
           final CustomCircleView customCircleView = (CustomCircleView) findViewById(R.id.customCircleView);
           customCircleView.setmPercent(0.8f);

           Button button = (Button) findViewById(R.id.change);
           button.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   double percent = Math.random();
                   customCircleView.setmPercent((float) percent);
               }
           });
       }
   */
    protected void loadData() {

    }

}
