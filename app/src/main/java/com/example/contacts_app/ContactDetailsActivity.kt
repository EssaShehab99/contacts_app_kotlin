package com.example.contacts_app

import android.annotation.SuppressLint
import android.content.ContentProviderOperation
import android.content.Intent
import android.content.OperationApplicationException
import android.os.Bundle
import android.os.RemoteException
import android.provider.ContactsContract
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton

class ContactDetailsActivity : AppCompatActivity() {
    private var id: Long = 0
    lateinit var fullName: TextView
    lateinit var firstNumber: TextView
    lateinit var secondNumber: TextView
    lateinit var thirdNumber: TextView
    private lateinit var linearLayout_2: LinearLayout
    private lateinit var linearLayout_3: LinearLayout

    private val numbers: MutableList<String> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact_details)
        fullName = findViewById(R.id.fullName)
        firstNumber = findViewById(R.id.first_number_text_view)
        secondNumber = findViewById(R.id.second_number_text_view)
        thirdNumber = findViewById(R.id.third_number_text_view)
        linearLayout_2=findViewById(R.id.layout_2)
        linearLayout_3=findViewById(R.id.layout_3)
        id = intent.getLongExtra("id", 0)
        val editBTN: FloatingActionButton = findViewById(R.id.edit_btn)
        val deleteBTN: FloatingActionButton = findViewById(R.id.delete_btn)
        editBTN.setOnClickListener {
            val intent = Intent(this, ManageContactActivity::class.java).apply {
                putExtra("id", id)
                putExtra("fullName", fullName.text)
                putExtra("numbers", numbers.toTypedArray())
            }
            startActivity(intent)

        }
        deleteBTN.setOnClickListener {
            deletePhone()
        }
    }

    override fun onStart() {
        super.onStart()
        numbers.clear()
        getContactDetails()
        showNumbers(numbers)
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
                numbers.add(phonesCursor.getString(
                    phonesCursor.getColumnIndex(
                        ContactsContract.CommonDataKinds.Phone.NUMBER
                    )
                ).replace("-", "").replace(" ", ""))

            }
        }
    }

    private fun deletePhone() {
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

    private fun showNumbers(numbers: MutableList<String>){
        when (numbers.size){
            1->{
                firstNumber.setText(numbers[0])
            }
            2->{
                firstNumber.setText(numbers[0])
                secondNumber.setText(numbers[1])
                linearLayout_2.visibility= View.VISIBLE
            }
        }
        if(numbers.size>2){
            firstNumber.setText(numbers[0])
            secondNumber.setText(numbers[1])
            thirdNumber.setText(numbers[2])
            linearLayout_2.visibility= View.VISIBLE
            linearLayout_3.visibility= View.VISIBLE
        }
    }
}