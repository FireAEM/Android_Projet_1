package com.example.projectapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import com.example.projectapp.R;
import com.example.projectapp.engine.GameDataManager;
import com.example.projectapp.engine.GameEngine;
import com.example.projectapp.engine.CombatEngine;

public class CombatActivity extends AppCompatActivity {

    private TextView textViewHeader, textViewEnemyInfo, textViewContext, textViewLife, textViewMoney;
    private Button buttonAttack, buttonBlock;
    private GameEngine gameEngine;
    private CombatEngine combatEngine;
    private GameDataManager gameDataManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_combat);

        textViewHeader = findViewById(R.id.textViewHeader);
        textViewEnemyInfo = findViewById(R.id.textViewEnemyInfo);
        textViewContext = findViewById(R.id.textViewContext);
        textViewLife = findViewById(R.id.textViewLife);
        textViewMoney = findViewById(R.id.textViewMoney);
        buttonAttack = findViewById(R.id.buttonAttack);
        buttonBlock = findViewById(R.id.buttonBlock);

        gameEngine = GameEngine.getInstance(this);
        combatEngine = CombatEngine.getInstance(this);
        gameDataManager = GameDataManager.getInstance(this);
        combatEngine.spawnCurrentEnemy();
        updateCombatUI();

        buttonAttack.setOnClickListener(v -> {
            combatEngine.playerAttack();
            updateCombatUI();
            resolveCombatEnd();
        });

        buttonBlock.setOnClickListener(v -> {
            combatEngine.playerBlock();
            updateCombatUI();
            resolveCombatEnd();
        });

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Afficher un message au joueur pour l'informer qu'il ne peut pas quitter le combat
                Toast.makeText(CombatActivity.this, "Vous ne pouvez pas quitter un combat !", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateCombatUI() {
        Log.d("Combat activity", "updateCombatUI");
        textViewHeader.setText("Combat !");
        textViewEnemyInfo.setText(combatEngine.getEnemyInfo());
        textViewContext.setText(combatEngine.getCombatContext());
        textViewLife.setText("PV: " + gameDataManager.getPlayerLife());
        textViewMoney.setText("Or: " + gameDataManager.getPlayerMoney());
    }

    private void resolveCombatEnd() {
        if (combatEngine.checkGameOver()) {
            Intent resultIntent = new Intent();
            setResult(RESULT_OK, resultIntent);
            finish();
        } else if (combatEngine.checkVictory()) {
            Intent resultIntent = new Intent();
            setResult(RESULT_OK, resultIntent);
            finish();
        }
    }
}
