/*
 * aPuppet: Open Source Remote Access Software for Android
 * https://apuppet.org
 *
 * Copyright (C) 2020 apuppet.org
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
 *
 */

package com.hmdm.control.janus.json;

public class JanusAttachRequest extends JanusRequest {
    private String plugin;

    public JanusAttachRequest() {
    }

    public JanusAttachRequest(String secret, String pluginName) {
        super(secret, "attach", true);
        this.plugin = pluginName;
    }

    public String getPlugin() {
        return plugin;
    }

    public void setPlugin(String plugin) {
        this.plugin = plugin;
    }
}
