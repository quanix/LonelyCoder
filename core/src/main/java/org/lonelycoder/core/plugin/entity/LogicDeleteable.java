package org.lonelycoder.core.plugin.entity;

/**
 * Author : lihaoquan
 * Description : ʵ��ʵ�ָýӿڱ�ʾ��Ҫ�߼�ɾ��
 * Լ��ɾ����ʶ����Ϊdeleted
 */
public interface LogicDeleteable {

    public Boolean getDeleted();

    public void setDeleted(Boolean deleted);

    /**
     * ���Ϊ�Ѿ�ɾ��
     */
    public void markDeleted();
}
