package com.scarlett.myserver.pll;

import java.util.ArrayList;

public class index_in{//顶点的in索引结构体
    public index_in(){
        this.spt_d =new ArrayList<>();
        this.spt_v = new ArrayList<>();
        this.prev = new ArrayList<>();
    }
    public ArrayList<Integer> spt_v;
    public ArrayList<Float> spt_d;
    public ArrayList<Integer> prev;

}
