package com.ly.groovydemo;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.lang.Script;

import java.util.ArrayList;
import java.util.List;

public class GStringTest {
    public static void main(String[] args) {
        Binding binding = new Binding();
        GroovyShell groovyShell = new GroovyShell(binding);
        String[] m = new String[3];
        m[0] = "aa";
        m[1] = "bb";
        m[2] = "cc";
        binding.setVariable("m", m);
        binding.setVariable("aa", "aasfasd");
        List<String> c=new ArrayList<>();
        c.add("qq");
        c.add("ww");
        binding.setVariable("c", c);
        Script script = groovyShell.parse("\"${m[0]}${m[1]}aa${aa}1a$c[0]\"");
        System.out.println(script.run());

    }
}
