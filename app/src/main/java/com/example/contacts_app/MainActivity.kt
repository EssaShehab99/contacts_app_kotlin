package com.example.contacts_app

import android.provider.ContactsContract
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val fram = supportFragmentManager.beginTransaction()
        fram.replace(R.id.contacts_fragment, ContactsFragment())
        fram.commit()

    }
}