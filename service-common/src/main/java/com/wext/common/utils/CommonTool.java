package com.wext.common.utils;

import lombok.NonNull;
import org.springframework.beans.BeanUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class CommonTool {

    public static <E, T> T transBean(E src, Class<T> tClass) {
        try {
            var res = tClass.getDeclaredConstructor().newInstance();
            BeanUtils.copyProperties(src, res);
            return res;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static <E, T> List<T> transBean(@NonNull List<E> src, Class<T> tClass) {
        try {
            var res = new ArrayList<T>(src.size());
            for (E e : src) {
                var newOne = tClass.getDeclaredConstructor().newInstance();
                BeanUtils.copyProperties(e, newOne);
                res.add(newOne);
            }
            return res;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
            return null;
        }
    }
}
