package com.example.karmjit.androidlabs;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import static com.example.karmjit.androidlabs.ChatDatabaseHelper.TABLE_NAME;

public class ChatWindow extends Activity {
    Button send_btn;
    ListView lv;
    EditText edit;
    static SQLiteDatabase db;
    static Cursor cursor;
    static  ChatAdapter messageAdapter ;
    static ChatDatabaseHelper object;
    static ArrayList<String> list = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_chat_window);


        object = new ChatDatabaseHelper(this);
        messageAdapter = new ChatAdapter(this);
        db=object.getWritableDatabase();
        list.clear();
        moveCursor();

        send_btn = (Button) findViewById(R.id.send);
        lv = (ListView) findViewById(R.id.listview);
        edit = (EditText)findViewById(R.id.editText3);


        send_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                list.clear();
                ContentValues contentValues = new ContentValues();
                contentValues.put(ChatDatabaseHelper.KEY_MESSAGE,edit.getText().toString());
                db.insert(ChatDatabaseHelper.TABLE_NAME,null,contentValues);
                messageAdapter.notifyDataSetChanged();
                moveCursor();

                edit.setText("");
            }
        });
        lv.setAdapter(messageAdapter);
    }




    private class ChatAdapter extends ArrayAdapter<String>{

        public ChatAdapter( Context ctx) {
            super(ctx,0);
        }


        public int getCount(){
            return list.size();
        }


        public String getItem(int position) {
            return list.get(position);
        }

       public View getView(int position, View convertView , ViewGroup parent){
           LayoutInflater inflater = ChatWindow.this.getLayoutInflater();
           View result = null ;
           if(position%2 == 0)
               result = inflater.inflate(R.layout.chat_row_incoming, null);
           else
               result = inflater.inflate(R.layout.chat_row_outgoing, null);
           TextView message = (TextView)result.findViewById(R.id.message_text);
           message.setText(   getItem(position)  ); // get the string at position
           return result;

       }


       public long getId(int position){
               cursor.moveToPosition(position);
           return cursor.getLong(cursor.getColumnIndex(ChatDatabaseHelper.KEY_ID));
       }

    }

    protected void onDestroy() {
        super.onDestroy();
        db.close();
    }

    public static void moveCursor() {

        cursor = db.query(true, ChatDatabaseHelper.TABLE_NAME,
                new String[] { ChatDatabaseHelper.KEY_ID, ChatDatabaseHelper.KEY_MESSAGE},
                ChatDatabaseHelper.KEY_MESSAGE + " Not Null" , null, null, null, null, null);

        cursor.moveToFirst();

//
//        cursor = db.query(true, ChatDatabaseHelper.TABLE_NAME,
//                new String[]{ChatDatabaseHelper.KEY_ID,ChatDatabaseHelper.KEY_MESSAGE},
//                ChatDatabaseHelper.KEY_MESSAGE + "Not Null",null,null,null,null,null);
//                 cursor.moveToFirst();
        while (!cursor.isAfterLast()){
            list.add(cursor.getString(cursor.getColumnIndex(ChatDatabaseHelper.KEY_MESSAGE)));
            messageAdapter.notifyDataSetChanged();
            cursor.moveToNext();

        }
        messageAdapter.notifyDataSetChanged();
    }

}
