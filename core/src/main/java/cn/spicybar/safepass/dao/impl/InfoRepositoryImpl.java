package cn.spicybar.safepass.dao.impl;

import cn.spicybar.safepass.dao.IInfoRepository;
import cn.spicybar.safepass.domain.Info;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 信息表操作接口实现
 *
 * @author hackyo
 * Created on 2017/12/28 22:35.
 */
@Repository
public class InfoRepositoryImpl implements IInfoRepository {

    private MongoTemplate mongoTemplate;

    @Autowired
    public InfoRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void insert(Info info) {
        mongoTemplate.insert(info);
    }

    @Override
    public List<Info> getInfoForPage(String userId, int page) {
        Query query = new Query(Criteria.where("userId").is(userId));
        query.skip((page - 1) * 10);
        query.limit(10);
        return this.mongoTemplate.find(query, Info.class);
    }

    @Override
    public Info getInfoForDescription(String userId, String description) {
        Query query = new Query(Criteria.where("userId").is(userId).and("description").is(description));
        return this.mongoTemplate.findOne(query, Info.class);
    }

    @Override
    public long getCount(String userId) {
        Query query = new Query(Criteria.where("userId").is(userId));
        return this.mongoTemplate.count(query, Info.class);
    }

    @Override
    public void delete(String userId, String id) {
        Query query = new Query(Criteria.where("userId").is(userId).and("id").is(id));
        this.mongoTemplate.remove(query, Info.class);
    }

    @Override
    public void update(Info info) {
        Query query = new Query(Criteria.where("userId").is(info.getUserId()).and("id").is(info.getId()));
        Update update = new Update();
        update.set("description", info.getDescription());
        update.set("content", info.getContent());
        update.set("labels", info.getLabels());
        mongoTemplate.updateFirst(query, update, Info.class);
    }

    @Override
    public List<Info> searchInfo(String userId, String search) {
        Query query = new Query(Criteria.where("userId").is(userId).and("description").is(search));
        return this.mongoTemplate.find(query, Info.class);
    }

}
