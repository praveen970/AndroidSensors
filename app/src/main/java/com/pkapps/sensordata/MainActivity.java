package com.pkapps.sensordata;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class MainActivity extends AppCompatActivity {

    ListView listView;
    Socket s;
    EditText ipadd;
    String ip = "192.168.86.101";
    SensorManager sensorManager;
    BufferedReader in;
    PrintWriter out;
    List<Sensor> listsensor;
    List<String> liststring;
    ArrayAdapter<String> adapter;
    SensorEventListener sensorListener;
    TextView text1, text2, text3, text4, text5, text6, text7, text8, text9, text10;
    TextView text11, text12, text13, text14, text15, text16, text17, text18, text19, text20, text21, text22;
    Button capture, stop, connect;
    AtomicBoolean isWriteAllowed = new AtomicBoolean(false);
    BufferedWriter bufAcc, bufGyro, bufProx, bufLux, bufLine, bufRot, bufmag, bufpres, bufgrav;
    File fileProx = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        listView = (ListView)findViewById(R.id.listview1);
        stop = (Button) findViewById(R.id.buttonStop);
        connect = (Button) findViewById(R.id.buttonConnect);
        capture = (Button) findViewById(R.id.buttonCapture);
        text1 = (TextView) findViewById(R.id.textView2);
        text2 = (TextView) findViewById(R.id.textView3);
        text3 = (TextView) findViewById(R.id.textView4);
        text4 = (TextView) findViewById(R.id.textView6);
        text5 = (TextView) findViewById(R.id.textView7);
        text6 = (TextView) findViewById(R.id.textView8);
        text7 = (TextView) findViewById(R.id.textView10);
        text8 = (TextView) findViewById(R.id.textView11);
        text9 = (TextView) findViewById(R.id.textView12);
        text10 = (TextView) findViewById(R.id.textView14);
        text11 = (TextView) findViewById(R.id.textView16);
        text12 = (TextView) findViewById(R.id.textView18);
        text13 = (TextView) findViewById(R.id.textView20);
        text14 = (TextView) findViewById(R.id.textView21);
        text15 = (TextView) findViewById(R.id.textView22);
        text16 = (TextView) findViewById(R.id.textView24);
        text17 = (TextView) findViewById(R.id.textView25);
        text18 = (TextView) findViewById(R.id.textView26);
        text19 = (TextView) findViewById(R.id.textView28);
        text20 = (TextView) findViewById(R.id.textView29);
        text21 = (TextView) findViewById(R.id.textView30);
        text22 = (TextView) findViewById(R.id.datastatus);
        stop.setEnabled(false);
        connect.setEnabled(false);
        /*connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                connect.setEnabled(false);
                Thread t = new Thread() {
                    public void run() {

                        try {

                            Log.d("Status", "Thread Started");
                            s = new Socket(ip, 5000);
                            in = new BufferedReader(new InputStreamReader(s.getInputStream()));
                            out = new PrintWriter(s.getOutputStream(), true);
                            out.println("This is from mobile");

                            while (true) {
                                String val = in.readLine();
                                Log.d("Value", val);
                                if (val.equals("start")) {
                                    Log.d("Value", "True");
                                    isWriteAllowed.set(true);
                                    text22.setText("Capturing Data");
                                }
                                if (val.equals("stop")) {
                                    Log.d("Value", "False");
                                    isWriteAllowed.set(false);
                                    text22.setText("Data Capture Stopped");

                                }
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                };
                t.start();
            }
        });*/

        capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String timeStamp = new SimpleDateFormat("HH.mm.ss.dd.MM.yyyy").format(new Date());
                File filegrav = new File(getExternalFilesDir(null), "gravity-"+timeStamp+".csv");
                File filemag = new File(getExternalFilesDir(null), "magnetic-"+timeStamp+".csv");
                File filepres = new File(getExternalFilesDir(null), "pressure-"+timeStamp+".csv");
                File fileLine = new File(getExternalFilesDir(null), "linear-"+timeStamp+".csv");
                File fileAcc = new File(getExternalFilesDir(null), "accelerometer-"+timeStamp+".csv");
                File fileGyro = new File(getExternalFilesDir(null), "gyroscope-"+timeStamp+".csv");
                File fileLux = new File(getExternalFilesDir(null), "light-"+timeStamp+".csv");
                fileProx = new File(getExternalFilesDir(null), "proximity-"+timeStamp+".csv");
                File fileRot = new File(getExternalFilesDir(null), "rotation-"+timeStamp+".csv");
                try {
                    bufgrav = new BufferedWriter(new FileWriter(filegrav));
                    bufmag = new BufferedWriter(new FileWriter(filemag));
                    bufpres = new BufferedWriter(new FileWriter(filepres));
                    bufLine = new BufferedWriter(new FileWriter(fileLine));
                    bufAcc = new BufferedWriter(new FileWriter(fileAcc));
                    bufGyro = new BufferedWriter(new FileWriter(fileGyro));
                    bufLux = new BufferedWriter(new FileWriter(fileLux));
                    bufProx = new BufferedWriter(new FileWriter(fileProx));
                    bufRot = new BufferedWriter(new FileWriter(fileRot));
                    text22.setText("Data capture in process");
                    isWriteAllowed.set(true);
                    stop.setEnabled(true);
                    capture.setEnabled(false);

                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isWriteAllowed.set(false);
                capture.setEnabled(true);
                stop.setEnabled(false);
                String absolutePath = fileProx.getAbsolutePath();
                String filePath = absolutePath.
                        substring(0, absolutePath.lastIndexOf(File.separator));

                text22.setText("Data capture stopped. Saved at "+filePath);
                try {
                    bufgrav.close();
                    bufRot.close();
                    bufmag.close();
                    bufProx.close();
                    bufpres.close();
                    bufLux.close();
                    bufAcc.close();
                    bufGyro.close();
                    bufLine.close();


                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
        });
//        liststring = new ArrayList<String>();

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

//        listsensor = sensorManager.getSensorList(Sensor.TYPE_ALL);

//        for(int i=0; i<listsensor.size(); i++){
//
//            liststring.add(listsensor.get(i).getName());
//        }
//
//        adapter = new ArrayAdapter<String>(MainActivity.this,
//                android.R.layout.simple_list_item_2,
//                android.R.id.text1, liststring
//        );
//
//        listView.setAdapter(adapter);

        sensorListener = new SensorEventListener() {

            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                Sensor sensor = sensorEvent.sensor;
                //String timeStamp = new SimpleDateFormat("HH.mm.ss.dd.MM.yyyy").format(new Date());
                Timestamp time = new Timestamp(new Date().getTime());
                String timeStamp = time.toString();
                switch (sensor.getType()) {

                    case Sensor.TYPE_ACCELEROMETER:


                        text1.setText("AxisX: " + Float.toString(sensorEvent.values[0]));
                        text2.setText("AxisY: " + Float.toString(sensorEvent.values[1]));
                        text3.setText("AxisZ: " + Float.toString(sensorEvent.values[2]));

                        if (isWriteAllowed.get()) {
                            try {
                                Log.d("Value", "isWrite" + isWriteAllowed);
                                //StringBuilder sb = new StringBuilder();

                                bufAcc.append(timeStamp+":"+Float.toString(sensorEvent.values[0]) + "," + Float.toString(sensorEvent.values[1]) + "," + Float.toString(sensorEvent.values[2]));
                                bufAcc.newLine();
                                bufAcc.flush();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        break;
                    case Sensor.TYPE_GYROSCOPE:
                        text4.setText("AxisX: " + Float.toString(sensorEvent.values[0]));
                        text5.setText("AxisY: " + Float.toString(sensorEvent.values[1]));
                        text6.setText("AxisZ: " + Float.toString(sensorEvent.values[2]));
                        if (isWriteAllowed.get()) {
                            try {
                                bufGyro.append(timeStamp+":"+Float.toString(sensorEvent.values[0]) + "," + Float.toString(sensorEvent.values[1]) + "," + Float.toString(sensorEvent.values[2]));
                                bufGyro.newLine();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        break;
                    case Sensor.TYPE_MAGNETIC_FIELD:
                        text7.setText("AxisX: " + Float.toString(sensorEvent.values[0]));
                        text8.setText("AxisY: " + Float.toString(sensorEvent.values[1]));
                        text9.setText("AxisZ: " + Float.toString(sensorEvent.values[2]));
                        if (isWriteAllowed.get()) {
                            try {
                                bufmag.append(timeStamp+":"+Float.toString(sensorEvent.values[0]) + "," + Float.toString(sensorEvent.values[1]) + "," + Float.toString(sensorEvent.values[2]));
                                bufmag.newLine();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        break;
                    case Sensor.TYPE_PRESSURE:
                        text10.setText("Pressure: " + Float.toString(sensorEvent.values[0]));
                        if (isWriteAllowed.get()) {
                            try {
                                bufpres.append(timeStamp+":"+Float.toString(sensorEvent.values[0]));
                                bufpres.newLine();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        break;
                    case Sensor.TYPE_PROXIMITY:
                        text11.setText("Distance: " + Float.toString(sensorEvent.values[0]));
                        if (isWriteAllowed.get()) {
                            try {
                                bufProx.append(timeStamp+":"+Float.toString(sensorEvent.values[0]));
                                bufProx.newLine();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        break;
                    case Sensor.TYPE_LIGHT:
                        text12.setText("LUX: " + Float.toString(sensorEvent.values[0]));
                        if (isWriteAllowed.get()) {
                            try {
                                bufLux.append(timeStamp+":"+Float.toString(sensorEvent.values[0]));
                                bufLux.newLine();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        break;
                    case Sensor.TYPE_GRAVITY:
                        text13.setText("AxisX: " + Float.toString(sensorEvent.values[0]));
                        text14.setText("AxisY: " + Float.toString(sensorEvent.values[1]));
                        text15.setText("AxisZ: " + Float.toString(sensorEvent.values[2]));
                        if (isWriteAllowed.get()) {
                            try {
                                bufgrav.append(timeStamp+":"+Float.toString(sensorEvent.values[0]) + "," + Float.toString(sensorEvent.values[1]) + "," + Float.toString(sensorEvent.values[2]));
                                bufgrav.newLine();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        break;
                    case Sensor.TYPE_LINEAR_ACCELERATION:
                        text16.setText("AxisX: " + Float.toString(sensorEvent.values[0]));
                        text17.setText("AxisY: " + Float.toString(sensorEvent.values[1]));
                        text18.setText("AxisZ: " + Float.toString(sensorEvent.values[2]));
                        if (isWriteAllowed.get()) {
                            try {
                                bufLine.append(timeStamp+":"+Float.toString(sensorEvent.values[0]) + "," + Float.toString(sensorEvent.values[1]) + "," + Float.toString(sensorEvent.values[2]));
                                bufLine.newLine();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        break;
                    case Sensor.TYPE_GAME_ROTATION_VECTOR:
                        text19.setText("AxisX: " + Float.toString(sensorEvent.values[0]));
                        text20.setText("AxisY: " + Float.toString(sensorEvent.values[1]));
                        text21.setText("AxisZ: " + Float.toString(sensorEvent.values[2]));
                        if (isWriteAllowed.get()) {
                            try {
                                bufRot.append(timeStamp+":"+Float.toString(sensorEvent.values[0]) + "," + Float.toString(sensorEvent.values[1]) + "," + Float.toString(sensorEvent.values[2]));
                                bufRot.newLine();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        break;
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {

            }
        };

        sensorManager.registerListener(sensorListener, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(sensorListener, sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE), SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(sensorListener, sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(sensorListener, sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE), SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(sensorListener, sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY), SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(sensorListener, sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT), SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(sensorListener, sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR), SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(sensorListener, sensorManager.getDefaultSensor(Sensor.TYPE_SIGNIFICANT_MOTION), SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(sensorListener, sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY), SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(sensorListener, sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION), SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(sensorListener, sensorManager.getDefaultSensor(Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR), SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(sensorListener, sensorManager.getDefaultSensor(Sensor.TYPE_GAME_ROTATION_VECTOR), SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(sensorListener, sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER), SensorManager.SENSOR_DELAY_NORMAL);


    }

    public void createFiles() {
        //capture.setEnabled(false);

    }


}
