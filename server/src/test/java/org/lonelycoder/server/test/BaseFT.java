package org.lonelycoder.server.test;

import org.lonelycoder.core.spring.test.Profiles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.transaction.TransactionConfiguration;

import javax.sql.DataSource;

/**
 * @author : lihaoquan
 * 功能测试抽象基类
 */
@ContextConfiguration(locations = {
        "classpath:applicationContext-core.xml",
        "classpath:applicationContext.xml",
        "classpath:applicationContext-test.xml"
})
@ActiveProfiles(Profiles.DEVELOPMENT)
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
public abstract class BaseFT extends AbstractTransactionalJUnit4SpringContextTests {


    protected DataSource dataSource;

    @Override
    @Autowired
    public void setDataSource(DataSource dataSource) {
        super.setDataSource(dataSource);
        this.dataSource = dataSource;
    }
}
