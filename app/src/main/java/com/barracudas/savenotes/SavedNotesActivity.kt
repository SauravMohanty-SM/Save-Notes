package com.barracudas.savenotes

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase

class SavedNotesActivity : AppCompatActivity() {

    private lateinit var newNotesBotton: FloatingActionButton
    private lateinit var recycleView: RecyclerView
    private lateinit var mCurrentUID: String
    private lateinit var userArrayList: ArrayList<User>
    private lateinit var mProgressBar: ProgressBar

    //Firebase
    private lateinit var mAuth: FirebaseAuth
    private lateinit var firebaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_saved_notes2)


        initialize()

        recycleView.layoutManager = GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false)
        recycleView.setHasFixedSize(false)
        //TODO : Initialize the ArrayList
        userArrayList = arrayListOf<User>()

        getDataFromFireBase()

        newNotesBotton.setOnClickListener {
            startActivity(Intent(this, NewNotesPage::class.java))
        }
    }

    private fun getDataFromFireBase() {
        firebaseReference = FirebaseDatabase.getInstance().reference.child(mCurrentUID)

        firebaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                userArrayList.clear()
                if (snapshot.exists()) {

                    mProgressBar.visibility = View.INVISIBLE
                    for (userSnapshot in snapshot.children) {

                        val user = userSnapshot.getValue(User::class.java)
                        userArrayList.add(user!!)
                    }

                    recycleView.adapter = MyAdepter(userArrayList)

                }

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }


        })
    }

    private fun initialize() {
        newNotesBotton = findViewById(R.id.fab)
        recycleView = findViewById(R.id.recycleViewForNotes)
        mAuth = Firebase.auth
        mCurrentUID = mAuth.currentUser!!.uid
        mProgressBar = findViewById(R.id.progressBar)
    }
}