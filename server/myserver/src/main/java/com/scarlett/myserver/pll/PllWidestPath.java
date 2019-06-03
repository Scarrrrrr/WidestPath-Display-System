package com.scarlett.myserver.pll;//
// Created by String on 2019/3/19.
//

import java.io.*;
import java.util.*;

import static java.lang.Integer.max;

public class PllWidestPath{
    //变量后面带_表示是私有变量
    private static final float INF =999;     //-INF表示最宽路径不可达，INF表示最宽路径最宽的值
    private int num_v_;                  //顶点数量
    private ArrayList<index_out> index_out_ = new ArrayList<>();    //保存的out索引
    private ArrayList<index_in>  index_in_ = new ArrayList<>();    //保存的in索引
    private double time_load_;
    private double time_indexing_; //统计数据加载时间和索引构建时间

    // 顶点ID 从0开始，构建索引成功返回 |true|
    public boolean constructIndex(String filename){
        String encoding = "UTF-8";
        ArrayList<tuple<Integer,Integer,Float>> es = new ArrayList<>(999);
        int u,v,w;
        float d;
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
                    u = Integer.parseInt(numbers[i]);
                    v = Integer.parseInt(numbers[i+1]);
                    d = Float.parseFloat(numbers[i+2]);
                    tuple<Integer,Integer,Float> edge = new tuple<>(u,v,d);
                    es.add(edge);
                }
            }
        }catch (Exception e){
            System.out.println("error ocurred when reading");
            e.printStackTrace();
        }
        this.time_load_ = -GetCurrentTimeSec();
        int V =0;
        //获取顶点最大的ID，顶点从0开始，V = ID + 1
        for(int i = 0; i < es.size(); i++){
            V = max(V, max(es.get(i).first, (es.get(i).second + 1)));
        }
        System.out.println("graph.V = "+V);
        this.num_v_ = V;
        ArrayList<ArrayList<Pair<Integer,Float>>> adj_out =new ArrayList<>(V);
        ArrayList<ArrayList<Pair<Integer,Float>>> adj_in = new ArrayList<>(V);
        this.time_load_ += GetCurrentTimeSec();
        this.time_indexing_ = -GetCurrentTimeSec();
        //分配存储空间
        for(int i =0;i< V;i++){
            this.index_in_.add(new index_in());
            this.index_out_.add(new index_out());
            adj_in.add(new ArrayList<>());
            adj_out.add(new ArrayList<>());
        }
        for(int i=0;i<es.size();i++){
            int ve = es.get(i).first;
            int we = es.get(i).second;
            float de = es.get(i).third;
            adj_out.get(ve).add(new Pair<>(we,de));
            adj_in.get(we).add(new Pair<>(ve,de));
        }
        // 先不对顶点重排列，先使用默认的方式
        //构建索引,剪枝核心算法
        ArrayList<Boolean> usd=new ArrayList(V); //usd用来标记顶点是否已经被当做root产生索引了 (in new label)
        //临时索引，需要添加一个Sentinel (V, -INF),构建入索引和出度索引,0表示没有最宽路径
        ArrayList<ArrayList<tuple<Integer,Float,Integer>>> tmp_index_in = new ArrayList<>(V);
        ArrayList<ArrayList<tuple<Integer,Float,Integer>>> tmp_index_out = new ArrayList<>(V);
        ArrayList<Float > arr_out_r = new ArrayList<>(V+1);
        ArrayList<Float > arr_in_r = new ArrayList<>(V+1);
        ArrayList<Float> dst_out_r = new ArrayList<>(V + 1);  //记录上一轮迭代的r的索引
        ArrayList<Float> dst_in_r =  new ArrayList<>(V + 1);
        //路径数组
        ArrayList<Integer> out_P = new ArrayList<>(V+1);
        ArrayList<Integer> in_P = new ArrayList<>(V+1);
        for(int t=0;t<V;t++){
            usd.add(false);//usd用来标记顶点是否已经被当做root产生索引了 (in new label)
            out_P.add(V);
            in_P.add(V);
            tmp_index_in.add(new ArrayList<>());
            tmp_index_out.add(new ArrayList<>());
            tmp_index_in.get(t).add(new tuple<>(V,-INF,V));
            tmp_index_out.get(t).add(new tuple<>(V,-INF,V));
            arr_out_r.add(-INF);
            arr_in_r.add(-INF);
            dst_in_r.add((float) 0);
            dst_out_r.add((float) 0);

        }
        arr_out_r.add(-INF);//顶点r到其他顶点的最宽距离，多了一个Sentinel，故为V+1
        arr_in_r.add(-INF);//其他顶点到r的最宽距离
        dst_in_r.add((float) 0);
        dst_out_r.add((float) 0);
        out_P.add(V);
        in_P.add(V);
        Comparator<iPair> paircmp;//重写比较器
        paircmp =new Comparator<iPair>() {
            @Override
            public int compare(iPair o1, iPair o2) {
                if(o1.first>o2.first){
                    return -1;
                }
                else if(o1.first<o2.first) {
                    return 1;
                }
                else return 0;
            }
        };
        Queue<iPair> maxHeap1=new PriorityQueue<iPair>(paircmp);
        Queue<iPair> maxHeap2 = new PriorityQueue<iPair>(paircmp);
        //这里是按边的权值从大到小排序,利用优先队列，重写比较方法构建最大堆


        for(int r = 0; r < V; r++){
            if(r == V / 10) System.out.println("10%");
            else if(r == V * 2/ 10) System.out.println("20%");
            else if(r == V * 3 / 10)System.out.println("30%");
            else if(r == V * 4 / 10) System.out.println("40%");
            else if(r == V / 2) System.out.println("50%");
            else if(r == V * 7 / 10) System.out.println("60%");
            else if(r == V * 9 / 10) System.out.println("70%");
            else if(r == V - 1) System.out.println("80%");
            if(usd.get(r)) continue;
            //初始化最宽距离数组
            for(int i = 0; i < V + 1; i++){
                arr_out_r.set(i,-INF);
                arr_in_r.set(i,-INF);
                out_P.set(i,V);
                in_P.set(i,V);
            }
            arr_in_r.set(r,INF);
            arr_out_r.set(r,INF);
            out_P.set(r,r);
            in_P.set(r,r);

            //上一轮所留下来的r索引,最宽距离保存在dst_out_t和dst_in_t中
            ArrayList<tuple<Integer,Float,Integer>> tmp_index_out_r = tmp_index_out.get(r);
            ArrayList<tuple<Integer,Float,Integer>> tmp_index_in_r = tmp_index_in.get(r);


            //r指向其他顶点的路径保存在paths中，每个路径是个arraylist，包含沿途的顶点ID
            for(int i=0;i<tmp_index_out_r.size();i++) {
                dst_out_r.set(tmp_index_out_r.get(i).first,tmp_index_out_r.get(i).second);
            }
            for(int i=0;i<tmp_index_in_r.size();i++) {
                dst_in_r.set(tmp_index_in_r.get(i).first,tmp_index_in_r.get(i).second);
            }
            //正向执行修改的Dijkstra算法
            maxHeap1.add(new iPair(INF,r));
            pruned1:
            while(!maxHeap1.isEmpty()){
                int max = maxHeap1.poll().second;//删除第一个节点
                ArrayList<tuple<Integer,Float,Integer>> tmp_index_in_max = tmp_index_in.get(max);

                //判断是否剪枝
                if(usd.get(max)) continue;//相当于直接跳到剪枝框pruned1里去了
                for(int i=0;i<tmp_index_in_max.size();i++) {
                    w = tmp_index_in_max.get(i).first;
                    float   td = dst_out_r.get(w) < tmp_index_in_max.get(i).second ? dst_out_r.get(w) : tmp_index_in_max.get(i).second;
                    d = arr_out_r.get(max);
                    if(td >= d) break pruned1;
                }
                //将该索引保存在tmp_index_in_max中

                tmp_index_in_max.set(tmp_index_in_max.size()-1,new tuple<>(r,arr_out_r.get(max),out_P.get(max)));
                tmp_index_in_max.add(new tuple<>(V,-INF,V));
                for(int i=0;i<adj_out.get(max).size();i++){
                    v = adj_out.get(max).get(i).first;
                    float weight = adj_out.get(max).get(i).second;
                    float dis = arr_out_r.get(max) > weight ? weight : arr_out_r.get(max);
                    if(arr_out_r.get(v) < dis){
                        if(arr_out_r.get(v) != -INF){
                            maxHeap1.remove(new iPair(arr_out_r.get(v),v));
                            //说明最大堆的v需要更新其value
                        }
                        arr_out_r.set(v,dis);
                        maxHeap1.add(new iPair(arr_out_r.get(v),v));
                        out_P.set(v,max);
                    }
                }
            }
            //逆向执行修改的Dijkstra算法
            maxHeap2.add(new iPair(INF,r));
            pruned2:
            while(!maxHeap2.isEmpty()){
                int max = maxHeap2.poll().second;
                ArrayList<tuple<Integer,Float,Integer>> tmp_index_out_max = tmp_index_out.get(max);

                //判断是否剪枝
                if(usd.get(max)) continue;
                for(int i=0;i<tmp_index_out_max.size();i++) {
                    w = tmp_index_out_max.get(i).first;
                    float   td = dst_in_r.get(w) < tmp_index_out_max.get(i).second ? dst_in_r.get(w) : tmp_index_out_max.get(i).second;
                    d = arr_in_r.get(max);
                    if(td >= d) break pruned2;
                }
                //将该索引保存在tmp_index_out_max中
                tmp_index_out_max.set(tmp_index_out_max.size()-1,new tuple<>(r,arr_in_r.get(max),in_P.get(max)));
                tmp_index_out_max.add(new tuple<>(V,-INF,V));
                for(int i=0;i<adj_in.get(max).size();i++){
                    v=adj_in.get(max).get(i).first;
                    float weight = adj_in.get(max).get(i).second;
                    float dis = arr_in_r.get(max) > weight ? weight : arr_in_r.get(max);
                    if(arr_in_r.get(v) < dis){
                        if(arr_in_r.get(v) != -INF){
                            maxHeap2.remove(new iPair(arr_in_r.get(v),v));//说明最大堆的v需要更新其value
                        }
                        arr_in_r.set(v,dis);
                        maxHeap2.add(new iPair(arr_in_r.get(v),v));
                        in_P.set(v,max);
                    }
                }
            }
            //将保存索引距离还原成-INF
            for(int i=0;i<tmp_index_out_r.size();i++) {
                dst_in_r.set(tmp_index_out_r.get(i).first,-INF);
            }
            for(int i=0;i<tmp_index_in_r.size();i++) {
                dst_out_r.set(tmp_index_in_r.get(i).first,-INF);
            }
            usd.set(r, true);
        }
        //上述for循环已经将所有索引保存在tmp_index_in、tmp_index_out中了
        for(v = 0; v < V; v++){
            int k = tmp_index_in.get(v).size();
            for(int i=0;i<k;i++) {
                this.index_in_.get(v).spt_v.add(i,tmp_index_in.get(v).get(i).first);
                this.index_in_.get(v).spt_d.add(i,tmp_index_in.get(v).get(i).second);
                this.index_in_.get(v).prev.add(tmp_index_in.get(v).get(i).third);
            }

            k = tmp_index_out.get(v).size();
            for(int i=0;i<k;i++) {
                this.index_out_.get(v).spt_v.add(i,tmp_index_out.get(v).get(i).first);
                this.index_out_.get(v).spt_d.add(i,tmp_index_out.get(v).get(i).second);
                this.index_out_.get(v).next.add(tmp_index_out.get(v).get(i).third);
            }

        }
        this.time_indexing_ += GetCurrentTimeSec();
        return true;
    };

    //返回顶点|v|到|w|的最宽距离，如果顶点没有有路径，返回|-INF
    public float queryDistance(int v, int w){
        if (v >= this.num_v_ || w >= this.num_v_){
            System.out.println("The input vertex numbers out of bound");
            return v == w ? INF : 0;
        }

        index_out idx_out_v = this.index_out_.get(v);
        index_in idx_in_w = this.index_in_.get(w);
        float d = -INF;
        //打印v的出索引label数量
        int v_num = 0;
        for(int i = 0; ; i++){
            int v1 = idx_out_v.spt_v.get(i);
            if(v1 == this.num_v_)
                break;
            v_num++;
        }
        System.out.println(v+"的出索引label数量: "+v_num);

        //打印w的入索引label数量
        int w_num = 0;
        for(int i = 0; ; i++){
            int v1 = idx_in_w.spt_v.get(i);
            if(v1 == this.num_v_) break;
            w_num++;
        }
        System.out.println(w+"的入索引label数量: "+w_num);
/*
        //打印v的出索引
        for(int i=0;;i++){
            int v1 = idx_out_v.spt_v.get(i);
            if(v1==num_v_)break;
            System.out.println(v+"->"+v1+",dis: "+idx_out_v.spt_d.get(i));
        }
        //打印w的入索引
        for(int i=0;;i++){
            int v2 = idx_in_w.spt_v.get(i);
            if(v2==num_v_)break;
            System.out.println(v2+"->"+w+",dis:"+idx_in_w.spt_d.get(i));
        }
        */
        for(int i1 = 0,i2 = 0;;){
            int v1 = idx_out_v.spt_v.get(i1), v2 = idx_in_w.spt_v.get(i2);
            if(v1 == v2){
                if(v1 == this.num_v_) {
                    break;
                }
                float d1 = idx_out_v.spt_d.get(i1);
                float d2 = idx_in_w.spt_d.get(i2);
                float td = d1 < d2 ? d1 : d2;
                if(td > d)
                    d = td;
                ++i1;
                ++i2;
            }else{
                i1 += v1 < v2 ? 1 : 0;
                i2 += v1 > v2 ? 1 : 0;
            }
        }
        return d;
    };


    //加载索引，当成功时返回|true|
    public boolean loadIndex(String filename) throws IOException {
        InputStream instream = new FileInputStream(filename);
        DataInputStream dis = new DataInputStream(instream);
        int num_v;
        Double loading_index_time = -GetCurrentTimeSec();
        num_v = dis.readInt();
        num_v_ = num_v;
        this.index_out_ = new ArrayList<>(num_v);
        this.index_in_ = new ArrayList<>(num_v);
        for(int times =0;times<num_v;times++){
            this.index_out_.add(new index_out());
            this.index_in_.add(new index_in());
        }
        for (int v = 0; v < num_v_; ++v){
            index_out idx_out = this.index_out_.get(v);
            index_in  idx_in  = this.index_in_.get(v);
            int s;
            s=dis.readInt();
            //idx_out，那么返回false，保证输入文件不符合格式的情况
            for (int i = 0; i < s; ++i) {
                idx_out.spt_v.add(i,dis.readInt());
                idx_out.spt_d.add(i,dis.readFloat());
                idx_out.next.add(i,dis.readInt());
            }
            s = dis.readInt();
            //idx_out，那么返回false，保证输入文件不符合格式的情况
            for (int i = 0; i < s; ++i) {
                idx_in.spt_v.add(i,dis.readInt());
                idx_in.spt_d.add(i,dis.readFloat());
                idx_in.prev.add(i,dis.readInt());
            }
        }
        instream.close();
        dis.close();
        loading_index_time +=GetCurrentTimeSec();
        System.out.println("load time:  "+loading_index_time+" seconds.");
        return true;
    };

    //存储索引到磁盘文件，成功返回|true|
    public boolean storeIndex(String filename) throws IOException {
        OutputStream os = new FileOutputStream(filename);
        DataOutputStream dos = new DataOutputStream(os);
        int num_v = num_v_;
        dos.writeInt(num_v);//注意这里写数据按顺序的，先写先读
        index_out idx_out = new index_out();
        for(int v = 0; v < num_v_; v++){
            idx_out = this.index_out_.get(v);
            int s;
            for(s = 1; idx_out.spt_v.get(s - 1) != num_v; s++) continue;
            dos.writeInt(s);  //s在这里就表示顶点v的出label数量
            for(int i = 0; i < s; i++){
                int l = idx_out.spt_v.get(i);
                float d = idx_out.spt_d.get(i);
                int n = idx_out.next.get(i);
                dos.writeInt(l);
                dos.writeFloat(d);
                dos.writeInt(n);
            }

            index_in idx_in = this.index_in_.get(v);
            for(s = 1; idx_in.spt_v.get(s - 1) != num_v; s++) continue;
            dos.writeInt(s);  //s在这里就表示顶点v的入label数量
            for(int i = 0; i < s; i++){
                int l = idx_in.spt_v.get(i);
                float d = idx_in.spt_d.get(i);
                int p =idx_in.prev.get(i);
                dos.writeInt(l);
                dos.writeFloat(d);
                dos.writeInt(p);
            }
        }
        dos.flush();
        os.close();
        dos.close();
        return true;
    };
