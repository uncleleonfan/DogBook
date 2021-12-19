package com.xuneng.helloworld;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private RecyclerView mRecyclerView;
    private JSONArray mPetFamilyList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRecyclerView = findViewById(R.id.recycler);
        //设置适配器，这里设置自定义的MyAdapter
        mRecyclerView.setAdapter(new MyAdapter());
        //设置布局管理器，这里设置线性的布局管理器，默认为纵向列表
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        sendRequest();
    }


    public class MyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View item = LayoutInflater.from(MainActivity.this).inflate(R.layout.dog_list_item, parent, false);
            return new MyViewHolder(item);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            TextView textView = holder.itemView.findViewById(R.id.dog_name);
            ImageView imageView = holder.itemView.findViewById(R.id.dog_image);
            try {
                JSONObject jsonObject = mPetFamilyList.getJSONObject(position);
                String name = jsonObject.getString("name");
                String coverUrl = jsonObject.getString("coverURL");
                textView.setText(name);
                Glide.with(MainActivity.this).load(coverUrl).into(imageView);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            if (mPetFamilyList == null) {
                return 0;
            }
            return mPetFamilyList.length();
        }
    }

    private static class MyViewHolder extends RecyclerView.ViewHolder {

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    private void sendRequest() {
        String url = "https://api.apishop.net/common/dogFamily/queryDogList?apiKey=m2JtRylc30409e704d72097b91405654c79f754f24b8f44";
        //创建OkHttpClient
        OkHttpClient client = new OkHttpClient();
        //创建Request
        Request request = new Request.Builder()
                .url(url)
                .build();
        //使用OkHttpClient发起一个请求，把请求加入到请求队列里面，通过子线程执行
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.d("OKHttp", "onFailure: " + e.toString());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                Log.d("OKHttp", "onResponse: ");
                //获取HTTP请求响应正文的字符串形式
                String string = response.body().string();
                Log.d("OKHttp", "onResponse: " + string);
                try {
                    //将字符串转化成JSONObject
                    JSONObject jsonObject = new JSONObject(string);
                    //获取JSON数据中"desc"对应的值
                    String desc = jsonObject.getString("desc");
                    Log.d("OKHttp", "onResponse: desc " + desc);
                    //
                    JSONObject result = jsonObject.getJSONObject("result");
                    mPetFamilyList = result.getJSONArray("petFamilyList");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mRecyclerView.getAdapter().notifyDataSetChanged();
                        }
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}