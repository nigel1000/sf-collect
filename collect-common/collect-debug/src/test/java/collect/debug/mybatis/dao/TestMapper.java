package collect.debug.mybatis.dao;

import collect.debug.mybatis.domain.Test;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by hznijianfeng on 2019/03/21.
 */

public interface TestMapper {

    Integer create(@Param("item") Test item);

    Integer creates(List<Test> items);

    Integer delete(@Param("id") Long id);

    Integer deletes(List<Long> ids);

    Test load(@Param("id") Long id);

    List<Test> loads(List<Long> ids);

    Integer update(@Param("item") Test item);

}