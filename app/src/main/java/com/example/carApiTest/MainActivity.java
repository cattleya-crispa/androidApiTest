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
//    private String urlIpText = "http://192.168.0.5:3000/api/v1/doorState1Right";
//    private String urlPostText = "http://192.168.0.5:3000/api/v1/add2";
    private String urlIpText = "http://10.0.2.2:3000/api/v1/doorState1Right";
    private String urlPostText = "http://10.0.2.2:3000/api/v1/add2";
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
                        Log.d("hoge", "????????????jsonStr=" + response);
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
                                textView.setText("??????JSON?????????:" + ip);
                                textViewGetValue.setText("??????????????????:" + dS2R);
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
                            Log.d("hoge", "????????????POST????????????=" + response);
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
                                    + "\n?????????" + rootJSON.getString("desire")
                                    + "\n???????????????" + rootJSON.getString("done")
                                    + "\n???????????????" + rootJSON.getString("reported")
                                    + "\n????????????" + rootJSON.getString("identifier")
                                    + "\n???????????????" + rootJSON.getString("attribute")
                                    + "\n?????????" + rootJSON.getString("location")
                                    + "\n??????" + rootJSON.getString("attributeValue")
                            ;
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                textView2.setText("POST???????????????:\n"+postData);
                                textViewPostValue.setText("??????????????????:\n" + nameAndType);
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
            urlConnection = (HttpURLConnection) url.openConnection();//??????????????????????????????????????????
            urlConnection.setConnectTimeout(10000);//????????????????????????
            urlConnection.setReadTimeout(10000);//???????????????????????????????????????????????????
            urlConnection.addRequestProperty("User-Agent", "Android");//???????????????????????????????????????????????????
            urlConnection.addRequestProperty("Accept-Language", Locale.getDefault().toString());//???????????????????????????
            urlConnection.setRequestMethod("GET");//HTTP??????????????????
            urlConnection.setDoInput(true);//????????????????????????????????????
            urlConnection.setDoOutput(false);//????????????????????????????????????
            urlConnection.connect();//????????????
            int statusCode = urlConnection.getResponseCode();//??????????????????????????????
            if (statusCode == 200){//200?????????
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
        return str;//???????????????JSON???string????????????
    }

    public String postAPI(){
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        OutputStream outputStream = null;
        String result = "";
        String str = "";
        try {
            URL url = new URL(urlPostText);
            urlConnection = (HttpURLConnection) url.openConnection();//??????????????????
            String postData = "name=??????????????????";//????????????????????????????????????????????????
            final String postJson = "{" + "\"name\": \"?????????\"}";
            Log.d("hoge", "????????????POSTJSON????????????=" + postJson);
            urlConnection.setConnectTimeout(10000);//????????????????????????
            urlConnection.setReadTimeout(10000);//?????????????????????????????????
            urlConnection.addRequestProperty("User-Agent", "Android");
            urlConnection.addRequestProperty("Accept-Language", Locale.getDefault().toString());
            urlConnection.addRequestProperty("Content-Type", "application/json; charset=UTF-8");
            //urlConnection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoInput(true);//????????????????????????????????????
            urlConnection.setDoOutput(true);//????????????????????????????????????
            urlConnection.connect();
            outputStream = urlConnection.getOutputStream();//???????????????????????????????????????
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
