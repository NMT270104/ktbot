package com.example.iot_app.ui.handed_controller;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;

import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleWriteCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;
import com.clj.fastble.utils.HexUtil;
import com.example.iot_app.R;
import com.example.iot_app.ui.base.BaseActivity;
import com.example.iot_app.ui.blockly_activity.BlocklyActivity;
import com.example.iot_app.ui.blockly_activity.TypeConnected;
import com.example.iot_app.ui.bluetooth.AppBluetoothDevice;
import com.example.iot_app.ui.bluetooth.ble.BleActivity;
import com.example.iot_app.ui.bluetooth.hc05.BlueToothActivity;
import com.example.iot_app.ui.main.MainActivity;
import com.example.iot_app.utils.ConvertUtils;
import com.iot.blockly.RainbowHatBlocklyBaseActivity;
import com.shashank.sony.fancygifdialoglib.FancyGifDialog;
import com.shashank.sony.fancygifdialoglib.FancyGifDialogListener;

import java.io.IOException;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.github.controlwear.virtual.joystick.android.JoystickView;

import static com.example.iot_app.ui.blockly_activity.BlocklyActivity.WRITE_CHAR_UUID;
import static com.example.iot_app.ui.blockly_activity.BlocklyActivity.typeConnected;

public class HandedControllerActivity extends BaseActivity {
    private static final int PERMISSION_REQUEST_CODE = 199;
    public static final String HANDEDACTIVITY = "HandedControllerActivity";
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    public static final String KEY_DATA = "key_data";

