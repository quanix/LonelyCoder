package org.lonelycoder.core.repository;


import org.lonelycoder.core.entity.search.Searchable;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;
import java.util.List;

/**
 * created by lihaoquan
 *
 * 基础存储仓库
 * 抽象DAO层基类 提供一些简便方法
 * 想要使用该接口需要在spring配置文件的jpa:repositories中添加
 *
 * 泛型 ： M 表示实体类型；ID表示主键类型
 */
@NoRepositoryBean
public interface BaseRepository<M,ID extends Serializable> extends JpaRepository<M,ID> {

    /**
     * 根据主键删除
     * @param ids
     */
    public void delete(ID[] ids);


    /**
     * 查找所有
     * @return
     */
    List<M> findAll();



    /**
     * 打印模型信息
     * @param m
     */
    public void printModel(M m);

    /**
     * 根据条件查询所有
     * 条件 + 分页 + 排序
     *
     * @param searchable
     * @return
     */
    public Page<M> findAll(Searchable searchable);

    /**
     * 根据条件统计所有记录数
     *
     * @param searchable
     * @return
     */
    public long count(Searchable searchable);

}