package com.drovisfrovis.contactsmanager.activities

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.drovisfrovis.contactsmanager.R
import com.drovisfrovis.contactsmanager.classes.ContactsDataModel
import kotlinx.android.synthetic.main.activity_edit_or_show_contact_details.*
import kotlinx.android.synthetic.main.activity_main.*
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStreamWriter

class EditOrShowContactDetails : AppCompatActivity() {

    interface UpdateRecyclerView{
        fun updateRVByNotify()
        fun updateFileAllContacts(name: String)
        fun updateFileFavContacts()
        fun addFavContact()
        fun makeCrossAndNegBtnInvisible()
        fun checkVisibilityCrossAndNeg(): Boolean
    }
    private var checkActivityMode: Int = 0      //1 is for contact details OR 2 is for add new contact
    private var isEditable = false
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_or_show_contact_details)
        if(intent.hasExtra("name") && intent.hasExtra("phone") && intent.hasExtra("email")
            && intent.hasExtra("address") && intent.hasExtra("dob") && intent.hasExtra("image"))
            {   //when you click on the RV
                title = "Contact Details"
                intent.apply {
                    tv_person_name_c_detail.text = getStringExtra("name")
                    et_person_name_c_detail.visibility = View.GONE
                    iv_person_c_detail.setImageResource(getContactImage(getStringExtra("image")!!))
                    btn_edit_contact.text = "Confirm Edit"
                }

                intent.apply {
                    setEditableFalse(et_person_name_c_detail, getStringExtra("name")!!)
                    setEditableFalse(et_phone_c_details, getLongExtra("phone", 0).toString())
                    setEditableFalse(et_email_c_detail, getStringExtra("email")!!)
                    setEditableFalse(et_address_c_detail, getStringExtra("address")!!)
                    setEditableFalse(et_dob_c_detail, getStringExtra("dob")!!)
                }
                checkActivityMode = 1

        }
        else{   //when you want to add new contact
            title = "Add New Contact"
            iv_person_c_detail.setImageResource(R.drawable.ic_default)
            iv_call_c_detail.visibility = View.GONE
            iv_cam_c_detail.visibility = View.GONE
            iv_edit_c_details.visibility = View.GONE
            btn_edit_contact.visibility = View.VISIBLE
            tv_person_name_c_detail.visibility = View.GONE
            checkActivityMode = 2
            isEditable = true
        }

        iv_back_c_detail.setOnClickListener {
            val makeCrossAndNegInvisible:UpdateRecyclerView  = MainActivity.context as MainActivity
            if(makeCrossAndNegInvisible.checkVisibilityCrossAndNeg()){
                makeCrossAndNegInvisible.makeCrossAndNegBtnInvisible()
            }

            finish()
        }

        var tempName = getEditText(et_person_name_c_detail)
        var tempPhone = getEditText(et_phone_c_details)
        var tempEmail = getEditText(et_email_c_detail)
        var tempAddress = getEditText(et_address_c_detail)
        var tempDob = getEditText(et_dob_c_detail)

        iv_call_c_detail.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:+92${tempPhone}"))
            MainActivity.context.startActivity(intent)
        }

        btn_edit_contact.setOnClickListener {
            if(checkActivityMode == 2){
                if(!checkDataValidation())
                    return@setOnClickListener
                else{
                    if (MainActivity.list.indexOf( MainActivity.list.find { it.name == et_person_name_c_detail.text.toString() }) != -1){
                        Toast.makeText(this, "Contact with same name already exist", Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }
                }
            }

            if(!(tempName == et_person_name_c_detail.text.toString()
                && tempPhone == et_phone_c_details.text.toString()
                && tempEmail == et_email_c_detail.text.toString()
                && tempAddress == et_address_c_detail.text.toString()
                && tempDob == et_dob_c_detail.text.toString())) {
                val image: String

                val index: Int = MainActivity.list.indexOf(MainActivity.list.find { it.name == tempName })
                if(index != -1){  //update the previous record
                    image = MainActivity.list[index].contactImage
                    MainActivity.list.removeAt(index)
                }
                else{   //add new record
                    image = "default"
                }

                tv_person_name_c_detail.text = getEditText(et_person_name_c_detail)
                tempName = getEditText(et_person_name_c_detail)
                tempPhone = getEditText(et_phone_c_details)
                tempEmail = getEditText(et_email_c_detail)
                tempAddress = getEditText(et_address_c_detail)
                tempDob = getEditText(et_dob_c_detail)
                if(index != -1){ //update the previous record
                    MainActivity.list.add(index, ContactsDataModel(tempName, tempPhone.toLong(), tempEmail, tempAddress, tempDob,  image))
                }
                else{   //add new record
                    MainActivity.list.add(ContactsDataModel(tempName, tempPhone.toLong(), tempEmail, tempAddress, tempDob,  image))
                }
                MainActivity.list.sortBy { it.name }
                val rvNotify:UpdateRecyclerView  = MainActivity.context as MainActivity
                rvNotify.updateRVByNotify()
                writeDataToFile(MainActivity.list)

            }
            if(checkActivityMode == 1){
                editingView(false)
            }
            else{
                btn_edit_contact.visibility = View.GONE
                et_person_name_c_detail.visibility = View.GONE

                iv_edit_c_details.visibility = View.VISIBLE
                tv_person_name_c_detail.visibility = View.VISIBLE
            }

            isEditable = !isEditable
            switchEditable(et_person_name_c_detail)
            switchEditable(et_phone_c_details)
            switchEditable(et_email_c_detail)
            switchEditable(et_address_c_detail)
            switchEditable(et_dob_c_detail)
        }

        iv_edit_c_details.setOnClickListener {
            if(et_person_name_c_detail.visibility == View.GONE){    //in editing view
                editingView(true)
                tempName = getEditText(et_person_name_c_detail)
                tempPhone = getEditText(et_phone_c_details)
                tempEmail = getEditText(et_email_c_detail)
                tempAddress = getEditText(et_address_c_detail)
                tempDob = getEditText(et_dob_c_detail)
            }
            else{
                if(checkActivityMode == 1){
                    editingView(false)
                }else{
                    iv_edit_c_details.visibility = View.GONE
                    tv_person_name_c_detail.visibility = View.GONE

                    btn_edit_contact.visibility = View.VISIBLE
                    et_person_name_c_detail.visibility = View.VISIBLE
                }
                setEditText(et_person_name_c_detail, tempName)
                setEditText(et_phone_c_details, tempPhone)
                setEditText(et_email_c_detail, tempEmail)
                setEditText(et_address_c_detail, tempAddress)
                setEditText(et_dob_c_detail, tempDob)
            }
            isEditable = !isEditable
            switchEditable(et_person_name_c_detail)
            switchEditable(et_phone_c_details)
            switchEditable(et_email_c_detail)
            switchEditable(et_address_c_detail)
            switchEditable(et_dob_c_detail)
        }

    }

    private fun writeDataToFile(dataList: List<ContactsDataModel>) {
        val fileName = "userContacts.txt"
        try {
            val outputStream: FileOutputStream?
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

    private fun setEditText(et: EditText, data: String){
        et.setText(data)
    }

    private fun getEditText(et: EditText): String{
        return et.text.toString()
    }

    private fun editingView(isEditingView: Boolean){
        if(isEditingView){
            tv_person_name_c_detail.visibility = View.GONE
            et_person_name_c_detail.visibility = View.VISIBLE
            btn_edit_contact.visibility = View.VISIBLE
            iv_call_c_detail.visibility = View.GONE
            iv_cam_c_detail.visibility = View.GONE
        }
        else{
            tv_person_name_c_detail.visibility = View.VISIBLE
            et_person_name_c_detail.visibility = View.GONE
            btn_edit_contact.visibility = View.GONE
            iv_call_c_detail.visibility = View.VISIBLE
            iv_cam_c_detail.visibility = View.VISIBLE
        }
    }

    private fun switchEditable(et: EditText){
        et.apply {
            isClickable = isEditable
            isFocusable = isEditable
            isFocusableInTouchMode = isEditable
        }
    }

    private fun setEditableFalse(et: EditText, data: String){   // sets the editable text false and sets data
        et.apply {
            setText(data)
            isClickable = false
            isFocusable = false
            isFocusableInTouchMode = false
        }
    }

    private fun getContactImage(name: String): Int{
        return when (name) {
            "hamza"     -> R.drawable.ic_hamza
            "asad"      -> R.drawable.ic_asad
            "abdullah"  -> R.drawable.ic_abdullah
            "naeem"     -> R.drawable.ic_muhammad_naeem
            "faisal"    -> R.drawable.ic_faisal
            "waleed"    -> R.drawable.ic_waleed
            "waqas"     -> R.drawable.ic_sir_waqas
            else        -> R.drawable.ic_default
        }
    }

    private fun checkDataValidation(): Boolean{

        var flag = true
        if(et_person_name_c_detail.text.toString().isEmpty()){
            et_person_name_c_detail.error = "Name should not be empty"
            flag = false
        }

        if(et_phone_c_details.text.toString().isEmpty()){
            et_phone_c_details.error = "Phone should not be empty"
            flag = false
        }

        if(et_email_c_detail.text.toString().isEmpty()){
            et_email_c_detail.error = "Email Address should not be empty"
            flag = false
        }

        if(et_address_c_detail.text.toString().isEmpty()){
            et_address_c_detail.error = "Address should not be empty"
            flag = false
        }

        if(et_dob_c_detail.text.toString().isEmpty()){
            et_dob_c_detail.error = "Date of Birth should not be empty"
            flag = false
        }

        if(flag){
            return true
        }
        return false
    }
}