package com.zhou.MyBrowser;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import java.lang.reflect.Field;


public class PersonFragment extends Fragment {

    private View view;
    private TextView userTextView = null;
    private Button quitButton = null;
    private Button registerButton = null;
    private Button loginButton = null;
    private LinearLayout linearLayout = null;
    private String currentUser = null;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return view = inflater.inflate(R.layout.personfragment,container,false);
    }

    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        userTextView = (TextView) view.findViewById(R.id.user);
        quitButton = (Button) view.findViewById(R.id.quit);
        registerButton = (Button) view.findViewById(R.id.register);
        loginButton = (Button) view.findViewById(R.id.login);
        linearLayout = (LinearLayout) view.findViewById(R.id.instruction);
        //获取当前用户名
        MyDBHelper myDBHelper = new MyDBHelper(getActivity(),"browserDatabase");
        SQLiteDatabase personDB = myDBHelper.getReadableDatabase();
        Cursor cursor = personDB.query("user",new String[]{"password"},null,null,null,null,null,null);
        cursor.moveToNext();
        currentUser = cursor.getString(cursor.getColumnIndex("password"));
        cursor.close();
        personDB.close();
        userTextView.setText(currentUser);

        if(currentUser.equals("guest")){
            userTextView.setText("游客");
            linearLayout.setVisibility(View.VISIBLE);
            quitButton.setVisibility(View.GONE);
        }else{
            linearLayout.setVisibility(View.INVISIBLE);
            quitButton.setVisibility(View.VISIBLE);
        }

        quitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyDBHelper myDBHelper = new MyDBHelper(getActivity(),"browserDatabase");
                SQLiteDatabase personDB = myDBHelper.getReadableDatabase();
                ContentValues contentValues = new ContentValues();
                contentValues.put("password","guest");
                personDB.update("user",contentValues,"name=?",new String[]{"currentUser"});
                personDB.close();
                userTextView.setText("游客");
                linearLayout.setVisibility(View.VISIBLE);
                quitButton.setVisibility(View.GONE);
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = LayoutInflater.from(getActivity());
                final View layout = inflater.inflate(R.layout.register, null);
                //创建注册对话框
                new AlertDialog.Builder(getActivity())
                        .setView(layout)
                        .setTitle("注册帐号")
                        .setPositiveButton("注册", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                              //  LayoutInflater inflater = LayoutInflater.from(getActivity());
                              //  View layout = inflater.inflate(R.layout.register, null);

                                EditText userNameEditText = (EditText) layout.findViewById(R.id.userEditText2);
                                EditText password1EditText = (EditText) layout.findViewById(R.id.passwordEditView2);
                                EditText password2EditText = (EditText) layout.findViewById(R.id.passwordEditView3);
                                TextView errorTextView = (TextView) layout.findViewById(R.id.errorTextView2);

                                String userName = userNameEditText.getText().toString();
                                String password1 = password1EditText.getText().toString();
                                String password2 = password2EditText.getText().toString();

                               /* System.out.println(userName+"1");
                                System.out.println(password1+"2");
                                System.out.println(password2+"3");*/
                                if(!password1.equals(password2)){
                                    errorTextView.setText("密码输入不一样，请重新输入");
                                    errorTextView.setVisibility(View.VISIBLE);
                                    //密码输入不一样，请重新输入
                                    try {
                                        Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
                                        field.setAccessible(true);
                                        field.set(dialog, false);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    //Toast.makeText(getActivity(),"密码输入不一样，请重新输入", Toast.LENGTH_SHORT).show();
                                }else{
                                    MyDBHelper myDBHelper = new MyDBHelper(getActivity(),"browserDatabase");
                                    SQLiteDatabase sqLiteDatabase = myDBHelper.getWritableDatabase();
                                    Cursor cursor = sqLiteDatabase.query("user",new String[] {"name"},null,null,null,null,null,null);
                                    while(cursor.moveToNext()){
                                        if(cursor.getString(cursor.getColumnIndex("name")).equals(userName)){
                                            errorTextView.setText("用户名已存在");
                                            errorTextView.setVisibility(View.VISIBLE);
                                            sqLiteDatabase.close();
                                            cursor.close();
                                            try {
                                                Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
                                                field.setAccessible(true);
                                                field.set(dialog, false);
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }

                                            //Toast.makeText(getActivity(),"用户名已存在", Toast.LENGTH_SHORT).show();
                                            break;

                                        }
                                    }
                                    if(cursor.isAfterLast()) {
                                        //修改当前用户
                                        ContentValues contentValues = new ContentValues();
                                        contentValues.put("password",userName);
                                        sqLiteDatabase.update("user",contentValues,"name=?",new String[]{"currentUser"});
                                        //创建新用户
                                        contentValues = new ContentValues();
                                        contentValues.put("name",userName);
                                        contentValues.put("password",password1);
                                        sqLiteDatabase.insert("user",null,contentValues);
                                        //创建新用户的收藏夹
                                        String sql ="create table " + userName + "(URLName VARCHAR(20), URL VARCHAR(100))";
                                        sqLiteDatabase.execSQL(sql);

                                        sqLiteDatabase.close();
                                        cursor.close();

                                        errorTextView.setText("");
                                        errorTextView.setVisibility(View.INVISIBLE);
                                        linearLayout.setVisibility(View.INVISIBLE);
                                        quitButton.setVisibility(View.VISIBLE);
                                        userTextView.setText(userName);
                                        Toast.makeText(getActivity(),"注册成功", Toast.LENGTH_SHORT).show();
                                        try {
                                            Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
                                            field.setAccessible(true);
                                            field.set(dialog, true);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }
                       })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
                                    field.setAccessible(true);
                                    field.set(dialog, true);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        })
                        .create()
                        .show();
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = LayoutInflater.from(getActivity());
                final View layout = inflater.inflate(R.layout.login, null);
                //创建登录对话框
                new AlertDialog.Builder(getActivity())
                        .setView(layout)
                        .setTitle("登录帐号")
                        .setPositiveButton("登录", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                //  LayoutInflater inflater = LayoutInflater.from(getActivity());
                                //  View layout = inflater.inflate(R.layout.register, null);

                                EditText userNameEditText = (EditText) layout.findViewById(R.id.userEditText1);
                                EditText passwordEditText = (EditText) layout.findViewById(R.id.passwordEditView1);
                                TextView errorTextView = (TextView) layout.findViewById(R.id.errorTextView1);

                                String userName = userNameEditText.getText().toString();
                                String password = passwordEditText.getText().toString();

                                MyDBHelper myDBHelper = new MyDBHelper(getActivity(), "browserDatabase");
                                SQLiteDatabase sqLiteDatabase = myDBHelper.getWritableDatabase();
                                Cursor cursor = sqLiteDatabase.query("user", new String[]{"name", "password"}, null, null, null, null, null, null);
                                while (cursor.moveToNext()) {
                                    if (cursor.getString(cursor.getColumnIndex("name")).equals(userName) &&
                                            cursor.getString(cursor.getColumnIndex("password")).equals(password)) {
                                        //切换当前用户
                                        ContentValues contentValues = new ContentValues();
                                        contentValues.put("password", userName);
                                        sqLiteDatabase.update("user", contentValues, "name=?", new String[]{"currentUser"});
                                        sqLiteDatabase.close();
                                        cursor.close();

                                        userTextView.setText(userName);
                                        errorTextView.setText("");
                                        errorTextView.setVisibility(View.INVISIBLE);
                                        linearLayout.setVisibility(View.INVISIBLE);
                                        quitButton.setVisibility(View.VISIBLE);
                                        Toast.makeText(getActivity(), "登录成功", Toast.LENGTH_SHORT).show();
                                        try {
                                            Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
                                            field.setAccessible(true);
                                            field.set(dialog, true);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        return;
                                    }
                                }
                                if(cursor.isAfterLast()){
                                    errorTextView.setText("用户名或密码错误");
                                    errorTextView.setVisibility(View.VISIBLE);
                                    sqLiteDatabase.close();
                                    cursor.close();
                                    try {
                                        Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
                                        field.setAccessible(true);
                                        field.set(dialog, false);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        })
                        .setNegativeButton("退出", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
                                    field.setAccessible(true);
                                    field.set(dialog, true);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        })
                        .create()
                        .show();
            }
        });

    }
}
