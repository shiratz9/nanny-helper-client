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

package com.hmdm.control;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.app.Service;
import android.content.Intent;
import android.graphics.Path;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.HashMap;
import java.util.Map;

public class GestureDispatchService extends AccessibilityService {
    // Sharing state
    private boolean isSharing = false;

    private static class PasswordText {
        public long timestamp;
        public String text;

        public PasswordText(String text) {
            this.text = text;
            this.timestamp = System.currentTimeMillis();
        }

        public boolean isExpired() {
            return timestamp < System.currentTimeMillis() - 300000;
        }
    }

    private Map<Integer,PasswordText> passwordTexts = new HashMap<>();

    @Override
    public void onAccessibilityEvent(AccessibilityEvent e) {
        // Notice: clipboard can't be retrieved by a background service in Android 10 and above.
        // Therefore we do not include the function of clipboard tracking from the application
    }

    @Override
    public void onInterrupt() {
    }

    // This is called from the main activity when it gets a message from the Janus socket
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null || intent.getAction() == null) {
            return Service.START_STICKY;
        }
        String action = intent.getAction();
        if (action.equals(Const.ACTION_GESTURE)) {
            String event = intent.getStringExtra(Const.EXTRA_EVENT);
            if (event != null && isSharing) {
                processMessage(event);
            }
        } else if (action.equals(Const.ACTION_SCREEN_SHARING_PERMISSION_NEEDED)) {
        } else if (action.equals(Const.ACTION_SCREEN_SHARING_START)) {
            isSharing = true;
        } else if (action.equals(Const.ACTION_SCREEN_SHARING_STOP)) {
            isSharing = false;
        }
        return Service.START_STICKY;
    }

    private void processMessage(String message) {
        float scale = SettingsHelper.getInstance(this).getFloat(SettingsHelper.KEY_VIDEO_SCALE);
        if (scale == 0) {
            scale = 1;
        }
        String[] parts = message.split(",");
        if (parts.length == 0) {
            // Empty message?
            return;
        }
        for (int i = 0; i < parts.length; i++) {
            parts[i] = parts[i]
                    .replace("%2C", ",")
                    .replace("%25", "%");
        }
        if (parts[0].equalsIgnoreCase("tap"))  {
            if (parts.length != 4) {
                Log.w(Const.LOG_TAG, "Wrong gesture event format: '" + message + "' Should be tap,X,Y,duration");
                return;
            }
            try {
                int x = Integer.parseInt(parts[1]);
                int y = Integer.parseInt(parts[2]);
                if (scale != 1) {
                    x = (int)(x / scale);
                    y = (int)(y / scale);
                }
                int duration = Integer.parseInt(parts[3]);
                simulateGesture(x, y, null, null, duration);
            } catch (Exception e) {
                Log.w(Const.LOG_TAG, "Wrong gesture event format: '" + message + "': " + e);
            }
        } else if (parts[0].equalsIgnoreCase("swipe")) {
            if (parts.length != 6) {
                Log.w(Const.LOG_TAG, "Wrong message format: '" + message + "' Should be swipe,X1,Y1,X2,Y2");
                return;
            }
            try {
                int x1 = Integer.parseInt(parts[1]);
                int y1 = Integer.parseInt(parts[2]);
                int x2 = Integer.parseInt(parts[3]);
                int y2 = Integer.parseInt(parts[4]);
                if (scale != 1) {
                    x1 = (int)(x1 / scale);
                    y1 = (int)(y1 / scale);
                    x2 = (int)(x2 / scale);
                    y2 = (int)(y2 / scale);                }
                int duration = Integer.parseInt(parts[5]);
                simulateGesture(x1, y1, x2, y2, duration);
            } catch (Exception e) {
                Log.w(Const.LOG_TAG, "Wrong gesture event format: '" + message + "': " + e);
            }
        } else if (parts[0].equalsIgnoreCase("back")) {
            performGlobalAction(GLOBAL_ACTION_BACK);
        } else if (parts[0].equalsIgnoreCase("home")) {
            performGlobalAction(GLOBAL_ACTION_HOME);
        } else if (parts[0].equalsIgnoreCase("notifications")) {
            performGlobalAction(GLOBAL_ACTION_NOTIFICATIONS);
        } else if (parts[0].equalsIgnoreCase("recents")) {
            performGlobalAction(GLOBAL_ACTION_RECENTS);
        } else if (parts[0].equalsIgnoreCase("key")) {
            if (parts.length != 2) {
                Log.w(Const.LOG_TAG, "Wrong key event format: '" + message + "' Should be key,X");
                return;
            }
            if (parts[1].length() > 1) {
                // This is a special character
                if (parts[1].equals("Backspace")) {
                    removeCharacterAtCursor(false);
                } else if (parts[1].equals("Delete")) {
                    removeCharacterAtCursor(true);
                } else if (parts[1].equals("ArrowLeft") || parts[1].equals("ArrowRight") ||
                        parts[1].equals("Home") || parts[1].equals("End")) {
                    moveCursor(parts[1]);
                }
            } else {
                enterText(parts[1]);
            }
        } else if (parts[0].equalsIgnoreCase("paste")) {
            if (parts.length != 2) {
                Log.w(Const.LOG_TAG, "Wrong key event format: '" + message + "' Should be paste,X");
                return;
            }
            enterText(parts[1]);
        } else {
            Log.w(Const.LOG_TAG, "Ignoring wrong gesture event: '" + message + "'");
        }
    }

    private void enterText(String text) {
        AccessibilityNodeInfo nodeRoot = getRootInActiveWindow();
        if (nodeRoot != null) {
            AccessibilityNodeInfo nodeFocused = findFocusedField(nodeRoot);
            if (nodeFocused != null) {
                CharSequence existingText = getExistingText(nodeFocused);
                String newText = existingText != null ? existingText.toString() : "";

                // If we're typing in the middle of the text, then textSelectionStart()
                // and textSelectionEnd() determine where should we insert the text
                int selectionStart = nodeFocused.getTextSelectionStart();
                int selectionEnd = nodeFocused.getTextSelectionEnd();
                boolean typeInMiddle = false;
                if (selectionStart > -1 && selectionStart < newText.length()) {
                    newText = newText.substring(0, selectionStart) + text + newText.substring(selectionEnd);
                    typeInMiddle = true;
                } else {
                    newText += text;
                }

                Bundle arguments = new Bundle();
                arguments.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, newText);
                nodeFocused.performAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_SET_TEXT.getId(), arguments);

                // After inserting text in the middle, we need to set the selection markers explicitly
                // because ACTION_SET_TEXT clears the selection markers
                if (typeInMiddle) {
                    arguments = new Bundle();
                    arguments.putInt(AccessibilityNodeInfo.ACTION_ARGUMENT_SELECTION_START_INT, selectionStart + text.length());
                    arguments.putInt(AccessibilityNodeInfo.ACTION_ARGUMENT_SELECTION_END_INT, selectionStart + text.length());
                    nodeFocused.performAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_SET_SELECTION.getId(), arguments);
                }
                if (nodeFocused.isPassword()) {
                    savePasswordText(nodeFocused, newText);
                }
            }
        }
    }

    private void removeCharacterAtCursor(boolean removeForward) {
        AccessibilityNodeInfo nodeRoot = getRootInActiveWindow();
        if (nodeRoot != null) {
            AccessibilityNodeInfo nodeFocused = findFocusedField(nodeRoot);
            if (nodeFocused != null) {
                CharSequence existingText = getExistingText(nodeFocused);
                if (existingText == null || existingText.equals("")) {
                    return;
                }
                String newText = existingText.toString();

                // If we're erasing in the middle of the text, then textSelectionStart()
                // and textSelectionEnd() determine what should be removed
                int selectionStart = nodeFocused.getTextSelectionStart();
                int selectionEnd = nodeFocused.getTextSelectionEnd();
                boolean typeInMiddle = false;
                if (selectionStart > -1 && selectionStart < newText.length()) {
                    if (selectionEnd > selectionStart) {
                        newText = newText.substring(0, selectionStart) + newText.substring(selectionEnd);
                    } else {
                        if (selectionStart > 0 && !removeForward) {
                            newText = newText.substring(0, selectionStart - 1) + newText.substring(selectionEnd);
                            selectionStart--;
                        } else if (selectionEnd < newText.length() && removeForward) {
                            newText = newText.substring(0, selectionStart) + newText.substring(selectionEnd + 1);
                        }
                    }
                    typeInMiddle = true;
                } else {
                    // We are at the end; Here Delete will not work and Backspace erases the last character
                    if (!removeForward && newText.length() > 0) {
                        newText = newText.substring(0, newText.length() - 1);
                    }
                }

                Bundle arguments = new Bundle();
                arguments.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, newText);
                nodeFocused.performAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_SET_TEXT.getId(), arguments);

                // After erasing text in the middle, we need to set the selection markers explicitly
                // because ACTION_SET_TEXT clears the selection markers
                if (typeInMiddle) {
                    arguments = new Bundle();
                    arguments.putInt(AccessibilityNodeInfo.ACTION_ARGUMENT_SELECTION_START_INT, selectionStart);
                    arguments.putInt(AccessibilityNodeInfo.ACTION_ARGUMENT_SELECTION_END_INT, selectionStart);
                    nodeFocused.performAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_SET_SELECTION.getId(), arguments);
                }
                if (nodeFocused.isPassword()) {
                    savePasswordText(nodeFocused, newText);
                }
            }
        }
    }

    private void moveCursor(String command) {
        AccessibilityNodeInfo nodeRoot = getRootInActiveWindow();
        if (nodeRoot != null) {
            AccessibilityNodeInfo nodeFocused = findFocusedField(nodeRoot);
            if (nodeFocused != null) {
                CharSequence existingText = getExistingText(nodeFocused);
                if (existingText == null || existingText.equals("")) {
                    return;
                }

                int selectionStart = nodeFocused.getTextSelectionStart();
                int selectionEnd = nodeFocused.getTextSelectionEnd();
                int selectionPos = -1;

                if (command.equals("ArrowLeft")) {
                    if (selectionEnd > selectionStart) {
                        selectionPos = selectionStart;
                    } else if (selectionStart > 0) {
                        selectionPos = selectionStart - 1;
                    }
                } else if (command.equals("ArrowRight")) {
                    if (selectionEnd > selectionStart) {
                        selectionPos = selectionEnd;
                    } else if (selectionEnd < existingText.length()) {
                        selectionPos = selectionEnd + 1;
                    }
                } else if (command.equals("Home")) {
                    selectionPos = 0;
                } else if (command.equals("End")) {
                    selectionPos = existingText.length();
                }

                if (selectionPos != -1) {
                    Bundle arguments = new Bundle();
                    arguments.putInt(AccessibilityNodeInfo.ACTION_ARGUMENT_SELECTION_START_INT, selectionPos);
                    arguments.putInt(AccessibilityNodeInfo.ACTION_ARGUMENT_SELECTION_END_INT, selectionPos);
                    nodeFocused.performAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_SET_SELECTION.getId(), arguments);
                }
            }
        }
    }

    private AccessibilityNodeInfo findFocusedField(AccessibilityNodeInfo node) {
        if (node.isEditable() && node.isFocused()) {
            return node;
        }
        for (int i = 0; i < node.getChildCount(); i++) {
            AccessibilityNodeInfo nodeChild = node.getChild(i);
            AccessibilityNodeInfo nodeFocused = findFocusedField(nodeChild);
            if (nodeFocused != null) {
                return nodeFocused;
            }
        }
        return null;
    }

    private CharSequence getExistingText(AccessibilityNodeInfo node) {
        if (node.isPassword()) {
            // getText() for password fields returns dots instead of characters!
            // So we save typed text and return the saved text

            // There is an issue: if both virtual keyboard and remote input are used,
            // the saved text may be wrong.
            // Here's a workaround: if there's no text, we clear the previously saved text
            CharSequence existingText = node.getText();
            if (existingText == null || existingText.length() == 0) {
                passwordTexts.remove(node.getWindowId());
                return null;
            }

            PasswordText passwordText = passwordTexts.get(node.getWindowId());
            if (passwordText != null) {
                return !passwordText.isExpired() ? passwordText.text : null;
            } else {
                return null;
            }
        }
        // node.getText() returns a hint for text fields (terrible!)
        // Here's a workaround against this
        CharSequence hintText = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            hintText = node.getHintText();
        }
        CharSequence existingText = node.getText();
        if (hintText != null && existingText.equals(hintText)) {
            existingText = null;
        }
        return existingText;
    }

    private void savePasswordText(AccessibilityNodeInfo node, String text) {
        passwordTexts.put(node.getWindowId(), new PasswordText(text));
    }

    private void simulateGesture(Integer x1, Integer y1, Integer x2, Integer y2, int duration) {
        GestureDescription.Builder gestureBuilder = new GestureDescription.Builder();

        if (x2 == null || y2 == null) {
            // Tap
            Path clickPath = new Path();
            clickPath.moveTo(x1, y1);
            GestureDescription.StrokeDescription clickStroke =
                    new GestureDescription.StrokeDescription(clickPath, 0, duration);
            gestureBuilder.addStroke(clickStroke);
            Log.d(Const.LOG_TAG, "Simulating a gesture: tap, x1=" + x1 + ", y1=" + y1 + ", duration=" + duration);
        } else {
            // Swipe
            Path clickPath = new Path();
            clickPath.moveTo(x1, y1);
            clickPath.lineTo(x2, y2);
            GestureDescription.StrokeDescription clickStroke =
                    new GestureDescription.StrokeDescription(clickPath, 0, duration);
            gestureBuilder.addStroke(clickStroke);
            Log.d(Const.LOG_TAG, "Simulating a gesture: swipe, x1=" + x1 + ", y1=" + y1 + ", x2=" + x2 + ", y2=" + y2 + ", duration=" + duration);
        }

        boolean result = dispatchGesture(gestureBuilder.build(), null, null);
        Log.d(Const.LOG_TAG, "Gesture dispatched, result=" + result);
    }
}
