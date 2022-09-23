package com.example.contacts_app

import android.content.ContentUris
import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.UserDictionary
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity


class ManageContactActivity : AppCompatActivity() {
    private lateinit var saveBTN: Button
    private lateinit var fullName: EditText
    private lateinit var phoneNumber: EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_contact)
        var id = intent.getLongExtra("id", 0)
        saveBTN = findViewById(R.id.save_btn)
        fullName = findViewById(R.id.full_name)
        phoneNumber = findViewById(R.id.phone_number_edit_text)
        if (id.toInt() != 0) {
            fullName.setText(intent.getStringExtra("fullName"))
            phoneNumber.setText(intent.getStringExtra("phoneNumber"))
        }
        if (id.toInt() != 0)
            saveBTN.text = "Edit"
        else
            saveBTN.text = "Save"
        saveBTN.setOnClickListener {
            if (id.toInt() != 0) {
                updatePhone(id,phoneNumber.text.toString(),fullName.text.toString())
//                val intent = Intent(Intent.ACTION_INSERT_OR_EDIT).apply {
//
//                    type = ContactsContract.Contacts.CONTENT_ITEM_TYPE
//
//                    putExtra(
//                        ContactsContract.Intents.Insert.NAME,
//                        fullName.text.toString()
//                    )
//                    putExtra(
//                        ContactsContract.Intents.Insert.PHONETIC_NAME,
//                        ContactsContract.CommonDataKinds.Email.TYPE_WORK
//                    )
//                    putExtra(ContactsContract.Intents.Insert.PHONE, phoneNumber.text.toString())
//                    putExtra(
//                        ContactsContract.Intents.Insert.PHONE_TYPE,
//                        ContactsContract.CommonDataKinds.Phone.TYPE_WORK
//                    )
//
//                }
//                startActivity(intent)
            } else {
                val intent = Intent(ContactsContract.Intents.Insert.ACTION).apply {

                    type = ContactsContract.RawContacts.CONTENT_TYPE

                    putExtra(
                        ContactsContract.Intents.Insert.NAME,
                        fullName.text.toString()
                    )
                    putExtra(
                        ContactsContract.Intents.Insert.PHONETIC_NAME,
                        ContactsContract.CommonDataKinds.Email.TYPE_WORK
                    )
                    putExtra(ContactsContract.Intents.Insert.PHONE, phoneNumber.text.toString())
                    putExtra(
                        ContactsContract.Intents.Insert.PHONE_TYPE,
                        ContactsContract.CommonDataKinds.Phone.TYPE_WORK
                    )

                }
                startActivity(intent)
            }
        }

    }

    private fun updatePhone(contactId:Long, newNumber:String, displayName:String) {
       try {
           val contentValues = ContentValues()
           contentValues.put(ContactsContract.CommonDataKinds.Phone.NUMBER, newNumber)
           contentValues.put(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, displayName)

           val where = ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=?" + " AND " + ContactsContract.Contacts.Data.MIMETYPE + "=?"
           val whereArgs = arrayOf((contactId).toString(), ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)

           contentResolver.update(ContentUris.withAppendedId(UserDictionary.Words.CONTENT_URI, 4), contentValues, where, whereArgs)
       }catch (e:Exception){
       var x=e
       }
//    }
//    private fun updatePhone(contactId:Long, newNumber:String, displayName:String) {
//       try {
//           val contentValues = ContentValues()
//           contentValues.put(ContactsContract.CommonDataKinds.Phone.NUMBER, newNumber)
////           contentValues.put(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, displayName)
//
//           val where = ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=?" + " AND " + ContactsContract.Contacts.Data.MIMETYPE + "=?"
//           val whereArgs = arrayOf((contactId).toString(), ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
//
//           contentResolver.update(ContactsContract.Data.CONTENT_URI, contentValues, where, whereArgs)
//       }catch (e:Exception){
//       var x=e
//       }
    }
}