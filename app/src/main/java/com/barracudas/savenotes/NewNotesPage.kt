package com.barracudas.savenotes

import android.app.ProgressDialog
import android.content.Intent
import android.icu.text.CaseMap
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.scottyab.aescrypt.AESCrypt

class NewNotesPage : AppCompatActivity() {

    private lateinit var title: EditText
    private lateinit var note: EditText
    private lateinit var saveButton: TextView
    private lateinit var currentUID : String
    private lateinit var titles: String
    private lateinit var notes : String
    private lateinit var mDeleteButton : ImageView

    //DataBase
    private lateinit var mAuth: FirebaseAuth
    private lateinit var UID: String
    private lateinit var mDatabase: DatabaseReference
    private lateinit var mDeleteDatabase: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_notes_page)

        initialize()

        try {
            mAuth = Firebase.auth
            UID = mAuth.currentUser!!.uid
            Log.d("TAG", "The UID is : $UID")
        } catch (e: Exception) {
            startActivity(Intent(this, SavedNotesActivity::class.java))
            finish()
            Toast.makeText(this, "Please Check Internet and try again", Toast.LENGTH_SHORT).show()
        }

        titles = intent.getStringExtra("title").toString()
        notes = intent.getStringExtra("notes").toString()
        currentUID = intent.getStringExtra("UserID").toString()

        if (titles != "null" || notes != "null") {
            title.setText(titles)
            note.setText(notes)
        }



        saveButton.setOnClickListener {
            var Title = title.text.toString()
            var Notes = note.text.toString()

            if (Title == "") {
                title.error = "This field is mandatory"
            } else if (Notes == "") {
                note.error = "This field is mandatory"
            } else {
                if (currentUID == "null") {
                    updateDataToFireBase(Title, Notes)
                } else {
                    updateSavedDataToFireBase(Title, Notes)
                }
            }
        }

        mDeleteButton.setOnClickListener {
            deleteDatabaseData()
        }

    }

    private fun deleteDatabaseData() {
        mDeleteDatabase = FirebaseDatabase.getInstance().getReference(UID).child(currentUID)

        Log.d("Delete", "The current UID id : $currentUID")

        if (currentUID != "null") {
            mDeleteDatabase.removeValue().addOnCompleteListener {
                if (it.isSuccessful) {
                    startActivity(Intent(this, SavedNotesActivity::class.java))
                    finish()
                    Toast.makeText(this, "Deleted Successfully", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Invalid task !!!", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(this, "Invalid task !!!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateSavedDataToFireBase(Title: String, Notes: String) {
        val progressBar = ProgressDialog(this)
        progressBar.setTitle("Saving Note")
        progressBar.setMessage("Please wait while saving your note.")
        progressBar.setCanceledOnTouchOutside(false)
        progressBar.show()

        var encryptedTitle = AESCrypt.encrypt(currentUID, Title)
        var encryptedNote = AESCrypt.encrypt(currentUID, Notes)

        val profilrMap = HashMap<String, Any>()
        profilrMap["title"] = encryptedTitle
        profilrMap["Notes"] = encryptedNote
        profilrMap["UserID"] = currentUID
        mDatabase.child(UID).child(currentUID).setValue(profilrMap)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    Toast.makeText(this, "Note saved successfully", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, SavedNotesActivity::class.java))
                    finish()
                    progressBar.dismiss()
                } else {
                    Toast.makeText(this, "Unable to save. Please try again", Toast.LENGTH_SHORT).show()
                    progressBar.dismiss()
                }
            }
    }

    private fun updateDataToFireBase(Title: String, Notes: String) {

        val progressBar = ProgressDialog(this)
        progressBar.setTitle("Saving Note")
        progressBar.setMessage("Please wait while saving your note.")
        progressBar.setCanceledOnTouchOutside(false)
        progressBar.show()

        val messageKEY: String? = mDatabase.push().key

        var encryptedTitle = AESCrypt.encrypt(messageKEY, Title)
        var encryptedNote = AESCrypt.encrypt(messageKEY, Notes)

        val profilrMap = HashMap<String, Any>()
        profilrMap["title"] = encryptedTitle
        profilrMap["Notes"] = encryptedNote
        profilrMap["UserID"] = messageKEY.toString()
        Log.d("TAG", "The key is : $messageKEY")
        mDatabase.child(UID).child(messageKEY!!).setValue(profilrMap)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    Toast.makeText(this, "Note saved successfully", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, SavedNotesActivity::class.java))
                    finish()
                    progressBar.dismiss()
                } else {
                    Toast.makeText(this, "Unable to save. Please try again", Toast.LENGTH_SHORT).show()
                    progressBar.dismiss()
                }
            }
    }

    private fun initialize() {
        title = findViewById(R.id.titles)
        note = findViewById(R.id.notes)
        saveButton = findViewById(R.id.saveButtons)
        mDatabase = FirebaseDatabase.getInstance().reference
        mDeleteButton = findViewById(R.id.delete)
    }
}