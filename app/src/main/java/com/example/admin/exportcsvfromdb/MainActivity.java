package com.example.admin.exportcsvfromdb;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.io.FileWriter;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    DatabaseHandler db;
    EditText nameEdtxt, phoneEdtxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        nameEdtxt = (EditText) findViewById(R.id.editText1);
        phoneEdtxt = (EditText) findViewById(R.id.editText2);
        db = new DatabaseHandler(this);
    }

    public void onClick(View view) {
        if (view.getId() == R.id.button) {
            String name = nameEdtxt.getText().toString();
            String phone = phoneEdtxt.getText().toString();
            if (name.isEmpty()) {
                Toast.makeText(this, "Enter Name", Toast.LENGTH_SHORT).show();
            } else if (phone.isEmpty()) {
                Toast.makeText(this, "Enter Contact Number", Toast.LENGTH_SHORT).show();
            } else {
                db.addContact(new Contact(name, phone));
                Toast.makeText(this, "Contact added successfully!", Toast.LENGTH_SHORT).show();
            }
        }
        if (view.getId() == R.id.button2){
            // Reading all contacts
            List<Contact> contacts = db.getAllContacts();
            for (Contact cn : contacts) {
                String log = "Id: " + cn.getID() + " ,Name: " + cn.getName() + " ,Phone: " + cn.getPhoneNumber();
                Log.d("Name: ", log);
                Toast.makeText(this, log, Toast.LENGTH_LONG).show();
            }
        }
        if (view.getId() == R.id.button3){
            exportDB();
        }
    }

    private void exportDB() {
        DatabaseHandler dbhelper = new DatabaseHandler(getApplicationContext());
        File exportDir = null;
        if (Build.VERSION.SDK_INT > 19) {
            exportDir = new File(Environment.getExternalStorageDirectory(), Environment.DIRECTORY_DOCUMENTS);
        }else {
            exportDir = new File(Environment.getExternalStorageDirectory(), "");
        }
        if (!exportDir.exists()) {
            exportDir.mkdirs();
        }

        File file = new File(exportDir, "csvname.csv");
        try {
            file.createNewFile();
            CSVWriter csvWrite = new CSVWriter(new FileWriter(file));
            SQLiteDatabase db = dbhelper.getReadableDatabase();
            Cursor curCSV = db.rawQuery("SELECT * FROM contacts", null);
            csvWrite.writeNext(curCSV.getColumnNames());
            while (curCSV.moveToNext()) {
                //Which column you want to exprort
                String arrStr[] = {curCSV.getString(0), curCSV.getString(1), curCSV.getString(2)};
                csvWrite.writeNext(arrStr);
            }
            csvWrite.close();
            curCSV.close();
            Toast.makeText(this, "Successfully exported to "+file.toString(), Toast.LENGTH_LONG).show();
        } catch (Exception sqlEx) {
            Log.e("MainActivity", sqlEx.getMessage(), sqlEx);
        }
    }
}
