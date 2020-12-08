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

package com.hmdm.control.janus.server;

import com.hmdm.control.janus.json.JanusAttachRequest;
import com.hmdm.control.janus.json.JanusJsepRequest;
import com.hmdm.control.janus.json.JanusMessageRequest;
import com.hmdm.control.janus.json.JanusPollResponse;
import com.hmdm.control.janus.json.JanusRequest;
import com.hmdm.control.janus.json.JanusResponse;
import com.hmdm.control.janus.json.JanusStreamingCreateRequest;
import com.hmdm.control.janus.json.JanusStreamingCreateResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface JanusServerApi {

    @POST("/janus")
    @Headers("Content-Type: application/json")
    Call<JanusResponse> createSession(@Body JanusRequest request);

    @POST("/janus/{session}")
    @Headers("Content-Type: application/json")
    Call<JanusResponse> attachPlugin(@Path("session") String sessionId, @Body JanusAttachRequest request);

    @POST("/janus/{session}/{handle}")
    @Headers("Content-Type: application/json")
    Call<JanusResponse> sendJsep(@Path("session") String sessionId, @Path("handle") String handleId, @Body JanusJsepRequest request);

    @POST("/janus/{session}/{handle}")
    @Headers("Content-Type: application/json")
    Call<JanusResponse> sendMessage(@Path("session") String sessionId, @Path("handle") String handleId, @Body JanusMessageRequest request);

    @POST("/janus/{session}/{handle}")
    @Headers("Content-Type: application/json")
    Call<JanusStreamingCreateResponse> createStreaming(@Path("session") String sessionId, @Path("handle") String handleId, @Body JanusStreamingCreateRequest request);

    @POST("/janus/{session}")
    @Headers("Content-Type: application/json")
    Call<JanusResponse> destroySession(@Path("session") String sessionId, @Body JanusRequest request);

    @GET("/janus/{session}")
    Call<JanusPollResponse> poll(@Path("session") String sessionId, @Query("apisecret") String secret);

}
