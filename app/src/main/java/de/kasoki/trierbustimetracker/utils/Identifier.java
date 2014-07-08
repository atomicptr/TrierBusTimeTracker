// Copyright (c) 2014 Christopher Kaster
//
// This file is part of Trier Bus Time Tracker <https://github.com/kasoki/TrierBusTimeTracker>
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in
// all copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
// THE SOFTWARE.
package de.kasoki.trierbustimetracker.utils;

public class Identifier {

    public static final String APP_FAVORITE_FILE_IDENTIFIER = "de.kasoki.trierbustimetracker.CONF_FAVORITES";
    public static final String APP_SETTINGS_FILE_IDENTIFIER = "de.kasoki.trierbustimetracker.SETTINGS";
    public static final String APP_PREFERENCES_FAVORITE_IDENTIFIER = "de.kasoki.trierbustimetracker.FAVORITES";

    public static final String APP_SETTINGS_USE_NOTIFICATIONS_IDENTIFIER = "de.kasoki.trierbustimetracker.SettingsActivity.USE_NOTIFICATIONS";
    public static final String APP_SETTINGS_USE_AUTO_RELOAD_IDENTIFIER = "de.kasoki.trierbustimetracker.SettingsActivity.USE_AUTO_RELOAD";
    public static final String APP_SETTINGS_USE_MOBILE_CONN_FOR_APP_UPDATE = "de.kasoki.trierbustimetracker.SettingsActivity.USE_MOBILE_CONN_FOR_APP_UPDATE";

    public static final int SETTINGS_REQUEST_CODE = 0x01;

    private Identifier() {
    }
}
