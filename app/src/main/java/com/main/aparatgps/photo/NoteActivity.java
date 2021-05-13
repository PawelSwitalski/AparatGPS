package com.main.aparatgps.photo;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.main.aparatgps.R;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class NoteActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        Intent intent = getIntent();

        //jeśli klikneliśmy utworzoną notatkę to tutaj wczyta jej nazwę do Stringa
        String nazwa = intent.getStringExtra("nazwa");

        Button zapiszNotatke = findViewById(R.id.saveNote);

        EditText text = findViewById(R.id.note);

        loadNote(nazwa, text);

        zapiszNotatke.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tekst = text.getText().toString();
                saveNote(nazwa, tekst);
                finish();
            }
        });

    }

    //zapisuje notatkę do pliku
    public void saveNote (String nazwa, String tekst){
        FileOutputStream fos = null;
        try {
            fos = openFileOutput(nazwa, MODE_PRIVATE);
            fos.write(tekst.getBytes());
            Context context = getApplicationContext();
            CharSequence text = "Zapisano notatkę";
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(fos==null){
                try{
                    fos.close();
                }
                catch (IOException e){
                    e.printStackTrace();
                }
            }
        }
    }

    //wczytuje tekst otworzonej przez nas notatki do pola EditText
    public void loadNote(String nazwa, EditText editText){
        FileInputStream fis = null;
        try {
            fis = openFileInput(nazwa);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String text;

            while ((text = br.readLine())!=null){
                sb.append(text).append("\n");
            }

            editText.setText(sb.toString());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(fis!=null){
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}