package com.jdgames.notes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.InstallStateUpdatedListener;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.InstallStatus;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.tasks.Task;
import com.jdgames.notes.Database.DatabaseClient;
import com.jdgames.notes.Entity.Notes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private AdView adView;
    private InterstitialAd interstitialAd = null;

    List<NoteParse> notesArrayList = new ArrayList<>();

    AppUpdateManager appUpdateManager;
    private InstallStateUpdatedListener installStateUpdatedListener;
    public static int FLEXIBLE_APP_UPDATE_REQ_CODE = 20202;
    public static int IMMEDIATE_APP_UPDATE_REQ_CODE = 10101;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item.getItemId() == R.id.about) {
            Toast.makeText(this, "Developed by JD with ❤️", Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }

    RecyclerView recyclerView;
    NotesAdapter notesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);

        notesArrayList.add(new NoteParse(-1, "", ""));
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(staggeredGridLayoutManager);
        notesAdapter = new NotesAdapter(MainActivity.this, notesArrayList);
        recyclerView.setAdapter(notesAdapter);

        ItemClickSupport.addTo(recyclerView).setOnItemClickListener((recyclerView, position, v) -> {
            Intent intent = new Intent(MainActivity.this, ShowNote.class);
            intent.putExtra("id", notesArrayList.get(position).getId());
            startActivity(intent);
        });

        ItemClickSupport.addTo(recyclerView).setOnItemLongClickListener((recyclerView, position, v) -> {
            if (notesArrayList.get(position).getId() != -1) {
                loadInterstitialAd();
                new MaterialAlertDialogBuilder(MainActivity.this)
                        .setTitle("Delete Note")
                        .setMessage("Do you want to Delete this Note?")
                        .setPositiveButton("Delete", (dialogInterface, i) -> {
                            DatabaseClient.getInstance(getApplicationContext())
                                    .getAppDatabase().notesDao().deleteNoteWithID(notesArrayList.get(position).getId());
                            notesArrayList.remove(position);
                            notesAdapter.notifyDataSetChanged();
                            if (interstitialAd != null) {
                                interstitialAd.show(MainActivity.this);
                            }
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
            }

            return true;
        });

        MobileAds.initialize(this);

        AdRequest adRequest = new AdRequest.Builder().build();
        adView = findViewById(R.id.ad_view);
        adView.loadAd(adRequest);

        appUpdateManager = AppUpdateManagerFactory.create(getApplicationContext());
        installStateUpdatedListener = state -> {
            if (state.installStatus() == InstallStatus.DOWNLOADED) {
                popupSnackBarForCompleteUpdate();
            } else if (state.installStatus() == InstallStatus.INSTALLED) {
                removeInstallStateUpdateListener();
            }
        };
        appUpdateManager.registerListener(installStateUpdatedListener);
        checkUpdate();
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
    public void onResume() {
        super.onResume();

        LoadNotes();

        if (adView != null) {
            adView.resume();
        }
    }

    public void LoadNotes() {
        notesArrayList.clear();
        List<Notes> notesList = DatabaseClient.getInstance(getApplicationContext())
                .getAppDatabase().notesDao().getAll();
        for (Notes note : notesList) {
            notesArrayList.add(new NoteParse(note.getId(), note.getTitle(), note.getSubtitle()));
        }
        Collections.reverse(notesArrayList);
        notesArrayList.add(new NoteParse(-1, "", ""));
        notesAdapter.notifyDataSetChanged();
    }

    private void checkUpdate() {
        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();
        appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                try {
                    appUpdateManager.startUpdateFlowForResult(appUpdateInfo, AppUpdateType.IMMEDIATE, this, IMMEDIATE_APP_UPDATE_REQ_CODE);
                } catch (IntentSender.SendIntentException e) {
                    e.printStackTrace();
                }
            } else if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)) {
                try {
                    appUpdateManager.startUpdateFlowForResult(appUpdateInfo, AppUpdateType.FLEXIBLE, this, FLEXIBLE_APP_UPDATE_REQ_CODE);
                } catch (IntentSender.SendIntentException e) {
                    e.printStackTrace();
                }
            } else if (appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                try {
                    appUpdateManager.startUpdateFlowForResult(appUpdateInfo, AppUpdateType.IMMEDIATE, this, IMMEDIATE_APP_UPDATE_REQ_CODE);
                } catch (IntentSender.SendIntentException e) {
                    e.printStackTrace();
                }
            } else if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
                popupSnackBarForCompleteUpdate();
            }
        });
    }

    private void popupSnackBarForCompleteUpdate() {
        Snackbar.make(findViewById(android.R.id.content).getRootView(), "New app is ready!", Snackbar.LENGTH_INDEFINITE)
                .setAction("Install", view -> {
                    if (appUpdateManager != null) {
                        appUpdateManager.completeUpdate();
                    }
                })
                .setActionTextColor(getResources().getColor(R.color.colorPrimary))
                .show();
    }

    private void removeInstallStateUpdateListener() {
        if (appUpdateManager != null) {
            appUpdateManager.unregisterListener(installStateUpdatedListener);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        removeInstallStateUpdateListener();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMMEDIATE_APP_UPDATE_REQ_CODE) {
            if (resultCode == RESULT_CANCELED) {
                Toast.makeText(getApplicationContext(), "Update canceled!", Toast.LENGTH_LONG).show();
            } else if (resultCode == RESULT_OK) {
                Toast.makeText(getApplicationContext(), "Update success!", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getApplicationContext(), "Update Failed!", Toast.LENGTH_LONG).show();
                checkUpdate();
            }
        }
    }

    @Override
    public void onPause() {
        if (adView != null) {
            adView.pause();
        }
        super.onPause();
    }

    @Override
    public void onDestroy() {
        if (adView != null) {
            adView.destroy();
        }
        super.onDestroy();
    }
}