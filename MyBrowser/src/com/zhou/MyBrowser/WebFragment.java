package com.zhou.MyBrowser;

import android.app.Fragment;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class WebFragment extends Fragment {
    private int flag = 1;//1表示加载完成，0表示未完成
    private View view = null;
    private EditText editText = null;
    private Button confirmButton = null;
    private Button stopButton = null;
    private Button backButton = null;
    private Button forwardButton = null;
    private Button saveButton = null;
    private Button refreshButton = null;
    private Button menuButton = null;
    private WebView webView = null;
    private String currentURL;
    private DrawerLayout drawerLayout = null;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return view =  inflater.inflate(R.layout.webfragment, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        editText = (EditText) view.findViewById(R.id.myEditURL);
        confirmButton = (Button) view.findViewById(R.id.confirmURL);
        stopButton = (Button) view.findViewById(R.id.stopURL);
        backButton = (Button) view.findViewById(R.id.backURL);
        forwardButton = (Button) view.findViewById(R.id.forwardURL);
        saveButton = (Button) view.findViewById(R.id.saveURL);
        refreshButton = (Button) view.findViewById(R.id.refreshURL);
        menuButton = (Button) view.findViewById(R.id.menu);
        drawerLayout = (DrawerLayout)  getActivity().findViewById(R.id.drawerLayout);
        webView = (WebView) view.findViewById(R.id.myWebVIew);
        webView.getSettings().setJavaScriptEnabled(true);

        webView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                confirmButton.setText("暂停");
                editText.setText(url);
                flag = 0;
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                confirmButton.setText("确认");
                flag = 1;
            }
        });


        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                webView.loadUrl("http://www.hao123.com");
            }
        });

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(flag == 1){
                    currentURL = editText.getText().toString();
                    if(!(currentURL.contains("http://")||currentURL.contains("https://"))){
                        currentURL = "http://"+currentURL;
                    }
                    webView.loadUrl(currentURL);
                    confirmButton.setText("暂停");
                }
                if(flag == 0){
                    webView.stopLoading();
                    confirmButton.setText("确认");
                }

            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(webView.canGoBack()){
                    webView.goBack();
                }

            }
        });
        forwardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(webView.canGoForward()){
                    webView.goForward();
                }
            }
        });
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyDBHelper myDBHelper = new MyDBHelper(getActivity(),"browserDatabase");
                SQLiteDatabase mySQL = myDBHelper.getWritableDatabase();
                Cursor cursor = mySQL.query("user",new String[] {"password"},null,null,null,null,null,null);
                cursor.moveToNext();
                String user = cursor.getString(cursor.getColumnIndex("password"));
                cursor.close();
                cursor = mySQL.query(user,new String[]{"URL"},null,null,null,null,null,null);
                while(cursor.moveToNext()){
                    if(webView.getUrl().equals(cursor.getString(cursor.getColumnIndex("URL")))){
                        cursor.close();
                        mySQL.close();
                        Toast.makeText(getActivity(),"已收藏", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                cursor.close();
                ContentValues contentValues = new ContentValues();
                contentValues.put("URLName", webView.getTitle());
                contentValues.put("URL",webView.getUrl());
                mySQL.insert(user,null,contentValues);
                mySQL.close();
                Toast.makeText(getActivity(),"已收藏", Toast.LENGTH_SHORT).show();
            }
        });
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                webView.reload();
            }
        });

        menuButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(!drawerLayout.isDrawerOpen(Gravity.LEFT)){
                    drawerLayout.openDrawer(Gravity.LEFT);
                }
                else if(drawerLayout.isDrawerOpen(Gravity.LEFT)){
                    drawerLayout.closeDrawer(Gravity.LEFT);
                }
            }
        });
        MyFirstState();
    }

    //初始化窗口
    public void MyFirstState(){
        if(FavouritesFragment.getURL() == null){
            editText.setText("http://www.hao123.com");
            webView.loadUrl("http://www.hao123.com");
        }else if(FavouritesFragment.getURL()!=null){
            editText.setText(FavouritesFragment.getURL());
            webView.loadUrl(FavouritesFragment.getURL());
            FavouritesFragment.setURL();
        }
    }
}
