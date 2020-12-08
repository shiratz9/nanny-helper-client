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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class JanusStreamingCreateResponse extends JanusPluginResponse {

    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Stream {
        private String id;
        private String type;
        private String description;
        private boolean is_private;
        private int audio_port;
        private int video_port;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public boolean isIs_private() {
            return is_private;
        }

        public void setIs_private(boolean is_private) {
            this.is_private = is_private;
        }

        public int getAudio_port() {
            return audio_port;
        }

        public void setAudio_port(int audio_port) {
            this.audio_port = audio_port;
        }

        public int getVideo_port() {
            return video_port;
        }

        public void setVideo_port(int video_port) {
            this.video_port = video_port;
        }

        @Override
        public String toString() {
            return "{\"id\": \"" + id + "\",\"type\":\"" + type + "\",\"description\":\"" + description + "\",\"is_private\":" + is_private +
                    ",\"audio_port\":" + audio_port + ",\"video_port\":" + video_port + "}";
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class StreamingData {
        private String streaming;
        private String created;
        private String permanent;
        private Stream stream;

        public String getStreaming() {
            return streaming;
        }

        public void setStreaming(String streaming) {
            this.streaming = streaming;
        }

        public String getCreated() {
            return created;
        }

        public void setCreated(String created) {
            this.created = created;
        }

        public String getPermanent() {
            return permanent;
        }

        public void setPermanent(String permanent) {
            this.permanent = permanent;
        }

        public Stream getStream() {
            return stream;
        }

        public void setStream(Stream stream) {
            this.stream = stream;
        }

        @Override
        public String toString() {
            return "{\"streaming\":\"" + streaming + "\",\"created\":\"" + created + "\",\"permanent\":" + permanent +
                    ",\"stream\":" + (stream != null ? stream.toString() : "null") + "}";
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class PluginData {
        private String plugin;
        private StreamingData data;

        public String getPlugin() {
            return plugin;
        }

        public void setPlugin(String plugin) {
            this.plugin = plugin;
        }

        public StreamingData getData() {
            return data;
        }

        public void setData(StreamingData data) {
            this.data = data;
        }

        @Override
        public String toString() {
            return "{\"plugin\":\"" + plugin + "\",\"data\":" + (data != null ? data.toString() : "null") + "}";
        }
    }

    private PluginData plugindata;

    public PluginData getPlugindata() {
        return plugindata;
    }

    public void setPlugindata(PluginData plugindata) {
        this.plugindata = plugindata;
    }

    @Override
    public String toString() {
        return "{\"janus\":\"" + getJanus() + "\",\"session_id\":" + getSession_id() + "\",\"transaction\":\"" + getTransaction() + "\",\"sender\":\"" + getSender() + "\","
                + "\"plugindata\":" + (plugindata != null ? plugindata.toString() : "null") + "}";
    }
}
