package com.example.contacts_app

import android.annotation.SuppressLint
import android.content.ContentProviderOperation
import android.content.Intent
import android.content.OperationApplicationException
import android.os.Bundle
import android.os.RemoteException
import android.provider.ContactsContract
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton

class ContactDetailsActivity : AppCompatActivity() {
    private var id: Long = 0
    lateinit var fullName: TextView
    lateinit var phoneNumber: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact_details)
        fullName = findViewById(R.id.fullName)
        phoneNumber = findViewById(R.id.phone_number_text_view)
        id = intent.getLongExtra("id", 0)
        val editBTN: FloatingActionButton = findViewById(R.id.edit_btn)
        val deleteBTN: FloatingActionButton = findViewById(R.id.delete_btn)
        editBTN.setOnClickListener {
            val intent = Intent(this, ManageContactActivity::class.java).apply {
                putExtra("id", id)
                putExtra("fullName", fullName.text)
                putExtra("phoneNumber", phoneNumber.text)
            }
            startActivity(intent)

        }
        deleteBTN.setOnClickListener {
            deletePhone()
        }
    }

    override fun onStart() {
        super.onStart()
        getContactDetails()
    }

    @SuppressLint("Range")
    private fun getContactDetails() {
        val phonesCursor = contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null,
            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + id,
            null,
            null
        )
        phonesCursor?.let {
            while (phonesCursor.moveToNext()) {
                fullName.text = phonesCursor.getString(
                    phonesCursor.getColumnIndex(
                        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
                    )
                )
                phoneNumber.text = phonesCursor.getString(
                    phonesCursor.getColumnIndex(
                        ContactsContract.CommonDataKinds.Phone.NUMBER
                    )
                ).replace("-", "").replace(" ", "")

            }
        }
    }

    private fun  deletePhone(){
        val ops = ArrayList<ContentProviderOperation>()
        ops.add(
            ContentProviderOperation
                .newDelete(ContactsContract.RawContacts.CONTENT_URI)
                .withSelection(
                    ContactsContract.RawContacts.CONTACT_ID
                            + " = ?",
                    arrayOf(id.toString())
                )
                .build()
        );

        try {
            contentResolver.applyBatch(
                ContactsContract.AUTHORITY, ops
            )
            Toast.makeText(this, "Contact Delete Successfully", Toast.LENGTH_LONG).show()
            finish()
        } catch (e: OperationApplicationException) {
            e.printStackTrace()
        } catch (e: RemoteException) {
            e.printStackTrace()
        }

    }
}