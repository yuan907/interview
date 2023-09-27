package org.example.DesignPatterns;


/**
 * 不保证整个应用全局唯一，但保证线程内部全局唯一，以空间换时间，且线程安全。
 */
public class ThreadLocalSingleton {

    private ThreadLocalSingleton() {
    }

    private static final ThreadLocal<ThreadLocalSingleton> threadLocalInstance = ThreadLocal.withInitial(() -> new ThreadLocalSingleton());

    public static ThreadLocalSingleton getInstance() {
        return threadLocalInstance.get();
    }

    public static void main(String[] args) {
        new Thread(() -> {
            System.out.println(Thread.currentThread().getName() + "-----" + ThreadLocalSingleton.getInstance());
            System.out.println(Thread.currentThread().getName() + "-----" + ThreadLocalSingleton.getInstance());
        }).start();
        new Thread(() -> {
            System.out.println(Thread.currentThread().getName() + "-----" + ThreadLocalSingleton.getInstance());
            System.out.println(Thread.currentThread().getName() + "-----" + ThreadLocalSingleton.getInstance());
        }).start();
//        Thread-0-----com.ruoyi.library.domain.vo.ThreadLocalSingleton@53ac93b3
//        Thread-1-----com.ruoyi.library.domain.vo.ThreadLocalSingleton@7fe11afc
//        Thread-0-----com.ruoyi.library.domain.vo.ThreadLocalSingleton@53ac93b3
//        Thread-1-----com.ruoyi.library.domain.vo.ThreadLocalSingleton@7fe11afc
    }
}
