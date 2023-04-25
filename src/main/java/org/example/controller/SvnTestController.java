package org.example.controller;

import org.example.service.SvnTestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.tmatesoft.svn.core.SVNException;


/**
 * @Author: yuanchao
 * @DATE: 2023/4/20 15:08
 */
@RestController
public class SvnTestController {

    @Autowired
    private SvnTestService svnTestService;

    @PostMapping("/svn")
    public void testSvnUpload(@RequestParam(value="file",required=false) MultipartFile file) throws SVNException {
        svnTestService.testSvnUpload(file);
    }

}
