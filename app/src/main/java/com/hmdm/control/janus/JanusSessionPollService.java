/*
 * Headwind Remote: Open Source Remote Access Software for Android
 * https://headwind-remote.com
 *
 * Copyright (C) 2022 headwind-remote.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hmdm.control.janus;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.hmdm.control.Const;
import com.hmdm.control.ServerApiHelper;
import com.hmdm.control.janus.json.JanusPollResponse;
import com.hmdm.control.janus.server.JanusServerApi;
import com.hmdm.control.janus.server.JanusServerApiFactory;

import retrofit2.Call;
import retrofit2.Response;

public class JanusSessionPollService extends Service {
    private int state = Const.STATE_DISCONNECTED;
    private String session;

    private JanusServerApi apiInstance;
    private String secret;
    Call<JanusPollResponse> currentCall;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) {
            // Exit the app, let's just finish
            return Service.START_NOT_STICKY;
        }
        String session = intent.getStringExtra(Const.EXTRA_SESSION);
        if (session != null) {
            this.session = session;
        }

        if (state == Const.STATE_DISCONNECTED) {
            apiInstance = JanusServerApiFactory.getApiInstance(this);
            secret = JanusServerApiFactory.getSecret(this);

            // Start polling
            state = Const.STATE_CONNECTED;
            loop();
        }
        return Service.START_STICKY;
    }

    public void loop() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    currentCall = apiInstance.poll(session, secret);
                    Response<JanusPollResponse> response = ServerApiHelper.execute(currentCall, "poll session");
                    if (response == null) {
                        if (state == Const.STATE_DISCONNECTING) {
                            // Service stopped
                            state = Const.STATE_DISCONNECTED;
                            break;
                        } else {
                            // Occasional error (already logged)
                            try {
                                Thread.sleep(1000);         // Avoid looping with 100% CPU
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            continue;
                        }
                    }
                    Log.d(Const.LOG_TAG, "Polled data: " + response.body().toString());
                    String janus = response.body().getJanus();
                    if (janus == null) {
                        // Error!
                        Log.w(Const.LOG_TAG, "Wrong response body: " + response.body().toString());
                    } else if (janus.equalsIgnoreCase("keepalive")) {
                        Log.d(Const.LOG_TAG, "Janus: Keep-Alive");
                    } else if (janus.equalsIgnoreCase("webrtcup")) {
                        Intent intent = new Intent(Const.ACTION_JANUS_SESSION_POLL);
                        intent.putExtra(Const.EXTRA_EVENT, Const.EXTRA_WEBRTCUP);
                        sendBroadcast(intent);
                    } else if (janus.equalsIgnoreCase("event")) {
                        Intent intent = new Intent(Const.ACTION_JANUS_SESSION_POLL);
                        intent.putExtra(Const.EXTRA_EVENT, Const.EXTRA_EVENT);
                        intent.putExtra(Const.EXTRA_MESSAGE, response.body());
                        sendBroadcast(intent);
                    } else {
                        Log.w(Const.LOG_TAG, "Unknown poll result: " + response.body().toString());
                    }
                }
            }
        }).start();
    }

    @Override
    public void onDestroy() {
        state = Const.STATE_DISCONNECTING;
        if (currentCall != null) {
            currentCall.cancel();
        }
    }
}
