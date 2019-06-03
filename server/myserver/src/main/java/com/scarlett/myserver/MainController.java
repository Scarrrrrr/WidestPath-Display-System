package com.scarlett.myserver;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static com.scarlett.myserver.MyserverApplication.*;

@RestController
public class MainController {
    @Autowired // This means to get the bean called userRepository
    // Which is auto-generated by Spring, we will use it to handle the data
    private UserRepository userRepository;
    //public PllWidestPath pll = new PllWidestPath();

    //@RequestParam("name")
    @GetMapping(path = "/find_by_name")
    public List<Optional<User>> findByName(String src, String dst) throws IOException {
        double find_id_time = -pll.GetCurrentTimeSec();
        int u = userRepository.findByName(src).getId();
        int w = userRepository.findByName(dst).getId();
        find_id_time += pll.GetCurrentTimeSec();
        System.out.println("Find id time is "+ find_id_time+" seconds.");
        Double query_time = -pll.GetCurrentTimeSec();
        System.out.println("distance: " +pll.queryDistance(u, w));
        query_time += pll.GetCurrentTimeSec();
        System.out.println("query_time:" +query_time  + "seconds" );
        double print_time = -pll.GetCurrentTimeSec();
        System.out.print("path:");
        LinkedList<Integer> a =pll.getPath(u,w);
        print_time += pll.GetCurrentTimeSec();
        System.out.println("The print path time is "+print_time+" seconds.");
        List<Optional<User>> ans = new ArrayList();
        int size = a.size();
        double return_answer_time = -pll.GetCurrentTimeSec();

        for (int j = 0; j < size; j++) {
            Optional<User> b =userRepository.findById(a.get(j));
            ans.add(b);
        }
        while(!a.isEmpty()){
            System.out.print(a.pop());
            if(!a.isEmpty()) System.out.print("->");
            else System.out.println();
        }
        return_answer_time += pll.GetCurrentTimeSec();
        System.out.println("Time for returning answers is " +return_answer_time+" seconds");

        return ans;
    };
    @GetMapping(path = "/find_by_hashmap")
    public List<GeoInfo> findByHashMap(String src, String dst){
        int u = name_id.get(src);
        int w = name_id.get(dst);
        System.out.print("path:");
        LinkedList<Integer> a =pll.getPath(u,w);
        List<GeoInfo> b = new ArrayList<>();
        double return_answer_time = -pll.GetCurrentTimeSec();
        for(int j =0;j<a.size();j++){
            b.add(new GeoInfo(dataSource.get(a.get(j)).longitude,dataSource.get(a.get(j)).latitude));
        }
        while(!a.isEmpty()){
            System.out.print(a.pop());
            if(!a.isEmpty()) System.out.print("->");
            else System.out.println();
        }
        return_answer_time += pll.GetCurrentTimeSec();
        System.out.println("Time for returning answer is "+return_answer_time +" seconds.");
        return b;
    }
}