package org.example.juc.Thread;

import org.junit.jupiter.api.Test;

/**
 * @Author: yuanchao
 * @DATE: 2023/8/7 17:31
 */
public class RunnableTest {


    @Test
    void testRunable()
    {
        TestRunnable testRunnable=new TestRunnable("============TestRunnable=========");
        testRunnable.run();
    }


    class TestRunnable implements Runnable
    {
        private String name;

        public TestRunnable(String name) {
            this.name = name;
        }

        @Override
        public void run() {
            System.out.println(name);
        }
    }
}
