import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author lulx
 * @ClassName PACKAGE_NAME.ProjectDownLoad
 * @Description: gitlab项目下载
 * @Date 2021/10/21
 * @ModifiedTime 2021/10/21
 **/
public class ProjectDownLoad {
    public static void main(String[] args) {
//        AtomicInteger size = new AtomicInteger(1);
//        cloneCommand("git@10.3.98.21:tool/sceo-commons-utils.git", "sceo-commons-utils", size);
        List<GitlabProject> gitlabProjects = new ArrayList<>();
//        gitlabProjects.addAll(getAllProjectsBySearch("rx-cloud-sceo"));
        gitlabProjects.addAll(getAllProjects("1"));
        gitlabProjects.addAll(getAllProjects("2"));
        cloneCommand(gitlabProjects);
    }

    /**
     * 下载到的本地路径
     */
    private static final String CLIENT_PATH = "D:/gitlab/";
    /**
     * 异步下载线程数
     */
    private static final Integer Thread_SIZE = Runtime.getRuntime().availableProcessors() * 2;
    /**
     * gitlab服务地址
     */
    private static final String GITLAB_URL = "http://192.168.1.1:1111";
    /**
     * gitlab 用户名，token
     */
    private static final String GITLAB_TOKEN = "";
    private static final String GITLAB_USER = "";

    private static CredentialsProvider credentialsProvider = new UsernamePasswordCredentialsProvider(GITLAB_USER, GITLAB_TOKEN);

    private static List<GitlabProject> getAllProjects(String page) {
        return getAllProjects(page, null);
    }

    private static List<GitlabProject> getAllProjectsBySearch(String search) {
        return getAllProjects(null, search);
    }

    private static List<GitlabProject> getAllProjects(String page, String search) {
        Map<String, String> map = new HashMap<>(3);
        map.put("private_token", GITLAB_TOKEN);
        StringBuilder stringBuilder = new StringBuilder(GITLAB_URL);
        stringBuilder.append("/api/v3/projects?private_token={private_token}");
        if (page != null && !"".equals(page)) {
            map.put("page", page);
            stringBuilder.append("&per_page=100&page={page}&order_by=id");
        }
        if (search != null && !"".equals(search)) {
            map.put("search", search);
            stringBuilder.append("&search={search}");
        }

        RestTemplate restTemplate = new RestTemplate();
        List<HttpMessageConverter<?>> list = new ArrayList<>();
        FastJsonHttpMessageConverter fjc = new FastJsonHttpMessageConverter();
        list.add(fjc);
        restTemplate.setMessageConverters(list);

        HttpHeaders requestHeaders = new HttpHeaders();
        HttpEntity<Object> requestEntity = new HttpEntity<Object>(null, requestHeaders);
        ResponseEntity<List<GitlabProject>> exchange = restTemplate.exchange(stringBuilder.toString()
                , HttpMethod.GET, requestEntity, new ParameterizedTypeReference<List<GitlabProject>>() {
                }, map);
        List<GitlabProject> gitlabProjects = exchange.getBody();
        System.out.println("gitlabProjects.size() = " + gitlabProjects.size());
        return gitlabProjects;
    }

    private static void cloneCommand(List<GitlabProject> gitlabProjects) {
        ExecutorService exec = Executors.newFixedThreadPool(Thread_SIZE);
        AtomicInteger size = new AtomicInteger(gitlabProjects.size());
        gitlabProjects.forEach(item -> {
            String name = item.getName();
            String ssh_url_to_repo = item.getSsh_url_to_repo();
            exec.submit(() -> cloneCommand(ssh_url_to_repo, name, size));
        });
        while (size.get() > 0) {
        }
        System.out.println("下载完成，共" + gitlabProjects.size());
        exec.shutdown();
    }

    private static void cloneCommand(String ssh_url_to_repo, String name, AtomicInteger size) {
        CloneCommand cloneCommand = Git.cloneRepository();
        try {
            Git git = cloneCommand.setURI(ssh_url_to_repo)//要从中克隆的uri
                    .setDirectory(new File(CLIENT_PATH + name))//克隆到的目录
                    //                    .setBranchesToClone(branchs)//.setBranch("refs/heads/test")
                    .setCredentialsProvider(credentialsProvider)
                    //                    .setBranch("refs/heads/test")
                    .setCloneAllBranches(Boolean.TRUE)
                    .call();
            git.close();
        } catch (GitAPIException e) {
            e.printStackTrace();
            System.out.println(e.getLocalizedMessage());
        } finally {
            int decrementAndGet = size.decrementAndGet();
            System.out.println("剩余下载项目： " + decrementAndGet);
        }
    }
}
