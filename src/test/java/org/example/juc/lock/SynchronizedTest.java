package org.example.juc.lock;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.Test;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * @Author: yuanchao
 * @DATE: 2023/8/9 18:09
 * 使用线程池交替打印0-100
 */
public class SynchronizedTest {

    private static Object object = new Object();

    private static volatile int num=0;

    public static void main(String[] args) {

        Executor executor = Executors.newFixedThreadPool(2);

        executor.execute(new Runnable() {
            @SneakyThrows
            @Override
            public void run() {
                while (num<100)
                {
                    synchronized (object)
                    {
                        while (num%2==0)
                        {
                            object.wait();
                        }
                        System.out.println(Thread.currentThread().getName()+":"+num++);
                        object.notifyAll();
                    }
                }
            }
        });
        executor.execute(new Runnable() {
            @SneakyThrows
            @Override
            public void run() {
                while (num<100)
                {
                    synchronized (object)
                    {
                        while (num%2!=0)
                        {
                            object.wait();
                        }
                        System.out.println(Thread.currentThread().getName()+":"+num++);
                        object.notifyAll();
                    }
                }
            }
        });
    }


}
