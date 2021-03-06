package com.ly.groovydemo;

import groovy.lang.*;
import groovy.util.GroovyScriptEngine;
import groovy.util.ResourceException;
import groovy.util.ScriptException;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.script.Bindings;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.io.*;
import java.util.Date;

@SpringBootApplication
public class GroovydemoApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(GroovydemoApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        scriptThread();
    }

    private static class ShellThread implements Runnable{

        Script script;
        public ShellThread(Script script){
            this.script=script;
        }
        @Override
        public void run() {
            Binding binding=new Binding();
            binding.setVariable("sum",0);
            shellRun(script,binding);
        }
    }
    public static void shellRun(Script script,Binding binding){
        script.setBinding(binding);
        script.run();
    }
    public static void scriptThread() throws InterruptedException {
        GroovyShell shell=new GroovyShell();
        GroovyCodeSource groovyCodeSource=new GroovyCodeSource("for (i in 1..100) { sum+=i;println(\"$i - $sum\");}","aa",GroovyShell.DEFAULT_CODE_BASE);
        //Script script=shell.parse("for (i in 1..100) { sum+=i;println(\"$i - $sum\");}");

        //Script script1=shell.parse("for (i in 1..100) { sum+=i;println(\"$i - $sum\");}");
        Script script=shell.parse(groovyCodeSource);
        Script script1=shell.parse(groovyCodeSource);
        Thread thread=new Thread(new ShellThread(script));
        Thread thread1=new Thread(new ShellThread(script1));
        thread.start();
        thread1.start();
        //thread.join();
    }
    public static void evalScriptText() throws Exception{
        //groovy.lang.Binding
        Binding binding = new Binding();
        GroovyShell shell = new GroovyShell(binding);

        binding.setVariable("name", "zhangsan");
        shell.evaluate("println 'Hello World! I am ' + name;");
        //???script???,????????????,????????????def,??????scrope?????????.
        shell.evaluate("date = new Date();");
        Date date = (Date)binding.getVariable("date");
        System.out.println("Date:" + date.getTime());
        //?????????????????????,??????script???????????????,??????????????????
        //??????shell?????????,???????????????,????????????"session"???????????????."date"??????????????????script?????????
        Script script = shell.parse("def time = date.time; return time;");
        for (int i=0;i<100000;i++) {

//            Binding binding1 = new Binding();
//            binding1.setVariable("date",new Date());
//            script.setBinding(binding1);

            //Long time = (Long)shell.evaluate("def time = date.time; return time;");

            Long time = (Long) script.run();
            System.out.println("Time:" + time+"--"+i);
        }
//        binding.setVariable("list", new String[]{"A","B","C"});
//        //invoke method
//        String joinString = (String)shell.evaluate("def call(){return list.join(' - ')};call();");
//        System.out.println("Array join:" + joinString);
//        shell = null;
//        binding = null;
    }
    /**
     * ???groovy??????,?????????????????????,??????????????????main??????????????????????????????,????????????.
     */
    public static void evalScriptAsMainMethod(){
        String[] args = new String[]{"Zhangsan","10"};//main(String[] args)
        Binding binding = new Binding(args);
        GroovyShell shell = new GroovyShell(binding);
        shell.evaluate("static void main(String[] args){ if(args.length != 2) return;println('Hello,I am ' + args[0] + ',age ' + args[1])}");
        shell = null;
        binding = null;
    }
    /**
     * ??????????????????
     * @throws Exception
     */
    public static void evalScriptTextFull() throws Exception{
        StringBuffer buffer = new StringBuffer();
        //define API
        buffer.append("class User{")
                .append("String name;Integer age;")
                //.append("User(String name,Integer age){this.name = name;this.age = age};")
                .append("String sayHello(){return 'Hello,I am ' + name + ',age ' + age;}}\n");
        //Usage
        buffer.append("def user = new User(name:'zhangsan',age:1);")
                .append("user.sayHello();");
        //groovy.lang.Binding
        Binding binding = new Binding();
        GroovyShell shell = new GroovyShell(binding);
        String message = (String)shell.evaluate(buffer.toString());
        System.out.println(message);
        //??????main??????,????????????
        String mainMethod = "static void main(String[] args){def user = new User(name:'lisi',age:12);print(user.sayHello());}";
        shell.evaluate(mainMethod);
        shell = null;
    }
    /**
     * ?????????"??????"?????????????????????
     * @throws Exception
     */
    public static void evalScript1() throws Exception{
        Binding binding = new Binding();
        GroovyShell shell = new GroovyShell(binding);
        //??????????????????
        //shell.parse(new File(//))
        Script script = shell.parse("def join(String[] list) {return list.join('--');}");
        String joinString = (String)script.invokeMethod("join", new String[]{"A1","B2","C3"});
        System.out.println(joinString);
        //???????????????????????????,?????????main??????,????????????????????????
        //1) def call(){...};call();
        //2) call(){...};
        script = shell.parse("static void main(String[] args){i = i * 2;}");
        script.setProperty("i", new Integer(10));
        script.run();//??????,
        System.out.println(script.getProperty("i"));
        //the same as
        System.out.println(script.getBinding().getVariable("i"));
        script = null;
        shell = null;
    }
    /**
     * from source file of *.groovy
     */
    public static void parse() throws Exception{
        GroovyClassLoader classLoader = new GroovyClassLoader(Thread.currentThread().getContextClassLoader());
        File sourceFile = new File("D:\\TestGroovy.groovy");
        Class testGroovyClass = classLoader.parseClass(new GroovyCodeSource(sourceFile));
        GroovyObject instance = (GroovyObject)testGroovyClass.newInstance();//proxy
        Long time = (Long)instance.invokeMethod("getTime", new Date());
        System.out.println(time);
        Date date = (Date)instance.invokeMethod("getDate", time);
        System.out.println(date.getTime());
        //here
        instance = null;
        testGroovyClass = null;
    }
    public static void load() throws Exception {
        GroovyClassLoader classLoader = new GroovyClassLoader(Thread.currentThread().getContextClassLoader());
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream("D:\\TestGroovy.class"));
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        for(;;){
            int i = bis.read();
            if( i == -1){
                break;
            }
            bos.write(i);
        }
        Class testGroovyClass = classLoader.defineClass(null, bos.toByteArray());
        //instance of proxy-class
        //if interface API is in the classpath,you can do such as:
        //MyObject instance = (MyObject)testGroovyClass.newInstance()
        GroovyObject instance = (GroovyObject)testGroovyClass.newInstance();
        Long time = (Long)instance.invokeMethod("getTime", new Date());
        System.out.println(time);
        Date date = (Date)instance.invokeMethod("getDate", time);
        System.out.println(date.getTime());

        //here
        instance = null;
        testGroovyClass = null;
    }
    public static void evalScript() throws Exception{
        ScriptEngineManager factory = new ScriptEngineManager();
        //??????????????????engine??????
        ScriptEngine engine = factory.getEngineByName("groovy");
        System.out.println(engine.toString());
        assert engine != null;
        //javax.script.Bindings
        Bindings binding = engine.createBindings();
        binding.put("date", new Date());
        //??????script??????????????????,???????????????????????????
        engine.eval("def getTime(){return date.getTime();}",binding);
        engine.eval("def sayHello(name,age){return 'Hello,I am ' + name + ',age' + age;}");
        Long time = (Long)((Invocable)engine).invokeFunction("getTime", null);
        System.out.println(time);
        String message = (String)((Invocable)engine).invokeFunction("sayHello", "zhangsan",new Integer(12));
        System.out.println(message);
    }
    static void simpleTest() throws IOException, InstantiationException, IllegalAccessException, ResourceException, ScriptException {
        String[] roots = new String[] { "src/main/groovy/com/mobile263/billing/groovy/" };
        //???????????????roots????????????GroovyScriptEngine
        GroovyScriptEngine gse = new GroovyScriptEngine(roots);
        GroovyObject groovyObject = (GroovyObject) gse.loadScriptByName("TestScript.groovy").newInstance();
        String result = (String) groovyObject.invokeMethod("output", "hello");
        System.out.println(result);
    }
}
