package org.lonelycoder.server.system.user.repository;

import org.lonelycoder.core.entity.search.Searchable;
import org.lonelycoder.core.repository.callback.DefaultSearchCallback;

/**
 * @author : lihaoquan
 */
public class UserSearchCallback extends DefaultSearchCallback {

    @Override
    public void prepareQL(StringBuilder ql, Searchable search) {
        super.prepareQL(ql, search);
    }
}
