package com.scarlett.myserver.pll;

class tuple<A, B, C> {
    public A first;
    public B second;
    public C third;
    public tuple(A a, B b, C c) {
        this.first = a;
        this.second = b;
        this.third = c;
    }
    public A first(){ return first;}
    public B second(){ return second; }
    public C third() { return third; }
}
