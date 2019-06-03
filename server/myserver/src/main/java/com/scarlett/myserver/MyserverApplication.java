package com.scarlett.myserver;

import com.scarlett.myserver.pll.PllWidestPath;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

@SpringBootApplication
public class MyserverApplication {
    public static PllWidestPath pll = new PllWidestPath();
    public static HashMap<Integer, GeoInfo> dataSource = new HashMap();
    public static HashMap<String, Integer> name_id = new HashMap<>();
    public static void main(String[] args) {
        SpringApplication.run(MyserverApplication.class, args);
        try {
            if(pll.loadIndex("C:\\Users\\String\\Desktop\\graph.idx")){
                System.out.println("Loading succeeds.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        /*
        System.out.println("Start storing Map ! Please wait ...");
        String filename = "D:\\A_Adata\\id_gaode.txt";
        String encoding = "UTF-8";
        double store_time = -pll.GetCurrentTimeSec();
        try{
            InputStreamReader read = new InputStreamReader(new FileInputStream(filename), encoding);
            BufferedReader bufferedReader = new BufferedReader(read);
            String lineTXT = null;
            String allNumString = "";
            while ((lineTXT = bufferedReader.readLine()) != null) {
                // every line
                //System.out.println(lineTXT.toString().trim());
                allNumString += lineTXT+" ";
            }
            if(allNumString != null && !"".equals(allNumString)){
                String[] numbers = allNumString.split(" ");
                for (int i = 0; i < numbers.length; i+=3) {
                    int id = Integer.parseInt(numbers[i]);
                    double latitude = Float.parseFloat(numbers[i+1]);
                    double longitude = Float.parseFloat(numbers[i+2]);
                    dataSource.put(id,new GeoInfo(longitude,latitude));
                }
            }
        }catch (Exception e){
            System.out.println("error ocurred when storing");
            e.printStackTrace();
        }

        System.out.println("Storing hashmap succeeds.");
        store_time += pll.GetCurrentTimeSec();
        System.out.println("map storing time is "+ store_time + " seconds.");
        */
        name_id.put("江滩",47371);
        name_id.put("沙河",139034);
        name_id.put("友谊大道",95691);
    }
}
