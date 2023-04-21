package org.example;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.*;
import org.camunda.bpm.engine.history.HistoricActivityStatisticsQuery;
import org.camunda.bpm.engine.history.HistoricProcessInstance;
import org.camunda.bpm.engine.history.HistoricTaskInstance;
import org.camunda.bpm.engine.identity.Group;
import org.camunda.bpm.engine.identity.GroupQuery;
import org.camunda.bpm.engine.identity.User;
import org.camunda.bpm.engine.identity.UserQuery;
import org.camunda.bpm.engine.repository.Deployment;
import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.camunda.bpm.engine.repository.ProcessDefinitionQuery;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.runtime.ProcessInstantiationBuilder;
import org.camunda.bpm.engine.task.Comment;
import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.spring.boot.starter.annotation.EnableProcessApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import spinjar.com.sun.xml.bind.v2.TODO;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @Author: yuanchao
 * @DATE: 2023/3/3 16:19
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
@EnableProcessApplication
public class stratTest {

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private HistoryService historyService;

    @Autowired
    private IdentityService identityService;


    /**
     * 完成流程的部署操作
     */
    @Test
    public void testDeploy() {
        // 1.创建ProcessEngine
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        // 2.使用ProcessEngine得到操作数据库的服务(RepositoryService)
        RepositoryService repositoryService = processEngine.getRepositoryService();
        // 3.使用RepositoryService进行流程部署，定义一个流程的名字，把bpmn文件和png文件部署到数据库里
        Deployment deploy = repositoryService.createDeployment()
                .name("出差申请流程")//流程名称
                .addClasspathResource("processes/simpleTest.bpmn20.xml") //资源文件 bpmn配置文件
//                .disableSchemaValidation()//禁止校验文件
                .deploy();
        // 4.输出部署信息
        System.out.println("流程部署的ID:" + deploy.getId());
        System.out.println("流程部署的名字:" + deploy.getName());
    }

    /**
     * 查询代办任务
     * */
    @Test
    public void queryTaskList()
    {
        List<Task> list = taskService.createTaskQuery().list();

        for (Task task:list)
        {
            String assignee = task.getAssignee();

            System.out.println(11);
        }
    }

    /**
     * 查询已办任务
     * */
    @Test
    public void queryFinishTask()
    {
        ProcessEngine processEngine=ProcessEngines.getDefaultProcessEngine();

        List<HistoricTaskInstance> finishTaskList = processEngine.getHistoryService().createHistoricTaskInstanceQuery().finished().list();

        System.out.println(11);
    }


    /**
     * 执行任务
     **/
    @Test
    public void testTask() {
//        List<ProcessInstance> list = runtimeService.createProcessInstanceQuery().processDefinitionKey("xmlTest").active().list();
        List<ProcessInstance> list = runtimeService.createProcessInstanceQuery()
                .processDefinitionId("Process_1n6xfcx:1:c95387e8-beeb-11ed-94de-080058000005").active().list();
        if (list.isEmpty()) {
            throw new RuntimeException("没有流程实例！");
        }
        ProcessInstance processInstance = list.get(0);
        // 查询task
//        Task task = taskService.createTaskQuery()
//                .processInstanceId(processInstance.getId())
//                .active()
//                .singleResult();
//        // 强制拾取操作
//        taskService.setAssignee(task.getId(), task.getTaskDefinitionKey());
//        // 完成操作
//        taskService.complete(task.getId());

        List<Task> list1 = taskService.createTaskQuery()
                .processInstanceId(processInstance.getId())
                .active().list();
        for (Task task : list1) {

            Map<String, Object> valueMap = runtimeService.getVariables(task.getExecutionId());

            System.out.println(11);
//            //强制拾取操作
//            taskService.setAssignee(task.getId(), task.getTaskDefinitionKey());
//            // 完成操作
//            taskService.complete(task.getId());
        }
    }


    @Test
    public void queryHistory() {
        HistoricActivityStatisticsQuery historicActivityStatisticsQuery = historyService.createHistoricActivityStatisticsQuery("796dbe1b-bc89-11ed-bf1a-080058000005");

        System.out.println(111);

    }


