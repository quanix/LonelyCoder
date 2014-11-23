package org.lonelycoder.core.repository.support;

import org.lonelycoder.core.entity.search.Searchable;
import org.lonelycoder.core.repository.callback.SearchCallback;
import org.lonelycoder.core.repository.support.annotation.EnableQueryCache;
import org.lonelycoder.core.util.Assert;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.JpaEntityInformationSupport;
import org.springframework.orm.jpa.SharedEntityManagerCreator;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import java.util.List;

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
     * @param entityClass 是否开启查询缓存
     */
    public RepositoryHelper(Class<?> entityClass) {
        this.entityClass = entityClass;

        EnableQueryCache enableQueryCacheAnnotation =
                AnnotationUtils.findAnnotation(entityClass, EnableQueryCache.class);

        boolean enableQueryCache = false;
        if (enableQueryCacheAnnotation != null) {
            enableQueryCache = enableQueryCacheAnnotation.value();
        }
        this.enableQueryCache = enableQueryCache;
    }

    /**
     * 在applicationContext.xml中已经使用MethodInvok为本静态方法设置了entityManagerFactory的属性
     * @param entityManagerFactory
     */
    public static void setEntityManagerFactory(EntityManagerFactory entityManagerFactory) {
        entityManager = SharedEntityManagerCreator.createSharedEntityManager(entityManagerFactory);
    }

    public static EntityManager getEntityManager() {
        //System.out.println("entityManager.getClass().getSimpleName() = "+entityManager.getClass().getSimpleName());
        Assert.notNull(entityManager,"entityManager must not null");
        return entityManager;
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


    private void assertConverted(Searchable searchable) {
        if (!searchable.isConverted()) {
            searchable.convert(this.entityClass);
        }
    }

    /**
     * 按顺序设置Query参数
     *
     * @param query
     * @param params
     */
    public void setParameters(Query query, Object[] params) {
        if (params != null) {
            for (int i = 0; i < params.length; i++) {
                query.setParameter(i + 1, params[i]);
            }
        }
    }

    /**
     * 拼排序
     *
     * @param sort
     * @return
     */
    public String prepareOrder(Sort sort) {
        if (sort == null || !sort.iterator().hasNext()) {
            return "";
        }
        StringBuilder orderBy = new StringBuilder("");
        orderBy.append(" order by ");
        orderBy.append(sort.toString().replace(":", " "));
        return orderBy.toString();
    }

    /**
     * <p>执行批处理语句.如 之间insert, update, delete 等.<br/>
     * 具体使用请参考测试用例：{@see com.sishuok.es.common.repository.UserRepository2ImplIT#testBatchUpdate()}
     *
     * @param ql
     * @param params
     * @return
     */
    public int batchUpdate(final String ql, final Object... params) {

        Query query = getEntityManager().createQuery(ql);
        setParameters(query, params);

        return query.executeUpdate();
    }


    /**
     * 根据查询语句的统计功能
     * @param ql
     * @param params
     * @return
     */
    public long count(final String ql, final Object... params) {
        Query query = entityManager.createQuery(ql);
        applyEnableQueryCache(query);
        setParameters(query,params);

        return (Long) query.getSingleResult();
    }

    public long count(final String ql, final Searchable searchable, final SearchCallback searchCallback) {

        assertConverted(searchable);

        StringBuilder s = new StringBuilder(ql);
        searchCallback.prepareQL(s, searchable);
        Query query = getEntityManager().createQuery(s.toString());
        applyEnableQueryCache(query);
        searchCallback.setValues(query, searchable);

        return (Long) query.getSingleResult();
    }

    /**
     * 批量更新操作
     * @param ql
     * @param params
     * @return
     */
    public int batchupdate(final String ql, final Object... params) {
        Query query = getEntityManager().createQuery(ql);
        setParameters(query,params);
        return query.executeUpdate();
    }



    public <M> List<M> findAll(final String ql, final Object... params) {
        //此处必须 (Pageable) null  否则默认有调用自己了 可变参列表
        return findAll(ql, (Pageable) null, params);
    }


    /**
     * <p>根据ql和按照索引顺序的params执行ql，sort存储排序信息 null表示不排序<br/>
     *
     * @param ql
     * @param sort   null表示不排序
     * @param params
     * @param <M>
     * @return
     */
    public <M> List<M> findAll(final String ql, final Sort sort, final Object... params) {

        Query query = getEntityManager().createQuery(ql + prepareOrder(sort));
        applyEnableQueryCache(query);
        setParameters(query, params);

        return query.getResultList();
    }


    /**
     * <p>根据ql和按照索引顺序的params执行ql，pageable存储分页信息 null表示不分页<br/>
     *
     * @param ql
     * @param pageable null表示不分页
     * @param params
     * @param <M>
     * @return
     */
    public <M> List<M> findAll(final String ql, final Pageable pageable, final Object... params) {

        Query query = getEntityManager().createQuery(ql + prepareOrder(pageable != null ? pageable.getSort() : null));
        applyEnableQueryCache(query);
        setParameters(query, params);
        if (pageable != null) {
            query.setFirstResult(pageable.getOffset());
            query.setMaxResults(pageable.getPageSize());
        }

        return query.getResultList();
    }


    /**
     *
     * @param ql
     * @param searchable      查询条件、分页 排序
     * @param searchCallback  查询回调  自定义设置查询条件和赋值
     * @param <M>
     * @return
     */
    public <M> List<M> findAll(final String ql, final Searchable searchable,final SearchCallback searchCallback) {

        assertConverted(searchable);
        StringBuilder s = new StringBuilder(ql);
        searchCallback.prepareQL(s, searchable);
        searchCallback.prepareOrder(s, searchable);
        Query query = getEntityManager().createQuery(s.toString());
        applyEnableQueryCache(query);
        searchCallback.setValues(query, searchable);
        searchCallback.setPageable(query, searchable);

        return query.getResultList();
    }
}
