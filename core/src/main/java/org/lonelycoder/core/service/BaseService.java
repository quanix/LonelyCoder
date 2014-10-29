package org.lonelycoder.core.service;

import org.lonelycoder.core.entity.AbstractEntity;
import org.lonelycoder.core.repository.BaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.Serializable;
import java.util.List;

/**
 * @author : lihaoquan
 */
public abstract class BaseService<M extends AbstractEntity,ID extends Serializable>  {

    protected BaseRepository<M, ID> baseRepository;

    @Autowired
    public void setBaseRepository(BaseRepository<M, ID> baseRepository) {
        this.baseRepository = baseRepository;
    }

    /**
     * 按照主键查询
     *
     * @param id 主键
     * @return 返回id对应的实体
     */
    public M findOne(ID id) {
        return baseRepository.findOne(id);
    }

    /**
     * 统计实体总数
     *
     * @return 实体总数
     */
    public long count() {
        return baseRepository.count();
    }

    public List<M> findAll() {
        return baseRepository.findAll();
    }

    /**
     * 分页查找列表
     * @param pageable
     * @return
     */
    public Page<M> findAll(Pageable pageable) {
        return baseRepository.findAll(pageable);
    }

    public M save(M model) {
        return baseRepository.save(model);
    }

    public M saveAndFlush(M model) {
        model = save(model);
        baseRepository.flush();
        return model;
    }

    public void delete(ID id) {
        baseRepository.delete(id);
    }

    public void delete(ID[] ids) {
        for(ID id : ids) {
            delete(id);
        }
    }

    public void printModel(M m) {
        baseRepository.printModel(m);
    }
}
