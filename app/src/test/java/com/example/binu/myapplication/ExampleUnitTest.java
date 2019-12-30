package com.example.binu.myapplication;

import com.demo.MainActivity;
import com.sportstalk247.APICallback;
import com.sportstalk247.SportsTalkClient;

import org.json.JSONObject;
import org.junit.Test;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @
 *
 * see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {


    @Test
    public void addition_isCorrect() {
        final MainActivity ma = new MainActivity();
        final APICallback callback = new APICallback() {
            @Override
            public void execute(JSONObject jsonObject) {

            }

            @Override
            public void error(JSONObject jsonObject) {

            }
        };
        Runnable r = new Runnable() {
            @Override
            public void run() {
                final SportsTalkClient sportsTalkClient = new SportsTalkClient("");
                sportsTalkClient.listUsers(ma.getApplicationContext(), callback);
            }
        };

        Thread t = new Thread(r);
        t.start();
        //assertEquals(4, 2 + 2);
    }


    private void execute1() {

        OkHttpClient client = new OkHttpClient();

        final okhttp3.Request request = new okhttp3.Request.Builder()
                .header("x-api-token", "vfZSpHsWrkun7Yd_fUJcWAHrNjx6VRpEqMCEP3LJV9Tg")
                .url("http://api-origin.sportstalk247.com/api/v3/user/?limit=100&cursor=")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                call.cancel();
            }

            @Override
            public void onResponse(Call call, okhttp3.Response response) throws IOException {
                System.out.println(response.body().string());

            }
        });

        ////
    }

}
