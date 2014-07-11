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
}
