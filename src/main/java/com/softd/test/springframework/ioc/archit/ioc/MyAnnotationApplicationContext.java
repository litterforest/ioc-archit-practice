package com.softd.test.springframework.ioc.archit.ioc;

import com.softd.test.springframework.ioc.archit.Starter;
import com.softd.test.springframework.ioc.archit.annotation.MyComponent;
import com.softd.test.springframework.ioc.archit.util.ClassUtils;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 功能描述
 *
 * @author cobee
 * @since 2020-09-20
 */
public class MyAnnotationApplicationContext {
    private static ConcurrentHashMap<String, Object> singletonObjectMap = new ConcurrentHashMap<>(256);

    public Object getBean(String beanId) {
        if (beanId == null) {
            throw new RuntimeException("id不能为空");
        }
        if (singletonObjectMap.containsKey(beanId)) {
            return singletonObjectMap.get(beanId);
        }
        return doCreateBean(beanId);
    }

    /**
     * 创建bean对象，没有就返回null，不抛出异常
     *
     * @param beanId
     * @return
     */
    private Object doCreateBean(String beanId) {
        // 包扫描
        List<Class<?>> allClassByPackageName = null;
        try {
            allClassByPackageName = ClassUtils.getAllClassByPackageName(Starter.class.getPackage());
            Object instance = null;
            // 第一次调用初始化所有bean
            for (Class<?> aClass : allClassByPackageName) {
                if (aClass.isAnnotationPresent(MyComponent.class)) {
                    MyComponent myComponent = aClass.getAnnotation(MyComponent.class);
                    String beanName = myComponent.value();
                    if ("".equals(beanName)) {
                        beanName = aClass.getSimpleName();
                        String firstAlpha = beanName.substring(0,1).toLowerCase();
                        String leftLetter = beanName.substring(1);
                        beanName = firstAlpha + leftLetter;
                    }
                    Class<?> beanClass = Class.forName(aClass.getCanonicalName());
                    Object newInstance = beanClass.newInstance();
                    singletonObjectMap.put(beanName, newInstance);
                    if (beanId.equals(beanName)) {
                        instance = newInstance;
                    }
                }
            }
            return instance;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) {
        MyAnnotationApplicationContext myAnnotationApplicationContext = new MyAnnotationApplicationContext();
        Object userServiceImpl = myAnnotationApplicationContext.getBean("userService");
        System.out.println(userServiceImpl);
    }
}
