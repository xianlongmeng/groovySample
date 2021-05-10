package aa

import org.codehaus.groovy.control.CompilerConfiguration

class MyScript2 extends Script {
    String name

    String greet() {
        "Hello, $name!"
    }

    @Override
    Object run() {
        return null
    }
    static GroovyClassLoader groovyClassLoader = null;

    static void initGroovyClassLoader() {
        CompilerConfiguration config = new CompilerConfiguration();
        config.setSourceEncoding("UTF-8");
        // 设置该GroovyClassLoader的父ClassLoader为当前线程的加载器(默认)
        groovyClassLoader = new GroovyClassLoader(Thread.currentThread().getContextClassLoader(), config);
    }

    @Override
    Object invokeMethod(String name, Object args) {
        if (!name?.trim()) {
            return super.invokeMethod(name, args)
        }
        if ("test" == name) {
            return "test1"
        } else if ("testScript1" == name) {
            File groovyFile = new File("/Users/menglong/JavaDev/IdeaProject/groovySample/src/main/hello1.groovy");
            if (!groovyFile.exists()) {
                System.out.println("文件不存在");
                return super.invokeMethod(name, args)
            }

            initGroovyClassLoader();

            try {
                List<String> result;
                // 获得TestGroovy加载后的class
                Class<?> groovyClass = groovyClassLoader.parseClass(groovyFile);
                def ms=groovyClass.getDeclaredMethods()
                if (Script.class.isAssignableFrom(groovyClass)) {
                    // 获得TestGroovy的实例
                    Class[] cArg = new Class[1];
                    cArg[0] = Binding.class

                    GroovyObject groovyObject = (GroovyObject) groovyClass.getDeclaredConstructor(cArg).newInstance(binding);
                    // 反射调用printArgs方法得到返回值
                    Object methodResult = groovyObject.invokeMethod("testScript1", args)
                    return methodResult
                }else{

                    GroovyObject groovyObject = (GroovyObject) groovyClass.getDeclaredConstructor().newInstance();
                    // 反射调用printArgs方法得到返回值
                    Object methodResult = groovyObject.invokeMethod("testScript1", args)
                    return methodResult
                }


            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
        return super.invokeMethod(name, args)
    }
}

