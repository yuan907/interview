package org.example.test.gitlab;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.apache.commons.io.IOUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.ProjectApi;
import org.gitlab4j.api.RepositoryApi;
import org.gitlab4j.api.models.Branch;
import org.gitlab4j.api.models.Project;
public class GitLabReader {
    public static void main(String[] args) throws IOException, GitLabApiException, GitAPIException {
        String gitLabUrl = "http://git.croot.com";
        String personalAccessToken = "AvDgwRinc8_vT_eLezcW";
        String projectName = "rootnet-k8s";
        String branchName = "master";
        String dirPath = "legacy-service/deploy-configfile/demo/common";
        String filePath = "legacy-service/deploy-configfile/demo/common/nginx.conf";
        // 创建 GitLab API 客户端
        GitLabApi gitLabApi = new GitLabApi(gitLabUrl, personalAccessToken);
        // 获取 GitLab 仓库 API 实例
        ProjectApi projectApi = gitLabApi.getProjectApi();
        // 获取指定仓库信息
        Project project = projectApi.getProject(projectName);
        // 获取 GitLab 仓库 HTTPS URL
        String repositoryUrl = project.getHttpUrlToRepo();
        // 获取 GitLab 仓库分支信息
        RepositoryApi repositoryApi = gitLabApi.getRepositoryApi();
        Branch branch = repositoryApi.getBranch(project.getId(), branchName);
        // 获取 GitLab 仓库分支的 commit ID
        String commitId = branch.getCommit().getId();
        // 创建 JGit 仓库对象
        Repository repo = Git.cloneRepository().setURI(repositoryUrl).call().getRepository();
        // 获取指定文件的对象 ID
        ObjectId objectId = repo.resolve(commitId + ":" + filePath);
        // 获取指定 commit 的信息
        RevCommit commit = new RevWalk(repo).parseCommit(repo.resolve(commitId));
        PersonIdent committer = commit.getCommitterIdent();
        String commitMessage = commit.getFullMessage();
        // 加载文件内容到内存中
        try (InputStream inputStream = repo.open(objectId).openStream()) {
            String fileContent = IOUtils.toString(inputStream, String.valueOf(StandardCharsets.UTF_8));
            System.out.println("Content of " + filePath + ":");
            System.out.println(fileContent);
        }
        // 关闭 JGit 仓库对象
        repo.close();
    }
}