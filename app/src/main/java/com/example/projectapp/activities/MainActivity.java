package com.example.projectapptest.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import com.example.interactivestory.R;

public class MainActivity extends AppCompatActivity {

    private Button buttonStartContinue;
    private static final String PREFS_NAME = "gamePrefs";
    private static final String KEY_GAME_SAVED = "gameSaved";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonStartContinue = findViewById(R.id.buttonStartContinue);

        // Vérification de l'existence d'une partie sauvegardée
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean gameSaved = prefs.getBoolean(KEY_GAME_SAVED, false);
        buttonStartContinue.setText(gameSaved ? "Continuer" : "Commencer");

        buttonStartContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Lancer l'activité de jeu (chargement de la sauvegarde ou nouvelle partie)
                Intent intent = new Intent(MainActivity.this, GameActivity.class);
                startActivity(intent);
            }
        });
    }
}