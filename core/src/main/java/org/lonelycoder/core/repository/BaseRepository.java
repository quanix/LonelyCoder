package org.lonelycoder.core.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;

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
     * 打印模型信息
     * @param m
     */
    public void printModel(M m);
}