package com.drovisfrovis.contactsmanager.activities

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.drovisfrovis.contactsmanager.R
import com.drovisfrovis.contactsmanager.classes.ContactsDataModel
import java.io.*


class SplashScreen : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(false)
        } else {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        }

        val isFirstRun = getSharedPreferences("PREFERENCE", MODE_PRIVATE).getBoolean("firstTurn", true)

        MainActivity.list = readDataFromFile(MainActivity.ALL_CONTACT_FILE)
        MainActivity.favContList = readDataFromFile(MainActivity.FAV_CONTACT_FILE)
        if(isFirstRun){
            val names = resources.getStringArray(R.array.names)
            val phones = resources.getStringArray(R.array.phones)
            val emails = resources.getStringArray(R.array.emails)
            val address = resources.getStringArray(R.array.addresses)
            val dobs = resources.getStringArray(R.array.dobs)
            val images = resources.getStringArray(R.array.images)

            for(i in names.indices){
                MainActivity.list.add(ContactsDataModel(names[i], phones[i].toLong(), emails[i], address[i], dobs[i], images[i]))
            }
            MainActivity.favContList.add(ContactsDataModel(names[2], phones[2].toLong(), emails[2], address[2], dobs[2], images[2]))

            MainActivity.list.sortBy { it.name }
            MainActivity.favContList.sortBy { it.name }
            writeDataToFile(MainActivity.ALL_CONTACT_FILE, MainActivity.list)
            writeDataToFile(MainActivity.FAV_CONTACT_FILE, MainActivity.favContList)

            getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit().putBoolean("firstTurn", false).apply()
        }

        Handler(Looper.getMainLooper()).postDelayed(Runnable{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }, 2300)

    }

    private fun writeDataToFile(fileName: String, dataList: List<ContactsDataModel>) {
        try {
            val outputStreamWriter = OutputStreamWriter(openFileOutput(fileName, MODE_PRIVATE))
            var data = ""
            for(i in dataList.indices){
                dataList[i].apply {
                    data += "$name|$phoneNo|$email|$address|$dateOfBirth|$contactImage\n"
                }
            }
            outputStreamWriter.append(data)
            outputStreamWriter.close()
        } catch (e: IOException) {
            Log.e("Exception", "File write failed: $e")
        }
    }

    private fun readDataFromFile(fileName: String): MutableList<ContactsDataModel> {
        try {
            val isr = InputStreamReader(openFileInput(fileName))
            val br = BufferedReader(isr)
            val dataList = mutableListOf<ContactsDataModel>()
            var dataString: String? = br.readLine()
            var list: List<String>

            while (dataString != null) {
                list = dataString.split('|')
                dataList.add(ContactsDataModel(list[0], list[1].toLong(), list[2], list[3], list[4], list[5]))
                dataString = br.readLine()
            }
            return dataList
        } catch (e: FileNotFoundException) {
            Log.e("Exception", "File not found: $e")
        } catch (e: IOException) {
            Log.e("Exception", "Can not read file: $e")
        }
        return mutableListOf()
    }
}