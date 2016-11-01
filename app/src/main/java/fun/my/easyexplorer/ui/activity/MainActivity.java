package fun.my.easyexplorer.ui.activity;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.util.List;

import fun.my.easyexplorer.R;
import fun.my.easyexplorer.model.MountPoint;

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
        setContentView(R.layout.activity_fileexplorer);

    }

    protected void loadData() {

    }

}
