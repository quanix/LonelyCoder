package org.lonelycoder.core.entity;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.springframework.data.domain.Persistable;

import java.io.Serializable;

/**
 * Author : lihaoquan
 * Description : 抽象实体基类
 *
 * 对于ID的生存策略在继承类中实现
 */
public abstract class AbstractEntity<ID extends Serializable> implements Persistable<ID> {

    public abstract ID getId();

    /**
     * 设置实体的主键
     *
     * @param id the id to set
     */
    public abstract void setId(final ID id);

    /*
     * 继承 Persistable 的 isNew() 方法
     *
     */
    public boolean isNew() {

        return null == getId();
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {

        if (null == obj) {
            return false;
        }

        if (this == obj) {
            return true;
        }

        if (!getClass().equals(obj.getClass())) {
            return false;
        }

        AbstractEntity<?> that = (AbstractEntity<?>) obj;

        return null == this.getId() ? false : this.getId().equals(that.getId());
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {

        int hashCode = 17;

        hashCode += null == getId() ? 0 : getId().hashCode() * 31;

        return hashCode;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }


}
