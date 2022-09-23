package com.example.contacts_app

import android.annotation.SuppressLint
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.loader.app.LoaderManager
import androidx.loader.content.CursorLoader
import androidx.loader.content.Loader
import com.google.android.material.floatingactionbutton.FloatingActionButton

@SuppressLint("InlinedApi", "ObsoleteSdkInt")
private val FROM_COLUMNS: Array<String> = arrayOf(
    if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)) {
        ContactsContract.Contacts.DISPLAY_NAME_PRIMARY
    } else {
            ContactsContract.Contacts.DISPLAY_NAME
    }
)

@SuppressLint("InlinedApi", "ObsoleteSdkInt")
private val PROJECTION: Array<out String> = arrayOf(
    ContactsContract.Contacts._ID,
    ContactsContract.Contacts.LOOKUP_KEY,
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
        ContactsContract.Contacts.DISPLAY_NAME_PRIMARY
    else
        ContactsContract.Contacts.DISPLAY_NAME
)
private const val CONTACT_ID_INDEX: Int = 0
private const val CONTACT_KEY_INDEX: Int = 1
private val searchString: String = ""
private val selectionArgs = arrayOf(searchString)
private val TO_IDS: IntArray = intArrayOf(android.R.id.text1)
@SuppressLint("InlinedApi", "ObsoleteSdkInt")
private val SELECTION: String =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
        "${ContactsContract.Contacts.DISPLAY_NAME_PRIMARY} LIKE ?"
    else
        "${ContactsContract.Contacts.DISPLAY_NAME} LIKE ?"

class ContactsFragment : Fragment(),
    LoaderManager.LoaderCallbacks<Cursor>,
    AdapterView.OnItemClickListener {

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
        val floatingActionBtn: FloatingActionButton=view.findViewById(R.id.floating_action)
        floatingActionBtn.setOnClickListener{
            val intent=Intent(this.activity,ManageContactActivity::class.java)
            startActivity(intent)
        }
        return view
    }

    @SuppressLint("Recycle", "Range")
    @Deprecated("Deprecated in Java")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        // Gets the ListView from the View list of the parent activity
        activity?.also {
            contactsList = it.findViewById(R.id.contact_list_view)
            cursorAdapter = SimpleCursorAdapter(
                it,
                android.R.layout.simple_list_item_2,
                null,
                FROM_COLUMNS, TO_IDS,
                0
            )
            contactsList.onItemClickListener = this
            contactsList.adapter = cursorAdapter

        }
    }

    override fun onItemClick(parent: AdapterView<*>, view: View?, position: Int, id: Long) {

        var contactId: Long = 0
        var contactKey: String? = null
        var contactUri: Uri? = null

        val cursor: Cursor? = (parent.adapter as? CursorAdapter)?.cursor?.apply {
            moveToPosition(position)
            contactId = getLong(CONTACT_ID_INDEX)
            contactKey = getString(CONTACT_KEY_INDEX)
            contactUri = ContactsContract.Contacts.getLookupUri(contactId, "+966")
        }
        if (cursor != null) {
           val intent=Intent(this.activity,ContactDetailsActivity::class.java).apply {
               putExtra("id",contactId)
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
                PROJECTION,
                SELECTION,
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