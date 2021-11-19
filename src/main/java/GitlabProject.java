/**
 * @author lulx
 **/
public class GitlabProject {
    private Integer id;
    private String description;
    private String name;
    private String path;
    private String http_url_to_repo;
    private String ssh_url_to_repo;

    @Override
    public String toString() {
        return "GitlabProject{" +
                "id=" + id +
                ", description='" + description + '\'' +
                ", name='" + name + '\'' +
                ", path='" + path + '\'' +
                ", http_url_to_repo='" + http_url_to_repo + '\'' +
                ", ssh_url_to_repo='" + ssh_url_to_repo + '\'' +
                '}';
    }

    public String getHttp_url_to_repo() {
        return http_url_to_repo;
    }

    public void setHttp_url_to_repo(String http_url_to_repo) {
        this.http_url_to_repo = http_url_to_repo;
    }

    public String getSsh_url_to_repo() {
        return ssh_url_to_repo;
    }

    public void setSsh_url_to_repo(String ssh_url_to_repo) {
        this.ssh_url_to_repo = ssh_url_to_repo;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

}
