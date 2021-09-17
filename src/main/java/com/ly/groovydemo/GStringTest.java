package com.ly.groovydemo;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.lang.Script;

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
        Script script = groovyShell.parse("\"${m[0]}${m[1]}aa${aa}1a\"");
        System.out.println(script.run());

    }
}
