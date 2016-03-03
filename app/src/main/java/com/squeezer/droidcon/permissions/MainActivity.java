package com.squeezer.droidcon.permissions;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squeezer.droidcon.permissions.eventbus.MyEvents;
import com.squeezer.droidcon.permissions.eventbus.MyEvents.PlayerEvent;
import com.squeezer.droidcon.permissions.eventbus.PlayerService;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MainActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback,
        View.OnClickListener {

    private static final int REQUEST_APP_SETTINGS = 168;

    private View mLayout;


    private LinearLayout mTopLayout;
    private LinearLayout buttonsLayout;


    private static final String TAG = "recorder_micro";
    final private int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 124;

    //
    private boolean isRec = false;

    private MediaRecorder myRecorder;
    private ImageButton mBtnStart;
    private ImageButton mPauseButton;
    private Button mBtnOpenSettings;

    private ImageButton mPlayButton;
    private ImageButton mStopButton;

    private PlayerEvent mPlayerService;
    private TextView mMyText;

    private static final String[] requiredPermissions = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO
    };

    public static String pathFileRecorder = "Record/squeezerRecording.3gpp";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //showRootFiles();

        initViews();


        //initRecorder();
        initServicePlayer();

    }

    @Override
    protected void onResume() {
        super.onResume();

        if (hasPermissions(requiredPermissions)) {
            recorderStarted();
            initRecorder();
        } else {
            //Ask for permission: Type 1
            checkPermissions();
            //Ask for permission: type 2
            //recorderDenied();
        }
    }

    private void initServicePlayer() {

        mPlayerService = new PlayerEvent();
        Intent serviceIntent = new Intent(this,
                PlayerService.class);
        startService(serviceIntent);

        EventBus.getDefault().register(this);
    }

    private void initViews() {
        mLayout = findViewById(R.id.coordinatorLayout);

        mMyText = (TextView) findViewById(R.id.textView_second_fragment);
        mMyText.setText("Welcome");

        mBtnStart = (ImageButton) findViewById(R.id.player_rec);
        mBtnStart.setOnClickListener(this);

        mBtnOpenSettings = (Button) findViewById(R.id.button_setting);
        mBtnOpenSettings.setOnClickListener(this);


        mMyText.setVisibility(View.INVISIBLE);
        mBtnStart.setVisibility(View.INVISIBLE);
        mBtnOpenSettings.setVisibility(View.INVISIBLE);

        mPlayButton = (ImageButton) findViewById(R.id.player_play);
        mPlayButton.setOnClickListener(this);

        mPauseButton = (ImageButton) findViewById(R.id.player_pause);
        mPauseButton.setOnClickListener(this);

        mStopButton = (ImageButton) findViewById(R.id.player_stop);
        mStopButton.setOnClickListener(this);

        mTopLayout = (LinearLayout) findViewById(R.id.top_layout);
        buttonsLayout = (LinearLayout) findViewById(R.id.buttons_layout);


        mMyText.setVisibility(View.INVISIBLE);
        mMyText.setVisibility(View.INVISIBLE);
        mBtnStart.setVisibility(View.INVISIBLE);
        mBtnOpenSettings.setVisibility(View.GONE);
        buttonsLayout.setVisibility(View.GONE);
        mLayout.setBackgroundResource(R.color.colorPrimaryDark);

    }

    private void recorderStarted() {

        mMyText.setText(getResources().getString(R.string.message_permission_granted));
        mMyText.setVisibility(View.VISIBLE);
        mBtnStart.setVisibility(View.VISIBLE);
        mBtnOpenSettings.setVisibility(View.GONE);
        buttonsLayout.setVisibility(View.VISIBLE);

        mTopLayout.setBackgroundResource(R.drawable.recorder_background);

    }

    private void recorderDenied() {

        mMyText.setText(getResources().getString(R.string.message_permission_denied));
        mMyText.setVisibility(View.VISIBLE);
        mBtnStart.setVisibility(View.GONE);
        mBtnOpenSettings.setVisibility(View.VISIBLE);
        buttonsLayout.setVisibility(View.GONE);

        mLayout.setBackgroundResource(R.color.colorPrimaryDark);

    }


    private void initRecorder() {
        String outputFile = Environment.getExternalStorageDirectory().
                getAbsolutePath() + "/" + pathFileRecorder;

        myRecorder = new MediaRecorder();

        myRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        myRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        myRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        myRecorder.setOutputFile(outputFile);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.player_rec:
                recorder();
                break;
            case R.id.button_setting:
                goToSettings();
                break;
            case R.id.player_play:
                playPlayer();
                break;
            case R.id.player_pause:
                PausePlayer();
                break;
            case R.id.player_stop:
                stopPlayer();
                break;
            default:
                break;
        }

    }

    private void playPlayer() {


        mPlayerService.setStatus(PlayerService.MEDIA_PLAYER_CONTROL_START);

        mPlayButton.setEnabled(false);
        mStopButton.setEnabled(true);

        EventBus.getDefault().post(mPlayerService);
    }

    /*********************************************************************
     *
     */

    private void PausePlayer() {

        mPlayerService.setStatus(PlayerService.MEDIA_PLAYER_CONTROL_PAUSE);

        mPlayButton.setEnabled(true);
        mStopButton.setEnabled(false);

        EventBus.getDefault().post(mPlayerService);

    }

    private void stopPlayer() {

        mPlayerService.setStatus(PlayerService.MEDIA_PLAYER_CONTROL_STOP);

        mPlayButton.setEnabled(true);
        mStopButton.setEnabled(false);

        EventBus.getDefault().post(mPlayerService);
    }

    /*********************************************************************
     *
     */

    private void recorder() {

        if (isRec) {
            isRec = false;
            mPlayButton.setEnabled(true);
            mPauseButton.setEnabled(true);
            stopRecord();
        } else {
            isRec = true;
            mPlayButton.setEnabled(false);
            mPauseButton.setEnabled(false);
            startRecord();
        }
    }

    private void stopRecord() {
        try {
            myRecorder.stop();
            myRecorder.release();
            myRecorder = null;

            mMyText.setText(getResources().getString(R.string.message_stop_record));

            mPlayerService.setStatus(PlayerService.MEDIA_PLAYER_CONTROL_STOP_RECORD);
            EventBus.getDefault().post(mPlayerService);

            showMessage(getResources().getString(R.string.message_stop_record));

            stopPlayer();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (RuntimeException e) {
            e.printStackTrace();
        }

    }

    public void startRecord() {

        try {
            initRecorder();
            myRecorder.prepare();
            myRecorder.start();
            mMyText.setText(R.string.message_start_record);
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        showMessage(getResources().getString(R.string.message_start_record));

    }

    /*********************************************************************************
     *
     *********************************************************************************/

    private void checkPermissions() {
        List<String> permissionsNeeded = new ArrayList<String>();

        final List<String> permissionsList = new ArrayList<String>();
        if (!addPermission(permissionsList, Manifest.permission.WRITE_EXTERNAL_STORAGE))
            permissionsNeeded.add("WRITE_EXTERNAL_STORAGE");
        if (!addPermission(permissionsList, Manifest.permission.RECORD_AUDIO))
            permissionsNeeded.add("RECORD_AUDIO");

        if (permissionsList.size() > 0) {
            if (permissionsNeeded.size() > 0) {
                // Need Rationale
                String message = "You need to grant access to " + permissionsNeeded.get(0);
                for (int i = 1; i < permissionsNeeded.size(); i++)
                    message = message + ", " + permissionsNeeded.get(i);

                showMessageOKCancel(message,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(MainActivity.this, permissionsList.toArray(new String[permissionsList.size()]),
                                        REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
                            }
                        });
                return;
            }
            ActivityCompat.requestPermissions(MainActivity.this, permissionsList.toArray(new String[permissionsList.size()]),
                    REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
            return;
        }

        recorderStarted();
    }


    public boolean hasPermissions(@NonNull String... permissions) {
        for (String permission : permissions)
            if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(MainActivity.this, permission))
                return false;
        return true;
    }


    private void showMessage(String message) {

        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

    }


    private boolean addPermission(List<String> permissionsList, String permission) {
        if (ActivityCompat.checkSelfPermission(MainActivity.this, permission) != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(permission);
            // Check for Rationale Option
            if (!ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, permission))
                return false;
        }
        return true;
    }


    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(MainActivity.this)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, okListener)
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        recorderDenied();
                    }
                })
                .create()
                .show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS: {
                Map<String, Integer> perms = new HashMap<String, Integer>();
                // Initial
                perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.RECORD_AUDIO, PackageManager.PERMISSION_GRANTED);
                // Fill with results
                for (int i = 0; i < permissions.length; i++)
                    perms.put(permissions[i], grantResults[i]);
                // Check for ACCESS_FINE_LOCATION
                if (perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                        && perms.get(Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                    // All Permissions Granted
                    recorderStarted();
                } else {
                    recorderDenied();
                }
            }
            break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void goToSettings() {
        Intent myAppSettings = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + getPackageName()));
        myAppSettings.addCategory(Intent.CATEGORY_DEFAULT);
        myAppSettings.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivityForResult(myAppSettings, REQUEST_APP_SETTINGS);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.e(TAG, "On result, " + requestCode + "," + resultCode);
        if (resultCode == REQUEST_APP_SETTINGS) {

            if (hasPermissions(requiredPermissions)) {
                recorderStarted();
                showMessage(getResources().getString(R.string.message_access_recorder_authorized));
                initRecorder();
            } else {
                recorderDenied();
                showMessage(getResources().getString(R.string.message_access_recorder_denied));
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Subscribe
    public void onEvent(MyEvents.UpdateTitleEvent event) {

        showMessage(event.getTitle());

        switch (event.getStatus()) {
            case PlayerService.MEDIA_PLAYER_SERVICE_STARTED:
                //target.doBind();
                break;
            case PlayerService.MEDIA_PLAYER_CONTROL_START:
                updatePlayButton();
                break;
            case PlayerService.MEDIA_PLAYER_CONTROL_PAUSE:
                updatePauseButton();
                break;
            case PlayerService.MEDIA_PLAYER_CONTROL_STOP:
                stopPerformed();
                break;
        }
    }

    private void updatePlayButton() {
        mPlayButton.setBackgroundResource(R.drawable.player_play_on);
    }

    private void updatePauseButton() {
        //mPlayButton.setBackgroundResource(R.drawable.player_play);
    }

    private void stopPerformed() {
        //mPlayButton.setBackgroundResource(R.drawable.play_pause_on);
    }
}
