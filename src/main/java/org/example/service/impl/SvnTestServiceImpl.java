package org.example.service.impl;

import groovy.util.logging.Slf4j;
import org.example.service.SvnTestService;
import org.example.util.SvnKitUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.tmatesoft.svn.core.SVNException;

import java.io.File;


/**
 * @Author: yuanchao
 * @DATE: 2023/4/20 15:09
 */
@Service
@Slf4j
public class SvnTestServiceImpl implements SvnTestService {

    private static final String workSpace="";



    @Override
    public void testSvnUpload(MultipartFile multipartFile) throws SVNException {
//        File uploadFile = null;
//        try {
//            String originalFilename = multipartFile.getOriginalFilename();
//            String[] filename = originalFilename.split("\\.");
//            uploadFile = File.createTempFile(filename[0], filename[1]);
//            multipartFile.transferTo(uploadFile);
//            uploadFile.deleteOnExit();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }



        File uploadFile=new File("C:/Users/Administrator/Desktop/Devops开发/Devops部署流程手册.docx");

        SvnKitUtils svnDeal = new SvnKitUtils();
//        svnDeal.upload("C:/Users/Administrator/Desktop/Devops开发","/test/file","Devops部署流程手册.docx",true);



        svnDeal.upload(uploadFile,"/test/file",true);

        System.out.println(1);
    }
}
