package org.example.juc.Thread;

/**
 * @Author: yuanchao
 * @DATE: 2023/8/7 17:19
 */
public class ThreadTest {

    public static void main(String[] args) {

        TestThread thread=new TestThread("name");
        thread.run();
    }
    static class TestThread extends Thread{

        private String name;

        public TestThread(String name) {
            this.name = name;
        }

        @Override
        public void run() {
            System.out.println(name);
        }
    }
}
