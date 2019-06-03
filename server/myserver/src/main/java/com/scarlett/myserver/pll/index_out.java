package com.scarlett.myserver.pll;

import java.util.ArrayList;

public class index_out{//顶点的out索引结构体，先不保存路径
    public index_out(){
        this.spt_d =new ArrayList<>();
        this.spt_v = new ArrayList<>();
        this.next = new ArrayList<>();
    }
    public ArrayList<Integer> spt_v;
    public ArrayList<Float> spt_d;
    public ArrayList<Integer> next;
}
