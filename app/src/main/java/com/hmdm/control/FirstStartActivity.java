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

package com.hmdm.control;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class FirstStartActivity extends AppCompatActivity {

    private TextView tvUsagePolicy;
    private TextView tvPrivacyPolicy;
    private Button buttonAgree;
    private TextView tvExit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_start);

        tvUsagePolicy = findViewById(R.id.usage_policy);
        String appName = getString(R.string.app_name);
        String accessibilityPolicy = getString(R.string.accessibility_policy);
        String screenSharingPolicy = getString(R.string.screen_sharing_policy);
        String usagePolicy = String.format(getString(R.string.usage_policy),
                appName, accessibilityPolicy, screenSharingPolicy, appName, appName);
        tvUsagePolicy.setText(Html.fromHtml(usagePolicy, Html.FROM_HTML_MODE_COMPACT));

        tvPrivacyPolicy = findViewById(R.id.privacy_policy);
        tvPrivacyPolicy.setOnClickListener(view -> {
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.privacy_policy_url))));
            } catch (ActivityNotFoundException e) {
                Toast.makeText(FirstStartActivity.this, "Browser is not installed!",  Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        });

        buttonAgree = findViewById(R.id.agree);
        buttonAgree.setOnClickListener(view -> {
            setResult(RESULT_OK);
            finish();
        });

        tvExit = findViewById(R.id.exit);
        tvExit.setOnClickListener(view -> {
            finish();
        });
    }
}