     @BindView(R.id.gamepad_moving)
     JoystickView mJoystickView;
    //bluetooth
    String address = null;
    public  BleDevice mBleDevice;
    private ProgressDialog progress ;
    BluetoothAdapter myBluetooth = null;
    BluetoothSocket btSocket = null;
    private boolean isBtConnected = false;
    public static BluetoothGattService mService;
    public String hc05Address = null;
    Menu optionsMenu;
    //menu
    MenuItem bluetoothMenu;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.handed_controller_activity2);
        ButterKnife.bind(this);
        ConvertUtils.requestBlePermissions(this, PERMISSION_REQUEST_CODE);
        checkingBluetooth();
        setupGamePad();
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        try {
            //hc05
            Intent newint = getIntent();
            hc05Address = newint.getStringExtra(BlueToothActivity.EXTRA_ADDRESS);

            //ble
            AppBluetoothDevice.setmBleDevice(getIntent().getParcelableExtra(KEY_DATA));
            mBleDevice = AppBluetoothDevice.getmBleDevice();
            if (mBleDevice != null) {
                writeBle("ff550400010103");
            }

        } catch (Exception e) {
        }

        if (hc05Address != null && !hc05Address.equals("")) {
            new ConnectBT().execute();
            typeConnected = TypeConnected.HC05;
            Toast.makeText(this, "HC05 Connected Successful", Toast.LENGTH_SHORT);
            Log.d("TAG", "onCreateContentView: " + typeConnected);
        } else {
            if (AppBluetoothDevice.getmBleDevice() != null) {
                typeConnected = TypeConnected.HM10;
                Toast.makeText(this, "HM10 Connected Successful", Toast.LENGTH_SHORT);
                Log.d("TAG", "onCreateContentView: " + typeConnected);
            } else {
                showConnectDialog();
            }
    }
    }

    public static void writeBle(String data) {

        BleManager.getInstance().write(
                AppBluetoothDevice.getmBleDevice(),
                "0000ffe0-0000-1000-8000-00805f9b34fb",
                WRITE_CHAR_UUID,
                HexUtil.hexStringToBytes(data),
                new BleWriteCallback() {
                    @Override
                    public void onWriteSuccess(final int current, final int total, final byte[] justWrite) {
                        Log.d("TAG", "onWriteSuccess: " + data);
                    }

                    @Override
                    public void onWriteFailure(final BleException exception) {
                        Log.d("TAG", "onWriteFailure: " + exception.getDescription());
                    }
                });

    }





    private void setupGamePad(){
        mJoystickView.setOnMoveListener(new JoystickView.OnMoveListener() {
            @Override
            public void onMove(int angle, int strength) {
                Log.d("TAG", "onMove: "+ angle);
                Log.d("TAG", "onstrength: "+strength);
                if(hc05Address != null || mBleDevice != null){
                    if(angle == 0 ){
                       // writeBle("ff550700020500000000");
                        if (strength > 50){
                            if (BlocklyActivity.typeConnected == TypeConnected.HC05) {
                                Log.d("TAG", "onMove: x111111xxxxxx");
                                hc05Send("ff550700020500000000");
                            }
                            if (BlocklyActivity.typeConnected == TypeConnected.HM10) {
                                Log.d("TAG", "onMove: x111111xxxxxx");
                                writeBle("ff550700020500000000");
                                Log.d("TAG", "onMove: xxxxxxx");
                            }
//                            hc05Send("ff550700020500000000");
                        }
                        else{
                            if (BlocklyActivity.typeConnected == TypeConnected.HC05) {
                                hc05Send("ff550700020500000000");
                            }
                            if (BlocklyActivity.typeConnected == TypeConnected.HM10) {
                                writeBle("ff550700020500000000");
                            }
//                            hc05Send("ff550700020500000000");
                        }

                    }

                    if(45 <= angle && angle <= 135){
                        //move forward
                       // writeBle("ff550700020501ffff00");
                        if (strength > 50){
                            if (BlocklyActivity.typeConnected == TypeConnected.HC05) {
                                hc05Send("ff550700020501ffff00");
                            }
                            if (BlocklyActivity.typeConnected == TypeConnected.HM10) {
                                writeBle("ff550700020501ffff00");
                            }
//                            hc05Send("ff550700020501ffff00");
                        }
                        else{
                            if (BlocklyActivity.typeConnected == TypeConnected.HC05) {
                                hc05Send("ff55070002059cff6400");
                            }
                            if (BlocklyActivity.typeConnected == TypeConnected.HM10) {
                                writeBle("ff55070002059cff6400");
                            }
//                            hc05Send("ff55070002059cff6400");
                        }
                    }

                    if(135 < angle && angle <= 225){
                        if (strength > 50){
                            if (BlocklyActivity.typeConnected == TypeConnected.HC05) {
                                hc05Send("ff5507000205ff00ff00");
                            }
                            if (BlocklyActivity.typeConnected == TypeConnected.HM10) {
                                writeBle("ff5507000205ff00ff00");
                            }
//                            hc05Send("ff5507000205ff00ff00");
                        }
                        else{
                            if (BlocklyActivity.typeConnected == TypeConnected.HC05) {
                                hc05Send("ff550700020564006400");
                            }
                            if (BlocklyActivity.typeConnected == TypeConnected.HM10) {
                                writeBle("ff550700020564006400");
                            }
//                            hc05Send("ff550700020564006400");
                        }
                        //TL
//                        writeBle("ff5507000205ff00ff00");
                        Log.d("TAG", "onMove: TL");
                    }

                    if(225 < angle && angle <= 315){
                        if (strength > 50){
                            if (BlocklyActivity.typeConnected == TypeConnected.HC05) {
                                hc05Send("ff5507000205ff0001ff");
                            }
                            if (BlocklyActivity.typeConnected == TypeConnected.HM10) {
                                writeBle("ff5507000205ff0001ff");
                            }
//                            hc05Send("ff5507000205ff0001ff");
                        }
                        else{
                            if (BlocklyActivity.typeConnected == TypeConnected.HC05) {
                                hc05Send("ff550700020564009cff");
                            }
                            if (BlocklyActivity.typeConnected == TypeConnected.HM10) {
                                writeBle("ff550700020564009cff");
                            }
//                            hc05Send("ff550700020564009cff");
                        }
                        //back
//                        writeBle("ff5507000205ff0001ff");
                        Log.d("TAG", "onMove: MB");
                    }

                    if(315 <= angle && angle <= 360){
                        if (strength > 50){
                            if (BlocklyActivity.typeConnected == TypeConnected.HC05) {
                                hc05Send("ff550700020501ff01ff");
                            }
                            if (BlocklyActivity.typeConnected == TypeConnected.HM10) {
                                writeBle("ff550700020501ff01ff");
                            }
//                            hc05Send("ff550700020501ff01ff");
                        }
                        else{
                            if (BlocklyActivity.typeConnected == TypeConnected.HC05) {
                                hc05Send("ff55070002059cff9cff");
                            }
                            if (BlocklyActivity.typeConnected == TypeConnected.HM10) {
                                writeBle("ff55070002059cff9cff");
                            }
//                            hc05Send("ff55070002059cff9cff");
                        }
                        //right
//                        writeBle("ff550700020501ff01ff");
                        Log.d("TAG", "onMove: TR");
                    }

                    if(0 < angle && angle < 45){
                        if (strength > 50){
                            if (BlocklyActivity.typeConnected == TypeConnected.HC05) {
                                hc05Send("ff550700020501ff01ff");
                            }
                            if (BlocklyActivity.typeConnected == TypeConnected.HM10) {
                                writeBle("ff550700020501ff01ff");
                            }
//                            hc05Send("ff550700020501ff01ff");
                        }
                        else{
                            if (BlocklyActivity.typeConnected == TypeConnected.HC05) {
                                hc05Send("ff55070002059cff9cff");
                            }
                            if (BlocklyActivity.typeConnected == TypeConnected.HM10) {
                                writeBle("ff55070002059cff9cff");
                            }
//                            hc05Send("ff55070002059cff9cff");
                        }
//                        writeBle("ff550700020501ff01ff");
                        Log.d("TAG", "onMove: TR");
                    }
                }

            }
        });
    }

    private void checkingBluetooth(){
        myBluetooth = BluetoothAdapter.getDefaultAdapter();

        if(myBluetooth == null)
        {
            Toast.makeText(getApplicationContext(), "Bluetooth Device Not Available", Toast.LENGTH_LONG).show();
            finish();
        }
        else if(!myBluetooth.isEnabled())
        {
            Intent turnBTon = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(turnBTon,1);
        }
    }

    private void  showConnectDialog(){
        new FancyGifDialog.Builder(this)
                .setTitle(getResources().getString(R.string.choosing_connected))
                .setNegativeBtnText(getResources().getString(R.string.cancel))
                .setPositiveBtnBackground(R.color.cfdialog_positive_button_pressed_color)
                .setPositiveBtnText(getResources().getString(R.string.ok))
                .setGifResource(R.drawable.bluetooth)
                .setNegativeBtnBackground(R.color.cfdialog_negative_button_color)
                .isCancellable(true)
                .OnPositiveClicked(new FancyGifDialogListener() {
                    @Override
                    public void OnClick() {
                        //hc05
                        Intent intent = new Intent(HandedControllerActivity.this, BlueToothActivity.class) ;
                        intent.putExtra("activity",HANDEDACTIVITY);
                        startActivity(intent);
                        //ble
//                        Intent intent = new Intent(HandedControllerActivity.this, BleActivity.class);
//                        intent.putExtra("activity", HANDEDACTIVITY);
//                        startActivity(intent);
                        Toast.makeText(HandedControllerActivity.this, "Ok", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .OnNegativeClicked(new FancyGifDialogListener() {
                    @Override
                    public void OnClick() {
                        Toast.makeText(HandedControllerActivity.this,getResources().getString(R.string.cancel),Toast.LENGTH_SHORT).show();
                    }
                })
                .build();
    }


    private void  showDisConnectDialog(){

        new FancyGifDialog.Builder(this)
                .setTitle("You realy want to disconnect device")
                .setNegativeBtnText(getResources().getString(R.string.cancel))
                .setPositiveBtnBackground(R.color.cfdialog_positive_button_pressed_color)
                .setPositiveBtnText(getResources().getString(R.string.ok))
                .setGifResource(R.drawable.bluetooth_sleep)
                .setNegativeBtnBackground(R.color.cfdialog_negative_button_color)
                .isCancellable(true)
                .OnPositiveClicked(new FancyGifDialogListener() {
                    @Override
                    public void OnClick() {     
                       hc05Address = null;
                        try {
                            btSocket.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        bluetoothMenu.setIcon(R.drawable.black_bluetooth);
                    }
                })
                .OnNegativeClicked(new FancyGifDialogListener() {
                    @Override
                    public void OnClick() {
                        Toast.makeText(HandedControllerActivity.this,getResources().getString(R.string.cancel),Toast.LENGTH_SHORT).show();
                    }
                })
                .build();
    }
    
    public  void replaceFragment(int containerId, Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(containerId, fragment)
                .addToBackStack(fragment.getTag())
                .commit();
    }

    private class  ConnectBT extends AsyncTask<Void, Void, Void>  // UI thread
    {
        private boolean ConnectSuccess = true; //if it's here, it's almost connected

        @Override
        protected void onPreExecute()
        {
            progress = ProgressDialog.show(HandedControllerActivity.this, "Connecting...", getResources().getString(R.string.wait));  //show a progress dialog
        }

        @Override
        protected Void doInBackground(Void... devices) //while the progress dialog is shown, the connection is done in background
        {
            try
            {
                if (btSocket == null || !isBtConnected)
                {
                    myBluetooth = BluetoothAdapter.getDefaultAdapter();//get the mobile bluetooth device
                    BluetoothDevice dispositivo = myBluetooth.getRemoteDevice(hc05Address);//connects to the device's address and checks if it's available
                    btSocket = dispositivo.createInsecureRfcommSocketToServiceRecord(myUUID);//create a RFCOMM (SPP) connection
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    btSocket.connect();//start connection
                }
            }
            catch (IOException e)
            {
                ConnectSuccess = false;//if the try failed, you can check the exception here
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void result) //after the doInBackground, it checks if everything went fine
        {
            super.onPostExecute(result);

            if (!ConnectSuccess)
            {
                Toast.makeText(getApplicationContext(),"Connection Failed. Is it a SPP Bluetooth? Try again.", Toast.LENGTH_SHORT).show();
                finish();
            }
            else
            {
                Toast.makeText(getApplicationContext(),"Connected", Toast.LENGTH_SHORT).show();
                bluetoothMenu.setIcon(R.drawable.white_bluetooth);
                isBtConnected = true;
              //  new BlocklyActivity.ReceiveData().start();
            }
            progress.dismiss();
        }
    }

    public void hc05Send(String s){
        if (btSocket!=null)
        {
            try
            {
                btSocket.getOutputStream().write(HexUtil.hexStringToBytes(s));
            }
            catch (IOException e)
            {
                showDisConnectDialog();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.controller_menu, menu);
        optionsMenu = menu;
        bluetoothMenu = optionsMenu.findItem(R.id.controler_bluetooth);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id == android.R.id.home){
            onBack();
        }  else
            if(id == R.id.controler_bluetooth){
                if(hc05Address == null){
                    showConnectDialog();
                }
                else{
                    showDisConnectDialog();
                }

            }
        return super.onOptionsItemSelected(item);
    }


    public void onBack(){
        Intent intent = new Intent(HandedControllerActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

}
