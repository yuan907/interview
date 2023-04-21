package org.example.controller;


import org.camunda.bpm.engine.*;
import org.camunda.bpm.engine.history.HistoricActivityInstance;
import org.camunda.bpm.engine.history.HistoricProcessInstance;
import org.camunda.bpm.engine.history.HistoricTaskInstance;
import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.FlowNode;
import org.camunda.bpm.model.bpmn.instance.SequenceFlow;
import org.camunda.bpm.model.xml.instance.ModelElementInstance;
import org.example.vo.ActivitiHighLineDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;


/**
 * @Author: yuanchao
 * @DATE: 2023/3/3 16:19
 */
@RestController
public class StartController {

//    @Autowired
//    private RepositoryService repositoryService;

    @Autowired
    private RuntimeService runtimeService;


    @PostMapping("/process/{processDefinitionId}")
    public String startProcess(@PathVariable(value = "processDefinitionId") String processDefinitionId, @RequestBody Map<String, Object> map) {

        ProcessInstance simpleTest = runtimeService.startProcessInstanceById(processDefinitionId, map);
        return simpleTest.getProcessInstanceId();
    }


    @PostMapping("/getBpmnXml")
    public String getBpmnXml() throws Exception {
        String processDefinedID = "Process_1n6xfcx:2:e5eb2aae-c167-11ed-a97c-080058000005";
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        RepositoryService repositoryService = processEngine.getRepositoryService();

        ProcessDefinition processDefinition = repositoryService.getProcessDefinition(processDefinedID);
        InputStream b = repositoryService.getResourceAsStream(processDefinition.getDeploymentId(), processDefinition.getResourceName());
        BufferedInputStream bis = new BufferedInputStream(b);
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        int result = bis.read();
        while (result != -1) {
            buf.write((byte) result);
            result = bis.read();
        }
        bis.close();
        String str = buf.toString("UTF-8");

        return str;
    }

    @PostMapping("/getHighLight")
    public ActivitiHighLineDTO getHighLight()
    {
        String processInsId = "Process_1n6xfcx:2:e5eb2aae-c167-11ed-a97c-080058000005";
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        HistoryService historyService = processEngine.getHistoryService();
        RepositoryService repositoryService = processEngine.getRepositoryService();


        HistoricProcessInstance hisProIns = historyService.createHistoricProcessInstanceQuery().processDefinitionId(processInsId).singleResult();
        //System.out.println(hisProIns.getProcessDefinitionName()+" "+hisProIns.getProcessDefinitionKey());
        //===================已完成节点
        List<HistoricActivityInstance> finished = historyService.createHistoricActivityInstanceQuery()
                .processDefinitionId(processInsId)
                .finished()
                .orderByHistoricActivityInstanceStartTime().asc()
                .list();
        Set<String> highPoint = new HashSet<>();
        finished.forEach(t -> highPoint.add(t.getActivityId()));

        //=================待完成节点
        List<HistoricActivityInstance> unfinished = historyService.createHistoricActivityInstanceQuery().processDefinitionId(processInsId).unfinished().list();
        Set<String> waitingToDo = new HashSet<>();
        unfinished.forEach(t -> waitingToDo.add(t.getActivityId()));

        //=================iDo 我执行过的
        Set<String> iDo = new HashSet<>(); //存放 高亮 我的办理节点
//        List<HistoricTaskInstance> taskInstanceList = historyService.createHistoricTaskInstanceQuery().taskAssignee(SecurityUtils.getUsername()).finished().processInstanceId(processInsId).list();
//        taskInstanceList.forEach(a -> iDo.add(a.getTaskDefinitionKey()));

        //===========高亮线
        Set<String> highLine2 = new HashSet<>(); //保存高亮的连线
        //获取流程定义的bpmn模型
        BpmnModelInstance bpmn = repositoryService.getBpmnModelInstance(processInsId);
        //已完成任务列表 可直接使用上面写过的
        List<HistoricActivityInstance> finishedList = historyService.createHistoricActivityInstanceQuery()
                .processDefinitionId(processInsId)
                .finished()
                .orderByHistoricActivityInstanceStartTime().asc()
                .list();
        int finishedNum = finishedList.size();
        //循环 已完成的节点
        for (int i = 0; i < finishedNum; i++) {
            HistoricActivityInstance finItem = finishedList.get(i);
            //根据 任务key 获取 bpmn元素
            ModelElementInstance domElement = bpmn.getModelElementById(finItem.getActivityId());
            //转换成 flowNode流程节点 才能获取到 输出线 和输入线
            FlowNode act = (FlowNode)domElement;
            Collection<SequenceFlow> outgoing = act.getOutgoing();
            //循环当前节点的 向下分支
            outgoing.forEach(v->{
                String tarId = v.getTarget().getId();
                //已完成
                for (int j = 0; j < finishedNum; j++) {
                    //循环历史完成节点 和当前完成节点的向下分支比对
                    //如果当前完成任务 的结束时间 等于 下个任务的开始时间
                    HistoricActivityInstance setpFinish = finishedList.get(j);
                    String finxId = setpFinish.getActivityId();
                    if(tarId.equals(finxId)){
                        if(finItem.getEndTime().equals(setpFinish.getStartTime())){
                            highLine2.add(v.getId());
                        }
                    }
                }
                //待完成
                for (int j = 0; j < unfinished.size(); j++) {
                    //循环待节点 和当前完成节点的向下分支比对
                    HistoricActivityInstance setpUnFinish = unfinished.get(j);
                    String finxId = setpUnFinish.getActivityId();
                    if(tarId.equals(finxId)){
                        if(finItem.getEndTime().equals(setpUnFinish.getStartTime())){
                            highLine2.add(v.getId());
                        }
                    }
                }

            });
        }

        //返回结果
        ActivitiHighLineDTO activitiHighLineDTO =new ActivitiHighLineDTO();
        activitiHighLineDTO.setHighPoint(highPoint);
        activitiHighLineDTO.setHighLine(highLine2);
        activitiHighLineDTO.setWaitingToDo(waitingToDo);
        activitiHighLineDTO.setiDo(iDo);
        return activitiHighLineDTO;
    }

}
