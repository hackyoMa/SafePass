package cn.spicybar.safepass.service;

import cn.spicybar.safepass.domain.Info;

import java.util.List;

/**
 * 信息操作接口
 *
 * @author hackyo
 * Created on 2017/12/28 22:37.
 */
public interface IInfoService {

    /**
     * 添加信息
     *
     * @param info 信息
     * @return 操作结果
     */
    String insert(Info info);

    /**
     * 根据页码查询信息
     *
     * @param userId 用户ID
     * @param page   页码
     * @return 信息
     */
    List<Info> getInfoForPage(String userId, int page);

    /**
     * 查询总数量
     *
     * @param userId 用户ID
     * @return 数量
     */
    long getCount(String userId);

    /**
     * 删除指定条目
     *
     * @param userId 用户ID
     * @param id     条目ID
     * @return 操作结果
     */
    String delete(String userId, String id);

    /**
     * 更新信息
     *
     * @param info 信息
     * @return 操作结果
     */
    String update(Info info);

    /**
     * 验证信息描述是否存在
     *
     * @param id          信息ID
     * @param userId      用户ID
     * @param description 信息描述
     * @return 验证结果
     */
    boolean descriptionExists(String id, String userId, String description);

    /**
     * 描述的模糊查询
     *
     * @param userId 用户ID
     * @param search 搜索内容
     * @return 搜索结果
     */
    List<Info> searchInfo(String userId, String search);

}
