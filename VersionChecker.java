package com.wuqibo.bppcallbackservice.btoolkit;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class VersionChecker {

    public static void check(final Context context, String configUrl, final Callback onCancel) {
        Http.get(configUrl, new Http.Callback() {
            @Override
            public void call(String res) {
                try {
                    JSONObject json = new JSONObject(res);
                    String newVersion = json.getString("version");
                    final String url = json.getString("url");
                    String currVersion = AppInfo.getVersionName(context);
                    Log.i("VersionChecker >>>>>>", "newVersion:" + newVersion + " = currVersion:" + currVersion);
                    if (!newVersion.equals(currVersion)) {
                        Dialog.showConfirm(context, "温馨提示", "有新的版本，是否立即更新？", new Dialog.Callback() {
                            @Override
                            public void call() {
                                Uri uri = Uri.parse(url);
                                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                context.startActivity(intent);
                            }
                        }, null);
                        return;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
				if(onCancel != null){
                    onCancel.call();
				}
            }
        });
    }

    public interface Callback {
        void call();
    }

}

