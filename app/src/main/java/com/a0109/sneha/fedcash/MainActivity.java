package com.a0109.sneha.fedcash;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.example.whisk.CommonPack.IMyAidlInterface;

import org.json.JSONArray;

import java.util.Arrays;
import java.util.List;

import static android.content.Context.*;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    MyThread myWorker;
    Handler UIHandler = new Handler(Looper.getMainLooper());
    private IMyAidlInterface aidlInterface;
    TextView output;
    private Button moncash = null;
    private Button dailycash = null;
    private Button yearlyaverage = null;

    private EditText yearET;
    private EditText monthET;
    private EditText dateET;
    private EditText dayET;
    private boolean mIsBound;
    protected static final String TAG = "ServiceUser";
    private ServiceConnection myConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            aidlInterface = IMyAidlInterface.Stub.asInterface(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myWorker = new MyThread();
        myWorker.start();
        output = (TextView) findViewById(R.id.text);
        moncash = (Button)findViewById(R.id.moncash);
        moncash.setOnClickListener(this);
        dailycash = (Button)findViewById(R.id.dailycash);
        dailycash.setOnClickListener(this);
        yearlyaverage = (Button)findViewById(R.id.yearlyaverage);
        yearlyaverage.setOnClickListener(this);

        yearET = (EditText) findViewById(R.id.year_ET);
        monthET = (EditText) findViewById(R.id.month_ET);
        dateET = (EditText) findViewById(R.id.date_ET);
        dayET = (EditText) findViewById(R.id.day_ET);
        findViewById(R.id.bind).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(IMyAidlInterface.class.getName());
                ResolveInfo info = getPackageManager().resolveService(i, Context.BIND_AUTO_CREATE);
                i.setComponent(new ComponentName(info.serviceInfo.packageName, info.serviceInfo.name));
                bindService(i, myConnection, Context.BIND_AUTO_CREATE);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.moncash:
                System.out.println("Moncash pressed");
                final int year1 = Integer.parseInt(yearET.getText().toString()) ;
                myWorker.myHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        //Handle call
                        int[] result = new int[12];
                        try {
                            result = aidlInterface.monthlyCash(year1);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                        final int[] finalResult = result;
                        final StringBuilder resultInString = new StringBuilder();
                        for (int i=0; i<result.length; i++){
                            resultInString.append(" "+ result[i]);
                        }
                        UIHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                output.setText("Result Obtained "+ resultInString);
                            }
                        });
                    }
                });
                break;
            case R.id.dailycash:
                System.out.println("Daily cash pressed");
                final int year2 = Integer.parseInt(yearET.getText().toString());
                final int month2 = Integer.parseInt(monthET.getText().toString());
                final int date2 = Integer.parseInt(dateET.getText().toString());
                final int day2 = Integer.parseInt(dayET.getText().toString());
                myWorker.myHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        //Handle call
                        int[] result = new int[day2 + 1];
                        try {
                            result = aidlInterface.dailyCash(date2, month2, year2,day2);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                        final int[] finalResult = result;
                        UIHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                output.setText("Result Obtained "+finalResult.length);
                            }
                        });
                    }
                });
                break;
            case R.id.yearlyaverage:
                System.out.println("Yearly pressed");
                final int year3 = Integer.parseInt(yearET.getText().toString());
                myWorker.myHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        //Handle call
                        int result = 0;
                        try {
                            result = aidlInterface.yearlyAvg(year3);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                        final int finalResult = result;
                        UIHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                output.setText("Result Obtained "+finalResult);
                            }
                        });
                    }
                });
                break;
            default:
                break;
        }
    }

    public class MyThread extends Thread{
        Handler myHandler;

        @Override
        public void run() {
            Looper.prepare();
            myHandler = new Handler();
            Looper.loop();
        }
    }
}

