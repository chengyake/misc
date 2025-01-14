package com.android.cheng;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.content.Context;
import android.text.InputType;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;
import android.telephony.TelephonyManager;
import java.io.IOException;
import android.graphics.Color;
import android.view.View.OnFocusChangeListener;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date; 
import java.util.GregorianCalendar;
import android.widget.Toast;
import android.content.BroadcastReceiver;  
import android.content.Intent;
import android.content.IntentFilter;  

import android.os.IBinder;
import android.content.ServiceConnection;
import android.content.ComponentName;
import android.content.SharedPreferences;

import com.android.cheng.util.HexDump;
public class PUSGetInfoActivity extends Activity {
    
    private TextView pusinfo;
    private TextView puminfo;
    private TextView jobinfo;
    private TextView jobdate;


    private EditText edit_id;
    private EditText edit_lisence;
	private Button get_id;
	private Button check_lisence;
	private Button get_version;
    
    private String deviceId;


    private IComService mIComService;

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver(){
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(ComService.ACTION_GET_INFO)) {
                String info = intent.getExtras().getString("string"); 
                StringBuffer subBuf=new StringBuffer();

                if(info.length()<132) {
                    return;
                }
                subBuf.append(info);

                byte[] tmp = HexDump.hexStringToByteArray(subBuf.substring(36, 46));
                pusinfo.setText("" + (char)tmp[0] + (char)tmp[1]  + (char)tmp[2] + (char)tmp[3] + (char)tmp[4]);
                //Log.e("chengyakee","" + (char)tmp[0] + (char)tmp[1]  + (char)tmp[2] + (char)tmp[3] + (char)tmp[4]);

                puminfo.setText(subBuf.substring(222, 224) + "." + subBuf.substring(220, 222));
                //Log.e("chengyakee", subBuf.substring(222, 224) + "." + subBuf.substring(220, 222));

                tmp = HexDump.hexStringToByteArray(subBuf.substring(84, 108));
                jobinfo.setText("" + (char)tmp[0] + (char)tmp[1]  + (char)tmp[2] + (char)tmp[3] + (char)tmp[4] + 
                        (char)tmp[5] + (char)tmp[6]  + (char)tmp[7] + (char)tmp[8] + (char)tmp[9]  +
                        (char)tmp[10] + (char)tmp[11]);

                //Log.e("chengyakee", " " + (char)tmp[0] + (char)tmp[1]  + (char)tmp[2] + (char)tmp[3] + (char)tmp[4] + 
                //        (char)tmp[5] + (char)tmp[6]  + (char)tmp[7] + (char)tmp[8] + (char)tmp[9]  +
                //        (char)tmp[10] + (char)tmp[11]);

                tmp = HexDump.hexStringToByteArray(subBuf.substring(116, 132));
                jobdate.setText("" + (char)tmp[0] + (char)tmp[1]  + (char)tmp[2] + (char)tmp[3] + (char)tmp[4] + 
                        (char)tmp[5] + (char)tmp[6]  + (char)tmp[7]);

                //Log.e("chengyakee", " " + (char)tmp[0] + (char)tmp[1]  + (char)tmp[2] + (char)tmp[3] + (char)tmp[4] + 
                //        (char)tmp[5] + (char)tmp[6]  + (char)tmp[7]);

            } else if (action.equals(ComService.ACTION_FINISH)) {
                Log.e("chengyake", "in PUSActivity finish");
                finish();
            }
        }
    };

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName name, IBinder service) {  
            mIComService = IComService.Stub.asInterface(service);
        } 

        public void onServiceDisconnected(ComponentName name) {  
        }  
    }; 


	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.pus_get_info);

		IntentFilter myIntentFilter = new IntentFilter();
		myIntentFilter.addAction(ComService.ACTION_GET_INFO);
		myIntentFilter.addAction(ComService.ACTION_FINISH);
		registerReceiver(mBroadcastReceiver, myIntentFilter);


        Intent j  = new Intent();  
        j.setClass(this, ComService.class);
        getApplicationContext().bindService(j, mServiceConnection, BIND_AUTO_CREATE);


        deviceId = ((TelephonyManager) getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();

        edit_id = (EditText)findViewById(R.id.device_id);
        edit_id.setInputType(InputType.TYPE_NULL);
        edit_lisence = (EditText)findViewById(R.id.lisence);

        pusinfo = (TextView)findViewById(R.id.pusinfo);
        puminfo = (TextView)findViewById(R.id.puminfo);
        jobinfo = (TextView)findViewById(R.id.jobinfo);
        jobdate = (TextView)findViewById(R.id.jobdatecontent);



        get_id=(Button)findViewById(R.id.get_id);
        check_lisence=(Button)findViewById(R.id.check_lisence);
        get_version=(Button)findViewById(R.id.get_version);

        get_id.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
			    edit_id.setText(getRandomString());
			}
		});

        check_lisence.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
			    checkLisence(getRandomString(), edit_lisence.getText().toString());
			}
		});
        get_version.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
               try {
                   mIComService.getVersionInfo();
               } catch  (Exception e) {
                   throw new IllegalStateException("upload error", e);
               }
			}
		});


	}

    @Override
    protected void onDestroy() {
        Log.e("chengyake", "Override in PUSGetInfoActivity onDestroy func");
        getApplicationContext().unbindService(mServiceConnection);
        this.unregisterReceiver(mBroadcastReceiver);
        super.onDestroy();
    }

    public String getCurrentDate() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String tmp = df.format(new Date()); 

        StringBuffer dateBuf=new StringBuffer(); 
        dateBuf.append(tmp);
        //return df.format(new Date()); 
        return dateBuf.substring(8, 10);
    }

    public String getRandomString() { 
        return deviceId + getCurrentDate(); 
    }

    public boolean checkLisence(String devId, String lis) {

        SharedPreferences shareEdit = getSharedPreferences("data", 0); 

        if(shareEdit.getString("sunup_lisence", "false").equals("true")) {
            Toast.makeText(getApplicationContext(), "you have registed success",
                    Toast.LENGTH_SHORT).show();
            return true;
        }

        if(devId.equals(lis)) {
            SharedPreferences.Editor sharedata = getSharedPreferences("data", 0).edit(); 
            sharedata.putString("sunup_lisence","true"); 
            sharedata.commit();

            Toast.makeText(getApplicationContext(), "register success",
                    Toast.LENGTH_SHORT).show();
            return true;
        } else {
            Toast.makeText(getApplicationContext(), "register failed",
                    Toast.LENGTH_SHORT).show();
            return false;
        }
    }

}


