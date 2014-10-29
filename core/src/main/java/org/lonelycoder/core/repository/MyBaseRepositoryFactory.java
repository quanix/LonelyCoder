package org.lonelycoder.core.repository;

import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.query.QueryLookupStrategy;

import javax.persistence.EntityManager;
import java.io.Serializable;

/**
 * @author : lihaoquan
 */
public class MyBaseRepositoryFactory<M,ID extends Serializable> extends JpaRepositoryFactory {

    private EntityManager entityManager;

    /**
     * Creates a new {@link org.springframework.data.jpa.repository.support.JpaRepositoryFactory}.
     *
     * @param entityManager must not be {@literal null}
     */
    public MyBaseRepositoryFactory(EntityManager entityManager) {
        super(entityManager);
        this.entityManager = entityManager;
    }


    /**
     * 获取目的的资源库
     * @param metadata
     * @return
     */
    @Override
    protected Object getTargetRepository(RepositoryMetadata metadata) {

        Class<?> repositoryInterface = metadata.getRepositoryInterface();

        if(isBaseRepository(repositoryInterface)) {

            JpaEntityInformation<M, ID> entityInformation = getEntityInformation((Class<M>) metadata.getDomainType());
            MyBaseRepository repository = new MyBaseRepository<M, ID>(entityInformation, entityManager);

            return repository;
        }
        return super.getTargetRepository(metadata);
    }


    @Override
    protected Class<?> getRepositoryBaseClass(RepositoryMetadata metadata) {
        if (isBaseRepository(metadata.getRepositoryInterface())) {
            return MyBaseRepository.class;
        }
        return super.getRepositoryBaseClass(metadata);
    }


    //是否是基本的仓库类,主要考虑为了扩展Impl的时候,采用的业务处理方法
    private boolean isBaseRepository(Class<?> repositoryInterface) {
        return BaseRepository.class.isAssignableFrom(repositoryInterface);
    }

    @Override
    protected QueryLookupStrategy getQueryLookupStrategy(QueryLookupStrategy.Key key) {
        return super.getQueryLookupStrategy(key);
    }
}
