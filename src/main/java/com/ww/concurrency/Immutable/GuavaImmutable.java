package com.ww.concurrency.Immutable;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import lombok.extern.slf4j.Slf4j;

//不可变集合测试
@Slf4j
public class GuavaImmutable {

    private final static ImmutableList immutableList = ImmutableList.of(1,2,3);

    private final static ImmutableSet immutableSet = ImmutableSet.copyOf(immutableList);
    //ImmutableMap的第1种写法
    private final static ImmutableMap<Integer, String> map1 = ImmutableMap.of(1,"a",2,"b");
    //ImmutableMap的第2种写法
    private final static ImmutableMap<Integer, String> map2 = ImmutableMap.<Integer,String>builder().put(3,"c").put(4,"d").build();

    public static void main(String[] args) {
        //无法增加、修改，抛出异常java.lang.UnsupportedOperationException
        immutableList.add(4);

        immutableSet.add(5);

        map1.put(5,"e");

        map2.put(6,"f");
    }
}
