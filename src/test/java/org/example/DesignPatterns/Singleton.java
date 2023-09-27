package org.example.DesignPatterns;

import lombok.SneakyThrows;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Constructor;

/**
 * 单例模式
 * */
public class Singleton {

    //饿汉式  在类加载的时候就进行创建
    public Singleton() {
    }
    private static Singleton singleton = new Singleton();


    public static Singleton getInstance()
    {
        return singleton;
    }

    private Object readResolve() {
        return singleton;
    }

    //第一次调用才初始化，避免内存浪费。 双重检查锁
    //懒汉式创建单例模式 由于懒汉式是非线程安全， 所以加上线程锁保证线程安全
//    private volatile static Singleton singleton;

//    private static boolean singletonFlag = false;

//    private Singleton() {
//        if (singleton != null || singletonFlag) {
//            throw new RuntimeException("试图用反射破坏异常");
//        }
//        singletonFlag = true;
//    }
//    public static Singleton getInstance() {
//        if (singleton == null) {
//            synchronized (Singleton.class) {
//                if (singleton == null) {
//                    singleton = new Singleton();
//                }
//            }
//        }
//        return singleton;
//    }
    /**
     * 破坏单例模式
     * 1.反序列化 解决方法只需要在单例类里加上一个readResolve()方法即可
     * 2.反射  解决办法为使用一个布尔类型的标记变量标记一下即可
     * */
    @SneakyThrows
    public static void main(String[] args) {

        //反序列化
//        Singleton singleton = Singleton.getInstance();
//        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("D:/test.txt"));
//        oos.writeObject(singleton);
//        oos.flush();
//        oos.close();
//
//        ObjectInputStream ois = new ObjectInputStream(new FileInputStream("D:/test.txt"));
//        Singleton singleton1 = (Singleton)ois.readObject();
//        ois.close();
//        System.out.println(singleton);//com.ruoyi.base.mapper.Singleton@50134894
//        System.out.println(singleton1);//com.ruoyi.base.mapper.Singleton@5ccd43c2

        //反射
        Singleton singleton = Singleton.getInstance();
        Class<Singleton> singletonClass = Singleton.class;
        Constructor<Singleton> constructor = singletonClass.getDeclaredConstructor();
        constructor.setAccessible(true);
        Singleton singleton1 = constructor.newInstance();
        System.out.println(singleton);//com.ruoyi.base.mapper.Singleton@32a1bec0
        System.out.println(singleton1);//com.ruoyi.base.mapper.Singleton@22927a81

    }

}
