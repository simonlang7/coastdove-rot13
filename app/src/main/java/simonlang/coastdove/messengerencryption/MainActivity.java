/*  Coast Dove
    Copyright (C) 2016  Simon Lang
    Contact: simon.lang7 at gmail dot com

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package simonlang.coastdove.messengerencryption;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import simonlang.coastdove.lib.CoastDoveModules;

public class MainActivity extends AppCompatActivity {
    public static int OVERLAY_PERMISSION_REQUEST_CODE = 2236;
    public static final String PREF_VERSION = "appVersion";
    public static final int APP_VERSION = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkRegisterModule();
        checkOverlayPermissions();
    }

    private void checkRegisterModule() {
        // Check version
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        int currentVersion = preferences.getInt(PREF_VERSION, 0);
        if (APP_VERSION > currentVersion) {
            CoastDoveModules.registerModule(this, EncryptionService.class, "ROT13 Encryption", "com.google.android.talk", "com.whatsapp");
            preferences.edit().putInt(PREF_VERSION, APP_VERSION).apply();
        }
    }

    @TargetApi(23)
    public void checkOverlayPermissions() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (!Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, OVERLAY_PERMISSION_REQUEST_CODE);
            }
        }
    }
}
