package org.lonelycoder.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactoryBean;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;

import javax.persistence.EntityManager;
import java.io.Serializable;

/**
 * @author : lihaoquan
 *
 * 通过本FactotyBean 创建自定义的 Factory
 *
 * @参考 http://docs.spring.io/spring-data/data-jpa/docs/current/reference/html/repositories.html#repositories.custom-implementations
 */
public class MyBaseRepositoryFactoryBean<R extends JpaRepository<M, ID> , M, ID extends Serializable>
        extends JpaRepositoryFactoryBean<R, M, ID> {

    public MyBaseRepositoryFactoryBean() {

    }

    @Override
    protected RepositoryFactorySupport createRepositoryFactory(EntityManager entityManager) {
        return new MyBaseRepositoryFactory(entityManager);
    }
}
