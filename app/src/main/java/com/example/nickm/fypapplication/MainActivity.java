package com.example.nickm.fypapplication;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;

import static java.lang.System.in;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // view object for the notification
        View v = new View(this);

        String little = "LITTLE Governor: " + getGovernorLittle();
        String big = "big Governor: " + getGovernorBig();

        TextView textView1 = findViewById(R.id.textView1);
        TextView textView2 = findViewById(R.id.textView2);
        TextView textView3 = findViewById(R.id.textView3);
        textView1.setText(big);
        textView2.setText(little);
        textView3.setText(getScheduler());

        // show notification on startup
        showNotification(v);

        Button bigP = findViewById(R.id.button2);
        Button bigSA = findViewById(R.id.button3);
        Button bigSM = findViewById(R.id.button4);

        Button litP = findViewById(R.id.button5);
        Button litSA = findViewById(R.id.button6);
        Button litSM = findViewById(R.id.button7);

        bigP.setOnClickListener(new View.OnClickListener(){
            @Override
            //On click function
            public void onClick(View view) {
                ChangeGovernorBig("performance");
            }
        });

        bigSA.setOnClickListener(new View.OnClickListener(){
            @Override
            //On click function
            public void onClick(View view) {
                ChangeGovernorBig("smartassV2");
            }
        });

        bigSM.setOnClickListener(new View.OnClickListener(){
            @Override
            //On click function
            public void onClick(View view) {
                ChangeGovernorBig("smartmax");
            }
        });

        litP.setOnClickListener(new View.OnClickListener(){
            @Override
            //On click function
            public void onClick(View view) {
                ChangeGovernorLittle("performance");
            }
        });

        litSA.setOnClickListener(new View.OnClickListener(){
            @Override
            //On click function
            public void onClick(View view) {
                ChangeGovernorLittle("smartassV2");
            }
        });

        litSM.setOnClickListener(new View.OnClickListener(){
            @Override
            //On click function
            public void onClick(View view) {
                ChangeGovernorLittle("smartmax");
            }
        });
    }

    public void showNotification(View v)
    {
        //PendingIntent update = PendingIntent.getActivity(this,0,update,PendingIntent.FLAG_ONE_SHOT);
        // helps sets certain parameters of the notification, like icons etc
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle("CPU Details");
        builder.setStyle(new NotificationCompat.InboxStyle()    // allows the notification to become bigger
                .addLine("big Governor:   " + getGovernorBig())
                .addLine("LITTLE Governor:   " + getGovernorLittle()));
        //builder.addAction(R.mipmap.ic_launcher, "refresh",update);
        NotificationManager NM = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NM.notify(0,builder.build());

    }


    public void refresh (View v){
        finish();
        startActivity(getIntent());
    }

    private String getGovernorBig() {
        StringBuffer sb = new StringBuffer();
        String file = "/sys/devices/system/cpu/cpu4/cpufreq/scaling_governor";  // Gets governor for big cores

        if (new File(file).exists()) {
            try {
                BufferedReader br = new BufferedReader(new FileReader(new File(file)));
                String aLine;
                while ((aLine = br.readLine()) != null)
                    sb.append(aLine + "\n");

                if (br != null)
                    br.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }

        return sb.toString();
    }
    private String getGovernorLittle() {
        StringBuffer sb = new StringBuffer();
        String file = "/sys/devices/system/cpu/cpu0/cpufreq/scaling_governor";  // Gets governor for LITTLE cores

        if (new File(file).exists()) {
            try {
                BufferedReader br = new BufferedReader(new FileReader(new File(file)));
                String aLine;
                while ((aLine = br.readLine()) != null)
                    sb.append(aLine + "\n");

                if (br != null)
                    br.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }

        return sb.toString();
    }
    public void ChangeGovernorBig (String governor){
        String[] newGovernor = {"echo "+governor+" > /sys/devices/system/cpu/cpu4/cpufreq/scaling_governor",
                "echo "+governor+" > /sys/devices/system/cpu/cpu5/cpufreq/scaling_governor",
                "echo "+governor+" > /sys/devices/system/cpu/cpu6/cpufreq/scaling_governor",
                "echo "+governor+" > /sys/devices/system/cpu/cpu7/cpufreq/scaling_governor"};
        RunCommand(newGovernor);
        finish();
        startActivity(getIntent());
    }

   public void ChangeGovernorLittle (String governor){
        String[] newGovernor = {"echo "+governor+" > /sys/devices/system/cpu/cpu0/cpufreq/scaling_governor",
                "echo "+governor+" > /sys/devices/system/cpu/cpu1/cpufreq/scaling_governor",
                "echo "+governor+" > /sys/devices/system/cpu/cpu2/cpufreq/scaling_governor",
                "echo "+governor+" > /sys/devices/system/cpu/cpu3/cpufreq/scaling_governor"};
        RunCommand(newGovernor);
        finish();
        startActivity(getIntent());
    }
/*
       public void Performance (View v){
           String[] performance = {"echo performance > /sys/devices/system/cpu/cpu0/cpufreq/scaling_governor",
                   "echo performance > /sys/devices/system/cpu/cpu1/cpufreq/scaling_governor",
                   "echo performance > /sys/devices/system/cpu/cpu2/cpufreq/scaling_governor",
                   "echo performance > /sys/devices/system/cpu/cpu3/cpufreq/scaling_governor"};
           RunCommand(performance);
           finish();
           startActivity(getIntent());
       }
*/
    void RunCommand(String[] cmd) {

        //  run any terminal command through this function, that doesn't return anything
        Process process;
        try {
            //  run as root
            process = Runtime.getRuntime().exec("su");

            DataOutputStream os = new DataOutputStream(process.getOutputStream());

            // the command will be written
            if (os != null) {
                //  iterate through the command string
                for (int i = 0; i < cmd.length; i++){
                    os.writeBytes(cmd[i] + "\n");
                    os.flush();
                }
            }

            // command to exit the shell command
            os.writeBytes("exit" + "\n");
            os.flush();

            try {
                process.waitFor();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // close the DataOutputStream
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }




    private String getScheduler() {
        StringBuffer sb = new StringBuffer();

        String file = "/sys/block/sda/queue/scheduler";  // Gets governor for big cores
        if (new File(file).exists()) {
            try {
                BufferedReader br = new BufferedReader(new FileReader(new File(file)));
                String aLine;
                while ((aLine = br.readLine()) != null)
                    sb.append(aLine + "\n");
                if (br != null)
                    br.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (sb.toString().length() == 0) {
            return "File not available";
        }
        return sb.toString();
    }
}
