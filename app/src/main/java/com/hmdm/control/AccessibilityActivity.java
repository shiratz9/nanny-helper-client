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

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Html;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class AccessibilityActivity extends AppCompatActivity {

    private TextView tvHint;
    private Button buttonContinue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accessibility);

        tvHint = findViewById(R.id.accessibility_hint);
        String appName = getString(R.string.app_name);
        String accessibilityHint = String.format(getString(R.string.accessibility_hint),
                appName, appName, appName);
        tvHint.setText(Html.fromHtml(accessibilityHint, Html.FROM_HTML_MODE_COMPACT, source -> {
            Drawable drawFromPath;
            int path =
                    AccessibilityActivity.this.getResources().getIdentifier(source, "drawable",
                            getPackageName());
            drawFromPath = AccessibilityActivity.this.getResources().getDrawable(path);
            int w = drawFromPath.getIntrinsicWidth();
            int h = drawFromPath.getIntrinsicHeight();
            drawFromPath.setBounds(0, 0, drawFromPath.getIntrinsicWidth() / 2,
                    drawFromPath.getIntrinsicHeight() / 2);
            return drawFromPath;
        }, null));

        buttonContinue = findViewById(R.id.button_continue);
        buttonContinue.setOnClickListener(view -> finish());
    }
}
