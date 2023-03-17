package com.jdgames.notes;

import static com.jdgames.notes.Config.sharedPreferencesName;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class MainActivity extends AppCompatActivity {

    static ArrayList<String> notesList = new ArrayList<>();
    static ArrayList<String> notesListView = new ArrayList<>();
    static ArrayAdapter<String> arrayAdapter;
    private AdView adView;
    private InterstitialAd interstitialAd = null;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item.getItemId() == R.id.addNote) {
            Intent intent = new Intent(MainActivity.this, ShowNote.class);
            startActivity(intent);
            return true;
        }
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences sharedPreferences = this.getSharedPreferences(sharedPreferencesName, Context.MODE_PRIVATE);
        try {
            notesList = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("notes", ObjectSerializer.serialize(new ArrayList<String>())));
            notesListView = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("notes", ObjectSerializer.serialize(new ArrayList<String>())));
            assert notesListView != null;
            for (int i = 0; i < notesListView.size(); i++) {
                if (notesListView.get(i).length() > 30) {
                    notesListView.set(i, notesListView.get(i).substring(0, 25) + "...");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, notesListView);

        ListView listView = findViewById(R.id.listView);
        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(MainActivity.this, ShowNote.class);
            intent.putExtra("position", position);
            startActivity(intent);
        });
        listView.setOnItemLongClickListener((parent, view, position, id) -> {
            loadInterstitialAd();
            new MaterialAlertDialogBuilder(MainActivity.this)
                    .setTitle("Delete Note")
                    .setMessage("Do you want to Delete this Note?")
                    .setPositiveButton("Delete", (dialogInterface, i) -> {
                        notesList.remove(position);
                        notesListView.remove(position);
                        try {
                            sharedPreferences.edit().putString("notes", ObjectSerializer.serialize(MainActivity.notesList)).apply();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        arrayAdapter.notifyDataSetChanged();
                        interstitialAd.show(MainActivity.this);
                    })
                    .setOnCancelListener(dialog -> {
                        if (interstitialAd != null) {
                            interstitialAd.show(MainActivity.this);
                        }
                    })
                    .setOnDismissListener(dialog -> {
                        if (interstitialAd != null) {
                            interstitialAd.show(MainActivity.this);
                        }
                    })
                    .setNegativeButton("Cancel", (dialog, which) -> {
                        if (interstitialAd != null) {
                            interstitialAd.show(MainActivity.this);
                        }
                    })
                    .show();
            return true;
        });

        MobileAds.initialize(this);

        AdRequest adRequest = new AdRequest.Builder().build();
        adView = findViewById(R.id.ad_view);
        adView.loadAd(adRequest);
    }

    public void loadInterstitialAd() {
        AdRequest adRequest = new AdRequest.Builder().build();
        InterstitialAd.load(this, getResources().getString(R.string.interstitial_ad_unit_id), adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd mInterstitialAd) {
                        interstitialAd = mInterstitialAd;
                        Log.i("TAG", "onAdLoaded");
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        Log.d("TAG", loadAdError.toString());
                        interstitialAd = null;
                    }
                });
    }

    @Override
    public void onPause() {
        if (adView != null) {
            adView.pause();
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (adView != null) {
            adView.resume();
        }
    }

    @Override
    public void onDestroy() {
        if (adView != null) {
            adView.destroy();
        }
        super.onDestroy();
    }
}