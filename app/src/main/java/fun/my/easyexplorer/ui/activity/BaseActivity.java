package fun.my.easyexplorer.ui.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by admin on 2016/9/10.
 */
public abstract class BaseActivity extends AppCompatActivity{

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        initVariables();
        initViews(savedInstanceState);
        loadData();
    }

    protected abstract void initVariables();
    protected abstract void initViews(Bundle savedInstanceState);
    protected abstract void loadData();

}
