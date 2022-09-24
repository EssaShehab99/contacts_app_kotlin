package com.example.contacts_app

import android.content.ContentProviderOperation
import android.content.OperationApplicationException
import android.os.Bundle
import android.os.RemoteException
import android.provider.ContactsContract
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity


class ManageContactActivity : AppCompatActivity() {
    private lateinit var saveBTN: Button
    private lateinit var fullName: EditText
    private lateinit var phoneNumber: EditText
    private lateinit var oldNumber: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_contact)
        val id = intent.getLongExtra("id", 0)
        saveBTN = findViewById(R.id.save_btn)
        fullName = findViewById(R.id.full_name)
        phoneNumber = findViewById(R.id.phone_number_edit_text)
        if (id.toInt() != 0) {
            fullName.setText(intent.getStringExtra("fullName"))
            phoneNumber.setText(intent.getStringExtra("phoneNumber"))
            oldNumber = intent.getStringExtra("phoneNumber").toString()
        }
        if (id.toInt() != 0)
            saveBTN.text = "Edit"
        else
            saveBTN.text = "Save"
        saveBTN.setOnClickListener {
            if (id.toInt() != 0) {
                updatePhone(id, phoneNumber.text.toString(), fullName.text.toString())
            } else {
                insertPhone(phoneNumber.text.toString(), fullName.text.toString())

            }
        }

    }

    private fun updatePhone(contactId: Long, number: String, displayName: String) {
        val ops = ArrayList<ContentProviderOperation>()
        ops.add(
            ContentProviderOperation
                .newUpdate(ContactsContract.Data.CONTENT_URI)
                .withSelection(
                    ContactsContract.Data.MIMETYPE + " = ? AND " +
                            ContactsContract.Data.DATA1 + " = ?", arrayOf(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE,
                        oldNumber
                    )
                )
                .withValue(
                    ContactsContract.Data.DATA1,
                    number
                )
                .build()
        )
        ops.add(
            ContentProviderOperation
                .newUpdate(ContactsContract.Data.CONTENT_URI)
                .withSelection(
                    ContactsContract.Data.MIMETYPE + " = ? AND " +
                            ContactsContract.Data.CONTACT_ID + " = ?", arrayOf(
                        ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE,
                        contactId.toString()
                    )
                )
                .withValue(
                    ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME,
                    displayName
                )
                .build()
        )
        try {
            contentResolver.applyBatch(
                ContactsContract.AUTHORITY, ops
            )
            Toast.makeText(this, "Contact Updated Successfully", Toast.LENGTH_LONG).show()
        } catch (e: OperationApplicationException) {
            e.printStackTrace()
        } catch (e: RemoteException) {
            e.printStackTrace()
        }
    }

    private fun insertPhone(number: String, displayName: String) {
        val ops = ArrayList<ContentProviderOperation>()
        val rawContactInsertIndex: Int = ops.size
        ops.add(
            ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null).build()
        )
//Number number/Contact number
        ops.add(
            ContentProviderOperation
                .newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(
                    ContactsContract.Data.RAW_CONTACT_ID,
                    rawContactInsertIndex
                )
                .withValue(
                    ContactsContract.Contacts.Data.MIMETYPE,
                    ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE
                )
                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, number)
                .withValue(
                    ContactsContract.Contacts.Data.MIMETYPE,
                    ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE
                )
                .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, "1").build()
        )
        //Display name/Contact name
        ops.add(
            ContentProviderOperation
                .newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(
                    ContactsContract.Contacts.Data.RAW_CONTACT_ID,
                    rawContactInsertIndex
                )
                .withValue(
                    ContactsContract.Contacts.Data.MIMETYPE,
                    ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE
                )
                .withValue(
                    ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME,
                    displayName
                )
                .build()
        )

        try {
            contentResolver.applyBatch(
                ContactsContract.AUTHORITY, ops
            )
            Toast.makeText(this, "Contact Added Successfully", Toast.LENGTH_LONG).show()
        } catch (e: RemoteException) {
            // TODO Auto-generated catch block
            e.printStackTrace()
        } catch (e: OperationApplicationException) {
            // TODO Auto-generated catch block
            e.printStackTrace()
        }
    }
}