    /**
     * 根据字符串部署流程
     **/
    @Test
    public void testCreateBpmn() {

        String xmlText = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                " \n" +
                "<bpmn:definitions xmlns:bpmn=\"http://www.omg.org/spec/BPMN/20100524/MODEL\" xmlns:bpmndi=\"http://www.omg.org/spec/BPMN/20100524/DI\" xmlns:dc=\"http://www.omg.org/spec/DD/20100524/DC\" xmlns:camunda=\"http://camunda.org/schema/1.0/bpmn\" xmlns:di=\"http://www.omg.org/spec/DD/20100524/DI\" xmlns:modeler=\"http://camunda.org/schema/modeler/1.0\" id=\"Definitions_1lsa7z1\" targetNamespace=\"http://bpmn.io/schema/bpmn\" exporter=\"Camunda Modeler\" exporterVersion=\"4.8.1\" modeler:executionPlatform=\"Camunda Platform\" modeler:executionPlatformVersion=\"7.15.0\">\n" +
                " \n" +
                "  <bpmn:process id=\"Process_1n6xfcx\" name=\"测试会签流程\" isExecutable=\"true\" camunda:versionTag=\"1.0\">\n" +
                " \n" +
                "    <bpmn:startEvent id=\"StartEvent_1\">\n" +
                " \n" +
                "      <bpmn:outgoing>Flow_153mh25</bpmn:outgoing>\n" +
                " \n" +
                "    </bpmn:startEvent>\n" +
                " \n" +
                "    <bpmn:sequenceFlow id=\"Flow_153mh25\" sourceRef=\"StartEvent_1\" targetRef=\"Activity_18krv16\" />\n" +
                " \n" +
                "    <bpmn:sequenceFlow id=\"Flow_1258z36\" sourceRef=\"Activity_18krv16\" targetRef=\"Activity_1udxyrq\" />\n" +
                " \n" +
                "    <bpmn:sequenceFlow id=\"Flow_0248t03\" sourceRef=\"Activity_1udxyrq\" targetRef=\"Activity_0neq0ra\" />\n" +
                " \n" +
                "    <bpmn:endEvent id=\"Event_00b1j9n\">\n" +
                " \n" +
                "      <bpmn:incoming>Flow_0ucs2b7</bpmn:incoming>\n" +
                " \n" +
                "    </bpmn:endEvent>\n" +
                " \n" +
                "    <bpmn:sequenceFlow id=\"Flow_0ucs2b7\" sourceRef=\"Activity_0neq0ra\" targetRef=\"Event_00b1j9n\" />\n" +
                " \n" +
                "    <bpmn:userTask id=\"Activity_18krv16\" name=\"申请\">\n" +
                " \n" +
                "      <bpmn:extensionElements>\n" +
                " \n" +
                "        <camunda:executionListener event=\"start\">\n" +
                " \n" +
                "          <camunda:script scriptFormat=\"groovy\">def userList = ['root1', 'root2', 'root3'];execution.setVariable(\"assigneeList\", userList);</camunda:script>\n" +
                " \n" +
                "        </camunda:executionListener>\n" +
                " \n" +
                "      </bpmn:extensionElements>\n" +
                " \n" +
                "      <bpmn:incoming>Flow_153mh25</bpmn:incoming>\n" +
                " \n" +
                "      <bpmn:outgoing>Flow_1258z36</bpmn:outgoing>\n" +
                " \n" +
                "    </bpmn:userTask>\n" +
                " \n" +
                "    <bpmn:userTask id=\"Activity_1udxyrq\" name=\"会签\" camunda:assignee=\"${assignee}\">\n" +
                " \n" +
                "      <bpmn:incoming>Flow_1258z36</bpmn:incoming>\n" +
                " \n" +
                "      <bpmn:outgoing>Flow_0248t03</bpmn:outgoing>\n" +
                " \n" +
                "      <bpmn:multiInstanceLoopCharacteristics camunda:collection=\"assigneeList\" camunda:elementVariable=\"assignee\" />\n" +
                " \n" +
                "    </bpmn:userTask>\n" +
                " \n" +
                "    <bpmn:userTask id=\"Activity_0neq0ra\" name=\"审批\" camunda:assignee=\"user2\">\n" +
                " \n" +
                "      <bpmn:incoming>Flow_0248t03</bpmn:incoming>\n" +
                " \n" +
                "      <bpmn:outgoing>Flow_0ucs2b7</bpmn:outgoing>\n" +
                " \n" +
                "    </bpmn:userTask>\n" +
                " \n" +
                "  </bpmn:process>\n" +
                " \n" +
                "  <bpmndi:BPMNDiagram id=\"BPMNDiagram_1\">\n" +
                " \n" +
                "    <bpmndi:BPMNPlane id=\"BPMNPlane_1\" bpmnElement=\"Process_1n6xfcx\">\n" +
                " \n" +
                "      <bpmndi:BPMNEdge id=\"Flow_0ucs2b7_di\" bpmnElement=\"Flow_0ucs2b7\">\n" +
                " \n" +
                "        <di:waypoint x=\"690\" y=\"117\" />\n" +
                " \n" +
                "        <di:waypoint x=\"752\" y=\"117\" />\n" +
                " \n" +
                "      </bpmndi:BPMNEdge>\n" +
                " \n" +
                "      <bpmndi:BPMNEdge id=\"Flow_0248t03_di\" bpmnElement=\"Flow_0248t03\">\n" +
                " \n" +
                "        <di:waypoint x=\"530\" y=\"117\" />\n" +
                " \n" +
                "        <di:waypoint x=\"590\" y=\"117\" />\n" +
                " \n" +
                "      </bpmndi:BPMNEdge>\n" +
                " \n" +
                "      <bpmndi:BPMNEdge id=\"Flow_1258z36_di\" bpmnElement=\"Flow_1258z36\">\n" +
                " \n" +
                "        <di:waypoint x=\"370\" y=\"117\" />\n" +
                " \n" +
                "        <di:waypoint x=\"430\" y=\"117\" />\n" +
                " \n" +
                "      </bpmndi:BPMNEdge>\n" +
                " \n" +
                "      <bpmndi:BPMNEdge id=\"Flow_153mh25_di\" bpmnElement=\"Flow_153mh25\">\n" +
                " \n" +
                "        <di:waypoint x=\"215\" y=\"117\" />\n" +
                " \n" +
                "        <di:waypoint x=\"270\" y=\"117\" />\n" +
                " \n" +
                "      </bpmndi:BPMNEdge>\n" +
                " \n" +
                "      <bpmndi:BPMNShape id=\"_BPMNShape_StartEvent_2\" bpmnElement=\"StartEvent_1\">\n" +
                " \n" +
                "        <dc:Bounds x=\"179\" y=\"99\" width=\"36\" height=\"36\" />\n" +
                " \n" +
                "      </bpmndi:BPMNShape>\n" +
                " \n" +
                "      <bpmndi:BPMNShape id=\"Event_00b1j9n_di\" bpmnElement=\"Event_00b1j9n\">\n" +
                " \n" +
                "        <dc:Bounds x=\"752\" y=\"99\" width=\"36\" height=\"36\" />\n" +
                " \n" +
                "      </bpmndi:BPMNShape>\n" +
                " \n" +
                "      <bpmndi:BPMNShape id=\"Activity_068boyx_di\" bpmnElement=\"Activity_18krv16\">\n" +
                " \n" +
                "        <dc:Bounds x=\"270\" y=\"77\" width=\"100\" height=\"80\" />\n" +
                " \n" +
                "      </bpmndi:BPMNShape>\n" +
                " \n" +
                "      <bpmndi:BPMNShape id=\"Activity_10kl1f8_di\" bpmnElement=\"Activity_1udxyrq\">\n" +
                " \n" +
                "        <dc:Bounds x=\"430\" y=\"77\" width=\"100\" height=\"80\" />\n" +
                " \n" +
                "      </bpmndi:BPMNShape>\n" +
                " \n" +
                "      <bpmndi:BPMNShape id=\"Activity_1duyn2a_di\" bpmnElement=\"Activity_0neq0ra\">\n" +
                " \n" +
                "        <dc:Bounds x=\"590\" y=\"77\" width=\"100\" height=\"80\" />\n" +
                " \n" +
                "      </bpmndi:BPMNShape>\n" +
                " \n" +
                "    </bpmndi:BPMNPlane>\n" +
                " \n" +
                "  </bpmndi:BPMNDiagram>\n" +
                " \n" +
                "</bpmn:definitions>";

        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        Deployment deploy = processEngine.getRepositoryService()
                .createDeployment()
                .name("会签测试")
                .addString("会签流程测试.bpmn20.xml", xmlText).deploy();
        System.out.println("流程部署的ID:" + deploy.getId());
        System.out.println("流程部署的名字:" + deploy.getName());


        ProcessDefinition processDefinition = processEngine.getRepositoryService().createProcessDefinitionQuery()
                .deploymentId(deploy.getId()).singleResult();

        System.out.println("流程部署id:" + processDefinition.getId());

    }


