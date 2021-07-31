package com.drovisfrovis.contactsmanager.classes

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.drovisfrovis.contactsmanager.R
import com.drovisfrovis.contactsmanager.activities.EditOrShowContactDetails
import com.drovisfrovis.contactsmanager.activities.MainActivity
import kotlinx.android.synthetic.main.single_list_item_fav_contacts.view.*

class FavContactsAdapter(private var context: Context, private val listOfContacts: MutableList<ContactsDataModel>) : RecyclerView.Adapter<FavContactsAdapter.MyViewHolder>() {
    private var isRemoveFavAllowed = false
    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.single_list_item_fav_contacts, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        var tempName = listOfContacts[position].name

        listOfContacts[position].apply {
            if(name.length > 2){
                if(!name.substring(0, 3).equals("sir", true)){
                    if(name.contains(' ')){
                        tempName = name.substring(name.indexOf(' ')+1).trim()
                        if(tempName.contains(' ')){
                            tempName = name.substring(0, name.indexOf(' ', tempName.length))
                        }else{
                            tempName = name
                        }
                    }

                }
            }
        }

        holder.itemView.apply {

            iv_contact_fav.setImageResource(getContactImage(listOfContacts[position].contactImage))
            tv_name_fav_contact.text = tempName
            if(isRemoveFavAllowed)
                iv_remove_contact_fav.visibility = View.VISIBLE
            else
                iv_remove_contact_fav.visibility = View.INVISIBLE


            cl_single_list_item_fav.setOnClickListener {
                val intent = Intent(context, EditOrShowContactDetails::class.java)
                intent.putExtra("name",listOfContacts[position].name)
                intent.putExtra("phone",listOfContacts[position].phoneNo)
                intent.putExtra("email",listOfContacts[position].email)
                intent.putExtra("address",listOfContacts[position].address)
                intent.putExtra("dob",listOfContacts[position].dateOfBirth)
                intent.putExtra("image",listOfContacts[position].contactImage)
                context.startActivity(intent)
            }

            iv_remove_contact_fav.setOnClickListener {
                listOfContacts.removeAt(position)
                notifyItemRemoved(position)
                notifyItemRangeChanged(position, listOfContacts.size)
                val fileUpdater: EditOrShowContactDetails.UpdateRecyclerView = MainActivity.context as MainActivity
                fileUpdater.updateFileFavContacts()
            }
        }
    }

    override fun getItemCount(): Int {
        return listOfContacts.size
    }

    fun switchRemoveFavourite(){
        isRemoveFavAllowed = !isRemoveFavAllowed
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
}