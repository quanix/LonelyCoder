package org.lonelycoder.core.entity;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;

/**
 * Author : lihaoquan
 * Description :
 * ����ֻ��Ҫ����ͷ�ϼ� @SequenceGenerator(name="seq", sequenceName="���sequence����")
 */

@MappedSuperclass
public class BaseOracleEntity<PK extends Serializable> extends AbstractEntity<PK> {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq")
    private PK id;

    @Override
    public PK getId() {
        return null;
    }

    @Override
    public void setId(PK pk) {

    }
}
