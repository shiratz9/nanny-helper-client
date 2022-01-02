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

package com.hmdm.control.janus.json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class JanusPollResponse extends JanusPluginResponse implements Serializable {

    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class TextRoomData implements Serializable {
        private String textroom;
        private String result;

        public String getTextroom() {
            return textroom;
        }

        public void setTextroom(String textroom) {
            this.textroom = textroom;
        }

        public String getResult() {
            return result;
        }

        public void setResult(String result) {
            this.result = result;
        }

        @Override
        public String toString() {
            return "{\"textroom\":\"" + textroom + "\",\"result\":\"" + result + "\"}";
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class PluginData implements Serializable {
        private String plugin;
        private TextRoomData data;

        public String getPlugin() {
            return plugin;
        }

        public void setPlugin(String plugin) {
            this.plugin = plugin;
        }

        // Not really good because different plugins may send different data JSON
        // But since poll responds only to textroom events, that is ok
        public TextRoomData getData() {
            return data;
        }

        public void setData(TextRoomData data) {
            this.data = data;
        }

        @Override
        public String toString() {
            return "{\"plugin\":\"" + plugin + "\",\"data\":" + (data != null ? data.toString() : "null") + "}";
        }
    }

    private PluginData plugindata;
    private Jsep jsep;

    public Jsep getJsep() {
        return jsep;
    }

    public void setJsep(Jsep jsep) {
        this.jsep = jsep;
    }

    public PluginData getPlugindata() {
        return plugindata;
    }

    public void setPlugindata(PluginData plugindata) {
        this.plugindata = plugindata;
    }

    @Override
    public String toString() {
        return "{\"janus\":\"" + getJanus() + "\",\"session_id\":" + getSession_id() + "\",\"transaction\":\"" + getTransaction() + "\",\"sender\":\"" + getSender() + "\","
                + "\"plugindata\":" + (plugindata != null ? plugindata.toString() : "null") + ",\"jsep\":\"" + (jsep != null ? jsep.toString() : "null") + "}";
    }
}
