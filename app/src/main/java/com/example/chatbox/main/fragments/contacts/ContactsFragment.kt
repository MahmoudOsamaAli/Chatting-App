package com.example.chatbox.main.fragments.contacts

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.chatbox.R
import com.example.chatbox.databinding.FragmentContactsBinding
import com.example.chatbox.main.fragments.home.Chats.ChatMessages.ChatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ContactsFragment : Fragment() , ContactsAdapter.OnItemClickListener {
    companion object {
        const val REQUEST_CODE_READ_CONTACTS = 1
    }

    private lateinit var binding: FragmentContactsBinding
    private lateinit var contactRecyclerView: RecyclerView
    private lateinit var contactsAdapter: ContactsAdapter
    private val contactList = mutableListOf<Contact>()
    private lateinit var database: FirebaseDatabase

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentContactsBinding.inflate(inflater, container, false)
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_CONTACTS)
            != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(Manifest.permission.READ_CONTACTS),
                REQUEST_CODE_READ_CONTACTS
            )
        } else {
            setContactsRecyclerView()
        }
        return binding.root
    }
    private fun setContactsRecyclerView() {
        fetchContacts { contacts ->
            contactList.clear()
            contactList.addAll(contacts)
            contactsAdapter = ContactsAdapter(contactList,this)
            contactRecyclerView = binding.recyclerViewContactsList
            contactRecyclerView.adapter = contactsAdapter
            binding.contactsHeader.text = "${contactList.size} Contacts"
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_CODE_READ_CONTACTS) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setContactsRecyclerView()
            } else {
                Toast.makeText(
                    requireContext(),
                    "Permission denied to read contacts",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun fetchContacts(callback: (List<Contact>) -> Unit) {
        val tempContacts = mutableListOf<Pair<String, String>>()
        val contentResolver = requireContext().contentResolver
        val contactUri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI
        val projection = arrayOf(
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER
        )
        val cursor = contentResolver.query(contactUri, projection, null, null, null)
        cursor?.use {
            val nameIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
            val numberIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)

            while (it.moveToNext()) {
                val name = it.getString(nameIndex)
                var number = it.getString(numberIndex)
                number = formatPhoneNumber(number)
                tempContacts.add(Pair(name, number))
            }
        }
        val distinctContact = tempContacts.distinct()
        val userContacts = mutableListOf<Contact>()
        val nonUserContacts = mutableListOf<Contact>()

        distinctContact.forEach { (name, number) ->
            checkIfPhoneExists(number) { isUser ->
                if (isUser) {
                    fetchUsername(number) { username ->
                        userContacts.add(Contact(username ?: name, number, true))

                        if (userContacts.size + nonUserContacts.size == distinctContact.size) {
                            userContacts.sortBy { it.contactName.lowercase() }
                            nonUserContacts.sortBy { it.contactName.lowercase() }
                            val finalContacts = userContacts + nonUserContacts
                            callback(finalContacts)
                        }
                    }
                } else {
                    nonUserContacts.add(Contact(name, number, false))
                    if (userContacts.size + nonUserContacts.size == tempContacts.size) {
                        userContacts.sortBy { it.contactName.lowercase() }
                        nonUserContacts.sortBy { it.contactName.lowercase() }
                        val finalContacts = userContacts + nonUserContacts
                        callback(finalContacts)
                    }
                }
            }
        }
    }

    private fun fetchUsername(phoneNumber: String, callback: (String?) -> Unit) {
        database = FirebaseDatabase.getInstance()
        val userRef = database.getReference("users")
        userRef.orderByChild("phoneNumber").equalTo(phoneNumber)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val username = snapshot.children.firstOrNull()
                            ?.child("userName")
                            ?.getValue(String::class.java)
                        callback(username)
                    } else {
                        callback(null)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("Firebase", "Error fetching username: ${error.message}")
                    callback(null)
                }
            })
    }

    private fun checkIfPhoneExists(phoneNumber: String, callback: (Boolean) -> Unit) {
        database = FirebaseDatabase.getInstance()
        val userRef = database.getReference("users")
        userRef.orderByChild("phoneNumber").equalTo(phoneNumber)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    callback(snapshot.exists())
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("Firebase", "Error: ${error.message}", error.toException())
                    callback(false)
                }
            })
    }

    private fun formatPhoneNumber(phoneNumber: String): String {
        var formattedNumber = phoneNumber.replace("\\s+".toRegex(), "").replace("-", "")
        if (!formattedNumber.startsWith("+20")) {
            formattedNumber = if (formattedNumber.startsWith("0")) {
                "+20" + formattedNumber.substring(1)
            } else {
                "+20$formattedNumber"
            }
        }
        return formattedNumber
    }

    override fun onUserItemClick(contact: Contact, isContact: Boolean) {
        if (isContact) {
            openChatActivity(contact.contactNumber)
        } else {
            handleSmsPermission(contact)
        }
    }

    private fun openChatActivity(contactPhoneNumber:String) {
        val intent = Intent (requireContext(), ChatActivity::class.java)
        intent.putExtra("PhoneNumberContacts", contactPhoneNumber)
        requireContext().startActivity(intent)
    }

    private fun handleSmsPermission(contact: Contact) {
        if (isSmsPermissionGranted()) {
            sendSms(contact)
        } else {
            requestSmsPermission()
        }
    }

    private fun isSmsPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestSmsPermission() {
        ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.SEND_SMS), 1)
    }

    private fun sendSms(contact: Contact) {
        val smsBody = createSmsBody(contact)
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse("smsto:${contact.contactNumber}")
            putExtra("sms_body", smsBody)
        }
        startActivity(intent)
    }

    private fun createSmsBody(contact: Contact): String {
        val contactName = contact.contactName
        val invMessage = getString(R.string.invMassage)
        val appLink = getString(R.string.appLink)
        return "$contactName\n$invMessage\nAPP LINK: $appLink"
    }



}
