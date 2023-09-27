package org.example.aop;

import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

//@Aspect
@Component
public class TransactionAspect {

    @Autowired
    private TransactionManager transactionManager;

//    @Before("execution(* com.example.target.*.*(..))")
//    public void beforeAdvice() {
//        transactionManager.beginTransaction();
//    }
//
//    @AfterReturning(pointcut = "execution(* com.example.target.*.*(..))", returning = "result")
//    public void afterReturningAdvice(Object result) {
//        transactionManager.commitTransaction();
//    }
//
//    @AfterThrowing(pointcut = "execution(* com.example.target.*.*(..))", throwing = "ex")
//    public void afterThrowingAdvice(Exception ex) {
//        transactionManager.rollbackTransaction();
//    }

}
