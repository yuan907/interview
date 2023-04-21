package org.example;

import org.camunda.bpm.engine.*;
import org.camunda.bpm.engine.repository.Deployment;
import org.camunda.bpm.engine.task.Task;
import org.junit.Test;

import java.util.List;

/**
 * @Author: yuanchao
 * @DATE: 2023/3/10 17:15
 */
public class test2 {

    @Test
    public void test()
    {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();

        RepositoryService repositoryService = processEngine.getRepositoryService();

        TaskService taskService = processEngine.getTaskService();

        List<Task> list = taskService.createTaskQuery().list();



    }
}
