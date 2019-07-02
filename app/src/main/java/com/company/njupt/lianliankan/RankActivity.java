package com.company.njupt.lianliankan;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;

public class RankActivity  extends AppCompatActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rank);

        try {
            //将文件读取到String中
            FileInputStream fis = openFileInput("myrank.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
            String line;
            String[] name = new String[15];
            String[] score = new String[15];
            int cnt = 0;
            TextView easyNameTv = findViewById(R.id.easyNameTv);
            TextView easyScoreTv = findViewById(R.id.easyScoreTv);
            TextView normalNameTv = findViewById(R.id.normalNameTv);
            TextView normalScoreTv = findViewById(R.id.normalScoreTv);
            TextView hardNameTv = findViewById(R.id.hardNameTv);
            TextView hardScoreTv = findViewById(R.id.hardScoreTv);
            while((line = reader.readLine()) != null){
                if(cnt%2 == 0)
                    name[cnt/2] = line;
                else score[cnt/2] = line;
                cnt++;
            }
            while(cnt < 30) {
                if(cnt%2 == 0)
                    name[cnt/2] = "none";
                else score[cnt/2] = "-1";
                cnt++;
            }
            String nametext = "",scoretext = "";
            for (int i=0;i<5;i++) {
                nametext += name[i] + "\n";
                scoretext += score[i] + "\n";
            }
            easyNameTv.setText(nametext);
            easyScoreTv.setText(scoretext);
            nametext = scoretext = "";
            for (int i=5;i<10;i++) {
                nametext += name[i] + "\n";
                scoretext += score[i] + "\n";
            }
            normalNameTv.setText(nametext);
            normalScoreTv.setText(scoretext);
            nametext = scoretext = "";
            for (int i=10;i<15;i++) {
                nametext += name[i] + "\n";
                scoretext += score[i] + "\n";
            }
            hardNameTv.setText(nametext);
            hardScoreTv.setText(scoretext);
            nametext = scoretext = "";

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
