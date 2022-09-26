package com.example.contacts_app

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_CONTACTS,Manifest.permission.WRITE_CONTACTS), 1)

        val fram = supportFragmentManager.beginTransaction()
        if(checkAndRequestPermissions())
        fram.replace(R.id.contacts_fragment, ContactsFragment())
        else
            fram.replace(R.id.contacts_fragment, RequestPermissionScreen())
        fram.commit()

    }

    val REQUEST_ID_MULTIPLE_PERMISSIONS = 1

    private fun checkAndRequestPermissions(): Boolean {
        val writeContacts = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_CONTACTS)
        val readContacts =
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
        val listPermissionsNeeded: MutableList<String> = ArrayList()
        if (writeContacts != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_CONTACTS)
        }
        if (readContacts != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_CONTACTS)
        }
        if (listPermissionsNeeded.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                this,
                listPermissionsNeeded.toTypedArray(),
                REQUEST_ID_MULTIPLE_PERMISSIONS
            )
            return false
        }
        return true
    }
}