/*
  Copyright (C) 2017 - 2020 MWSOFT

  This program is free software: you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  (at your option) any later version.

  This program is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.

  You should have received a copy of the GNU General Public License
  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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
