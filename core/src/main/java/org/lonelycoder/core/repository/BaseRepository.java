package org.lonelycoder.core.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;

/**
 * created by lihaoquan
 *
 * 基础存储仓库
 *
 */
@NoRepositoryBean
public interface BaseRepository<M,ID extends Serializable> extends JpaRepository<M,ID> {

    public void printModel(M m);
}