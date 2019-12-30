package com.example.binu.myapplication;

import android.content.Context;

import com.demo.MainActivity;
import com.sportstalk247.APICallback;
import com.sportstalk247.FN;
import com.sportstalk247.SportsTalkClient;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import java.io.IOException;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;



@RunWith(RobolectricTestRunner.class)
public class MyExampleUnitTest {

    private final String apiKey = "vfZSpHsWrkun7Yd_fUJcWAHrNjx6VRpEqMCEP3LJV9Tg";
    private MainActivity mainActivity;
    private Context contex;


    @Before
    public void setup() {


        mainActivity = Robolectric.buildActivity(MainActivity.class).get();
        contex = mainActivity.getApplicationContext();
    }

    @Test
    public void isContextNull() {
        assertNotNull(mainActivity.getApplication());
    }

    @Test
    public void testForApiHeaderNotNullOrEmpty() {
        Map<String, String> headers = new FN().getApiHeaders("12");
        assertNotNull(headers);
    }

    @Test
    public void testForApiHeaderNotNullOrEmptyForEmptyApiKey() {
        Map<String, String> headers = new FN().getApiHeaders("");
        assertNotNull(headers);
    }

    @Test
    public void testForListUsers(){
        //FakeHttpLayer layer = new FakeHttpLayer();
        //FakeHttp.getFakeHttpLayer().interceptHttpRequests(false);
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {

                final SportsTalkClient sportsTalkClient = new SportsTalkClient(apiKey);
                mainActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        final APICallback callback = new APICallback() {
                            @Override
                            public void execute(JSONObject jsonObject) {
                                System.out.println("executed......");
                            }

                            @Override
                            public void error(JSONObject jsonObject) {
                                System.out.println("some exception occurred......");
                            }
                        };
                        sportsTalkClient.listUsers(contex, callback);
                    }
                });

            }
        });

        t.start();

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Robolectric.flushForegroundThreadScheduler();
//        try {
//            latch.await(20, TimeUnit.SECONDS);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        execute1();
    }

    private void execute1() {

        OkHttpClient client = new OkHttpClient();

        final okhttp3.Request request = new okhttp3.Request.Builder()
                .header("x-api-token", "")
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
