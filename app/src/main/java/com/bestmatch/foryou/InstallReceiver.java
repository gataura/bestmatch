package com.bestmatch.foryou;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;

import com.android.installreferrer.api.InstallReferrerClient;
import com.android.installreferrer.api.InstallReferrerStateListener;
import com.android.installreferrer.api.ReferrerDetails;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class InstallReceiver extends BroadcastReceiver {
//    private String m_referrer;
    private InstallReferrerClient m_referrerClient;
    private String m_url = "https://a.d8g.xyz/app/ref";

    public InstallReceiver() {
        this.m_referrerClient = null;
    }

    public void onReceive(final Context context, final Intent intent) {
        try {
            String uuid = UUID.randomUUID().toString();

            Map<String, String> postData = new HashMap<>();
            postData.put("type", "intent");
            postData.put("uuid", uuid);
            Bundle bundle = intent.getExtras();
            Set<String> keys = bundle.keySet();
            for (String key : keys) {
                Object o = bundle.get(key);
                postData.put(key, o.toString());
            }
            new HttpPostAsyncTask(postData).execute(m_url);

            (this.m_referrerClient = InstallReferrerClient.newBuilder(context).build()).startConnection(new InstallReferrerStateListener() {
                @Override
                public void onInstallReferrerServiceDisconnected() {
                }

                @Override
                public void onInstallReferrerSetupFinished(final int n) {
                    if (n == 0) {
                        try {
                            final ReferrerDetails installReferrer = InstallReceiver.this.m_referrerClient.getInstallReferrer();
                            installReferrer.getInstallReferrer();
                            Map<String, String> postData2 = new HashMap<>();
                            postData2.put("type", "client");
                            postData.put("uuid", uuid);
                            postData2.put("referer", installReferrer.getInstallReferrer());
                            new HttpPostAsyncTask(postData2).execute(m_url);
                        } catch (RemoteException ignored) {
                        }
                    }
                }
            });
        } catch (Exception ignored) {
        }
    }
}