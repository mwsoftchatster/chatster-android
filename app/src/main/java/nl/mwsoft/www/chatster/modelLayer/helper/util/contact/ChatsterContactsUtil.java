package nl.mwsoft.www.chatster.modelLayer.helper.util.contact;


import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.telephony.TelephonyManager;

import com.crashlytics.android.Crashlytics;

import java.util.ArrayList;
import java.util.Locale;

import io.fabric.sdk.android.Fabric;
import nl.mwsoft.www.chatster.R;
import nl.mwsoft.www.chatster.modelLayer.constantRegistry.ConstantRegistry;
import nl.mwsoft.www.chatster.modelLayer.model.Contact;

public class ChatsterContactsUtil {


    public ChatsterContactsUtil(){}

    public static ArrayList<Contact> getAllContactsWithPhoneNumber(Context context){
        Fabric.with(context, new Crashlytics());
        ArrayList<Contact> contacts = new ArrayList<>();
        ContentResolver cr = context.getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);

        if (cur != null && cur.getCount() > 0) {
            while (cur.moveToNext()) {
                String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

                if(cur.getInt(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id}, null);
                    if(pCur != null){
                        while (pCur.moveToNext()) {
                            String phoneNo = removeSpecialCharacters(pCur.getString(pCur.getColumnIndex(
                                    ContactsContract.CommonDataKinds.Phone.NUMBER)));
                            if (phoneNo.length() > 4) {

                                // if phone number starts with 00
                                if (phoneNo.substring(0, 2).equals(ConstantRegistry.CHATSTER_DOUBLE_ZERO)) {
                                    String doubleOONr = phoneNo;
                                    // remove 00 and save contact
                                    doubleOONr = doubleOONr.substring(0, 2).replace(ConstantRegistry.CHATSTER_DOUBLE_ZERO,
                                            ConstantRegistry.CHATSTER_EMPTY_STRING) +
                                            doubleOONr.substring(2, doubleOONr.length());
                                    Contact contact = new Contact();
                                    try {
                                        contact.setUserId(Long.parseLong(doubleOONr));
                                        contact.setUserName(name);
                                        contact.setStatusMessage(ConstantRegistry.CHATSTER_HI_MY_NAME_IS.concat(name));
                                        contacts.add(contact);
                                        continue;
                                    } catch (NumberFormatException e) {
                                        e.printStackTrace();
                                        continue;
                                    }
                                }

                                // if phone number doesn't start with +
                                if (!phoneNo.startsWith(ConstantRegistry.CHATSTER_PLUS)) {
                                    // if phone number doesn't start with 00
                                    if (!phoneNo.substring(0, 2).equals(ConstantRegistry.CHATSTER_DOUBLE_ZERO)) {
                                        // if phone number starts with 0
                                        if (phoneNo.startsWith(ConstantRegistry.CHATSTER_ZERO)) {
                                            String domesticNr = phoneNo;
                                            // 0645572649
                                            domesticNr = domesticNr.substring(0, 1).replace(ConstantRegistry.CHATSTER_ZERO,
                                                    getCountryPhoneCode(context)) +
                                                    domesticNr.substring(1, domesticNr.length());
                                            Contact contact = new Contact();
                                            try {
                                                contact.setUserId(Long.parseLong(domesticNr));
                                                contact.setUserName(name);
                                                contact.setStatusMessage(ConstantRegistry.CHATSTER_HI_MY_NAME_IS.concat(name));
                                                contacts.add(contact);
                                                continue;
                                            } catch (NumberFormatException e) {
                                                e.printStackTrace();
                                                continue;
                                            }
                                        }
                                    }
                                }

                                // +31234567890
                                if (phoneNo.startsWith(ConstantRegistry.CHATSTER_PLUS)) {
                                    String internationalNr = phoneNo;
                                    // if phone number starts with +
                                    internationalNr = internationalNr.replace(ConstantRegistry.CHATSTER_PLUS,
                                            ConstantRegistry.CHATSTER_EMPTY_STRING);
                                    Contact contact = new Contact();
                                    try {
                                        contact.setUserId(Long.parseLong(internationalNr));
                                        contact.setUserName(name);
                                        contact.setStatusMessage(ConstantRegistry.CHATSTER_HI_MY_NAME_IS.concat(name));
                                        contacts.add(contact);
                                    } catch (NumberFormatException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                        pCur.close();
                    }
                }
            }
        }
        if(cur != null){
            cur.close();
        }
        return contacts;
    }

    private static String removeSpecialCharacters(String phoneNo){
        phoneNo = removeOpenRoundBraces(phoneNo);

        phoneNo = removeCloseRoundBraces(phoneNo);

        phoneNo = removeForwardSlash(phoneNo);

        phoneNo = removeLetterN(phoneNo);

        phoneNo = removeComma(phoneNo);

        phoneNo = removePoint(phoneNo);

        phoneNo = removeStar(phoneNo);

        phoneNo = removeSemiColon(phoneNo);

        phoneNo = removeHashTag(phoneNo);

        phoneNo = removeMinus(phoneNo);

        phoneNo = removeSpace(phoneNo);

        phoneNo = removeASCIIControlCharacters(phoneNo);

        phoneNo = removeASCIINonPrintableCharacters(phoneNo);

        phoneNo = removeUnicodeNonPrintableCharacters(phoneNo);

        phoneNo = removeWhitespaces(phoneNo);

        phoneNo = replaceInvisibleControlCharactersAndUnusedCodePoints(phoneNo);

        return phoneNo.trim();
    }

    private static String removeASCIIControlCharacters(String phoneNo){
        return phoneNo.replaceAll("\\p{Cntrl}", ConstantRegistry.CHATSTER_EMPTY_STRING);
    }

    private static String removeASCIINonPrintableCharacters(String phoneNo){
        return phoneNo.replaceAll("[^\\p{Print}]", ConstantRegistry.CHATSTER_EMPTY_STRING);
    }

    private static String removeUnicodeNonPrintableCharacters(String phoneNo){
        return phoneNo.replaceAll("\\p{C}", ConstantRegistry.CHATSTER_EMPTY_STRING);
    }

    private static String removeWhitespaces(String phoneNo){
        return phoneNo.replaceAll("[\\p{C}\\p{Z}]", ConstantRegistry.CHATSTER_EMPTY_STRING);
    }

    private static String replaceInvisibleControlCharactersAndUnusedCodePoints(String phoneNo){
        StringBuilder newString = new StringBuilder(phoneNo.length());
        for (int offset = 0; offset < phoneNo.length();) {
            int codePoint = phoneNo.codePointAt(offset);
            offset += Character.charCount(codePoint);

            // Replace invisible control characters and unused code points
            switch (Character.getType(codePoint)) {
                case Character.CONTROL:     // \p{Cc}
                case Character.FORMAT:      // \p{Cf}
                case Character.PRIVATE_USE: // \p{Co}
                case Character.SURROGATE:   // \p{Cs}
                case Character.UNASSIGNED:  // \p{Cn}
                    newString.append(ConstantRegistry.CHATSTER_EMPTY_STRING);
                    break;
                default:
                    newString.append(Character.toChars(codePoint));
                    break;
            }
        }

        return newString.toString();
    }

    private static String removeSpace(String phoneNo) {
        if(phoneNo.contains(ConstantRegistry.CHATSTER_SPACE_STRING)){
            phoneNo = phoneNo.replace(ConstantRegistry.CHATSTER_SPACE_STRING, ConstantRegistry.CHATSTER_EMPTY_STRING);
        }
        return phoneNo;
    }

    private static String removeMinus(String phoneNo) {
        if(phoneNo.contains(ConstantRegistry.CHATSTER_MINUS)){
            phoneNo = phoneNo.replace(ConstantRegistry.CHATSTER_MINUS, ConstantRegistry.CHATSTER_EMPTY_STRING);
        }
        return phoneNo;
    }

    private static String removeHashTag(String phoneNo) {
        if(phoneNo.contains(ConstantRegistry.CHATSTER_HASH_TAG)){
            phoneNo = phoneNo.replace(ConstantRegistry.CHATSTER_HASH_TAG, ConstantRegistry.CHATSTER_EMPTY_STRING);
        }
        return phoneNo;
    }

    private static String removeSemiColon(String phoneNo) {
        if(phoneNo.contains(ConstantRegistry.CHATSTER_SEMICOLON)){
            phoneNo = phoneNo.replace(ConstantRegistry.CHATSTER_SEMICOLON, ConstantRegistry.CHATSTER_EMPTY_STRING);
        }
        return phoneNo;
    }

    private static String removeStar(String phoneNo) {
        if(phoneNo.contains(ConstantRegistry.CHATSTER_STAR)){
            phoneNo = phoneNo.replace(ConstantRegistry.CHATSTER_STAR, ConstantRegistry.CHATSTER_EMPTY_STRING);
        }
        return phoneNo;
    }

    private static String removePoint(String phoneNo) {
        if(phoneNo.contains(ConstantRegistry.CHATSTER_POINT)){
            phoneNo = phoneNo.replace(ConstantRegistry.CHATSTER_POINT, ConstantRegistry.CHATSTER_EMPTY_STRING);
        }
        return phoneNo;
    }

    private static String removeComma(String phoneNo) {
        if(phoneNo.contains(ConstantRegistry.CHATSTER_COMMA)){
            phoneNo = phoneNo.replace(ConstantRegistry.CHATSTER_COMMA, ConstantRegistry.CHATSTER_EMPTY_STRING);
        }
        return phoneNo;
    }

    private static String removeLetterN(String phoneNo) {
        if(phoneNo.contains(ConstantRegistry.CHATSTER_LETTER_N)){
            phoneNo = phoneNo.replace(ConstantRegistry.CHATSTER_LETTER_N, ConstantRegistry.CHATSTER_EMPTY_STRING);
        }
        return phoneNo;
    }

    private static String removeForwardSlash(String phoneNo) {
        if(phoneNo.contains(ConstantRegistry.CHATSTER_FORWARD_SLASH)){
            phoneNo = phoneNo.replace(ConstantRegistry.CHATSTER_FORWARD_SLASH, ConstantRegistry.CHATSTER_EMPTY_STRING);
        }
        return phoneNo;
    }

    private static String removeCloseRoundBraces(String phoneNo) {
        if(phoneNo.contains(ConstantRegistry.CHATSTER_CLOSE_ROUND_BRACKETS)){
            phoneNo = phoneNo.replace(ConstantRegistry.CHATSTER_CLOSE_ROUND_BRACKETS, ConstantRegistry.CHATSTER_EMPTY_STRING);
        }
        return phoneNo;
    }

    private static String removeOpenRoundBraces(String phoneNo) {
        if(phoneNo.contains(ConstantRegistry.CHATSTER_OPEN_ROUND_BRACKETS)){
            phoneNo = phoneNo.replace(ConstantRegistry.CHATSTER_OPEN_ROUND_BRACKETS, ConstantRegistry.CHATSTER_EMPTY_STRING);
        }
        return phoneNo;
    }


    private static String getCountryPhoneCode(Context context) {
        String countryID = "";
        String countryPhoneCode = "";
        // TelephonyManager determines telephony services and states and some types of subscriber information
        TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        // Returns the ISO country code equivalent for the SIM provider's country code
        // NL
        countryID = manager.getSimCountryIso().toUpperCase();
        Locale loc = new Locale("", countryID);
        // 31, NL
        String[] countryCodes = context.getResources().getStringArray(R.array.countryCodes);
        for (int i = 0; i < countryCodes.length; i++) {
            // [0]=31 & [1]=NL
            String[] countryCodesItem = countryCodes[i].split(ConstantRegistry.CHATSTER_COMMA);
            // NL == NL
            if (countryCodesItem[0].trim().equals(loc.getDisplayCountry().trim())) {
                // 31
                countryPhoneCode = countryCodesItem[1].replace(ConstantRegistry.CHATSTER_OPEN_ROUND_BRACKETS,
                        ConstantRegistry.CHATSTER_EMPTY_STRING);
                countryPhoneCode = countryPhoneCode.replace(ConstantRegistry.CHATSTER_CLOSE_ROUND_BRACKETS,
                        ConstantRegistry.CHATSTER_EMPTY_STRING);
                break;
            }
        }
        return countryPhoneCode;
    }
}

