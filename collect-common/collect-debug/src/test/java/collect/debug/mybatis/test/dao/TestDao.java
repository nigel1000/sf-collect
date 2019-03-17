package collect.debug.mybatis.test.dao;

import collect.debug.mybatis.test.domain.Test;
import com.common.collect.container.mybatis.BaseDao;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;

/**
 * Created by nijianfeng on 2019/3/17.
 */
@Repository
public class TestDao extends BaseDao<Test> {

    @Resource(name = "sqlSessionFactory")
    @Override
    public void init(SqlSessionFactory sqlSessionFactory) {
        super.setSqlSessionFactory(sqlSessionFactory);
    }

}
