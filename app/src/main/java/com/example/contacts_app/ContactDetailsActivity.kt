package com.example.contacts_app

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.provider.ContactsContract
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton

class ContactDetailsActivity : AppCompatActivity() {
    private var id:Long = 0
    lateinit var fullName:TextView
    lateinit var phoneNumber:TextView
    @SuppressLint("Range")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact_details)
        fullName=findViewById(R.id.fullName)
        phoneNumber=findViewById(R.id.phone_number_text_view)
        id=intent.getLongExtra("id",0)

        val floatingAction:FloatingActionButton=findViewById(R.id.floating_action)
        val phonesCursor = contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null,
            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + id,
            null,
            null
        )
        phonesCursor?.let {
            while (phonesCursor.moveToNext()) {
                fullName.text=phonesCursor.getString(phonesCursor.getColumnIndex(
                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
                ))
                phoneNumber.text=phonesCursor.getString(
                    phonesCursor.getColumnIndex(
                        ContactsContract.CommonDataKinds.Phone.NUMBER
                    )
                ).replace("-", "").replace(" ", "")

            }
        }
        floatingAction.setOnClickListener {
            val intent= Intent(this,ManageContactActivity::class.java).apply {
                putExtra("id",id)
                putExtra("fullName",fullName.text)
                putExtra("phoneNumber",phoneNumber.text)
            }
            startActivity(intent)

        }
    }
}