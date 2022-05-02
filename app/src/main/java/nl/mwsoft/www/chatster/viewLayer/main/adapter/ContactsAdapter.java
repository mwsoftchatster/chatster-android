package nl.mwsoft.www.chatster.viewLayer.main.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import nl.mwsoft.www.chatster.R;
import nl.mwsoft.www.chatster.modelLayer.constantRegistry.ConstantRegistry;
import nl.mwsoft.www.chatster.modelLayer.model.Contact;
import nl.mwsoft.www.chatster.modelLayer.helper.util.image.ImageCircleTransformUtil;
import nl.mwsoft.www.chatster.modelLayer.modelLayerManager.contact.ContactModelLayerManager;
import nl.mwsoft.www.chatster.viewLayer.main.MainActivity;

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.MyViewHolder> {

    private List<Contact> contactList;
    private Context context;
    private ContactModelLayerManager contactModelLayerManager;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView ivProfileContacts;
        public ImageView ivIsChatsterContact;
        public ImageView ivIsNotChatsterContact;
        public TextView tvUserNameContacts;
        public TextView tvUserStatusContacts;

        public MyViewHolder(View view) {
            super(view);
            context = view.getContext();
            ivProfileContacts = (ImageView) view.findViewById(R.id.ivProfileContacts);
            ivIsChatsterContact = (ImageView) view.findViewById(R.id.ivIsChatsterContact);
            ivIsNotChatsterContact = (ImageView) view.findViewById(R.id.ivIsNotChatsterContact);
            tvUserNameContacts = (TextView) view.findViewById(R.id.tvUserNameContacts);
            tvUserStatusContacts = (TextView) view.findViewById(R.id.tvUserStatusContacts);

            tvUserNameContacts.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Contact contact = contactList.get(getAdapterPosition());
                    ((MainActivity)context).openChat(contact);
                }
            });

            ivProfileContacts.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Contact contact = contactList.get(getAdapterPosition());
                    ((MainActivity)context).openChat(contact);
                }
            });

            tvUserStatusContacts.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Contact contact = contactList.get(getAdapterPosition());
                    ((MainActivity)context).openChat(contact);
                }
            });

            ivIsNotChatsterContact.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Contact contact = contactList.get(getAdapterPosition());
                    ((MainActivity)context).navigateToInviteActivity(context, contact.getUserName());
                }
            });
        }
    }


    public ContactsAdapter(List<Contact> contactList) {
        this.contactList = contactList;
        this.contactModelLayerManager = new ContactModelLayerManager();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.contacts_list_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Contact contact = contactList.get(position);

        if(contactModelLayerManager.getContactProfilePicUriById(context, contact.getUserId()) != null &&
                !contactModelLayerManager.getContactProfilePicUriById(context, contact.getUserId()).isEmpty()){

            Picasso.with(context).load(ConstantRegistry.IMAGE_URL_PREFIX.concat(contactModelLayerManager.getContactProfilePicUriById(context, contact.getUserId())))
                    .transform(new ImageCircleTransformUtil())
                    .into(holder.ivProfileContacts);
        }
        if(contact.getIsChatsterContact() == 1){
            holder.ivIsChatsterContact.setVisibility(View.VISIBLE);
            holder.ivIsNotChatsterContact.setVisibility(View.GONE);
        }
        if(contact.getIsChatsterContact() == 0){
            holder.ivIsChatsterContact.setVisibility(View.GONE);
            holder.ivIsNotChatsterContact.setVisibility(View.VISIBLE);
        }
        holder.tvUserNameContacts.setText(contact.getUserName());
        holder.tvUserStatusContacts.setText(contact.getStatusMessage());
    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }
}

