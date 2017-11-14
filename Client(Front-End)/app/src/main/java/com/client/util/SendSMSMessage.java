package com.ece651group8.uwaterloo.ca.ece_651_group8.util;

import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by liuyue on 2016-11-09.
 */
public class SendSMSMessage {

//    String phoneNumber;
//    String smsMessage;

//    public SendSMSMessage (){
//        this.phoneNumber = number;
//        this.smsMessage = message;
//    }

    public static String sendSMSMessage(String number, String message){
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(number, null,message, null, null);
            return "SMS sent.";
        } catch (Exception e) {
            e.printStackTrace();
            return "SMS faild, please try again.";
        }

    }
}
