package com.riskcontrol.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.NumberUtil;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class StreamUtils {

    public static <E, T> List<T> toList(Collection<E> collection, Function<? super E, T> mapper) {
        if (CollUtil.isEmpty(collection)) {
            return CollUtil.newArrayList();
        }
        return collection.stream().map(mapper).collect(Collectors.toList());
    }
}
