package org.lonelycoder.core.repository;

import org.lonelycoder.core.repository.callback.SearchCallback;
import org.lonelycoder.core.repository.support.annotation.SearchableQuery;
import org.springframework.beans.BeanUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.query.QueryLookupStrategy;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import java.io.Serializable;

/**
 * @author : lihaoquan
 *
 * 自定义基础仓库工厂
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
     * 也就是说,我们之前的类是implements BaseRepository的, 但,可用通过这个方式,使我们的类实现的是MyBaseRepositoty
     * @param metadata
     * @return
     */
    @Override
    protected Object getTargetRepository(RepositoryMetadata metadata) {

        Class<?> repositoryInterface = metadata.getRepositoryInterface();

        if(isBaseRepository(repositoryInterface)) {

            JpaEntityInformation<M, ID> entityInformation = getEntityInformation((Class<M>) metadata.getDomainType());
            MyBaseRepository repository = new MyBaseRepository<M, ID>(entityInformation, entityManager);

            SearchableQuery searchableQuery = AnnotationUtils.findAnnotation(repositoryInterface,SearchableQuery.class);

            if(searchableQuery != null) {
                String countAllQL = searchableQuery.countAllQuery();
                if(!StringUtils.isEmpty(countAllQL)) {
                    repository.setCountAllQL(countAllQL);
                }

                String findAllQL = searchableQuery.findAllQuery();
                if(!StringUtils.isEmpty(findAllQL)) {
                    repository.setFindAllQL(findAllQL);
                }

                Class<? extends SearchCallback> callbackClass = searchableQuery.callbackClass();
                if(callbackClass != null && callbackClass != SearchCallback.class) {
                    //实例化一个callback对象
                    repository.setSearchCallback(BeanUtils.instantiate(callbackClass));
                }

                //设置连接词
                repository.setJoins(searchableQuery.joins());
            }

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


    //用回父类的就可用了
    @Override
    protected QueryLookupStrategy getQueryLookupStrategy(QueryLookupStrategy.Key key) {
        return super.getQueryLookupStrategy(key);
    }
}
