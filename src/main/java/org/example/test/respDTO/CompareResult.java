package org.example.test.respDTO;

import java.util.List;

/**
 * @Author: yuanchao
 * @DATE: 2023/4/28 10:37
 */
public class CompareResult {

    private List<CompareRespDTO> compareList;

    private List<String> addFileList;

    private List<String> deleteFileList;

    public List<CompareRespDTO> getCompareList() {
        return compareList;
    }

    public void setCompareList(List<CompareRespDTO> compareList) {
        this.compareList = compareList;
    }

    public List<String> getAddFileList() {
        return addFileList;
    }

    public void setAddFileList(List<String> addFileList) {
        this.addFileList = addFileList;
    }

    public List<String> getDeleteFileList() {
        return deleteFileList;
    }

    public void setDeleteFileList(List<String> deleteFileList) {
        this.deleteFileList = deleteFileList;
    }
}
