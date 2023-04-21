package org.example.service.impl;

import groovy.util.logging.Slf4j;
import org.example.service.SvnTestService;
import org.example.util.SvnKitUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.tmatesoft.svn.core.SVNException;


/**
 * @Author: yuanchao
 * @DATE: 2023/4/20 15:09
 */
@Service
@Slf4j
public class SvnTestServiceImpl implements SvnTestService {

    private static final String workSpace="";



    @Override
    public void testSvnUpload(MultipartFile file) throws SVNException {

        String fileName = file.getOriginalFilename();

        SvnKitUtils svnDeal = new SvnKitUtils();
        svnDeal.upload("C:/Users/Administrator/Desktop/Devops开发","/test/file2",fileName,true);

        System.out.println(1);
    }
}
