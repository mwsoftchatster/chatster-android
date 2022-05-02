package nl.mwsoft.www.chatster.modelLayer.modelLayerManager.contact;


import android.content.ContentValues;
import android.content.Context;

import java.util.ArrayList;

import nl.mwsoft.www.chatster.dependencyRegistry.DependencyRegistry;
import nl.mwsoft.www.chatster.modelLayer.database.contact.ContactDatabaseLayer;
import nl.mwsoft.www.chatster.modelLayer.database.dblocal.ChatsProvider;
import nl.mwsoft.www.chatster.modelLayer.database.dblocal.DBOpenHelper;
import nl.mwsoft.www.chatster.modelLayer.model.Contact;

public class ContactModelLayerManager {

    private ContactDatabaseLayer contactDatabaseLayer;

    public ContactModelLayerManager() {
        this.contactDatabaseLayer = DependencyRegistry.shared.createContactDatabaseLayer();
    }

    // region Contacts DB

    public String getContactProfilePicUriById(Context context, long contactId) {
        return this.contactDatabaseLayer.getContactProfilePicUriById(context, contactId);
    }

    public String getContactNameById(Context context, long contactId) {
        return this.contactDatabaseLayer.getContactNameById(context, contactId);
    }

    public Contact getContactById(Context context, long contactId) {
        return this.contactDatabaseLayer.getContactById(context, contactId);
    }

    public ArrayList<Contact> getAllContacts(Context context) {
        return this.contactDatabaseLayer.getAllContacts(context);
    }

    public ArrayList<Long> getAllContactIds(Context context) {
        return this.contactDatabaseLayer.getAllContactIds(context);
    }

    public ArrayList<Contact> getAllContactsByGroupChat(Context context, String groupChatId) {
        return this.contactDatabaseLayer.getAllContactsByGroupChat(context, groupChatId);
    }

    public void insertContact(long contactId, String contactName, String contactStatusMessage, Context context) {
        this.contactDatabaseLayer.insertContact(contactId, contactName, contactStatusMessage, context);
    }

    public void deleteContactById(long contactId, Context context){
        this.contactDatabaseLayer.deleteContactById(contactId, context);
    }

    public String insertContacts(Context context){
        return this.contactDatabaseLayer.insertContacts(context);
    }

    public void updateContacts(ArrayList<Long> chatsterContacts, Context context){
        this.contactDatabaseLayer.updateContacts(chatsterContacts, context);
    }

    // endregion
}
