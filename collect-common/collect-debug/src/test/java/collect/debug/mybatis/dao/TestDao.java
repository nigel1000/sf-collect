package collect.debug.mybatis.dao;

import collect.debug.mybatis.domain.Test;
import com.common.collect.container.mybatis.BaseDao;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

/**
 * Created by nijianfeng on 2019/3/17.
 */
@Repository
public class TestDao extends BaseDao<Test> {

    @Autowired
    @Qualifier("sqlSessionFactory")
    @Override
    public void init(SqlSessionFactory sqlSessionFactory) {
        super.setSqlSessionFactory(sqlSessionFactory);
    }

}
