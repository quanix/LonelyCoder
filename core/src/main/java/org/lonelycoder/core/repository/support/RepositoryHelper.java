package org.lonelycoder.core.repository.support;

import org.lonelycoder.core.repository.support.annotation.EnableQueryCache;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.JpaEntityInformationSupport;
import org.springframework.orm.jpa.SharedEntityManagerCreator;
import org.springframework.util.Assert;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;

/**
 * @author : lihaoquan
 *
 * 资源仓库辅助类
 */
public class RepositoryHelper {

    private static EntityManager entityManager;
    private Class<?> entityClass;
    private boolean enableQueryCache = false;

    /**
     * 在applicationContext.xml中已经使用MethodInvok为本静态方法设置了entityManagerFactory的属性
     * @param entityManagerFactory
     */
    public static void setEntityManagerFactory(EntityManagerFactory entityManagerFactory) {
        entityManager = SharedEntityManagerCreator.createSharedEntityManager(entityManagerFactory);
    }

    public static EntityManager getEntityManager() {
        System.out.println("entityManager.getClass().getSimpleName() = "+entityManager.getClass().getSimpleName());
        return entityManager;
    }

    public RepositoryHelper(Class<?> entityClass) {
        Assert.notNull(entityManager, "entityManager is null");
        this.entityClass = entityClass;
        EnableQueryCache enableQueryCacheAnnotation = AnnotationUtils.findAnnotation(entityClass, EnableQueryCache.class);

        boolean enableQueryCache = false;
        if(enableQueryCacheAnnotation != null) {
            enableQueryCache = enableQueryCacheAnnotation.value();
            this.enableQueryCache = enableQueryCache;//设置是否采用缓存查询的服务
        }
    }

    public static void flush() {
        getEntityManager().flush();
    }

    public static void clear() {
        flush();
        getEntityManager().clear();
    }


    public <T> JpaEntityInformation<T, ?> getMetadata(Class<T> entityClass) {
        return JpaEntityInformationSupport.getMetadata(entityClass, entityManager);
    }

    public String getEntityName(Class<?> entityClass) {
        return getMetadata(entityClass).getEntityName();
    }


    /**
     * 利用Hibernate的缓存机制
     * @param query
     */
    public void applyEnableQueryCache(Query query) {
        if(enableQueryCache) {
            query.setHint("org.hibernate.cacheable",true);
        }
    }
}
