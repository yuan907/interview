package org.example.test.respDTO;

import org.json.JSONObject;

/**
 * @Author: yuanchao
 * @DATE: 2023/4/27 19:07
 */
public class CompareRespDTO {

    private String fileName;

    private String oldHeader;

    private String newHeader;

    private JSONObject prevData;

    private JSONObject newData;

    private boolean isJson = true;

    private boolean isYaml = false;

    private boolean isProperties = false;

    private boolean isXML = false;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getOldHeader() {
        return oldHeader;
    }

    public void setOldHeader(String oldHeader) {
        this.oldHeader = oldHeader;
    }

    public String getNewHeader() {
        return newHeader;
    }

    public void setNewHeader(String newHeader) {
        this.newHeader = newHeader;
    }

    public JSONObject getPrevData() {
        return prevData;
    }

    public void setPrevData(JSONObject prevData) {
        this.prevData = prevData;
    }

    public JSONObject getNewData() {
        return newData;
    }

    public void setNewData(JSONObject newData) {
        this.newData = newData;
    }

    public boolean isJson() {
        return isJson;
    }

    public void setJson(boolean json) {
        isJson = json;
    }

    public boolean isYaml() {
        return isYaml;
    }

    public void setYaml(boolean yaml) {
        isYaml = yaml;
    }

    public boolean isProperties() {
        return isProperties;
    }

    public void setProperties(boolean properties) {
        isProperties = properties;
    }

    public boolean isXML() {
        return isXML;
    }

    public void setXML(boolean XML) {
        isXML = XML;
    }
}
