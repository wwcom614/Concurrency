package com.ww.concurrency.Immutable;

import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.Map;

//不可变集合测试
@Slf4j
public class CollectionsUnmodifiable {

    private static Map<Integer, String> map = Maps.newHashMap();

    static {
        map.put(1,"a");
        map.put(2,"b");
        map.put(3,"c");
        map = Collections.unmodifiableMap(map);
    }

    public static void main(String[] args) {
        //无法增加、修改，抛出异常java.lang.UnsupportedOperationException
        map.put(4,"d");
        log.info("{}",map.get(1));
    }
}
