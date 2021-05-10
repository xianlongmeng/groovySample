package com.ly.groovydemo;

import com.ly.resource.test.ResourceTest;
import groovy.lang.*;
import org.codehaus.groovy.control.CompilerConfiguration;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class GroovyScriptFuncTest {
    public static void main(String[] args) throws IOException, URISyntaxException {

        CompilerConfiguration config = new CompilerConfiguration();
        config.setScriptBaseClass("com.ly.groovydemo.GroovyScriptFuncTest.MyScript");
        Binding binding = new Binding();
        GroovyShell groovyShell = new GroovyShell(binding, config);
        Sum sum = new Sum(groovyShell);
        binding.setVariable("sum", sum);
        binding.setVariable("aa", "aasfasd");
//        ResourceTest resourceTest=new ResourceTest();
//        resourceTest.findResources("/aa").forEach(System.out::println);
//
//        return;
        MyScript.addFuncExtent(new File("/Users/menglong/JavaDev/IdeaProject/groovySample/src/main/hello.groovy"));
        MyScript.addFuncExtent(new File("/Users/menglong/JavaDev/IdeaProject/groovySample/src/main/hello1.groovy"));
        Script script = groovyShell.parse("testScript1()");

        Script script1 = groovyShell.parse("greet()");
        Script script2 = groovyShell.parse("test()");
        Script script3 = groovyShell.parse("testScript11()");
        Script script4 = groovyShell.parse("test2()");
        String aa = (String) script1.run();
        String bb = (String) script2.run();
        String cc = (String) script3.run();
        String dd = (String) script4.run();
        System.out.println(script.run());
        System.out.println(aa);
        System.out.println(bb);
        System.out.println(cc);
        System.out.println(dd);
    }

    public static class Sum extends Closure<String> {

        public Sum(Object owner, Object thisObject) {
            super(owner, thisObject);
        }

        public Sum(Object owner) {
            super(owner);
        }

        @Override
        public String call() {
            return "sum.call()";
        }
    }

    public static class MyScript extends Script {

        private static final Map<String, GroovyObject> funcGroovyObjectMap = new ConcurrentHashMap<>();
        private static final Set<File> scriptFiles = Collections.synchronizedSet(new HashSet<>());
        private static final Set<Class<GroovyObject>> groovyClazzSet = Collections.synchronizedSet(new HashSet<>());
        private static final List<GroovyObject> groovyObjects = Collections.synchronizedList(new ArrayList<>());
        private static final GroovyClassLoader groovyClassLoader;

        static {
            CompilerConfiguration config = new CompilerConfiguration();
            config.setSourceEncoding("UTF-8");
            // 设置该GroovyClassLoader的父ClassLoader为当前线程的加载器(默认)
            groovyClassLoader = new GroovyClassLoader(Thread.currentThread().getContextClassLoader(), config);
        }

        public static void addFuncExtent(File scriptFile) {
            if (!scriptFile.exists() || scriptFiles.contains(scriptFile))
                return;
            Class<?> groovyClass;
            try {
                groovyClass = groovyClassLoader.parseClass(scriptFile);
                var groovyObject = (GroovyObject) groovyClass.getDeclaredConstructor().newInstance();
                addFuncExtent(groovyObject);
                scriptFiles.add(scriptFile);
            } catch (IOException | NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {

            }
        }

        public static void addFuncExtent(URL scriptUrl) throws URISyntaxException {
            if (scriptUrl == null)
                return;
            File file = new File(scriptUrl.toURI());
            addFuncExtent(file);
        }

        public static void addFuncExtent(Class<GroovyObject> groovyClazz) {
            if (groovyClazz == null || groovyClazzSet.contains(groovyClazz))
                return;
            try {
                var groovyObject = groovyClazz.getDeclaredConstructor().newInstance();
                addFuncExtent(groovyObject);
                groovyClazzSet.add(groovyClazz);
            } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {

            }
        }

        private static void addFuncExtent(GroovyObject groovyObject) {
            if (groovyObject == null || groovyObjects.contains(groovyObject))
                return;
            Method[] methods = groovyObject.getClass().getMethods();
            for (Method method : methods) {
                if (method != null && Modifier.isPublic(method.getModifiers())) {
                    funcGroovyObjectMap.put(method.getName(), groovyObject);
                }
            }
            groovyObjects.add(groovyObject);
        }

        public static Object nvl(Object str, Object val) {

            return str == null || "".equals(str) ? val : str;

        }

        public static Object nvl(Object str) {

            return str == null || "".equals(str) ? "val" : str;

        }

        @Override
        public Object run() {
            return null;
        }

        @Override
        public Object invokeMethod(String name, Object args) {
            GroovyObject groovyObject = funcGroovyObjectMap.get(name);
            if (groovyObject == null) {
                for (GroovyObject go : groovyObjects) {
                    if (go == null)
                        continue;
                    try {
                        return groovyFunc(go, name, args);
                    } catch (Exception e) {
                        //next
                    }
                }
            } else {
                return groovyFunc(groovyObject, name, args);
            }
            return super.invokeMethod(name, args);
        }

        private Object groovyFunc(GroovyObject groovyObject, String name, Object args) {
            if (groovyObject instanceof Script) {
                // 获得TestGroovy的实例
                ((Script) groovyObject).setBinding(this.getBinding());
            }
            return groovyObject.invokeMethod(name, args);
        }
    }
}
