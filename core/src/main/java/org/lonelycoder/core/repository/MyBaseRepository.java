package org.lonelycoder.core.repository;

import com.google.common.collect.Sets;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.lonelycoder.core.entity.search.Searchable;
import org.lonelycoder.core.plugin.entity.LogicDeleteable;
import org.lonelycoder.core.repository.callback.SearchCallback;
import org.lonelycoder.core.repository.support.RepositoryHelper;
import org.lonelycoder.core.repository.support.annotation.QueryJoin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.LockMetadataProvider;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static org.springframework.data.jpa.repository.query.QueryUtils.toOrders;

/**
 * created by lihaoquan
 *
 * 自定义的仓库基类抽象:
 *
 * 可以把这个类想象成一个 BaseRepository 的代理, BaseRepository是抽象,不带实现的
 *
 * 例如,我在 BaseRepository 增加一个"特有的"方法抽象
 * 那么可以在这里对这个"特有的"方法进行实现
 *
 * printModel(M m) 是例子
 */
public class MyBaseRepository<M , ID extends Serializable> extends SimpleJpaRepository<M, ID>
        implements BaseRepository<M,ID> {

    private static Logger logger = LoggerFactory.getLogger(MyBaseRepository.class);

    public static final String LOGIC_DELETE_ALL_QUERY_STRING = "update %s x set x.deleted=true where x in (?1)";
    public static final String DELETE_ALL_QUERY_STRING = "delete from %s x where x in (?1)";
    public static final String FIND_QUERY_STRING = "from %s where 1=1";
    public static final String COUNT_QUERY_STRING = "select count(x) from %s x where 1=1 ";

    private EntityManager em;
    private JpaEntityInformation<M,ID> entityInformation;
    /**
     * 仓库辅助服务
     */
    private RepositoryHelper repositoryHelper;

    private LockMetadataProvider lockMetadataProvider;

    private Class<M> entityClass;//实体类
    private String entityName;//实体名称
    private String idName;


    /**
     * 查询所有的QL
     */
    private String findAllQL;
    /**
     * 统计QL
     */
    private String countAllQL;

    private QueryJoin[] joins;

    private SearchCallback searchCallback = SearchCallback.DEFAULT;



    public MyBaseRepository(JpaEntityInformation<M, ID> entityInformation, EntityManager entityManager) {
        super(entityInformation, entityManager);

        this.entityInformation = entityInformation;
        this.entityClass = this.entityInformation.getJavaType();
        this.entityName =  this.entityInformation.getEntityName();
        this.idName = this.entityInformation.getIdAttributeNames().iterator().next();
        this.em = entityManager;
        repositoryHelper = new RepositoryHelper(entityClass);

        findAllQL = String.format(FIND_QUERY_STRING,entityName);
        countAllQL = String.format(COUNT_QUERY_STRING,entityName);


    }

    public MyBaseRepository(Class<M> domainClass, EntityManager em) {
        super(domainClass, em);
    }


    /**
     * Configures a custom {@link org.springframework.data.jpa.repository.support.LockMetadataProvider} to be used to detect {@link javax.persistence.LockModeType}s to be applied to
     * queries.
     *
     * @param lockMetadataProvider
     */
    public void setLockMetadataProvider(LockMetadataProvider lockMetadataProvider) {
        super.setLockMetadataProvider(lockMetadataProvider);
        this.lockMetadataProvider = lockMetadataProvider;
    }

    /**
     * 设置searchCallback
     *
     * @param searchCallback
     */
    public void setSearchCallback(SearchCallback searchCallback) {
        this.searchCallback = searchCallback;
    }

    /**
     * 设置查询所有的ql
     *
     * @param findAllQL
     */
    public void setFindAllQL(String findAllQL) {
        this.findAllQL = findAllQL;
    }

    /**
     * 设置统计的ql
     *
     * @param countAllQL
     */
    public void setCountAllQL(String countAllQL) {
        this.countAllQL = countAllQL;
    }

    public void setJoins(QueryJoin[] joins) {
        this.joins = joins;
    }



    @Override
    public List<M> findAll() {
        return repositoryHelper.findAll(findAllQL);
    }


    @Override
    public Page<M> findAll(final Pageable pageable) {
        //return super.findAll(pageable);
        return new PageImpl<M>(repositoryHelper.<M>findAll(findAllQL,pageable),
                pageable,repositoryHelper.count(countAllQL));
    }

    @Override
    public List<M> findAll(Sort sort) {
        //return super.findAll(sort);
        return repositoryHelper.findAll(findAllQL,sort);
    }

    @Override
    public Page<M> findAll(final Searchable searchable) {
        List<M> list = repositoryHelper.findAll(findAllQL, searchable, searchCallback);
        long total = searchable.hasPageable() ? count(searchable) : list.size();
        return new PageImpl<M>(
                list,
                searchable.getPage(),
                total
        );
    }



    /**
     * 根据实体删除相应的实体
     * @param id
     */
    @Transactional
    @Override
    public void delete(final ID id) {
        M m = findOne(id);
        delete(m);
    }

    /**
     * 删除实体
     *
     * @param m 实体
     */
    @Transactional
    @Override
    public void delete(final M m) {
        if (m == null) {
            return;
        }
        if (m instanceof LogicDeleteable) {
            ((LogicDeleteable) m).markDeleted();
            save(m);
        } else {
            super.delete(m);
        }
    }

    @Transactional
    @Override
    public void delete(final ID[] ids) {
        if(ArrayUtils.isEmpty(ids)) {
            return;
        }

        List<M> models = new ArrayList<M>();
        for(ID id : ids) {
            M model;
            try {
                model = entityClass.newInstance();
            }catch (Exception e) {
                throw new RuntimeException("batch delete " + entityClass + " error", e);
            }

            try {
                BeanUtils.setProperty(model,idName,id);
            }catch (Exception e) {
                throw new RuntimeException("batch delete " + entityClass + " error, can not set id", e);
            }

            models.add(model);
        }

        deleteInBatch(models);
    }


    @Override
    public void deleteInBatch(final Iterable<M> entities) {
        Iterator<M> iter = entities.iterator();
        if(entities == null || !iter.hasNext()) {
            return;
        }

        Set models = Sets.newHashSet(iter);

        boolean logicDeleteableEntity = LogicDeleteable.class.isAssignableFrom(this.entityClass);

        //判断是否逻辑删除
        if (logicDeleteableEntity) {
            String ql = String.format(LOGIC_DELETE_ALL_QUERY_STRING, entityName);
            repositoryHelper.batchUpdate(ql, models);
        } else {
            String ql = String.format(DELETE_ALL_QUERY_STRING, entityName);
            repositoryHelper.batchUpdate(ql, models);
        }
    }


    /**
     * 按照主键查询
     * @return
     */
    @Transactional
    @Override
    public M findOne(ID id) {
        if(id == null) {
            return null;
        }

        if(id instanceof Integer && ((Integer) id).intValue() == 0 ) {
            return null;
        }

        if(id instanceof Long &&  ((Long) id).longValue() ==0L ) {
            return null;
        }

        return super.findOne(id);
    }


    /**
     * 根据Specification查询 直接从SimpleJpaRepository复制过来的
     * @param spec
     * @return
     */
    @Override
    public M findOne(Specification<M> spec) {
        try {
            return getQuery(spec, (Sort) null).getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }


    /**
     * Creates a {@link javax.persistence.TypedQuery} for the given {@link org.springframework.data.jpa.domain.Specification} and {@link org.springframework.data.domain.Sort}.
     *
     * @param spec can be {@literal null}.
     * @param sort can be {@literal null}.
     * @return
     */
    private TypedQuery<M> getQuery(Specification<M>spec, Sort sort) {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<M> query = builder.createQuery(entityClass);

        Root<M> root = applySpecificationToCriteria(spec, query);
        query.select(root);

        applyJoins(root);

        if (sort != null) {
            query.orderBy(toOrders(sort, root, builder));
        }

        TypedQuery<M> q = em.createQuery(query);

        repositoryHelper.applyEnableQueryCache(q);

        return applyLockMode(q);
    }


    private TypedQuery<Long> getCountQuery(Specification<M> spec) {

        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<Long> query = builder.createQuery(Long.class);


        Root<M> root = applySpecificationToCriteria(spec, query);

        if (query.isDistinct()) {
            query.select(builder.countDistinct(root));
        } else {
            query.select(builder.count(root));
        }

        TypedQuery<Long> q = em.createQuery(query);
        repositoryHelper.applyEnableQueryCache(q);
        return q;
    }


    private void applyJoins(Root<M> root) {
        if(joins == null) {
            return;
        }

        for(QueryJoin join : joins) {
            root.join(join.property(), join.joinType());
        }
    }


    /**
     * Applies the given {@link org.springframework.data.jpa.domain.Specification} to the given {@link javax.persistence.criteria.CriteriaQuery}.
     *
     * @param spec  can be {@literal null}.
     * @param query must not be {@literal null}.
     * @return
     */
    private <S> Root<M> applySpecificationToCriteria(Specification<M> spec, CriteriaQuery<S> query) {

        Assert.notNull(query);
        Root<M> root = query.from(entityClass);

        if (spec == null) {
            return root;
        }

        CriteriaBuilder builder = em.getCriteriaBuilder();
        Predicate predicate = spec.toPredicate(root, query, builder);

        if (predicate != null) {
            query.where(predicate);
        }

        return root;
    }


    private TypedQuery<M> applyLockMode(TypedQuery<M> query) {
        LockModeType type = lockMetadataProvider == null ? null : lockMetadataProvider.getLockModeType();
        return type == null ? query : query.setLockMode(type);
    }


    /**
     * 针对目前的数据库特性,使用自定义的统计语句
     * @return
     */
    @Override
    public long count() {
        return repositoryHelper.count(countAllQL);
    }

    @Override
    public long count(final Searchable searchable) {
        return repositoryHelper.count(countAllQL, searchable, searchCallback);
    }


    /*
    * (non-Javadoc)
    * @see org.springframework.data.jpa.repository.JpaSpecificationExecutor#count(org.springframework.data.jpa.domain.Specification)
    */
    public long count(Specification<M> spec) {

        return getCountQuery(spec).getSingleResult();
    }

    @Override
    public boolean exists(ID id) {
        return findOne(id)!=null;
    }

    @Override
    public void printModel(M m) {
        System.out.println(m.getClass().getName());
    }
}

