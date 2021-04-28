package com.ly.groovydemo;

import groovy.lang.Binding;
import groovy.lang.Closure;
import groovy.lang.GroovyShell;
import groovy.lang.Script;
import org.codehaus.groovy.control.CompilerConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;

public class GroovyScriptFuncTest {
    public static void main(String[] args) throws IOException {
        CompilerConfiguration config=new CompilerConfiguration();
        config.setScriptBaseClass("MyScript");
        Binding binding=new Binding();
        GroovyShell groovyShell=new GroovyShell(binding,config);
        Sum sum=new Sum(groovyShell);
        binding.setVariable("sum",sum);
        groovyShell.getClassLoader().parseClass(new File("/Users/menglong/JavaDev/IdeaProject/groovySample/src/main/hello.groovy"));
        Script script=groovyShell.parse("testScript1()");
        Script script1=groovyShell.parse("greet()");
        Script script2=groovyShell.parse("test()");
        String aa=(String) script.run();
        String bb=(String) script1.run();
        String cc=(String) script2.run();
        System.out.println(aa);
        System.out.println(bb);
        System.out.println(cc);
    }

    public static class Sum extends Closure<String>{

        public Sum(Object owner, Object thisObject) {
            super(owner, thisObject);
        }

        public Sum(Object owner) {
            super(owner);
        }

        @Override
        public String call() {
            return "super.call()";
        }
    }
    public static class MyScript extends Script{

        @Override
        public Object run() {
            return null;
        }

        @Override
        public Object invokeMethod(String name, Object args) {
            if ("test".equals(name)){
                return "testaaa";
            }
            return super.invokeMethod(name, args);
        }

        public static Object nvl(Object str, Object val) {

            return str == null || "".equals(str) ? val : str;

        }
        public static Object nvl(Object str) {

            return str == null || "".equals(str) ? "val" : str;

        }
    }
}
