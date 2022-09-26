package com.example.contacts_app

import android.annotation.SuppressLint
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.ContactsContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.json.JSONObject


class ContactsFragment : Fragment(),
    AdapterView.OnItemClickListener {
    private var searchString: String = ""
    private lateinit var filterBTN: ImageView
    private val selectionArgs = arrayOf(searchString)
    var isSearchable: Boolean = false
    var isFiltering: Boolean = false
    private lateinit var contactsList: ListView
    private var cursorAdapter: SimpleCursorAdapter? = null
    var contacts: MutableList<ContactModel> = mutableListOf()
    var contactsName: MutableList<String> = mutableListOf()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view: View = inflater.inflate(R.layout.fragment_contacts, container, false)
        val floatingActionBtn: FloatingActionButton = view.findViewById(R.id.edit_btn)
        floatingActionBtn.setOnClickListener {
            val intent = Intent(this.activity, ManageContactActivity::class.java)
            startActivity(intent)
        }
        return view
    }

    @SuppressLint("Recycle", "Range")
    @Deprecated("Deprecated in Java")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val searchValue: EditText = requireActivity().findViewById(R.id.search_value)
        searchValue.doOnTextChanged { text, _, _, _ ->
            searchString = text.toString()
            if (text.isNullOrEmpty() || text.isNullOrBlank()) {
                isSearchable = false
                setContactsToListView()
            } else {
                isSearchable = true
                setContactsToListView(text.toString())
            }
        }
        activity?.also {
            contactsList = it.findViewById(R.id.contact_list_view)
            filterBTN = it.findViewById(R.id.filter_ntn)
            contactsList.onItemClickListener = this
        }

        filterBTN.setOnClickListener{
            isFiltering=!isFiltering
            if(isFiltering){
                filterBTN.setBackgroundResource(R.drawable.ic_baseline_clear_all_24)
                contactsName.clear()
                var json: JSONObject=getSearchContacts()?.let { JSONObject(it) }?: JSONObject()
                json.keys().forEach {keyStr ->
                    run {
                        if (json.get(keyStr).toString().toInt() > 3) {
                            setContactsToListView(keyStr.toString(), false)
                        }
                    }
                }
            }else{
                filterBTN.setBackgroundResource(R.drawable.ic_baseline_filter_list_24)
                setContactsToListView()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        getContactList()
        setContactsToListView()
    }
    override fun onItemClick(parent: AdapterView<*>, view: View?, position: Int, id: Long) {

        var contactId: String =
            contacts.firstOrNull { c -> c.name == contactsName[position] }?.id ?: "0"
        val intent = Intent(this.activity, ContactDetailsActivity::class.java).apply {
            putExtra("id", (contactId).toLong())
        }
        if(isSearchable){
            setSearchContacts(contacts.firstOrNull { c -> c.name == contactsName[position] }?.name?:"")
        }
        startActivity(intent)

    }

    private fun setSearchContacts(contactName:String) {
        var json: JSONObject = getSearchContacts()?.let { JSONObject(it) }?: JSONObject()
        if(json.has(contactName)){
            var value:String= json.get(contactName).toString()
            json.remove(contactName)
            json.put(contactName,value.toInt()+1)
        }else{
            json.put(contactName,1)
        }
        val sharedPreference  = requireActivity().getSharedPreferences("contact", MODE_PRIVATE)
        var editor = sharedPreference.edit()
        editor.putString("contacts",json.toString())
        editor.commit()
    }

    private fun getSearchContacts(): String? {
        val sharedPreference  = requireActivity().getSharedPreferences("contact", MODE_PRIVATE)
      return  sharedPreference.getString("contacts","{}")

    }

    @SuppressLint("Range")
    private fun getContactList() {
        contacts.clear()
        val cursor = requireActivity().contentResolver.query(
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
    }

    private fun setContactsToListView(searchValue: String="",clearList:Boolean=true) {
        if(clearList)
        contactsName.clear()
        var oldContactName:String=""
        contacts.forEach {
            if(oldContactName!=it.name)
            if (it.name.uppercase().contains(searchValue.uppercase()))
                contactsName.add(it.name)
            oldContactName=it.name
        }
        contactsList.adapter =
            ArrayAdapter(requireActivity(), android.R.layout.simple_list_item_1, contactsName)
    }
}