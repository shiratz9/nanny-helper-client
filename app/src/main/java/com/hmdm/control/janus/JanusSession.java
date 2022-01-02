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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import com.hmdm.control.Const;
import com.hmdm.control.R;
import com.hmdm.control.ServerApiHelper;
import com.hmdm.control.janus.json.JanusAttachRequest;
import com.hmdm.control.janus.json.JanusPollResponse;
import com.hmdm.control.janus.json.JanusRequest;
import com.hmdm.control.janus.json.JanusResponse;
import com.hmdm.control.janus.server.JanusServerApi;
import com.hmdm.control.janus.server.JanusServerApiFactory;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Response;

public class JanusSession {
    private JanusServerApi apiInstance;
    private String secret;
    private String sessionId;
    private String errorReason;
    private Map<String,JanusPlugin> pluginMap = new HashMap<>();

    public void init(Context context) {
        apiInstance = JanusServerApiFactory.getApiInstance(context);
        secret = JanusServerApiFactory.getSecret(context);
    }

    public String getSessionId() {
        return sessionId;
    }

    public String getErrorReason() {
        return errorReason;
    }

    private BroadcastReceiver pollServiceReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String event = intent.getStringExtra(Const.EXTRA_EVENT);
            for (Map.Entry<String,JanusPlugin> entry : pluginMap.entrySet()) {
                if (Const.EXTRA_WEBRTCUP.equalsIgnoreCase(event)) {
                    entry.getValue().onWebRtcUp(context);
                } else if (Const.EXTRA_EVENT.equalsIgnoreCase(event)) {
                    JanusPollResponse message = (JanusPollResponse) intent.getSerializableExtra(Const.EXTRA_MESSAGE);
                    if (message != null && message.getPlugindata() != null) {
                        String pluginName = message.getPlugindata().getPlugin();
                        if (entry.getKey().equalsIgnoreCase(pluginName)) {
                            entry.getValue().onPollingEvent(message);
                        }
                    }
                }
            }
        }
    };

    // Must be run in the background thread
    public int create(Context context) {
        errorReason = null;
        Response<JanusResponse> response = ServerApiHelper.execute(apiInstance.createSession(new JanusRequest(secret, "create", true)), "create session");
        if (response == null) {
            errorReason = ServerApiHelper.lastError;
            return Const.NETWORK_ERROR;
        }
        if (response.body() != null && response.body().getJanus().equalsIgnoreCase("success") && response.body().getData() != null) {
            sessionId = response.body().getData().getId();
        } else {
            if (response.body().getError() != null && response.body().getError().getCode() != null && response.body().getError().getCode() == 403) {
                errorReason = context.getString(R.string.wrong_secret);
            } else {
                errorReason = context.getString(R.string.server_error);
            }
            Log.w(Const.LOG_TAG, "Wrong server response: " + response.code());
            return Const.SERVER_ERROR;
        }
        Log.i(Const.LOG_TAG, "Created Janus session, id=" + sessionId);
        return Const.SUCCESS;
    }

    public int attachPlugin(JanusPlugin plugin) {
        JanusAttachRequest attachRequest = new JanusAttachRequest(secret, plugin.getName());
        Response<JanusResponse> response = ServerApiHelper.execute(apiInstance.attachPlugin(sessionId, attachRequest), "attach textroom");
        if (response == null) {
            errorReason = "Network error";
            return Const.NETWORK_ERROR;
        }
        if (response.body() != null && response.body().getJanus().equalsIgnoreCase("success") && response.body().getData() != null) {
            plugin.setHandleId(response.body().getData().getId());
            plugin.setSessionId(sessionId);
            pluginMap.put(plugin.getName(), plugin);
            Log.i(Const.LOG_TAG, "Attached plugin " + plugin.getName() + ", handle_id=" + plugin.getHandleId());
       } else {
            errorReason = "Server error";
            Log.w(Const.LOG_TAG, "Wrong server response: " + response.body().toString());
            return Const.SERVER_ERROR;
        }
        return Const.SUCCESS;

    }

    public void startPolling(Context context) {
        context.registerReceiver(pollServiceReceiver, new IntentFilter(Const.ACTION_JANUS_SESSION_POLL));
        Intent intent = new Intent(context, JanusSessionPollService.class);
        intent.putExtra(Const.EXTRA_SESSION, sessionId);
        context.startService(intent);
    }

    public void stopPolling(Context context) {
        Intent intent = new Intent(context, JanusSessionPollService.class);
        context.stopService(intent);
        try {
            context.unregisterReceiver(pollServiceReceiver);
        } catch (Exception e) {
            // IllegalArgumentException: receiver not registered
            e.printStackTrace();
        }
    }

    // Must be run in the background thread
    public int destroy() {
        for (Map.Entry<String,JanusPlugin> entry : pluginMap.entrySet()) {
            entry.getValue().destroy();
        }

        Response<JanusResponse> response = ServerApiHelper.execute(apiInstance.destroySession(sessionId, new JanusRequest(secret, "destroy", true)), "create session");
        sessionId = null;
        if (response == null) {
            errorReason = "Network error";
            return Const.NETWORK_ERROR;
        }
        return Const.SUCCESS;
    }
}
