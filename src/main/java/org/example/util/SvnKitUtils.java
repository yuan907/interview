package org.example.util;


import org.apache.commons.lang3.StringUtils;
import org.tmatesoft.svn.core.*;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.fs.FSRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.svn.SVNRepositoryFactoryImpl;
import org.tmatesoft.svn.core.internal.wc.DefaultSVNOptions;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc.SVNUpdateClient;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

import java.io.File;
import java.util.ResourceBundle;

/**
 * @ClassName SvnKitUtils
 * @Description svn公共类
 * @Author hzh
 * @Date 2020/4/21
 * @Version 1.0
 */

public class SvnKitUtils {

    private SVNClientManager clientManager;
    private ISVNAuthenticationManager authManager;
    private SVNRepository repository;

    /** svn地址  */
    private static String svnUrl = "https://DESKTOP-54I4QNA/svn/test";
    private static String svnUsername = "yuanchao";
    private static String svnPassword = "yuanchao66";

    /**
     * 启动svn连接
     * @throws SVNException 异常信息
     */
    public SvnKitUtils()throws SVNException {
        try {
            this.createDefaultAuthenticationManager(svnUsername, svnPassword);
            this.authSvn(svnUrl);
        } catch (SVNException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * 通过不同的协议初始化版本库
     */
    private void setupLibrary() {
        DAVRepositoryFactory.setup();
        SVNRepositoryFactoryImpl.setup();
        FSRepositoryFactory.setup();
    }

    /**
     *
     * @param username svn用户名称
     * @param password svn用户密码
     * @throws SVNException 异常信息
     */
    private void createDefaultAuthenticationManager(String username, String password)throws SVNException{
        try {
            // 身份验证
            authManager = SVNWCUtil.createDefaultAuthenticationManager(username, password.toCharArray());
        } catch (Exception e) {
            throw new RuntimeException("SVN身份认证失败：" + e.getMessage());
        }
    }

    /**
     * 验证登录svn
     * @param svnUrl 用户svn的仓库地址
     * @throws SVNException 异常信息
     */
    private void authSvn(String svnUrl) throws SVNException {
        // 初始化版本库
        setupLibrary();
        try {
            repository = SVNRepositoryFactory.create(SVNURL.parseURIEncoded(svnUrl));
        } catch (SVNException e) {
            throw new RuntimeException("SVN创建库连接失败：" + e.getMessage());
        }

        // 创建身份验证管理器
        repository.setAuthenticationManager(authManager);
        DefaultSVNOptions options = SVNWCUtil.createDefaultOptions(true);
        try {
            //创建SVN实例管理器
            clientManager = SVNClientManager.newInstance(options,authManager);
        } catch (Exception e) {
            throw new RuntimeException("SVN实例管理器创建失败：" + e.getMessage());
        }
    }

    /**
     * 添加文件和目录到版本控制下
     * @param wcPath 工作区路径
     * @throws SVNException 异常信息
     */
    private void addEntry(File wcPath) throws SVNException{
        try {
            clientManager.getWCClient().doAdd(new File[] { wcPath }, true,
                    false, false, SVNDepth.INFINITY, false, false, true);
        } catch (SVNException e) {
            throw new RuntimeException("SVN添加文件到版本控制下失败：" + e.getMessage());
        }
    }


    /**
     * 将工作副本提交到svn
     * @param wcPath  被提交的工作区路径
     * @param keepLocks 是否在SVN仓库中打开或不打开文件
     * @param commitMessage 提交信息
     * @return 返回信息
     * @throws SVNException 异常信息
     */
    private SVNCommitInfo commit(File wcPath, boolean keepLocks, String commitMessage) throws SVNException {
        try {
            return clientManager.getCommitClient().doCommit(
                    new File[] { wcPath }, keepLocks, commitMessage, null,
                    null, true, false, SVNDepth.INFINITY);
        } catch (SVNException e) {
            throw new RuntimeException("SVN提交失败：" + e.getMessage());
        }
    }

    /**
     *  创建svn文件夹
     * @param url svn地址
     * @param commitMessage 提交信息
     * @return 返回信息
     * @throws SVNException 异常信息
     */
    private SVNCommitInfo makeDirectory(SVNURL url,String commitMessage) throws SVNException {
        try {
            return clientManager.getCommitClient().doMkDir(new SVNURL[] { url }, commitMessage);
        } catch (SVNException e) {
            throw new RuntimeException("SVN新建文件夹失败：" + e.getMessage());
        }
    }

    /**
     *  删除
     * @param url svn地址
     * @param commitMessage 提交信息
     * @return
     * @throws SVNException
     */
    private SVNCommitInfo delete(SVNURL url,String commitMessage) throws SVNException {
        try {
            return clientManager.getCommitClient().doDelete(new SVNURL[] { url }, commitMessage);
        } catch (SVNException e) {
            throw new RuntimeException("SVN删除文件失败：" + e.getMessage());
        }
    }

    /**
     * 确定path是否是一个工作空间
     * @param path 文件路径
     * @return 返回信息
     * @throws SVNException 异常信息
     */
    private boolean isWorkingCopy(File path) throws SVNException{
        if(!path.exists()){
            return false;
        }
        try {
            if(null == SVNWCUtil.getWorkingCopyRoot(path, false)){
                return false;
            }
        } catch (SVNException e) {
            throw new RuntimeException("确定path是否是一个工作空间 失败：" + e.getMessage());
        }
        return true;
    }

    /**
     * 确定一个URL在SVN上是否存在
     * @param url svn访问地址
     * @return 返回信息
     * @throws SVNException 异常信息
     */
    private boolean isURLExist(SVNURL url) throws SVNException{
        try {
            SVNRepository svnRepository = SVNRepositoryFactory.create(url);
            svnRepository.setAuthenticationManager(authManager);
            SVNNodeKind nodeKind = svnRepository.checkPath("", -1);
            return nodeKind == SVNNodeKind.NONE ? false : true;
        } catch (SVNException e) {
            throw new RuntimeException("确定一个URL在SVN上是否存在失败：" + e.getMessage());
        }
    }

    /**
     * 递归检查不在版本控制的文件，并add到svn
     * @param wc  检查的文件
     * @throws SVNException 异常信息
     */
    private void checkVersiondDirectory(File wc) throws SVNException{
        if(!SVNWCUtil.isVersionedDirectory(wc)){
            this.addEntry(wc);
        }
        if(wc.isDirectory()){
            for(File sub:wc.listFiles()){
                if(sub.isDirectory() && sub.getName().equals(".svn")){
                    continue;
                }
                checkVersiondDirectory(sub);
            }
        }
    }

    /**
     * 删除目录（文件夹）以及目录下的文件
     * @param   sPath 被删除目录的文件
     * @return  目录删除成功返回true，否则返回false
     */
    private boolean deleteDirectory(String sPath) {
        //如果sPath不以文件分隔符结尾，自动添加文件分隔符
        if (!sPath.endsWith(File.separator)) {
            sPath = sPath + File.separator;
        }
        File dirFile = new File(sPath);
        //如果dir对应的文件不存在，或者不是一个目录，则退出
        if (!dirFile.exists() || !dirFile.isDirectory()) {
            return false;
        }
        boolean flag = true;
        //删除文件夹下的所有文件(包括子目录)
        File[] files = dirFile.listFiles();
        if(files != null){
            for (int i = 0; i < files.length; i++) {
                //删除子文件
                if (files[i].isFile()) {
                    flag = deleteFile(files[i].getAbsolutePath());
                    if (!flag) {
                        break;
                    }
                } else {
                    //删除子目录
                    flag = deleteDirectory(files[i].getAbsolutePath());
                    if (!flag){
                        break;
                    }
                }
            }
        }
        if (!flag){
            return false;
        }
        //删除当前目录
        if (dirFile.delete()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 删除单个文件
     * @param   sPath    被删除文件的文件
     * @return 单个文件删除成功返回true，否则返回false
     */
    private boolean deleteFile(String sPath) {
        boolean flag = false;
        File file = new File(sPath);
        // 路径为文件且不为空则进行删除
        if (file.isFile() && file.exists()) {
            file.delete();
            flag = true;
        }
        return flag;
    }

    /**
     *  根据路径删除指定的目录或文件，无论存在与否
     *@param sPath  要删除的目录或文件
     *@return 删除成功返回 true，否则返回 false。
     */
    private boolean DeleteFolder(String sPath) {
        boolean flag = false;
        File file = new File(sPath);
        // 判断目录或文件是否存在 不存在返回 false
        if (!file.exists()) {
            return flag;
        } else {
            // 判断是否为文件
            if (file.isFile()) {
                // 为文件时调用删除文件方法
                return deleteFile(file.getAbsolutePath());
            } else {
                // 为目录时调用删除目录方法
                return deleteDirectory(file.getAbsolutePath());
            }
        }
    }

    /**
     * 更新SVN工作区
     * @param wcPath 工作区路径
     * @param updateToRevision 更新版本
     * @param depth update的深度：目录、子目录、文件
     * @return 返回信息
     * @throws SVNException 异常信息
     */
    private long update(File wcPath, SVNRevision updateToRevision, SVNDepth depth) throws SVNException{
        SVNUpdateClient updateClient = clientManager.getUpdateClient();
        updateClient.setIgnoreExternals(false);
        try {
            return updateClient.doUpdate(wcPath, updateToRevision,depth, false, false);
        } catch (SVNException e) {
            throw new RuntimeException("更新SVN工作区失败：" + e.getMessage());
        }
    }

    /**
     * SVN仓库文件检出
     * @param url 文件url
     * @param revision 检出版本
     * @param destPath 目标路径
     * @param depth checkout的深度，目录、子目录、文件
     * @return 返回信息
     * @throws SVNException 异常信息
     */
    private long checkout(SVNURL url, SVNRevision revision, File destPath, SVNDepth depth) throws SVNException{
        SVNUpdateClient updateClient = clientManager.getUpdateClient();
        updateClient.setIgnoreExternals(false);
        try {
            return updateClient.doCheckout(url, destPath, revision, revision,depth, false);
        } catch (SVNException e) {
            throw new RuntimeException("检出SVN仓库失败：" + e.getMessage());
        }
    }

    /**
     * @param svnPathUrl svn地址
     * @param workspace 工作区
     * @param filepath 上传的文件地址
     * @param filename 文件名称
     * @throws SVNException 异常信息
     */
    private void checkWorkCopy(String svnPathUrl,String workspace,String filepath,String filename)throws SVNException{
        SVNURL repositoryURL = null;
        if(StringUtils.isNotBlank(filepath)){
            svnPathUrl = svnPathUrl + filepath;
        }
        try {
            repositoryURL = SVNURL.parseURIEncoded(svnPathUrl);
        } catch (SVNException e) {
            throw new RuntimeException("解析svnUrl失败：" + e.getMessage());
        }

        File wc = new File(workspace);
        File wc_project = new File( workspace);

        SVNURL projectURL = null;
        try {
            projectURL = repositoryURL.appendPath(filename, false);
        } catch (SVNException e) {
            throw new RuntimeException("解析svnUrl文件失败：" + e.getMessage());
        }

        /*
         *  循环新建svn文件夹，不能直接把多个文件夹一块传过去新建，否则报错
         */
        if(StringUtils.isNotBlank(filepath) && filepath.contains("/")){
            String[] filepathArray = filepath.split("/");
            SVNURL svnFilePath = SVNURL.parseURIEncoded(svnUrl);
            for (String path : filepathArray) {
                //jdk8 分割后第一个为空
                if(StringUtils.isBlank(path)){
                    continue;
                }
                svnFilePath = svnFilePath.appendPath("/"+path,false);
                //检查文件夹在svn中是否存在
                boolean flag = this.isURLExist(svnFilePath);
                if(!flag){
                    //新建文件夹；  此操作要在checkout之前，否则checkout会报错
                    makeDirectory(svnFilePath,"新建文件夹");
                }
            }
        }

        if(!this.isWorkingCopy(wc)){
            if(!this.isURLExist(projectURL)){
                this.checkout(repositoryURL, SVNRevision.HEAD, wc, SVNDepth.EMPTY);
            }else{
                this.checkout(projectURL, SVNRevision.HEAD, wc_project, SVNDepth.INFINITY);
            }
        }else{
            this.update(wc, SVNRevision.HEAD, SVNDepth.INFINITY);
        }
    }

    /**
     * 循环删除.svn目录
     * @param spath
     */
    private void deletePointSVN(String spath){
        File wc = new File(spath);
        File[] files = wc.listFiles();
        if(files != null){
            for(File sub:files){
                if(sub.isDirectory() && sub.getName().equals(".svn")){
                    this.deleteDirectory(sub.getAbsolutePath());
                    continue;
                }
                if(sub.isDirectory()){
                    deletePointSVN(sub.getAbsolutePath());
                }
            }
        }
    }

    /**
     * @param  workspace 文件工作空间路径
     * @param filepath 上传的文件地址 以"/"开头的路径，用于在svn上新建对应文件夹
     * @param filename 上传的文件名称
     * @param isOverwrite 是否覆盖
     * @throws SVNException 异常信息
     */
    public void upload(String workspace, String filepath,String filename,Boolean isOverwrite)throws SVNException{
        String svnFilePath = svnUrl +"/"+filename;
        if(StringUtils.isNotBlank(filepath)){
            svnFilePath = svnUrl + filepath + "/" +filename;
        }
        //检查文件在svn中是否存在
        boolean flag = this.isURLExist(SVNURL.parseURIEncoded(svnFilePath));
        String workFilePath = workspace + "/"+ filename;
        File file = new File(workFilePath);
        /*
         * 切合实际业务场景来选择是否需要，
         * 一般本地demo测试需要，同时注释结束后删除.svn。
         * 线上环境不建议使用，线上文件最好加个时间戳来区分文件，防止相同名称文件，
         * 如果文件不加时间戳，建议先查询该文件是否存在，若存在，先删除再上传。
         */
        if(flag){
            if(isOverwrite){
                this.commit(file, true, "commit file:"+file);
            }
        }else{
            //开始前删除以前的.svn文件目录
            deletePointSVN(workspace);
            //checkOut .svn
            this.checkWorkCopy(svnUrl, workspace, filepath,filename);
            this.checkVersiondDirectory(file);
            this.commit(file, true, "commit file:"+file);
            /*
             * 结束后删除以前的.svn文件目录。
             * 根据实际情况选择是否在结束后删除，因为.svn删除后，线上若有相同文件，会默认直接提交，
             * 然后没有.svn文件会报错。
             */
            deletePointSVN(workspace);
        }

    }

    /**
     *  删除svn上文件
     * @param filepath 上传的文件地址
     * @param filename 文件名称
     * @throws SVNException
     */
    public void deleteSvnFile(String filepath, String filename)throws SVNException{
        String svnFilePath = svnUrl +"/"+filename;
        if(StringUtils.isNotBlank(filepath)){
            svnFilePath = svnUrl + filepath + "/" +filename;
        }
        //检查文件在svn中是否存在
        boolean flag = this.isURLExist(SVNURL.parseURIEncoded(svnFilePath));
        if(flag){
            this.delete(SVNURL.parseURIEncoded(svnFilePath),"删除文件："+svnFilePath);
        }

    }

    public static void main(String[] args) throws SVNException {
        try {
            String filename = "354.txt";
            String workspace = "D:/WorkBase/files";
            String filepath = "/file/2";
            SvnKitUtils svnDeal = new SvnKitUtils();
            svnDeal.upload(workspace,filepath, filename,true);
//            svnDeal.deleteSvnFile(filepath,filename);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}