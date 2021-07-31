package com.drovisfrovis.contactsmanager.activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.drovisfrovis.contactsmanager.R
import com.drovisfrovis.contactsmanager.classes.AllContactsAdapter
import com.drovisfrovis.contactsmanager.classes.ContactsDataModel
import com.drovisfrovis.contactsmanager.classes.FavContactsAdapter
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_favourite_contacts.*
import kotlinx.android.synthetic.main.single_list_item_all_contacts.*
import java.io.*

class MainActivity : AppCompatActivity(), EditOrShowContactDetails.UpdateRecyclerView {
    companion object {
        lateinit var list:MutableList<ContactsDataModel>
        lateinit var favContList: MutableList<ContactsDataModel>
        lateinit var context: Context
        val ALL_CONTACT_FILE: String = "userContacts.txt"
        val FAV_CONTACT_FILE: String = "favContacts.txt"
    }

    private val rotateOpen: Animation by lazy{ AnimationUtils.loadAnimation(this, R.anim.rotate_open_anim) }
    private val rotateClose: Animation by lazy{ AnimationUtils.loadAnimation(this, R.anim.rotate_close_anim) }
    private val fromBottom: Animation by lazy{ AnimationUtils.loadAnimation(this, R.anim.from_bottom_anim) }
    private val toBottom: Animation by lazy{ AnimationUtils.loadAnimation(this, R.anim.to_bottom_anim) }
    private var isClicked = false
    private var isOutlinedView = false
    private var isClickedEdit = false
    private lateinit var allContactAdap: AllContactsAdapter
    private lateinit var favContactAdap: FavContactsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        context = this

        //Horizontal recycler view of favourite contacts
        rv_all_contacts.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        allContactAdap = AllContactsAdapter(this, list)
        rv_all_contacts.adapter = allContactAdap

        //Vertical recycler view of all contacts
        rv_fav_contacts.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        favContactAdap = FavContactsAdapter(this, favContList)
        rv_fav_contacts.adapter = favContactAdap

        iv_edit_contacts.setOnClickListener {
            isClickedEdit = !isClickedEdit
            allContactAdap.switchDeleteEnabled()
            allContactAdap.notifyDataSetChanged()
            favContactAdap.switchRemoveFavourite()
            if(favContList.isNotEmpty())
                favContactAdap.notifyDataSetChanged()
        }

        fb_activity_main.setOnClickListener {
            if(isOutlinedView){
                allContactAdap.switchOutlinedView()
                isOutlinedView = !isOutlinedView
            }
            onAddButtonActionListener(isClicked)
        }

        fb_add_favourite.setOnClickListener {
            allContactAdap.switchOutlinedView()
            isOutlinedView = !isOutlinedView
        }

        fb_add_new_contact.setOnClickListener {
            val intent = Intent(this, EditOrShowContactDetails::class.java)
            startActivity(intent)
        }
    }

    override fun onPause() {
        if(iv_delete_contact != null) {
            if(iv_delete_contact.visibility == View.VISIBLE) {
                isClickedEdit = !isClickedEdit
                allContactAdap.switchDeleteEnabled()
                allContactAdap.notifyDataSetChanged()
                favContactAdap.switchRemoveFavourite()
                if(favContList.isNotEmpty())
                    favContactAdap.notifyDataSetChanged()
            }
        }
        super.onPause()
    }

    override fun onResume() {
        if(isOutlinedView){
            allContactAdap.switchOutlinedView()
            isOutlinedView = !isOutlinedView
        }
        if(isClicked)
            onAddButtonActionListener(isClicked)
        super.onResume()
    }

    override fun onStart() {
        if(isOutlinedView){
            allContactAdap.switchOutlinedView()
            isOutlinedView = !isOutlinedView
        }
        if(isClicked)
            onAddButtonActionListener(isClicked)

        super.onStart()
    }

    private fun onAddButtonActionListener(clicked: Boolean){
        setAnimation(clicked)
        setVisibility(clicked)
        isClicked = !clicked
    }

    private fun setVisibility(isClicked: Boolean){
        if(!isClicked){ //when button is first press and floating button options are visible
            fb_add_new_contact.visibility = View.VISIBLE
            fb_add_favourite.visibility = View.VISIBLE
            fb_add_favourite.isClickable = true
            fb_add_favourite.isFocusable = true
        }
        else{
            fb_add_new_contact.visibility = View.GONE
            fb_add_favourite.visibility = View.GONE
            fb_add_favourite.isClickable = false
            fb_add_favourite.isFocusable = false
        }
    }

    private fun setAnimation(isClicked: Boolean){
        if(!isClicked){
            fb_add_new_contact.startAnimation(fromBottom)
            fb_add_favourite.startAnimation(fromBottom)
            fb_activity_main.startAnimation(rotateOpen)
        }else{
            fb_add_new_contact.startAnimation(toBottom)
            fb_add_favourite.startAnimation(toBottom)
            fb_activity_main.startAnimation(rotateClose)
        }
    }

    private fun writeDataToFile(fileName: String, dataList: List<ContactsDataModel>) {
        try {
            val outputStreamWriter = OutputStreamWriter(openFileOutput(fileName, MODE_PRIVATE))
            var data = ""
            for(i in dataList.indices){
                dataList[i].apply {
                    data += "$name|$phoneNo|$email|$address|$dateOfBirth|$contactImage\n"
                }
            }
            outputStreamWriter.append(data)
            outputStreamWriter.close()
        } catch (e: IOException) {
            Log.e("Exception", "File write failed: $e")
        }
    }

    override fun updateRVByNotify() {
        rv_all_contacts.adapter!!.notifyDataSetChanged()
    }

    override fun updateFileAllContacts(name: String) {
        val index = favContList.indexOf(favContList.find { it.name == name })
        if(index != -1){
            favContList.removeAt(index)
            rv_fav_contacts.adapter!!.notifyItemRemoved(index)
            rv_fav_contacts.adapter!!.notifyItemRangeChanged(index, favContList.size)

            writeDataToFile(FAV_CONTACT_FILE, favContList)
            writeDataToFile(ALL_CONTACT_FILE, list)
        }
        else{
            writeDataToFile(ALL_CONTACT_FILE, list)
        }
    }

    override fun updateFileFavContacts() {
        writeDataToFile(FAV_CONTACT_FILE, favContList)
    }

    override fun addFavContact() {  //update by notify recycler view
        rv_fav_contacts.adapter!!.notifyDataSetChanged()
        writeDataToFile(FAV_CONTACT_FILE, favContList)
    }

    override fun makeCrossAndNegBtnInvisible(){ //when the list is empty
        allContactAdap.switchDeleteEnabled()
        favContactAdap.switchRemoveFavourite()
    }

    override fun checkVisibilityCrossAndNeg(): Boolean {
        return isClickedEdit
    }

}

