package org.example.juc.Thread;

public class ThreadLocalTest {

    static ThreadLocal threadLocalOne=new ThreadLocal();
    static ThreadLocal threadLocalTwo=new ThreadLocal();


    public static void main(String[] args) {
        threadLocalOne.set(11);
        threadLocalTwo.set(22);

        Thread t1=new Thread(()->{
            System.out.println("threadLocalOne:" + threadLocalOne.get());
            System.out.println("threadLocalTwo:" + threadLocalTwo.get());
        });

        t1.start();

        System.out.println("main:"+threadLocalOne.get());
        System.out.println("main:"+threadLocalTwo.get());
    }
}
