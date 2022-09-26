package com.example.contacts_app

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_CONTACTS), 1)
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_CONTACTS), 1)

        val fram = supportFragmentManager.beginTransaction()
        fram.replace(R.id.contacts_fragment, ContactsFragment())
        fram.commit()

    }

}