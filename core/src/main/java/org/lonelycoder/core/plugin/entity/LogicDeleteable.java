package org.lonelycoder.core.plugin.entity;

/**
 * Author : lihaoquan
 * Description : 实体实现该接口表示想要逻辑删除
 * 约定删除标识列名为deleted
 */
public interface LogicDeleteable {

    public Boolean getDeleted();

    public void setDeleted(Boolean deleted);

    /**
     * 标记为已经删除
     */
    public void markDeleted();
}
