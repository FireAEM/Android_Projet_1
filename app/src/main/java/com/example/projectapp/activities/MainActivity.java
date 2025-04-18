package com.example.projectapp.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import com.example.projectapp.R;

import com.example.projectapp.engine.GameDataManager;
import com.example.projectapp.engine.GameEngine;
import com.example.projectapp.engine.StoryEngine;
import com.example.projectapp.engine.CombatEngine;
import com.example.projectapp.engine.PurchaseEngine;

public class MainActivity extends AppCompatActivity {

    private Button buttonStartContinue, buttonNewGame;
    private static final String PREFS_NAME = "game_save";
    private static final String KEY_PLAYER = "player"; // pour vérifier la présence d'une sauvegarde
    private GameDataManager gameDataManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonStartContinue = findViewById(R.id.buttonStartContinue);
        buttonNewGame = findViewById(R.id.buttonNewGame);

        // Vérifie l'existence d'une sauvegarde
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        boolean gameSaved = prefs.contains(KEY_PLAYER);
        buttonStartContinue.setText(gameSaved ? "Continuer" : "Commencer");

        buttonStartContinue.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, GameActivity.class);
            startActivity(intent);
        });

        buttonNewGame.setOnClickListener(v -> {
            // Réinitialiser la sauvegarde
            SharedPreferences.Editor editor = prefs.edit();
            editor.clear();
            editor.apply();
            // Réinitialiser les engines (on crée une nouvelle instance en réinitialisant le singleton)
            GameEngine.resetInstance();
            StoryEngine.resetInstance();
            CombatEngine.resetInstance();
            PurchaseEngine.resetInstance();
            GameDataManager.resetPlayer();
            GameDataManager.resetInstance();

            Intent intent = new Intent(MainActivity.this, GameActivity.class);
            startActivity(intent);
            finish();
        });
    }
}
