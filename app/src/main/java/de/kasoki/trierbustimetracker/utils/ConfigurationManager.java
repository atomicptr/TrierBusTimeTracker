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

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;

public class ConfigurationManager {

    private Activity parent;

    // settings_activity
    private boolean settingsLoaded = false;
    private boolean useAutoReload = false;
    private boolean useNotifications = false;
    private boolean useMobileConnectionForAppUpdates = true;

    public ConfigurationManager(Activity parent) {
        this.parent = parent;
    }

    public ArrayList<String> getFavorites() {
        ArrayList<String> favorites = new ArrayList<String>();

        SharedPreferences prefs = parent.getSharedPreferences(Identifier.APP_FAVORITE_FILE_IDENTIFIER, Context.MODE_PRIVATE);

        int length = prefs.getInt("favorites_length", 0);

        for(int i = 0; i < length; i++) {
            favorites.add(prefs.getString("favorites" + i, null));
        }

        return favorites;
    }

    public void saveFavorites(ArrayList<String> favorites) {
        SharedPreferences.Editor editor = parent.getSharedPreferences(Identifier.APP_FAVORITE_FILE_IDENTIFIER, Context.MODE_PRIVATE).edit();

        int length = favorites.size();

        editor.putInt("favorites_length", length);

        for(int i = 0; i < length; i++) {
            editor.putString("favorites" + i, favorites.get(i));
        }

        editor.commit();
    }

    /** Removes all configuration files */
    public void clear() {
        String[] configFileNames = {
            Identifier.APP_FAVORITE_FILE_IDENTIFIER,
            Identifier.APP_SETTINGS_FILE_IDENTIFIER
        };

        for(String configFile : configFileNames) {
            SharedPreferences.Editor editor = parent.getSharedPreferences(configFile, Context.MODE_PRIVATE).edit();
            editor.clear().commit();
        }
    }

    public void saveSettingsActivity(boolean useAutoReload, boolean useNotifications, boolean useMobileConnectionForAppUpdates) {
        SharedPreferences.Editor editor = parent.getSharedPreferences(Identifier.APP_SETTINGS_FILE_IDENTIFIER, Context.MODE_PRIVATE).edit();

        editor.putBoolean(Identifier.APP_SETTINGS_USE_AUTO_RELOAD_IDENTIFIER, useAutoReload);
        editor.putBoolean(Identifier.APP_SETTINGS_USE_NOTIFICATIONS_IDENTIFIER, useNotifications);
        editor.putBoolean(Identifier.APP_SETTINGS_USE_MOBILE_CONN_FOR_APP_UPDATE, useMobileConnectionForAppUpdates);

        editor.commit();
    }

    public void loadSettingsActivity() {
        SharedPreferences prefs = parent.getSharedPreferences(Identifier.APP_SETTINGS_FILE_IDENTIFIER, Context.MODE_PRIVATE);

        this.useAutoReload = prefs.getBoolean(Identifier.APP_SETTINGS_USE_AUTO_RELOAD_IDENTIFIER, false);
        this.useNotifications = prefs.getBoolean(Identifier.APP_SETTINGS_USE_NOTIFICATIONS_IDENTIFIER, false);
        this.useMobileConnectionForAppUpdates = prefs.getBoolean(Identifier.APP_SETTINGS_USE_MOBILE_CONN_FOR_APP_UPDATE, true);

        this.settingsLoaded = true;
    }

    public boolean useAutoReload() {
        if(!this.settingsLoaded) {
            this.loadSettingsActivity();
        }

        return this.useAutoReload;
    }

    public boolean useNotifications() {
        if(!this.settingsLoaded) {
            this.loadSettingsActivity();
        }

        return this.useNotifications;
    }

    public boolean useMobileConnectionForAppUpdates() {
        if(!this.settingsLoaded) {
            this.loadSettingsActivity();
        }

        return this.useMobileConnectionForAppUpdates;
    }
}
