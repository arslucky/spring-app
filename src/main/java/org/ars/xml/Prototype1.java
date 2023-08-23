package org.ars.xml;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author arsen.ibragimov
 *
 */
public class Prototype1 {

    static Logger log = LogManager.getLogger( Prototype1.class);

    static class A {
        private B b;
        private B b1;

        public B getB() {
            return b;
        }

        public void setB( B b) {
            this.b = b;
        }

        public B getB1() {
            return b1;
        }

        public void setB1( B b1) {
            this.b1 = b1;
        }

    }

    static class B {
    }

    public static void main( String[] args) {
        // Resource resource = new ClassPathResource( "prototypeContext1.xml");
        // BeanFactory context = new XmlBeanFactory( resource);
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext( "prototype1.xml");

        A a = (A) context.getBean( "a");

        log.info( "---- same objects for each call ----");
        log.info( "a.getB(): {}", a.getB());
        log.info( "a.getB(): {}", a.getB());
        log.info( "---- same objects for each call but differ to b ----");
        log.info( "a.getB1(): {}", a.getB1());
        log.info( "a.getB1(): {}", a.getB1());
        log.info( "---- differ objects ----");
        log.info( "getBean(b): {}", context.getBean( "b"));
        log.info( "getBean(b): {}", context.getBean( "b"));

        context.close();
    }
}
