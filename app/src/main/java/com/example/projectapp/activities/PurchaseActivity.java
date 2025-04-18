package com.example.projectapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.projectapp.R;
import com.example.projectapp.engine.GameDataManager;
import com.example.projectapp.engine.GameEngine;
import com.example.projectapp.engine.PurchaseEngine;
import com.example.projectapp.model.item.Item;

import java.util.List;

public class PurchaseActivity extends AppCompatActivity {

    private TextView textViewHeader, textViewLife, textViewMoney;
    private LinearLayout layoutItems;
    private Button buttonInventory, buttonContinue;
    private GameEngine gameEngine;
    private GameDataManager gameDataManager;
    private PurchaseEngine purchaseEngine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase);

        textViewHeader = findViewById(R.id.textViewHeader);
        textViewLife = findViewById(R.id.textViewLife);
        textViewMoney = findViewById(R.id.textViewMoney);
        layoutItems = findViewById(R.id.layoutItems);
        buttonInventory = findViewById(R.id.buttonInventory);
        buttonContinue = findViewById(R.id.buttonContinue);

        gameEngine = GameEngine.getInstance(this);
        gameDataManager = GameDataManager.getInstance(this);
        purchaseEngine = PurchaseEngine.getInstance(this);

        textViewHeader.setText("Achat");
        updateStats();

        populateItemsForSale();

        // Ouvrir InventoryActivity par-dessus PurchaseActivity
        buttonInventory.setOnClickListener(v -> {
            Intent intent = new Intent(PurchaseActivity.this, InventoryActivity.class);
            startActivity(intent);
        });

        buttonContinue.setOnClickListener(v -> {
            // Continuer l'histoire après achat
            purchaseEngine.continueAfterPurchase();
            finish();
        });

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                new AlertDialog.Builder(PurchaseActivity.this)
                        .setTitle("Quitter le marchand")
                        .setMessage("Voulez-vous vraiment quitter le marchand ?")
                        .setPositiveButton("Oui", (dialog, which) -> {
                            purchaseEngine.continueAfterPurchase();
                            finish();
                        })
                        .setNegativeButton("Non", (dialog, which) -> dialog.dismiss())
                        .show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Mettre à jour l'or et la vie si modifiés en InventoryActivity
        updateStats();
    }

    private void updateStats() {
        textViewLife.setText("PV: " + gameDataManager.getPlayerLife());
        textViewMoney.setText("Or: " + gameDataManager.getPlayerMoney());
    }

    private void populateItemsForSale() {
        List<Item> itemsForSale = purchaseEngine.getItemsForSale();
        layoutItems.removeAllViews();
        for (Item item : itemsForSale) {
            Button itemButton = new Button(this);
            itemButton.setText(item.getName() + " – " + item.getPrice() + " or");
            itemButton.setOnClickListener(v -> {
                if (purchaseEngine.attemptPurchase(item)) {
                    updateStats();
                }
            });
            layoutItems.addView(itemButton);
        }
    }
}
