package com.softd.test.springframework.ioc.archit.ioc;

import com.softd.test.springframework.ioc.archit.service.UserService;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 功能描述
 *
 * @author cobee
 * @since 2020-09-20
 */
public class MyClassPathXmlApplicationContext {
    private static ConcurrentHashMap<String, Object> singletonObjectMap = new ConcurrentHashMap<>(256);
    private String xmlPath = "applicationContext.xml";

    public MyClassPathXmlApplicationContext() { }

    public MyClassPathXmlApplicationContext(String xmlPath) {
        this.xmlPath = xmlPath;
    }

    public Object getBean(String id) {
        if (id == null) {
            throw new RuntimeException("id不能为空");
        }
        if (singletonObjectMap.containsKey(id)) {
            return singletonObjectMap.get(id);
        }
        // 创建对应的Bean对象
        try {
            String beanId = createBeanInstance(id);
            if (beanId == null) {
                throw new RuntimeException("beanId:" + id + "，没有找到对应的bean定义信息");
            }
            return singletonObjectMap.get(id);
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     *
     * @param id
     * @return 返回bean的id，如果为null，说明没有创建成功
     * @throws DocumentException
     */
    private String createBeanInstance(String id) throws DocumentException, ClassNotFoundException, IllegalAccessException, InstantiationException {
        List<Element> elements = readXml();
        if (elements == null || elements.size() == 0) {
            return null;
        }
        for (Element element : elements) {
            String beanId = element.attributeValue("id");
            if (Objects.equals(id, beanId)) {
                String className = element.attributeValue("class");
                Class<?> clazz = Class.forName(className);
                Object newInstance = clazz.newInstance();
                singletonObjectMap.put(id, newInstance);
                return id;
            }
        }
        return null;
    }

    private InputStream getInputStream() {
        InputStream resourceAsStream = getClass().getClassLoader().getResourceAsStream(xmlPath);
        System.out.println(resourceAsStream);
        return resourceAsStream;
    }

    private List<Element> readXml() throws DocumentException {
        SAXReader saxReader = new SAXReader();
        InputStream inputStream = getInputStream();
        Document document = saxReader.read(inputStream);
        Element rootElement = document.getRootElement();
        List elements = rootElement.elements();
        if (inputStream != null) {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return elements;
    }

    public static void main(String[] args) {
        MyClassPathXmlApplicationContext myClassPathXmlApplicationContext = new MyClassPathXmlApplicationContext();
        UserService userService = (UserService) myClassPathXmlApplicationContext.getBean("userService");
        System.out.println(userService);
        System.out.println(userService.getUser(1L));
    }

}
