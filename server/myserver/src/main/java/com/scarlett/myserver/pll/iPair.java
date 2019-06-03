package com.scarlett.myserver.pll;

public class iPair {
    public float first;
    public int second;
    public iPair(float first,int second){
        this.first = first;
        this.second = second;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof iPair)) return false;
        iPair ipair = (iPair) o;
        if((first != ipair.first)||(second != ipair.second)) return false;
        return true;
    }
}
