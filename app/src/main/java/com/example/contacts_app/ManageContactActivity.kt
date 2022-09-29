package com.example.contacts_app

import android.annotation.SuppressLint
import android.content.ContentProviderOperation
import android.content.OperationApplicationException
import android.os.Bundle
import android.os.RemoteException
import android.provider.ContactsContract
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout


class ManageContactActivity : AppCompatActivity() {
    private lateinit var saveBTN: Button
    private lateinit var firstName: EditText
    private lateinit var secondName: EditText
    private lateinit var firstNumber: EditText
    private lateinit var secondNumber: EditText
    private lateinit var thirdNumber: EditText
    private lateinit var linearLayout_1: LinearLayout
    private lateinit var linearLayout_2: LinearLayout
    private lateinit var linearLayout_3: LinearLayout
    private lateinit var constraintLayout_1: ConstraintLayout
    private lateinit var constraintLayout_2: ConstraintLayout
    private lateinit var constraintLayout_3: ConstraintLayout
    private var oldNumbers: MutableList<String> = mutableListOf()
    private val numbers: MutableList<String> = mutableListOf()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_contact)
        val id = intent.getLongExtra("id", 0)
        saveBTN = findViewById(R.id.save_btn)
        firstName = findViewById(R.id.first_name)
        secondName = findViewById(R.id.second_name)
        firstNumber = findViewById(R.id.first_phone_number_edit_text)
        secondNumber = findViewById(R.id.second_phone_number_edit_text)
        thirdNumber = findViewById(R.id.third_phone_number_edit_text)
        linearLayout_1 = findViewById(R.id.linear_1)
        linearLayout_2 = findViewById(R.id.linear_2)
        linearLayout_3 = findViewById(R.id.linear_3)
        constraintLayout_1 = findViewById(R.id.first_constraint_layout)
        constraintLayout_2 = findViewById(R.id.second_constraint_layout)
        constraintLayout_3 = findViewById(R.id.third_constraint_layout)
        constraintLayout_1.setOnClickListener {
            linearLayout_2.visibility = View.VISIBLE
            constraintLayout_1.visibility = View.GONE
        }
        constraintLayout_2.setOnClickListener {
            linearLayout_3.visibility = View.VISIBLE
            constraintLayout_2.visibility = View.GONE
            constraintLayout_3.visibility = View.GONE
        }
        if (id.toInt() != 0) {
            constraintLayout_1.visibility = View.GONE
            constraintLayout_2.visibility = View.GONE
            constraintLayout_3.visibility = View.GONE
            splitFullName(intent.getStringExtra("fullName")?.split(" ") ?: listOf())
            oldNumbers.addAll(intent.getStringArrayExtra("numbers")!!)
            showNumbers(oldNumbers)
        }
        if (id.toInt() != 0)
            saveBTN.text = "Edit"
        else
            saveBTN.text = "Save"
        saveBTN.setOnClickListener {
            if (firstNumber.text.toString().trim().isNotEmpty() ||
                firstNumber.text.toString().trim().isNotBlank()
            ) {
                numbers.add(firstNumber.text.toString())
            }
            if (secondNumber.text.toString().trim().isNotEmpty() ||
                secondNumber.text.toString().trim().isNotBlank()
            ) {
                numbers.add(secondNumber.text.toString())
            }
            if (thirdNumber.text.toString().trim().isNotEmpty() ||
                thirdNumber.text.toString().trim().isNotBlank()
            ) {
                numbers.add(thirdNumber.text.toString())
            }
            try {
                if (numbers.isNotEmpty()) {
                    if (id.toInt() != 0) {
                        updatePhone(
                            id,
                            numbers,
                            firstName.text.toString() + " " + secondName.text.toString()
                        )
                    } else {
                        var contactId: String? =
                            getIfExist(firstName.text.toString() + " " + secondName.text.toString())
                        if (contactId == null) {
                            insertPhone(
                                numbers,
                                firstName.text.toString() + " " + secondName.text.toString()
                            )
                        } else {
                            oldNumbers.forEach {
                                numbers.add(it)
                            }
                            deletePhone(contactId)
                            insertPhone(
                                numbers,
                                firstName.text.toString() + " " + secondName.text.toString()
                            )
                        }
                    }
                } else {
                    Toast.makeText(this, "Please insert phone number", Toast.LENGTH_SHORT).show()
                }
            } catch (e: java.lang.Exception) {
                Toast.makeText(this, "Please insert phone number", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updatePhone(contactId: Long, numbers: MutableList<String>, displayName: String) {
        val ops = ArrayList<ContentProviderOperation>()
        numbers.forEach {
            ops.add(
                ContentProviderOperation
                    .newUpdate(ContactsContract.Data.CONTENT_URI)
                    .withSelection(
                        ContactsContract.Data.MIMETYPE + " = ? AND " +
                                ContactsContract.Data.DATA1 + " = ?", arrayOf(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE,
                            oldNumbers[numbers.indexOf(it)]
                        )
                    )
                    .withValue(
                        ContactsContract.Data.DATA1,
                        it
                    )
                    .build()
            )
        }
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

    private fun insertPhone(numbers: MutableList<String>, displayName: String) {
        val ops = ArrayList<ContentProviderOperation>()
        val rawContactInsertIndex: Int = ops.size
        ops.add(
            ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null).build()
        )
//Number number/Contact number
        numbers.forEach {
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
                    .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, it)
                    .withValue(
                        ContactsContract.Contacts.Data.MIMETYPE,
                        ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE
                    )
                    .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, "1").build()
            )
        }

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
        } catch (e: Exception) {
            // TODO Auto-generated catch block
            e.printStackTrace()
        }
    }

    private fun splitFullName(fullName: List<String>) {
        when (fullName.size) {
            1 -> {
                firstName.setText(fullName[0])
            }
            2 -> {
                firstName.setText(fullName[0])
                secondName.setText(fullName[1])
            }
        }
        if (fullName.size > 2) {
            firstName.setText(fullName[0])
            var secondName: String = ""
            fullName.forEach {
                if (it == fullName[0]) {
                    return@forEach
                }
                secondName += " $it"
            }
            this.secondName.setText(secondName)
        }
    }

    private fun showNumbers(numbers: MutableList<String>) {
        when (numbers.size) {
            1 -> {
                firstNumber.setText(numbers[0])
            }
            2 -> {
                firstNumber.setText(numbers[0])
                secondNumber.setText(numbers[1])
                linearLayout_2.visibility = View.VISIBLE
            }
        }
        if (numbers.size > 2) {
            firstNumber.setText(numbers[0])
            secondNumber.setText(numbers[1])
            thirdNumber.setText(numbers[2])
            linearLayout_2.visibility = View.VISIBLE
            linearLayout_3.visibility = View.VISIBLE
        }
    }

    @SuppressLint("Range", "Recycle")
    private fun getIfExist(name: String): String? {
        var contacts: MutableList<ContactModel> = mutableListOf()
        val cursor = contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null, null, null, "${ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME} ASC"
        )
        if ((cursor?.count ?: 0) > 0) {
            var oldContactName = ""
            while (cursor != null && cursor.moveToNext()) {
                var contact: ContactModel = ContactModel()
                contact.id = cursor.getString(
                    cursor.getColumnIndex(
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID
                    )
                )
                contact.name = cursor.getString(
                    cursor.getColumnIndex(
                        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
                    )
                )
                if (contact.name != oldContactName)
                    contacts.add(contact)
                oldContactName = contact.name
            }
        }
        cursor?.close()

        val contactId: String? = contacts.firstOrNull { c -> c.name == name }?.id
        if (contactId != null) {
            val phonesCursor = contentResolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null,
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId,
                null,
                null
            )
            phonesCursor?.let {
                while (phonesCursor.moveToNext()) {
                    oldNumbers.add(
                        phonesCursor.getString(
                            phonesCursor.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.NUMBER
                            )
                        ).replace("-", "").replace(" ", "")
                    )

                }
            }
        }
        return  contactId
    }

    private fun deletePhone(id: String) {
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
        } catch (e: OperationApplicationException) {
            e.printStackTrace()
        } catch (e: RemoteException) {
            e.printStackTrace()
        }

    }
}