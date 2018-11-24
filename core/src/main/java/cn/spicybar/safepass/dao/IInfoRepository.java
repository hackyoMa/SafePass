package cn.spicybar.safepass.dao;

import cn.spicybar.safepass.domain.Info;

import java.util.List;

/**
 * 信息表操作接口
 *
 * @author hackyo
 * Created on 2017/12/28 22:34.
 */
public interface IInfoRepository {

    /**
     * 添加信息
     *
     * @param info 信息
     */
    void insert(Info info);

    /**
     * 根据页码查询信息
     *
     * @param userId 用户ID
     * @param page   页码
     * @return 信息
     */
    List<Info> getInfoForPage(String userId, int page);

    /**
     * 根据描述查询信息
     *
     * @param userId      用户ID
     * @param description 描述
     * @return 信息
     */
    Info getInfoForDescription(String userId, String description);

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
     */
    void delete(String userId, String id);

    /**
     * 修改信息
     *
     * @param info 信息
     */
    void update(Info info);

    /**
     * 描述的模糊查询
     *
     * @param userId 用户ID
     * @param search 搜索内容
     * @return 搜索结果
     */
    List<Info> searchInfo(String userId, String search);

}
