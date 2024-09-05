package com.example.iot_app.ui.blockly_activity.webview;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.Toast;

import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleWriteCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;
import com.example.iot_app.ui.blockly_activity.BlocklyActivity;
import com.example.iot_app.ui.blockly_activity.TypeConnected;
import com.example.iot_app.utils.HexUtil;
import com.google.android.gms.common.util.Hex;

public class WebAppInterface {
    private static String TAG = "WebAppInterface";
    Context mContext;

    /**
     * Instantiate the interface and set the context
     */
    public WebAppInterface(Context c) {
        mContext = c;
    }

    @JavascriptInterface
    public int getSensor() {
        return BlocklyActivity.sensorData;
    }

    @JavascriptInterface
    public void mBackward(int data) {
        Log.d(TAG, "moveBackward: " + data);
        String mBvalue = HexUtil.formatHexString4((int) (data*2.54));
        String mB1value = HexUtil.formatHexString4((int) ((-1)*data*2.54));
        if(data > 0){
            mBvalue = mBvalue + "00" + mB1value + "ff";
        }else if(data == 0){
            mBvalue = "00000000";
        }
        if (BlocklyActivity.typeConnected == TypeConnected.HC05) {
            BlocklyActivity.hc05Send("ff5507000205"+mBvalue);
        }
        if (BlocklyActivity.typeConnected == TypeConnected.HM10) {
            BlocklyActivity.writeBle("ff5507000205"+mBvalue);
        }
        Log.d(TAG, "motorM1,M2: " +mBvalue);
    }
    @JavascriptInterface
    public void mForward(int data) {
        Log.d(TAG, "moveBackward: " + data);
        String mF1value = HexUtil.formatHexString4((int) (data*2.54));
        String mFvalue = HexUtil.formatHexString4((int) ((-1)*data*2.54));
        if(data > 0){
            mFvalue = mFvalue + "ff" + mF1value + "00";
        }else if(data == 0){
            mFvalue = "00000000";
        }
        if (BlocklyActivity.typeConnected == TypeConnected.HC05) {
            BlocklyActivity.hc05Send("ff5507000205"+mFvalue);
        }
        if (BlocklyActivity.typeConnected == TypeConnected.HM10) {
            BlocklyActivity.writeBle("ff5507000205"+mFvalue);
        }
        Log.d(TAG, "motorM1,M2: " +mFvalue);
    }

    @JavascriptInterface
    public void mAServo(String port, int angle, int duaration) {
        //3 cặp cuối lần lượt là porti, slot, angle
        String mess = port + "01";
        String ag = "";
        String duarationmS = "";
        if (angle == 0) {
            ag = "00";
        } else {
            ag = HexUtil.formatHexString4(angle);
        }
        if (duaration < 10){
            duarationmS = "00";
        } else if ((duaration >= 10) && (duaration < 200)){
            duarationmS = "03";
        }else if ((duaration >= 200) && (duaration < 1000)){
            duarationmS = "05";
        }else if ((duaration >= 1000) && (duaration < 8000)){
            duarationmS = "07";
        }else if (duaration >= 8000){
            duarationmS = "0a";
        }
        if (BlocklyActivity.typeConnected == TypeConnected.HC05) {
            BlocklyActivity.hc05Send("ff550600020b"+ mess+duarationmS + ag);
        }
        if (BlocklyActivity.typeConnected == TypeConnected.HM10) {
            BlocklyActivity.writeBle("ff550600020b" + mess+ag+duarationmS);
        }
        Log.d(TAG, "Servo: " +"ff550900020b"+ mess+duarationmS + ag);
    }

    @JavascriptInterface
    public void mservo_NO(String port, int angle) {
        //3 cặp cuối lần lượt là porti, slot, angle
        String mess = "ff550600020b";
        String ag = Integer.toHexString(angle);
        mess += port;
        mess += "01";
        if (angle == 0) {
            mess += "00";
        } else {
            mess += ag;
        }
        if (BlocklyActivity.typeConnected == TypeConnected.HC05) {
            BlocklyActivity.hc05Send(mess);
        }
        if (BlocklyActivity.typeConnected == TypeConnected.HM10) {
            BlocklyActivity.writeBle(mess);
        }
    }

