package com.zhou.MyBrowser;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends Activity {

    private ListView listView = null;
    private DrawerLayout drawerLayout = null;
    private WebView webView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        listView = (ListView) findViewById(R.id.listMenuView);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        webView = (WebView) findViewById(R.id.myWebVIew);
        //创建Menu菜单的Adapter兵装载
        SimpleAdapter listAdapter = new SimpleAdapter(this,initListView(),R.layout.listmenu,
                new String[]{"listItemImage","listItemText"},new int[] {R.id.listItemImage,R.id.listItemText});
        listView.setAdapter(listAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //获取Fragment管理器
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                switch (position){
                    case 0:
                        fragmentTransaction.replace(R.id.mainLayout,new WebFragment());
                        break;
                    case 1:
                        fragmentTransaction.replace(R.id.mainLayout,new FavouritesFragment());
                        break;
                    case 2:
                        fragmentTransaction.replace(R.id.mainLayout,new PersonFragment());
                        break;
                    case 3:
                        fragmentTransaction.replace(R.id.mainLayout,new AboutFragment());
                        break;
                    default:
                        break;
                }
                //同步
                fragmentTransaction.commit();
                //关闭侧边菜单
                drawerLayout.closeDrawer(Gravity.LEFT);
            }
        });
    }

    //初始化menu菜单列表
        private List<Map<String,Object>> initListView(){
        List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
        Map<String,Object> map ;

        map = new HashMap<String,Object>();
        map.put("listItemImage",R.drawable.ie);
        map.put("listItemText","浏览网页");
        list.add(map);

        map = new HashMap<String,Object>();
        map.put("listItemImage",R.drawable.favourite);
        map.put("listItemText","我的收藏");
        list.add(map);

        map = new HashMap<String,Object>();
        map.put("listItemImage",R.drawable.person);
        map.put("listItemText","个人中心");
        list.add(map);

        map = new HashMap<String,Object>();
        map.put("listItemImage",R.drawable.about);
        map.put("listItemText","关于");
        list.add(map);

        return list;
    }

    //复写按键事件
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_MENU){
            if(!drawerLayout.isDrawerOpen(Gravity.LEFT)){
                drawerLayout.openDrawer(Gravity.LEFT);
            }
            else if(drawerLayout.isDrawerOpen(Gravity.LEFT)){
                drawerLayout.closeDrawer(Gravity.LEFT);
            }
        }
        if(keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()){
            webView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}