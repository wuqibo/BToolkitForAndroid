package com.wuqibo.bppcallbackservice.btoolkit;

import android.os.Build;
import android.os.Looper;
import android.support.annotation.RequiresApi;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

public class Http {

    private final static String TAG = "-------------------" + Http.class.getSimpleName();

    public interface Callback {
        void call(String res);
    }

    public static void get(final String url, final Callback callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    HttpURLConnection urlConn = (HttpURLConnection) new URL(url).openConnection();
                    InputStreamReader in = new InputStreamReader(urlConn.getInputStream());
                    BufferedReader buffer = new BufferedReader(in);
                    String resultData = "", inputLine = null;
                    while (((inputLine = buffer.readLine()) != null)) {
                        resultData += inputLine + "\n";
                    }
                    in.close();
                    urlConn.disconnect();

                    if (callback != null) {
                        Looper.prepare();
                        callback.call(resultData);
                        Looper.loop();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void post(final String url, final Map<String, String> params, final Callback callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.i(TAG, "start post:" + url);
                HttpURLConnection conn = null;
                try {
                    conn = (HttpURLConnection) new URL(url).openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    conn.setConnectTimeout(5 * 1000);
                    OutputStream outputStream = conn.getOutputStream();
                    outputStream.write(getRequestData(params, "UTF-8").getBytes());
                    outputStream.flush();
                    outputStream.close();
                    if (HttpURLConnection.HTTP_OK == conn.getResponseCode()) {
                        StringBuffer sb = new StringBuffer();
                        String readLine = new String();
                        BufferedReader responseReader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                        while ((readLine = responseReader.readLine()) != null) {
                            sb.append(readLine).append("\n");
                        }
                        responseReader.close();
                        if (callback != null) {
                            Looper.prepare();
                            callback.call(sb.toString());
                            Looper.loop();
                        }
                    } else {
                        Log.i(TAG, "post请求失败");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    conn.disconnect();
                }
            }
        }).start();
    }

    private static String getRequestData(Map<String, String> params, String encode) {
        StringBuffer stringBuffer = new StringBuffer();
        try {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                stringBuffer.append(entry.getKey())
                        .append("=")
                        .append(URLEncoder.encode(entry.getValue(), encode))
                        .append("&");
            }
            stringBuffer.deleteCharAt(stringBuffer.length() - 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stringBuffer.toString();
    }

}
