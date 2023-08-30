package org.ars.annotation;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author arsen.ibragimov
 *
 * use @PostConstruct, @PreDestroy annotations
 * TODO: works in java version 8 but not in 17
 */
public class BeanLifeCycle1 {

    static Logger log = LogManager.getLogger( BeanLifeCycle1.class);

    static class A {

        @Value( "1")
        String value;

        @Autowired
        B b;

        B b1;

        static {
            log.info( "instantiation");
        }

        A() {
            log.info( "initialization");
        }

        String getValue() {
            log.info( "getValue");
            return value;
        }

        void setValue( String value) {
            log.info( "setValue");
            this.value = value;
        }

        public B getB1() {
            log.info( "getB1");
            return b1;
        }

        @Autowired
        public void setB1( B b1) {
            log.info( "setB1");
            this.b1 = b1;
        }

        @PostConstruct
        void init() {
            log.info( "init");
        }

        @PreDestroy
        void destroy() {
            log.info( "destroy");
        }
    }

    static class B {
        static {
            log.info( "instantiation");
        }

        B() {
            log.info( "initialization");
        }

        void init() {
            log.info( "init");
        }

        void destroy() {
            log.info( "destroy");
        }
    }

    @Configuration
    static class AppConfig {

        @Bean
        public A a() {
            return new A();
        }

        @Bean( initMethod = "init", destroyMethod = "destroy")
        public B b() {
            return new B();
        }
    }

    public static void main( String[] args) {

        ConfigurableApplicationContext context = null;
        try {
            log.info( "main:start");
            context = new AnnotationConfigApplicationContext( AppConfig.class);
        } catch( Exception e) {
            log.error( e.getMessage(), e);
        } finally {
            context.close();
            log.info( "main:finish");
        }
    }
}
