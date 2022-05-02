package nl.mwsoft.www.chatster.viewLayer.groupChatInfo;


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

public class GroupChatInfoAdapter  extends RecyclerView.Adapter<GroupChatInfoAdapter.MyViewHolder> {

    private List<Contact> contactList;
    private Context context;
    private GroupChatPresenter groupChatPresenter;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView ivGroupChatInfoContact;
        public TextView tvUserNameGroupChatInfo;
        public TextView tvUserStatusGroupChatInfo;
        public View mRootView;


        public MyViewHolder(View view) {
            super(view);
            mRootView = view;
            context = view.getContext();
            ivGroupChatInfoContact = (ImageView) view.findViewById(R.id.ivGroupChatInfoContact);
            tvUserNameGroupChatInfo = (TextView) view.findViewById(R.id.tvUserNameGroupChatInfo);
            tvUserStatusGroupChatInfo = (TextView) view.findViewById(R.id.tvUserStatusGroupChatInfo);
        }
    }

    public GroupChatInfoAdapter(List<Contact> contactList) {
        this.contactList = contactList;
        this.groupChatPresenter = DependencyRegistry.shared.injectGroupChatInfoAdapter();
    }

    @Override
    public GroupChatInfoAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.group_chat_info_item, parent, false);

        return new GroupChatInfoAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final GroupChatInfoAdapter.MyViewHolder holder, int position) {
        Contact contact = contactList.get(position);
        if(groupChatPresenter.getContactProfilePicUriById(context, contact.getUserId()) != null &&
                !groupChatPresenter.getContactProfilePicUriById(context, contact.getUserId()).equals(ConstantRegistry.CHATSTER_EMPTY_STRING)){

            Picasso.with(context).load(ConstantRegistry.IMAGE_URL_PREFIX.concat(groupChatPresenter.getContactProfilePicUriById(context, contact.getUserId())))
                    .transform(new ImageCircleTransformUtil())
                    .into(holder.ivGroupChatInfoContact);
        }
        holder.tvUserNameGroupChatInfo.setText(contact.getUserName());
        holder.tvUserStatusGroupChatInfo.setText(contact.getStatusMessage());
    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }
}