    /**
     * 获取部署
     */
    @Test
    public void testGetDeploy() {
        Deployment deployment = repositoryService.createDeploymentQuery().deploymentId("bdfa1195-bda1-11ed-86ad-080058000005").singleResult();

        System.out.println("deployment:" + deployment);
    }


    /**
     * 查询流程定义
     */
    @Test
    public void queryProcessDefinition() {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        List<ProcessDefinition> list = processEngine.getRepositoryService()
                .createProcessDefinitionQuery()//创建一个流程定义查询
                /**查询条件**/
                // .deploymentId(deploymentId)//部署对象id查询
//                 .processDefinitionId("bf720fc0-bd9a-11ed-b5b9-080058000005")// 流程定义id查询
                .processDefinitionKey("bf720fc0-bd9a-11ed-b5b9-080058000005")// 流程定义key查询
                // .processDefinitionNameLike(processDefinitionNameLike) // 流程名称模糊查询
                /**排序***/
                // .orderByProcessDefinitionVersion().desc()
                .orderByProcessDefinitionName().asc()
                /**返回结果集**/
                .list(); //返回一个集合
        // .singleResult() // 返回唯一结果集
        // .count() // 返回结果集数量
        // .listPage(firstResult, maxResults) // 分页查询
        if (list != null && !list.isEmpty()) {
            for (ProcessDefinition processDefinition : list) {
                System.out.println(processDefinition.getId());
                System.out.println(processDefinition.getDeploymentId());
                System.out.println(processDefinition.getKey());//对应bpmn文件中的id
                System.out.println(processDefinition.getName());//对应bpmn文件中的name
                System.out.println(processDefinition.getResourceName());
                System.out.println(processDefinition.getDiagramResourceName());
                System.out.println(processDefinition.getVersion());
                System.out.println(processDefinition.getTenantId());
                System.out.println("---------------------------------");
            }
        }
    }

