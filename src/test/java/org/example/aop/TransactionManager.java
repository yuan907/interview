package org.example.aop;

// Step 1: 定义事务管理器接口
public interface TransactionManager {

    void beginTransaction();

    void commitTransaction();

    void rollbackTransaction();

}
