package org.pankai.shardingjdbc.config.db;

import com.dangdang.ddframe.rdb.sharding.config.common.api.config.ShardingRuleConfig;
import com.dangdang.ddframe.rdb.sharding.config.common.api.config.StrategyConfig;
import com.dangdang.ddframe.rdb.sharding.config.common.api.config.TableRuleConfig;
import com.dangdang.ddframe.rdb.sharding.spring.datasource.SpringShardingDataSource;
import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.ManagedMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created by pktczwd on 2016/9/22.
 */
@Configuration
public class DbConfig {

    @Autowired
    private Environment env;

    @Bean(destroyMethod = "close")
    public DataSource dbtbl_0() {
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName(env.getRequiredProperty("jdbc.driver.name"));
        dataSource.setUrl(env.getRequiredProperty("jdbc.url.dbtbl_0"));
        dataSource.setUsername(env.getRequiredProperty("jdbc.username"));
        dataSource.setPassword(env.getRequiredProperty("jdbc.password"));
        return dataSource;
    }

    @Bean(destroyMethod = "close")
    public DataSource dbtbl_1() {
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName(env.getRequiredProperty("jdbc.driver.name"));
        dataSource.setUrl(env.getRequiredProperty("jdbc.url.dbtbl_1"));
        dataSource.setUsername(env.getRequiredProperty("jdbc.username"));
        dataSource.setPassword(env.getRequiredProperty("jdbc.password"));
        return dataSource;
    }

    @Bean
    public StrategyConfig orderTableStrategy() {
        StrategyConfig config = new StrategyConfig();
        config.setShardingColumns("order_id");
        config.setAlgorithmExpression("t_order_${order_id.longValue() % 4}");
        return config;
    }

    @Bean
    public StrategyConfig orderItemTableStrategy() {
        StrategyConfig config = new StrategyConfig();
        config.setShardingColumns("order_id");
        config.setAlgorithmExpression("t_order_item_${order_id.longValue() % 4}");
        return config;
    }


    @Bean
    public SpringShardingDataSource shardingDataSource() {
        SpringShardingDataSource dataSource = new SpringShardingDataSource(shardingRuleConfig(), new Properties());
        return dataSource;
    }

    @Bean
    public ShardingRuleConfig shardingRuleConfig() {
        ShardingRuleConfig shardingRuleConfig = new ShardingRuleConfig();
        //dataSource属性
        Map<String, DataSource> map = new HashMap<>();
        map.put("dbtbl_0", dbtbl_0());
        map.put("dbtbl_1", dbtbl_1());
        shardingRuleConfig.setDataSource(map);
        //tables属性
        Map<String, TableRuleConfig> tables = new ManagedMap<>(2);

        TableRuleConfig tableRuleConfig0 = new TableRuleConfig();
        tableRuleConfig0.setActualTables("t_order_${0..3}");
        tableRuleConfig0.setTableStrategy(orderTableStrategy());
        tables.put("t_order", tableRuleConfig0);

        TableRuleConfig tableRuleConfig1 = new TableRuleConfig();
        tableRuleConfig1.setActualTables("t_order_item_${0..3}");
        tableRuleConfig1.setTableStrategy(orderItemTableStrategy());
        tables.put("t_order_item", tableRuleConfig1);

        shardingRuleConfig.setTables(tables);
        //defaultDatabseStrategy属性
        StrategyConfig strategyConfig = new StrategyConfig();
        strategyConfig.setShardingColumns("user_id");
        strategyConfig.setAlgorithmExpression("dbtbl_${user_id.longValue() % 2}");
        shardingRuleConfig.setDefaultDatabaseStrategy(strategyConfig);

        return shardingRuleConfig;
    }

    @Bean
    public JdbcTemplate jdbcTemplate() {
        return new JdbcTemplate(shardingDataSource());
    }

    @Bean
    public DataSourceTransactionManager dataSourceTransactionManager() {
        return new DataSourceTransactionManager(shardingDataSource());
    }

}