    @Test
    public void testStart() {
        Map<String, Object> params = new HashMap<>();
        params.put("userName", "yuanchao");
        params.put("age", "22");
        params.put("money", 300);

//        ProcessInstance simpleTest = runtimeService.startProcessInstanceByKey("xmlTest",params);

        ProcessInstance simpleTest = runtimeService.startProcessInstanceById
                ("Process_1n6xfcx:2:e5eb2aae-c167-11ed-a97c-080058000005", params);

        System.out.println("流程实例的ProcessInstanceId: " + simpleTest.getId());
        System.out.println("流程实例的ProcessDefinitionId: " + simpleTest.getProcessDefinitionId());
        System.out.println("————————————————————————————————————————————————————");
    }


    /**
     * 删除流程定义
     */
    @Test
    public void deleteProcessDefinition() {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        String deploymentId = "95aa0d04-be3f-11ed-acfb-080058000005";
        processEngine.getRepositoryService()
                // 如果已启动流程报异常
                // .deleteDeployment(deploymentId); //使用部署id删除
                // 级联删除，删除已经启动的流程
                .deleteDeployment(deploymentId, true);
        System.out.println("删除成功：" + deploymentId);
    }

    /**
     * 根据流程号获取任务列表
     */
    @Test
    public void getTaskListByTaskID() {
        String taskId = "66cda18a-be1b-11ed-8802-080058000005";
        List<Task> taskList = new ArrayList<>();

        taskList = taskService.createTaskQuery().processInstanceId(taskId).list();

        System.out.println(11);
    }

    /**
     * 查询组和用户
     */
    @Test
    public void queryGroup() {
        GroupQuery groupQuery = identityService.createGroupQuery();
        List<Group> list = groupQuery.list();
        for (int i = 0; i < list.size(); i++) {
            Group group = list.get(i);
            System.out.println(group.getId());
            System.out.println(group.getName());
            System.out.println(group.getType());
        }

        UserQuery userQuery = identityService.createUserQuery();
        List<User> list1 = userQuery.list();
        System.out.println();
    }


