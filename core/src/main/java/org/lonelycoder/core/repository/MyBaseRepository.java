package org.lonelycoder.core.repository;

import com.google.common.collect.Sets;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.lonelycoder.core.plugin.entity.LogicDeleteable;
import org.lonelycoder.core.repository.support.RepositoryHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.LockMetadataProvider;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

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

    @Override
    public List<M> findAll() {
        System.out.println("获取所有的:"+this.entityName);
        logger.info("通过JPA获取所有的["+entityName+"]集合");
        return super.findAll();
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
        //super.deleteInBatch(entities);
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

    @Override
    public void printModel(M m) {
        System.out.println(m.getClass().getName());
    }
}

