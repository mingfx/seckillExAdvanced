package com.ming.seckill.util;

import java.util.UUID;

public class UUIDUtil {
    //把原生uuid的-去掉
    public static String uuid(){
        return UUID.randomUUID().toString().replace("-","");
    }
}
