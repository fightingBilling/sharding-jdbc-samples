package org.pankai;

import com.dangdang.ddframe.rdb.sharding.api.HintManager;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by pktczwd on 2016/9/23.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class AppTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    public void test0() {
        jdbcTemplate.update("insert into t_order(order_id,user_id) values(?,?)", 1, 1);
    }

    @Test
    public void test1() {
        jdbcTemplate.update("insert into t_order(order_id,user_id) values(?,?)", 2, 1);
    }

    @Test
    public void test2() {
        List list = jdbcTemplate.query("select order_id,user_id from t_order", new RowMapper<Object>() {
            @Override
            public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                System.out.println("got data from DB.");
                System.out.println(rs.getInt("order_id"));
                System.out.println(rs.getInt("user_id"));
                return new Object();
            }
        });
        System.out.println("result size is:" + list.size());
    }

    /**
     * 基于暗示(Hint)的分片键值注册方法
     */
    @Test
    public void test3() {
        try (HintManager hintManager = HintManager.getInstance()) {
            hintManager.addDatabaseShardingValue("t_order", "user_id", 1);
            hintManager.addTableShardingValue("t_order", "order_id", 1);
            List list = jdbcTemplate.query("select order_id,user_id from t_order", new RowMapper<Object>() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    System.out.println("got data from DB.");
                    System.out.println(rs.getInt("order_id"));
                    System.out.println(rs.getInt("user_id"));
                    return new Object();
                }
            });
            System.out.println("result size is:" + list.size());
        }
    }

    /**
     * 对事务进行测试,在DBConfig中没有配置事务管理器的时候,当然不能回滚.
     */
    @Test
    public void test4() {
        jdbcTemplate.update("insert into t_order(order_id,user_id) values(?,?)", 3, 1);
        throw new RuntimeException("Expected exception.");
    }

    /**
     * 对事务进行测试,在DBConfig中配置了事务管理器,然而方法并没有加上@Transactional注解,仍然没有事务.
     */
    @Test
    public void test5() {
        jdbcTemplate.update("insert into t_order(order_id,user_id) values(?,?)", 6, 1);
        throw new RuntimeException("Expected exception.");
    }

    /**
     * 对事务进行测试,在DBConfig中配置了事务管理器,方法上加入了@Transactional注解,在插入数据之后人为抛出RuntimeException,数据发生了回滚.
     */
    @Transactional
    @Test
    public void test6() {
        jdbcTemplate.update("insert into t_order(order_id,user_id) values(?,?)", 5, 1);
        throw new RuntimeException("Expected exception.");
    }


}
