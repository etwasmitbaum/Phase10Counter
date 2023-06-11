package com.tjEnterprises.phase10Counter

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import com.tjEnterprises.phase10Counter.data.AppDatabase
import com.tjEnterprises.phase10Counter.data.roomBackup.RoomBackup
import java.io.BufferedReader
import java.io.InputStreamReader


class SettingsActivity() : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings, SettingsFragment())
                .commit()
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    class SettingsFragment : PreferenceFragmentCompat() {

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)

            val db = context?.let { AppDatabase.getInstance(it) }
            val roomBackup = context?.let { RoomBackup(it) }
            if (db != null) {
                roomBackup
                    ?.database(db)
                    ?.enableLogDebug(true)
                    ?.backupIsEncrypted(false)
                    ?.backupLocation(RoomBackup.BACKUP_FILE_LOCATION_EXTERNAL)
            }

            val backupPref: Preference? = findPreference("backup")
            val restorePref: Preference? = findPreference("restore")
            val savePathPref: Preference? = findPreference("save_path")

            val githubPref: Preference? = findPreference("link_to_github")
            val releaseNotesPref: Preference? = findPreference("release_notes")
            val appLicensePref: Preference? = findPreference("app_license")
            val allOpenSourceLicenses: Preference? = findPreference("all_opensource_license")
            val versionPres: Preference? = findPreference("app_version")

            backupPref?.isVisible = db?.PlayerDataDao()?.getPlayerCount()!! >= 1

            // create backup filename consisting of the player name, but considering potentially dangerous characters
            // then creating the backup
            backupPref?.setOnPreferenceClickListener {
                var name = ""
                val playerDao = db.PlayerDataDao()

                // add all player names
                for (i in 0 until playerDao.getPlayerCount()) {
                    name += (playerDao.getSinglePlayer(i).name) + "-"
                }
                // remove dangerous chars of path. I don't know hot to use regex, and this works
                name = name.replace("/", "_")
                    .replace("<", "_")
                    .replace(">", "_")
                    .replace(":", "_")
                    .replace("\"", "_")
                    .replace("/", "_")
                    .replace("\\", "_")
                    .replace("|", "_")
                    .replace("?", "_")
                    .replace("*", "_")

                // add current time
                name += "_" + RoomBackup.getTime()

                roomBackup?.customBackupFileName(name)?.backup()
                true
            }

            // restore backup and setting flag, to resync the highscore databases
            restorePref?.setOnPreferenceClickListener {
                val sharedPref = context?.getSharedPreferences(Controller.GLOBAL_FLAGS_SHARED_PREF_KEY, Context.MODE_PRIVATE)
                // must use .commit() here, because the restore action will restart the app and probably cancel the write process of .apply()
                sharedPref?.edit()
                    ?.putBoolean(Controller.GLOBAL_FLAGS_SHARED_PREF_RESOTORE_OCCURRED_KEY, true)?.commit()
                roomBackup?.restore()
                true
            }

            savePathPref?.summary = context?.getExternalFilesDir("backup").toString().removePrefix("/storage/emulated/0")

            // open repo on click
            if (githubPref != null) {
                githubPref.intent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://github.com/etwasmitbaum/Phase10Counter")
                )
            }

            // open releases page for release notes on clock
            if (releaseNotesPref != null) {
                releaseNotesPref.intent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://github.com/etwasmitbaum/Phase10Counter/releases")
                )
            }

            // open dialog with GPLv3 license
            appLicensePref?.setOnPreferenceClickListener {
                val builder = AlertDialog.Builder(context, R.style.AlertDialog_AppCompat_phase10Counter)
                builder.setTitle(getString(R.string.GPLv3License))
                builder.setMessage(readGPLv3LicenseText())
                builder.setPositiveButton(getString(R.string.ok)) { dialog, _ ->
                    dialog.dismiss()
                }
                val dialog: AlertDialog = builder.create()
                dialog.show()
                true
            }

            // open all the licenses for the dependencies used
            allOpenSourceLicenses?.setOnPreferenceClickListener {
                context?.let { it1 -> OssLicensesMenuActivity.setActivityTitle(it1.getString(R.string.app_license)) }
                startActivity(Intent(context, OssLicensesMenuActivity::class.java))
                true
            }

            // show version number
            versionPres?.summary = BuildConfig.VERSION_NAME
        }

        private fun readGPLv3LicenseText(): String {
            // Lese den Lizenztext aus einer raw-Ressource
            val inputStream = resources.openRawResource(R.raw.license)
            val reader = InputStreamReader(inputStream)
            val bufferedReader = BufferedReader(reader)
            val stringBuilder = StringBuilder()
            var line: String? = bufferedReader.readLine()
            while (line != null) {
                stringBuilder.append(line)
                stringBuilder.append("\n")
                line = bufferedReader.readLine()
            }
            return stringBuilder.toString()
        }
    }
}