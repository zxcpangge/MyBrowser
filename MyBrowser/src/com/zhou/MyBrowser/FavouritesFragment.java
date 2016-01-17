package com.zhou.MyBrowser;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FavouritesFragment extends Fragment{

    private ListView listView = null;
    private View view = null;
    private static String getURL = null;
    private String currentUser = null;
    private List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
    private SimpleAdapter favAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return view = inflater.inflate(R.layout.favouitesfragment,container,false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        listView = (ListView) view.findViewById(R.id.favListView);
        //获取数据库管理
        MyDBHelper myDBHelper = new MyDBHelper(getActivity(),"browserDatabase");
        SQLiteDatabase myBrowserDB = myDBHelper.getReadableDatabase();
        //显示收藏列表
        //获取当前用户
        Cursor cursor = myBrowserDB.query("user",new String[]{"password"},null,null,null,null,null);
        cursor.moveToNext();
        currentUser = cursor.getString(cursor.getColumnIndex("password"));
        cursor.close();
        //System.out.println(currentUser);测试获取当前用户
        //获取当前用户收藏列表
        //创建listView列表list

        Map<String,Object> map ;
        //遍历数据并添加到list中
        cursor = myBrowserDB.query(currentUser,new String[] {"URLName","URL"},null,null,null,null,null);
        while(cursor.moveToNext()){
            String URLName = cursor.getString(cursor.getColumnIndex("URLName"));
            String URL = cursor.getString(cursor.getColumnIndex("URL"));
            //System.out.println(URLName + URL);测试历遍SQL
            map = new HashMap<String,Object>();
            map.put("favName",URLName);
            map.put("favURL",URL);
            list.add(map);
        }
        cursor.close();
        //创建list的adapter并装载和显示收藏列表
        favAdapter = new SimpleAdapter(getActivity(),list,R.layout.favlist,
                new String[] {"favName","favURL"}, new int[]{R.id.favName,R.id.favURL});
        listView.setAdapter(favAdapter);
        //关闭数据库
        myBrowserDB.close();
        //为列表创建点击事件
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //创建Fragment管理器
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                //得到URL地址
                Map clickMap = new HashMap<String,Object>();
                clickMap = (Map)favAdapter.getItem(position);
                String URL = (String)clickMap.get("favURL");
                getURL = URL;
                System.out.println(URL);
                fragmentTransaction.replace(R.id.mainLayout,new WebFragment());
                fragmentTransaction.commit();
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                //得到当前item内容
                Map clickMap = new HashMap<String,Object>();
                clickMap = (Map)favAdapter.getItem(position);
                String clicked = (String)clickMap.get("favURL");
                //删除数据库中当前内容
                MyDBHelper myDBHelper = new MyDBHelper(getActivity(),"browserDatabase");
                SQLiteDatabase myBrowserDB = myDBHelper.getWritableDatabase();
                myBrowserDB.delete(currentUser,"URL=?",new String[]{clicked});
                myBrowserDB.close();
                //删除当前list表
                list.remove(position);
                favAdapter.notifyDataSetChanged();
                return true;
            }
        });
    }

    public static String getURL(){
        return getURL;
    }

    public static void setURL(){
        getURL = null;
    }


}
