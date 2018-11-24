package cn.spicybar.safepass.service.impl;

import cn.spicybar.safepass.dao.IInfoRepository;
import cn.spicybar.safepass.domain.Info;
import cn.spicybar.safepass.service.IInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 信息操作接口实现
 *
 * @author hackyo
 * Created on 2017/12/28 22:37.
 */
@Service
public class InfoServiceImpl implements IInfoService {

    private static final String SUCCESS = "success";
    private IInfoRepository infoRepository;

    @Autowired
    public InfoServiceImpl(IInfoRepository infoRepository) {
        this.infoRepository = infoRepository;
    }

    @Override
    public String insert(Info info) {
        String validatedLabels = validatedLabels(info.getLabels());
        if (!SUCCESS.equals(validatedLabels)) {
            return validatedLabels;
        }
        if (infoRepository.getInfoForDescription(info.getUserId(), info.getDescription()) == null) {
            infoRepository.insert(info);
            return SUCCESS;
        } else {
            return "existed";
        }
    }

    @Override
    public List<Info> getInfoForPage(String userId, int page) {
        return infoRepository.getInfoForPage(userId, page);
    }

    @Override
    public long getCount(String userId) {
        return infoRepository.getCount(userId);
    }

    @Override
    public String delete(String userId, String id) {
        infoRepository.delete(userId, id);
        return SUCCESS;
    }

    @Override
    public String update(Info info) {
        String validatedLabels = validatedLabels(info.getLabels());
        if (!SUCCESS.equals(validatedLabels)) {
            return validatedLabels;
        }
        Info existed = infoRepository.getInfoForDescription(info.getUserId(), info.getDescription());
        if (existed != null && !existed.getId().equals(info.getId())) {
            return "existed";
        } else {
            infoRepository.update(info);
            return SUCCESS;
        }
    }

    @Override
    public boolean descriptionExists(String id, String userId, String description) {
        Info existed = infoRepository.getInfoForDescription(userId, description);
        if ("".equals(id)) {
            return existed != null;
        } else {
            return existed != null && !existed.getId().equals(id);
        }
    }

    @Override
    public List<Info> searchInfo(String userId, String search) {
        if (search != null) {
            if (!"".equals(search)) {
                return infoRepository.searchInfo(userId, search);
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    /**
     * 验证信息标签
     *
     * @param labels 信息标签
     * @return 验证结果
     */
    private String validatedLabels(String[] labels) {
        for (int i = 0; i < labels.length; i++) {
            String label = labels[i];
            if ("".equals(label.trim())) {
                return "labelError";
            } else if (label.length() > 10) {
                return "labelError";
            }
            for (int j = 0; j < labels.length; j++) {
                if (j != i && label.equals(labels[j])) {
                    return "labelError";
                }
            }
        }
        return SUCCESS;
    }

}
