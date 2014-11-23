package org.lonelycoder.core.repository.callback;


import org.lonelycoder.core.entity.search.Searchable;

import javax.persistence.Query;

/**
 * @author : lihaoquan
 *
 * 空的搜索回调函数
 */
public class NoneSearchCallback implements SearchCallback {

    @Override
    public void prepareQL(StringBuilder ql, Searchable search) {

    }

    @Override
    public void prepareOrder(StringBuilder ql, Searchable search) {

    }

    @Override
    public void setValues(Query query, Searchable search) {

    }

    @Override
    public void setPageable(Query query, Searchable search) {

    }
}
