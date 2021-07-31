package com.drovisfrovis.contactsmanager.classes

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.drovisfrovis.contactsmanager.R
import com.drovisfrovis.contactsmanager.activities.EditOrShowContactDetails
import com.drovisfrovis.contactsmanager.activities.MainActivity
import kotlinx.android.synthetic.main.single_list_item_all_contacts.view.*

class AllContactsAdapter(private var context: Context, private val listOfContacts: MutableList<ContactsDataModel>) : RecyclerView.Adapter<AllContactsAdapter.MyViewHolder>() {
    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    private var isDeleteAllowed: Boolean = false
    private var isOutlinedView = false
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view: View
        if(viewType == R.layout.single_list_item_all_contacts){
            view = LayoutInflater.from(parent.context).inflate(R.layout.single_list_item_all_contacts, parent, false)
        }else{
            view = LayoutInflater.from(parent.context).inflate(R.layout.single_list_item_all_contacts_outlined, parent, false)
        }
        return MyViewHolder(view)
    }

    override fun getItemViewType(position: Int): Int {
        return if(isOutlinedView) R.layout.single_list_item_all_contacts_outlined else R.layout.single_list_item_all_contacts
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        var tempName = listOfContacts[position].name
        if(listOfContacts[position].name.length > 2){
            if(!listOfContacts[position].name.substring(0, 3).equals("sir", true)){
                if(listOfContacts[position].name.contains(' ')){
                    tempName = listOfContacts[position].name.substring(listOfContacts[position].name.indexOf(' ')+1).trim()
                    if(tempName.contains(' ')){
                        tempName = listOfContacts[position].name.substring(0, listOfContacts[position].name.indexOf(' ', tempName.length))
                    }else{
                        tempName = listOfContacts[position].name
                    }
                }

            }
        }

        holder.itemView.apply {
            listOfContacts[position].apply {
                //here we set the views of the single item on the recycler view
                tv_name_all_contacts.text = tempName
                tv_email_all_contacts.text = email
                iv_person_img_all_contacts.setImageResource(getContactImage(contactImage))
                if(isDeleteAllowed){
                    iv_delete_contact.visibility = VISIBLE
                }
                else{
                    iv_delete_contact.visibility = View.INVISIBLE
                }

                //when you click on the single item in the All contacts
                cl_single_item_all_contacts.setOnClickListener {
                    if(isOutlinedView){
                        MainActivity.apply {
                            if(favContList.indexOf(favContList.find { it.name == name })  == -1){
                                favContList.add(listOfContacts[position])
                                favContList.sortBy { it.name }
                                val fileUpdater: EditOrShowContactDetails.UpdateRecyclerView = context as MainActivity
                                fileUpdater.addFavContact()
                            }else{
                                Toast.makeText(context, "Contact is already in the favourites", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }else{
                        val intent = Intent(context, EditOrShowContactDetails::class.java)
                        intent.putExtra("name", name)
                        intent.putExtra("phone",phoneNo)
                        intent.putExtra("email", email)
                        intent.putExtra("address", address)
                        intent.putExtra("dob", dateOfBirth)
                        intent.putExtra("image", contactImage)
                        context.startActivity(intent)
                    }
                }

                iv_delete_contact.setOnClickListener {
                    val name = name
                    listOfContacts.removeAt(position)
                    notifyItemRemoved(position)
                    notifyItemRangeChanged(position, listOfContacts.size)
                    val fileUpdater: EditOrShowContactDetails.UpdateRecyclerView = MainActivity.context as MainActivity
                    fileUpdater.updateFileAllContacts(name)
                }
                iv_call_all_contact.setOnClickListener {
                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:+92${phoneNo}"))
                    MainActivity.context.startActivity(intent)
                }
            }
        }

    }

    private fun getContactImage(name: String): Int {
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

    fun switchDeleteEnabled() {
        isDeleteAllowed = !isDeleteAllowed
    }


    override fun getItemCount(): Int {
        return listOfContacts.size
    }

    fun switchOutlinedView(){
        isOutlinedView = !isOutlinedView
        notifyDataSetChanged()
    }


}