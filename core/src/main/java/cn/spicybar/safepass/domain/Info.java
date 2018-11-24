package cn.spicybar.safepass.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * 信息模型
 *
 * @author hackyo
 * Created on 2017/12/28 22:28.
 */
@Document
public class Info implements Serializable {

    @Id
    private String id;
    private String userId;
    @NotEmpty(message = "信息描述不能为空")
    private String description;
    @NotEmpty(message = "信息内容不能为空")
    private String content;
    @Size(max = 10, message = "信息标签最多10个")
    private String[] labels;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String[] getLabels() {
        return labels;
    }

    public void setLabels(String[] labels) {
        this.labels = labels;
    }

}