/*
    public void showPath(int u ,int w){
        LinkedList<Integer> s = new LinkedList<>(); //保存路径的结果
        showWidestPath(u, w, s);
        while(!s.isEmpty()){
            System.out.print(s.pop());
            if(!s.isEmpty()) System.out.print("->");
            else System.out.println();
        }
    }
    */
    public LinkedList getPath_circle(int u, int w){
        LinkedList<Integer> s = new LinkedList<>(); //保存路径的结果
        showWidestPath(u, w, s);
        LinkedList paths = new LinkedList();
        while(!s.isEmpty()){
            paths.add(s.pop());
        }

        return paths;
    }

    public LinkedList getPath(int u,int w){
        LinkedList<Integer> s = new LinkedList<>(); //保存路径的结果
        showWidestPath(u, w, s);
        LinkedList paths = new LinkedList();

        ListNode node = new ListNode(0);
        ListNode root = node;
        ListNode head = node;
        while(!s.isEmpty()){
            int a = s.pop();
            /*
            if(a==w&&!s.isEmpty()){
                node.next = new ListNode(a);
                root = root.next;
                while (root!=null){
                    paths.add(root.value);
                    root = root.next;
                }
                return paths;
            }
            */
            node.next = new ListNode(a);
            node = node.next;
        }
        root =root.next;
        head = head.next;
        while (head!=null){
            ListNode k =head.next;
            while(k!=null){
                if(k.value==head.value){
                    head.next = k.next;
                }
                k=k.next;
                if(k==null) break;
            }
            head = head.next;
            if(head==null) break;
        }
        while (root!=null){
            paths.add(root.value);
            root = root.next;
        }

        return paths;
    }

    public void showWidestPath(int u, int w, LinkedList<Integer> s){
        if(u >= num_v_ || w >= num_v_) return;
        if(u == w) {s.push(w); return;}
        index_out idx_out_u = index_out_.get(u);
        index_in idx_in_w = index_in_.get(w);
        float d = -INF;
        boolean flag = true; //true表示使用prev，否则使用next

        int ans1, ans2;
        ans1 = ans2 = num_v_;
        for(int i1 = 0, i2 = 0; ; ){
            int v1 = idx_out_u.spt_v.get(i1), v2 = idx_in_w.spt_v.get(i2);
            if(v1 == v2){
                if(v1 == num_v_) break;
                float d1 = idx_out_u.spt_d.get(i1);
                float d2 = idx_in_w.spt_d.get(i2);
                float td = d1 + d2;
                if(td > d) {
                    d = td;
                    ans1 = i1;
                    ans2 = i2;
                    if(v1 == u || idx_in_w.prev.get(i2) == u) flag = true;
                    if(v2 == w || idx_out_u.next.get(i1) == w) flag = false;
                }
                ++i1;
                ++i2;
            }else{
                i1 += v1 < v2 ? 1 : 0;
                i2 += v1 > v2 ? 1 : 0;
            }
        }
        if(d == -INF) return;
        int next = idx_out_u.next.get(ans1);
        int prev = idx_in_w.prev.get(ans2);
        if(flag){
            s.push(w);
            showWidestPath(u, prev, s);
        }else{
            showWidestPath(next, w, s);
            s.push(u);
        }
    };

    //返回顶点数量
    public int getVerticeNum(){
        return num_v_;
    }
    //打印构建索引时间s，查询平均时间us
    public void printStatistic(){
        System.out.println("load time:  "+time_load_+"seconds");
        System.out.println("indexing time:  "+time_indexing_+"seconds");

    };
    //获取当前系统时间，单位秒（s）
    public Double GetCurrentTimeSec() {
        Long nanoTime = System.nanoTime(); // 纳秒
        return  nanoTime / 1000000000.0;
    }

    //构造方法
    public void PllWidestPath(){
        this.num_v_ = 0;
        this.index_in_ = null;
        this.index_out_ = null;
        this.time_load_ = 0.0;
        this.time_indexing_ = 0.0;
    }
    //主方法
    public static void main(String[] args) throws IOException {
        if (args.length != 1){
            System.out.println("usage: load_index GRAPH INDEX");
            System.exit(0);
        }
        PllWidestPath pll = new PllWidestPath();
/*
        if(!pll.constructIndex(args[0])){
            System.out.println("error: Construction failed");
            System.exit(0);
        }
        pll.printStatistic();
        if(!pll.storeIndex(args[1])){
            System.out.println("error: Store failed");
            System.exit(0);
        }
        */
        if(pll.loadIndex(args[0])){
            System.out.println("Loading succeeds.");
        }
        Scanner sc = new Scanner(System.in);
        System.out.println("Scanner, Please Enter u and w:");
        while (true){
            int u = sc.nextInt();
            int w = sc.nextInt();
            //while(true){
            Double query_time = -pll.GetCurrentTimeSec();
            System.out.println("distance: " +pll.queryDistance(u, w));
            query_time += pll.GetCurrentTimeSec();
            System.out.println("query_time:" +query_time  + "seconds" );
            System.out.print("path:");
            LinkedList paths = pll.getPath(u,w);
            Double aftercorrection = -pll.GetCurrentTimeSec();
            for(int i=0;i<paths.size();i++){
                System.out.print(paths.get(i));
                System.out.print("->");
            }
            aftercorrection += pll.GetCurrentTimeSec();
            System.out.print("The correction time cost is"+ aftercorrection);
            // }
        }


    }
}