    @Test
    public void test()
    {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();

        RepositoryService repositoryService = processEngine.getRepositoryService();

        TaskService taskService = processEngine.getTaskService();

        ManagementService managementService = processEngine.getManagementService();


        /**查询代办任务* */
        List<Task> taskList = taskService.createTaskQuery().processDefinitionId("Process_1n6xfcx:2:e5eb2aae-c167-11ed-a97c-080058000005").list();

        /**查询开启流程时一并传入的参数*/
        Map<String, Object> variables = runtimeService.getVariables(taskList.get(0).getExecutionId());
        System.out.println("=============================================");

        /**查询流程定义*/
        List<ProcessDefinition> processDefinitions = repositoryService.createProcessDefinitionQuery()
                .processDefinitionId("Process_1n6xfcx:2:e5eb2aae-c167-11ed-a97c-080058000005")
                .orderByProcessDefinitionVersion()
                .asc()
                .list();
        System.out.println("=============================================");

        /**根据用户id查询代办任务列表,然后进行审批*/
        Task root1Task= taskService.createTaskQuery().taskAssignee("root").singleResult();

        Map<String,Object> result=new HashMap<>();
        result.put(root1Task.getName()+"result","ok");
        taskService.complete(root1Task.getId(),result);

        System.out.println("=============================================");

        /**任务完成  审批通过*/
        String taskId="e2e4fb2d-beec-11ed-8b8e-080058000005";
//        Task task=taskService.createTaskQuery().taskId(taskId).singleResult();
//        String processInstancesId=task.getProcessInstanceId();
//        IdentityService identityService = processEngine.getIdentityService();
//        identityService.setAuthenticatedUserId("root1"); //这设置的时审批人及查询意见时的userId。
//        taskService.addComment(taskId,processInstancesId,"审批意见测试111111");
//        //idea意思是完成时的审批意见，可在Act_Hi_Comment里的massge查询到
//        Map<String,Object> map = new HashMap<>();
//        map.put("条件1","条件11111111");//这个map根据bpmn情况定，传入complete方法
//        taskService.complete(taskId);//可多参条件。
//        System.out.println("=============================================");


        /**审批拒绝*/
        //查找当前用户的审批任务
        List<Task> root2Task = taskService.createTaskQuery().taskAssignee("root2").
                processDefinitionId("Process_1n6xfcx:1:c95387e8-beeb-11ed-94de-080058000005").list();

        System.out.println("=============================================");


        /**查询审批意见 使用已经完成任务id*/
        HistoryService hisService = processEngine.getHistoryService();
        List<Comment> hisTaskList=taskService.getTaskComments(taskId);
        System.out.println("=============================================");

        /**查询这个流程的审批意见,用为未完成任务id查整个流程已完成任务的审批意见内容*/
        HistoryService historyService=processEngine.getHistoryService();
        List<Comment> list = new ArrayList();
        Task task = taskService.createTaskQuery()//
                .taskId("e2e4fb30-beec-11ed-8b8e-080058000005")//使用任务ID查询
                .singleResult();
        //获取流程实例ID
        String processInstanceId = task.getProcessInstanceId();
        //使用流程实例ID，查询历史任务，获取历史任务对应的每个任务ID
        List<HistoricTaskInstance> htiList = historyService.createHistoricTaskInstanceQuery()//历史任务表查询
                .processInstanceId(processInstanceId)//使用流程实例ID查询
                .list();
        //遍历集合，获取每个任务ID
        for(HistoricTaskInstance hti:htiList){//任务ID
            String htaskId = hti.getId();//获取批注信息
            List taskAllList = taskService.getTaskComments(htaskId);//对用历史完成后的任务ID                list.addAll(taskList);
            System.out.println(111);
        }
        System.out.println("=============================================");

        /**查询当前流程状态*/
        //获取历史流程实例
//        HistoricProcessInstance historicProcessInstance = hisService.createHistoricProcessInstanceQuery()
//                .processDefinitionId("Process_1n6xfcx:1:c95387e8-beeb-11ed-94de-080058000005").singleResult();
//        //根据流程定义获取输入流
//        InputStream is = repositoryService.getProcessDiagram("Process_1n6xfcx:1:c95387e8-beeb-11ed-94de-080058000005");
//        BufferedImage bi = null;
//        try {
//            bi = ImageIO.read(is);
//            File file = new File("demo2.png");
//            if(!file.exists()) file.createNewFile();
//            FileOutputStream fos = new FileOutputStream(file);
//            ImageIO.write(bi, "png", fos);
//            fos.close();
//            is.close();
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//
//        List<Task> tasks = taskService.createTaskQuery().taskCandidateUser("userId").list();
//        for(Task t : tasks) {
//            System.out.println(t.getName());
//        }
        System.out.println("=============================================");

        /**完结任务*/
//        runtimeService.deleteProcessInstance(task.getProcessInstanceId(),"提前结束测试");
//        System.out.println("=============================================");
    }

}