    @JavascriptInterface
    public void servo(String port, String slot, int angle) {
        //3 cặp cuối lần lượt là porti, slot, angle
        String mess = "ff550600020b";
        String ag = Integer.toHexString(angle);
        mess += port;
        mess += slot;
        if (angle == 0) {
            mess += "00";
        } else {
            mess += ag;
        }
        if (BlocklyActivity.typeConnected == TypeConnected.HC05) {
            BlocklyActivity.hc05Send(mess);
        }
        if (BlocklyActivity.typeConnected == TypeConnected.HM10) {
            BlocklyActivity.writeBle(mess);
        }

    }

    @JavascriptInterface
    public int requestSensor(String port) {
        String mess = "ff5504000124" + port;
        if (BlocklyActivity.typeConnected == TypeConnected.HC05) {
            BlocklyActivity.hc05Send(mess);
        }
        if (BlocklyActivity.typeConnected == TypeConnected.HM10) {
            BlocklyActivity.writeBle(mess);
        }
        return BlocklyActivity.sensorData;
    }
    @JavascriptInterface
    public boolean lineFollow2(String port, String s1, String s2) {
        String mess = "ff5504000111" + port;
        String x1 = "";
        String x2 = "";
        if (BlocklyActivity.typeConnected == TypeConnected.HC05) {
            BlocklyActivity.hc05Send(mess);
        }
        if (BlocklyActivity.typeConnected == TypeConnected.HM10) {
            BlocklyActivity.writeBle(mess);
        }

        if(s1.equals("black") && s2.equals("black")){
            x1 = "40";
            x2 = "40";
        } else

        if(s1.equals("black") && s2.equals("white")){
         
            x1 = "00";
            x2 = "40";
        } else

        if(s1.equals("white") && s2.equals("black")){
            x1 = "80";
            x2 = "3f";
        } else

        if(s1.equals("white") && s2.equals("white")){
            x1 = "00";
            x2 = "00";
        }

        if ((BlocklyActivity.lineValueS1.equals(x1) && BlocklyActivity.lineValueS2.equals(x2))){
            Log.d(TAG, "lineFollow2: xxxxxxxxxxx");
            return true;
        }else{
            Log.d(TAG, "lineFollow2: "+x1+x2);
            Log.d(TAG, "lineFollow2: "+BlocklyActivity.lineValueS1+BlocklyActivity.lineValueS2);
            return  false;
        }


    }
    @JavascriptInterface
    public boolean getTouchVal(String port) {
        //yêu cầu trả về touch
        String mess = "ff5504000133" + port;
        // BlocklyActivity.writeBle(mess);
        BlocklyActivity.hc05Send(mess);
        return BlocklyActivity.touchData;
    }
    @JavascriptInterface
    public boolean getAvoidVal(String port) {
        //yêu cầu trả về touch
        String mess = "ff5504000134" + port;
        // BlocklyActivity.writeBle(mess);
        BlocklyActivity.hc05Send(mess);
        return BlocklyActivity.avoidData;
    }
    @JavascriptInterface
    public int lightSensor(String port) {
        return BlocklyActivity.lightData;
    }

    @JavascriptInterface
    public int getTemVal(String port) {
        String mess = "ff5504000102" + port;
        if (BlocklyActivity.typeConnected == TypeConnected.HC05) {
            BlocklyActivity.hc05Send(mess);
        }
        if (BlocklyActivity.typeConnected == TypeConnected.HM10) {
            BlocklyActivity.writeBle(mess);
        }
        return BlocklyActivity.temperatureData;
    }

    @JavascriptInterface
    public int getPotenlVal(String port) {
        String mess = "ff5504000104" + port;
        if (BlocklyActivity.typeConnected == TypeConnected.HC05) {
            BlocklyActivity.hc05Send(mess);
        }
        if (BlocklyActivity.typeConnected == TypeConnected.HM10) {
            BlocklyActivity.writeBle(mess);
        }
        return BlocklyActivity.potenlData;
    }

    @JavascriptInterface
    public int isStop() {
        return BlocklyActivity.isStop;
    }

}
