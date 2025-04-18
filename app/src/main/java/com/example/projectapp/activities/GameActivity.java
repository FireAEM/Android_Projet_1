package com.example.projectapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.projectapp.R;
import com.example.projectapp.engine.GameDataManager;
import com.example.projectapp.engine.GameEngine;
import com.example.projectapp.engine.StoryEngine;
import com.example.projectapp.model.StoryNode;

import java.util.List;

public class GameActivity extends AppCompatActivity {

    private TextView textViewLocation, textViewLife, textViewMoney, textViewContext;
    private LinearLayout layoutChoices;
    private Button buttonInventory;
    private GameEngine gameEngine;
    private StoryEngine storyEngine;
    private GameDataManager gameDataManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        gameEngine = GameEngine.getInstance(this);
        storyEngine  = StoryEngine.getInstance(this);
        gameDataManager = GameDataManager.getInstance(this);

        textViewLocation = findViewById(R.id.textViewLocation);
        textViewLife     = findViewById(R.id.textViewLife);
        textViewMoney    = findViewById(R.id.textViewMoney);
        textViewContext  = findViewById(R.id.textViewContext);
        layoutChoices    = findViewById(R.id.layoutChoices);
        buttonInventory  = findViewById(R.id.buttonInventory);

        updateUI();

        buttonInventory.setOnClickListener(v -> {
            Intent intent = new Intent(GameActivity.this, InventoryActivity.class);
            startActivity(intent);
        });

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override public void handleOnBackPressed() {
                Intent intent = new Intent(GameActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
            }
        });
    }

    private void updateUI() {
        textViewLocation.setText(gameEngine.getCurrentLocation());
        textViewLife.setText("PV: " + gameDataManager.getPlayerLife());
        textViewMoney.setText("Or: " + gameDataManager.getPlayerMoney());
        textViewContext.setText(gameEngine.getCurrentContext());
        generateChoiceButtons();
        Log.d("GameActivity", "updateUI() appelé pour : " + gameEngine.getCurrentNode().getTitle());
    }

    private void generateChoiceButtons() {
        layoutChoices.removeAllViews();
        storyEngine.addEndingChoice();

        List<StoryNode> choices = storyEngine.getAvailableChoices();
        for (StoryNode node : choices) {
            Button choiceButton = new Button(this);
            choiceButton.setText(node.getTitle());
            choiceButton.setOnClickListener(v -> {
                Log.d("GameActivity", "Clic choix : " + node.getTitle());
                int result = storyEngine.processChoice(node.getTitle(), this);
                Log.d("GameActivity", "processChoice() -> " + result);
                if (result == GameEngine.RESULT_COMBAT) {
                    activityLauncher.launch(new Intent(GameActivity.this, CombatActivity.class));
                } else if (result == GameEngine.RESULT_MERCHANT) {
                    activityLauncher.launch(new Intent(GameActivity.this, PurchaseActivity.class));
                } else {
                    updateUI();
                }
            });
            layoutChoices.addView(choiceButton);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        gameEngine.saveGame();
    }

    @Override
    protected void onStop() {
        super.onStop();
        gameEngine.saveGame();
    }

    private final ActivityResultLauncher<Intent> activityLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == RESULT_OK) {
                            updateUI();
                        }
                    });
}
