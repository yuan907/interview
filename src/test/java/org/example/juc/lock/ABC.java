package org.example.juc.lock;

import java.util.concurrent.Semaphore;

/**
 * @Author: yuanchao
 * @DATE: 2023/8/23 13:56
 * 使用信号量交替打印ABC
 */
public class ABC {

    public static void main(String[] args) {

        final Semaphore first = new Semaphore(0);
        final Semaphore second = new Semaphore(0);
        final Semaphore third = new Semaphore(0);

        final Thread t1 = new Thread(() -> {
            try {
                for (; ; ) {
                    System.out.print(1);
                    Thread.sleep(1000);
                    second.release();
                    first.acquire();
                }
            } catch (InterruptedException exception) {

            }
        }, "t1");

        final Thread t2 = new Thread(() -> {
            try {
                for (; ; ) {
                    second.acquire();
                    System.out.print(2);
                    Thread.sleep(1000);
                    third.release();
                }
            } catch (InterruptedException exception) {

            }
        }, "t2");

        final Thread t3 = new Thread(() -> {
            try {
                for (; ; ) {
                    third.acquire();
                    System.out.print(3);
                    Thread.sleep(1000);
                    first.release();
                }
            } catch (InterruptedException exception) {

            }
        }, "t3");

        t1.start();
        t2.start();
        t3.start();

    }

}