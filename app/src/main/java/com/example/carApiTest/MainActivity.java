package com.example.carApiTest;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private Handler handler = new Handler();
    private Button button;
    private Button button2;
    private TextView textView;
    private TextView textView2;
    private TextView textViewGetValue;
    private TextView textViewPostValue;
    //private String urlIpText = "http://httpbin.org/ip";
    private String urlIpText = "http://192.168.0.5:3000/api/v1/doorState1Right";
    private String urlPostText = "http://192.168.0.5:3000/api/v1/add2";
    private String ip = "";
    private String dS2R ="";
    private String nameAndType = "";
    private String postData = "";
    private static final String BOUNDARY = "--boundary";
    private static final String CRLF = "\r\n";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button = findViewById(R.id.button);
        button2 = findViewById(R.id.button2);
        textView = findViewById(R.id.textView);
        textView2 = findViewById(R.id.textView2);
        textViewGetValue = findViewById(R.id.textViewGetValue);
        textViewPostValue = findViewById(R.id.textViewPostValue);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String response = "";
                        try {
                        response = getAPI();
                        Log.d("hoge", "ログ表示jsonStr=" + response);
                        //JSONObject rootJSON = new JSONObject(response);
                        //ip = rootJSON.getString("origin");
                        ip = response;
                        //JSONObject json = new JSONObject(response);
                        JSONArray jsonarray= new JSONArray(response);
                        for(int i=0; i < jsonarray.length(); i++){
                            JSONObject jsonobject = jsonarray.getJSONObject(i);
                            String door = jsonobject.getString("door");
                            dS2R = door;
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                textView.setText("受信JSONデータ:" + ip);
                                textViewGetValue.setText("オブジェクト:" + dS2R);
                            }
                        });
                    }
                });
                thread.start();
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String response = "";
                        try {
                            response = postAPI();
                            Log.d("hoge", "ログ表示POSTのデータ=" + response);
                            postData = response;
//                           JSONArray jsonarray= new JSONArray(response);
//                            for(int i=0; i < jsonarray.length(); i++){
//                                JSONObject jsonobject = jsonarray.getJSONObject(i);
//                                String desire = jsonobject.getString("desire");
//                                nameAndType = desire;
//                            }
                            JSONObject rootJSON = new JSONObject(response);
                            //JSONObject formJSON = rootJSON.getJSONObject("desire");
                            nameAndType = rootJSON.getString("id")
                                    + "\n要求：" + rootJSON.getString("desire")
                                    + "\nチェック：" + rootJSON.getString("done")
                                    + "\nレポート：" + rootJSON.getString("reported")
                                    + "\n要求先：" + rootJSON.getString("identifier")
                                    + "\n要求属性：" + rootJSON.getString("attribute")
                                    + "\n場所：" + rootJSON.getString("location")
                                    + "\n値：" + rootJSON.getString("attributeValue")
                            ;
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                textView2.setText("POSTレスポンス:\n"+postData);
                                textViewPostValue.setText("オブジェクト:\n" + nameAndType);
                            }
                        });

                    }
                });
                thread.start();
            }
        });
    }

    public String getAPI(){
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        String result = "";
        String str = "";
        try {
            URL url = new URL(urlIpText);
            urlConnection = (HttpURLConnection) url.openConnection();//接続先へのコネクションを開く
            urlConnection.setConnectTimeout(10000);//タイムアウト設定
            urlConnection.setReadTimeout(10000);//レスポンス読み込みタイムアウト設定
            urlConnection.addRequestProperty("User-Agent", "Android");//ヘッダー設定　ユーザーエージェント
            urlConnection.addRequestProperty("Accept-Language", Locale.getDefault().toString());//ヘッダー設定　言語
            urlConnection.setRequestMethod("GET");//HTTPメソッド設定
            urlConnection.setDoInput(true);//リクエストボデー送信設定
            urlConnection.setDoOutput(false);//レスポンスボデー受信設定
            urlConnection.connect();//通信開始
            int statusCode = urlConnection.getResponseCode();//レスポンスコード取得
            if (statusCode == 200){//200は成功
                inputStream = urlConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "utf-8"));
                result = bufferedReader.readLine();
                while (result != null){
                    str += result;
                    result = bufferedReader.readLine();
                }
                bufferedReader.close();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return str;//レスポンスJSONをstring型で返す
    }

    public String postAPI(){
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        OutputStream outputStream = null;
        String result = "";
        String str = "";
        try {
            URL url = new URL(urlPostText);
            urlConnection = (HttpURLConnection) url.openConnection();//コネクション
            String postData = "name=ドア開けてよ";//リクエストボデーに格納するデータ
            final String postJson = "{" + "\"name\": \"今日わ\"}";
            Log.d("hoge", "ログ表示POSTJSONのデータ=" + postJson);
            urlConnection.setConnectTimeout(10000);//接続タイムアウト
            urlConnection.setReadTimeout(10000);//レスポンスタイムアウト
            urlConnection.addRequestProperty("User-Agent", "Android");
            urlConnection.addRequestProperty("Accept-Language", Locale.getDefault().toString());
            urlConnection.addRequestProperty("Content-Type", "application/json; charset=UTF-8");
            //urlConnection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoInput(true);//リクエストボデー受信許可
            urlConnection.setDoOutput(true);//リクエストボデー送信許可
            urlConnection.connect();
            outputStream = urlConnection.getOutputStream();//リクエストボデーの書き込み
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "utf-8"));
            bufferedWriter.write(postJson);
            bufferedWriter.flush();
            bufferedWriter.close();

            int statusCode = urlConnection.getResponseCode();
            if (statusCode == 200){
                inputStream = urlConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                result = bufferedReader.readLine();
                while (result != null){
                    str += result;
                    result = bufferedReader.readLine();
                }
                bufferedReader.close();
            }

            urlConnection.disconnect();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return str;
    }

}

//https://qiita.com/f-paico/items/f03f48d3ecacba7dcc13


//(base) penguin@RyoheiMBP apiTest % curl http://httpbin.org/ip
//        {
//        "origin": "121.81.221.82"
//        }
