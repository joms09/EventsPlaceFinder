package views

import com.example.jomari.eventsplacefinder.R
import models.ChatMessage
import models.User
import utils.DateUtils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.latest_message_row.view.*

/**
 * Created by ansh on 04/09/18.
 */
class LatestMessageRow(val chatMessage: ChatMessage) : Item<ViewHolder>() {

    var chatPartnerUser: User? = null

    override fun getLayout(): Int {
        return R.layout.latest_message_row
    }

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.latest_message_textview.text = chatMessage.text

        val chatPartnerId: String
        if (chatMessage.fromId == FirebaseAuth.getInstance().uid) {
            chatPartnerId = chatMessage.toId
        } else {
            chatPartnerId = chatMessage.fromId
        }

        val ref = FirebaseDatabase.getInstance().getReference("/users/$chatPartnerId")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                chatPartnerUser = p0.getValue(User::class.java)
                viewHolder.itemView.username_textview_latest_message.text = chatPartnerUser?.name
                viewHolder.itemView.latest_msg_time.text = DateUtils.getFormattedTime(chatMessage.timestamp)

//                if (!chatPartnerUser?.profileImageUrl?.isEmpty()!!) {
//                    val requestOptions = RequestOptions().placeholder(R.drawable.no_image2)
//
//                    Glide.with(viewHolder.itemView.imageview_latest_message.context)
//                            .load(chatPartnerUser?.profileImageUrl)
//                            .thumbnail(0.1f)
//                            .apply(requestOptions)
//                            .into(viewHolder.itemView.imageview_latest_message)
//                }
            }

        })

    }

}