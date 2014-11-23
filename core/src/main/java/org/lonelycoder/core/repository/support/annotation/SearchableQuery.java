package org.lonelycoder.core.repository.support.annotation;

import org.lonelycoder.core.repository.callback.SearchCallback;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author : lihaoquan
 *
 * 覆盖默认的跟进条件查询数据
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface SearchableQuery {

    /**
     * 覆盖默认的所有查询QL
     * @return
     */
    String findAllQuery() default "";


    /**
     * 覆盖默认的统计所有QL
     * @return
     */
    String countAllQuery() default "";


    QueryJoin[] joins() default {};

    /**
     * 给ql拼接条件以及赋值的回调类型
     * @return
     */
    Class<? extends SearchCallback> callbackClass() default SearchCallback.class;

}
