package nl.mwsoft.www.chatster.viewLayer.main.fragment;


import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import nl.mwsoft.www.chatster.modelLayer.modelLayerManager.contact.ContactModelLayerManager;
import nl.mwsoft.www.chatster.R;
import nl.mwsoft.www.chatster.modelLayer.model.Contact;
import nl.mwsoft.www.chatster.modelLayer.helper.recyclerView.RecyclerTouchListener;
import nl.mwsoft.www.chatster.modelLayer.helper.recyclerView.RecyclerViewClickListener;
import nl.mwsoft.www.chatster.viewLayer.main.adapter.ContactsAdapter;

public class ContactsFragment extends Fragment {

    private ContactsAdapter contactsAdapter;
    public ArrayList<Contact> contacts;
    private RecyclerView contactsRecyclerView;
    private ContactModelLayerManager contactModelLayerManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contacts, container, false);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        contactModelLayerManager = new ContactModelLayerManager();

        readContacts(view);

        configureRecyclerView(view);
    }

    private void readContacts(View view) {
        contacts = new ArrayList<>();
        if(contactModelLayerManager.getAllContacts(view.getContext()) != null){
            contacts.addAll(contactModelLayerManager.getAllContacts(view.getContext()));
        }
    }

    private void configureRecyclerView(View view) {
        contactsAdapter = new ContactsAdapter(contacts);
        contactsRecyclerView = (RecyclerView)view.findViewById(R.id.rvContacts);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(view.getContext());
        contactsRecyclerView.setLayoutManager(mLayoutManager);
        contactsRecyclerView.setItemAnimator(new DefaultItemAnimator());
        contactsRecyclerView.setAdapter(contactsAdapter);

        contactsRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(view.getContext(),
                contactsRecyclerView, new RecyclerViewClickListener() {
            @Override
            public void onClick(View view, int position) {

            }

            @Override
            public void onLongClick(View view, int position) {
                // open alert dialog and ask user if user wants to delete this contact
                Contact contact = contacts.get(position);
                contactModelLayerManager.deleteContactById(contact.getUserId(),view.getContext());
            }
        }));
    }
}
