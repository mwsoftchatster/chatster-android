package nl.mwsoft.www.chatster.viewLayer.createGroupChat;


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
import nl.mwsoft.www.chatster.dependencyRegistry.DependencyRegistry;
import nl.mwsoft.www.chatster.modelLayer.constantRegistry.ConstantRegistry;
import nl.mwsoft.www.chatster.modelLayer.model.Contact;
import nl.mwsoft.www.chatster.modelLayer.helper.util.image.ImageCircleTransformUtil;
import nl.mwsoft.www.chatster.presenterLayer.groupChat.GroupChatPresenter;

public class CreateGroupChatContactsAdapter extends RecyclerView.Adapter<CreateGroupChatContactsAdapter.MyViewHolder> {

    private List<Contact> contactList;
    private Context context;
    private GroupChatPresenter groupChatPresenter;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView ivProfileContactsCreateGroup;
        public ImageView ivCreateGroupContactIsSelected;
        public ImageView ivIsChatsterContactCreateGroup;
        public TextView tvUserNameContactsCreateGroup;
        public TextView tvUserStatusContactsCreateGroup;

        public MyViewHolder(View view) {
            super(view);
            context = view.getContext();
            ivProfileContactsCreateGroup = (ImageView) view.findViewById(R.id.ivProfileContactsCreateGroup);
            ivCreateGroupContactIsSelected = (ImageView) view.findViewById(R.id.ivCreateGroupContactIsSelected);
            ivIsChatsterContactCreateGroup = (ImageView) view.findViewById(R.id.ivIsChatsterContactCreateGroup);
            tvUserNameContactsCreateGroup = (TextView) view.findViewById(R.id.tvUserNameContactsCreateGroup);
            tvUserStatusContactsCreateGroup = (TextView) view.findViewById(R.id.tvUserStatusContactsCreateGroup);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!contactList.get(getAdapterPosition()).isHasBeenSelected()){
                        contactList.get(getAdapterPosition()).setHasBeenSelected(true);
                        notifyDataSetChanged();
                    }else{
                        contactList.get(getAdapterPosition()).setHasBeenSelected(false);
                        notifyDataSetChanged();
                    }
                }
            });
        }
    }

    public CreateGroupChatContactsAdapter(List<Contact> contactList) {
        this.contactList = contactList;
        this.groupChatPresenter = DependencyRegistry.shared.injectCreateGroupChatContactsAdapter();
    }

    @Override
    public CreateGroupChatContactsAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.create_group_contact_list_item, parent, false);

        return new CreateGroupChatContactsAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final CreateGroupChatContactsAdapter.MyViewHolder holder, int position) {
        Contact contact = contactList.get(position);
        if(contact.getIsChatsterContact() == 1){
            holder.ivIsChatsterContactCreateGroup.setVisibility(View.VISIBLE);
        }
        if(contact.getIsChatsterContact() == 0){
            holder.ivIsChatsterContactCreateGroup.setVisibility(View.GONE);
        }

        if(groupChatPresenter.getContactProfilePicUriById(context, contact.getUserId()) != null &&
                !groupChatPresenter.getContactProfilePicUriById(context, contact.getUserId()).equals(ConstantRegistry.CHATSTER_EMPTY_STRING)){

            Picasso.with(context).load(ConstantRegistry.IMAGE_URL_PREFIX.concat(groupChatPresenter.getContactProfilePicUriById(context, contact.getUserId())))
                    .transform(new ImageCircleTransformUtil())
                    .into(holder.ivProfileContactsCreateGroup);
        }

        holder.tvUserNameContactsCreateGroup.setText(contact.getUserName());
        holder.tvUserStatusContactsCreateGroup.setText(contact.getStatusMessage());

        if(contact.isHasBeenSelected()){
            holder.ivCreateGroupContactIsSelected.setVisibility(View.VISIBLE);
        }else{
            holder.ivCreateGroupContactIsSelected.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }
}

