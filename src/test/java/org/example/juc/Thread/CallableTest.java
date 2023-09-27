package org.example.juc.Thread;

import org.junit.jupiter.api.Test;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

/**
 * @Author: yuanchao
 * @DATE: 2023/8/8 13:29
 */
public class CallableTest {


    @Test
    void testCallable() throws InterruptedException {
        TestCallable testCallable=new TestCallable();
        FutureTask<String> futureTask=new FutureTask<>(testCallable);
        Thread t1 =new Thread(futureTask);
        t1.start();
        Thread.sleep(5000);
        System.out.println(t1.getState());
    }


    class TestCallable implements Callable<String> {

        @Override
        public String call() throws Exception {
            System.out.println("执行call方法");
            Thread.sleep(5000);

            wait();
            return "111";
        }
    }

}


