package com.example.admin.exportcsvfromdb;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Admin on 10-07-2017.
 */

class DatabaseHandler extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "contactsManager";
    private static final String TABLE_CONTACTS = "contacts";            //table name

    private static final String KEY_ID = "id";                          //column id
    private static final String KEY_NAME = "name";                      //column name
    private static final String KEY_PH_NO = "phone_number";             //column phone_number

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_CONTACTS + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT,"
                + KEY_PH_NO + " TEXT" + ")";                                    //create table contacts(id integer primary key, name text, phone_number text)
        db.execSQL(CREATE_CONTACTS_TABLE);                                      //execute the sql query
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);                   // Drop older table if existed
        onCreate(db);                                                           // Create tables again
    }

    // Adding new contact
    public void addContact(Contact contact) {
        SQLiteDatabase db = this.getWritableDatabase();                         //get the database(writable)
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, contact.getName());                                // Contact Name
        values.put(KEY_PH_NO, contact.getPhoneNumber());                        // Contact Phone Number
        db.insert(TABLE_CONTACTS, null, values);                                // Inserting Row
        db.close();                                                             // Closing database connection
    }

    // Getting single contact
    public Contact getContact(int id) {
        SQLiteDatabase db = this.getReadableDatabase();                         //get the database(readable)
        Cursor cursor = db.query(TABLE_CONTACTS, new String[] { KEY_ID,
                        KEY_NAME, KEY_PH_NO }, KEY_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);   // Select id,name,phone_number from contacts where id=<id-args>
        if (cursor != null)                                                     //if cursor has at least 1 row
            cursor.moveToFirst();                                               //start reading
        Contact contact = new Contact(Integer.parseInt(cursor.getString(0)),
                cursor.getString(1), cursor.getString(2));                      //new Contact(id,name,phone_number)
        cursor.close();                                                         //free up the cursor
        db.close();
        return contact;                                                         // return contact
    }

    // Getting All Contacts
    public List<Contact> getAllContacts() {
        List<Contact> contactList = new ArrayList<>();                          //arraylist to hold all the contac objects
        String selectQuery = "SELECT  * FROM " + TABLE_CONTACTS;                // Select * from contacts
        SQLiteDatabase db = this.getWritableDatabase();                         //get the database(writable)
        Cursor cursor = db.rawQuery(selectQuery, null);                         // execute the select query
        if (cursor.moveToFirst()) {                                             // looping through all rows and adding to list
            do {
                Contact contact = new Contact();
                contact.setID(Integer.parseInt(cursor.getString(0)));           //set column number 1 to contact object field id
                contact.setName(cursor.getString(1));                            //set column number 2 to contact object field name
                contact.setPhoneNumber(cursor.getString(2));                    // //set column number 3 to contact object field phone number
                contactList.add(contact);                                       // Adding contact to list
            } while (cursor.moveToNext());                                      //continue till the cursor moves to the end rows
        }
        cursor.close();                                                         //free up the cursor
        db.close();
        return contactList;                                                     // return contact list
    }

    // Getting contacts Count
    public int getContactsCount() {
        int count = 0;
        String countQuery = "SELECT  * FROM " + TABLE_CONTACTS;                 //select * from contacts
        SQLiteDatabase db = this.getReadableDatabase();                         //get the database(readable)
        Cursor cursor = db.rawQuery(countQuery, null);                          //get query result set into the cursor
        count = cursor.getCount();                                              //get number of query result rows
        cursor.close();                                                         //free up the cursor
        db.close();
        return count;                                                           // return count
    }

    // Updating single contact
    public int updateContact(Contact contact) {
        SQLiteDatabase db = this.getWritableDatabase();                         //get the database(writable)
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, contact.getName());                                //put name string to content values
        values.put(KEY_PH_NO, contact.getPhoneNumber());                        //put phone_number string to content values
        return db.update(TABLE_CONTACTS, values, KEY_ID + " = ?",
                new String[] { String.valueOf(contact.getID()) });              // updating row, returns the number of rows affected
    }

    // Deleting single contact
    public void deleteContact(Contact contact) {
        SQLiteDatabase db = this.getWritableDatabase();                         //get the database(writable)
        db.delete(TABLE_CONTACTS, KEY_ID + " = ?",
                new String[] { String.valueOf(contact.getID()) });              //delete the contact rows
        db.close();
    }
}
