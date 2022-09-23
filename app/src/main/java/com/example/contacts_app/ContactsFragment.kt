package com.example.contacts_app

import android.annotation.SuppressLint
import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import android.provider.ContactsContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.loader.app.LoaderManager
import androidx.loader.content.CursorLoader
import androidx.loader.content.Loader
import com.google.android.material.floatingactionbutton.FloatingActionButton


class ContactsFragment : Fragment(),
    LoaderManager.LoaderCallbacks<Cursor>,
    AdapterView.OnItemClickListener {
    private var searchString: String = ""
    private val selectionArgs = arrayOf(searchString)

    private lateinit var contactsList: ListView
    private var cursorAdapter: SimpleCursorAdapter? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Initializes the loader
        loaderManager.initLoader(0, null, this)

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
        val searchValue:EditText=requireActivity().findViewById(R.id.search_value)
        searchValue.doOnTextChanged { text, _, _, _ ->
            searchString=text.toString()
            getLoaderManager().restartLoader(0, null, this);

        }
        // Gets the ListView from the View list of the parent activity
        activity?.also {
            contactsList = it.findViewById(R.id.contact_list_view)
            contactsList.onItemClickListener = this
            cursorAdapter = SimpleCursorAdapter(
                it,
                android.R.layout.simple_list_item_2,
                null,
                arrayOf(ContactsContract.Contacts.DISPLAY_NAME),
                intArrayOf(android.R.id.text1),
                0
            )
            contactsList.adapter = cursorAdapter
        }

    }

    override fun onItemClick(parent: AdapterView<*>, view: View?, position: Int, id: Long) {

        var contactId: Long = 0

        val cursor: Cursor? = (parent.adapter as? CursorAdapter)?.cursor?.apply {
            moveToPosition(position)
            contactId = getLong(0)
        }
        if (cursor != null) {
            val intent = Intent(this.activity, ContactDetailsActivity::class.java).apply {
                putExtra("id", contactId)
            }
            startActivity(intent)

        }

    }

    override fun onCreateLoader(loaderId: Int, args: Bundle?): Loader<Cursor> {
        selectionArgs[0] = "%$searchString%"
        return activity?.let {
            return CursorLoader(
                it,
                ContactsContract.Contacts.CONTENT_URI,
                arrayOf(
                    ContactsContract.Contacts._ID,
                    ContactsContract.Contacts.LOOKUP_KEY,
                    ContactsContract.Contacts.DISPLAY_NAME
                ),
                "${ContactsContract.Contacts.DISPLAY_NAME} LIKE ?",
                selectionArgs,
                null
            )
        } ?: throw IllegalStateException()
    }

    override fun onLoadFinished(loader: Loader<Cursor>, cursor: Cursor) {
        // Put the result Cursor in the adapter for the ListView
        cursorAdapter?.swapCursor(cursor)
    }

    override fun onLoaderReset(loader: Loader<Cursor>) {
        // Delete the reference to the existing Cursor
        cursorAdapter?.swapCursor(null)
    }
}