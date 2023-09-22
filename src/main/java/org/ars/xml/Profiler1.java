package org.ars.xml;

import javax.sql.DataSource;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.StopWatch;

/**
 * @author arsen.ibragimov
 * Profile/wrap transaction, print task execution result
 */
public class Profiler1 {
    static Logger log = LogManager.getLogger( Profiler1.class);

    static {
        Configurator.setRootLevel( Level.DEBUG);
        // Configurator.setLevel( "org.apache.kafka.clients.consumer", Level.WARN);
    }

    static class Account {
        long id;
        String accountNumber;

        public long getId() {
            return id;
        }

        public void setId( long id) {
            this.id = id;
        }

        public String getAccountNumber() {
            return accountNumber;
        }

        public void setAccountNumber( String accountNumber) {
            this.accountNumber = accountNumber;
        }
    }

    /**********************************************/
    static interface AccountDAO {
        int create( Account account);
    }

    static class AccountDAOImpl implements AccountDAO {

        DataSource dataSource;

        public void setDataSource( DataSource dataSource) {
            this.dataSource = dataSource;
        }

        @Override
        public int create( Account account) {
            String insert = "insert into account(account_number) values(?)";
            JdbcTemplate jdbcTemplate = new JdbcTemplate( dataSource);
            return jdbcTemplate.update( insert, new Object[] { account.getAccountNumber() });
        }
    }

    /**********************************************/
    public static interface AccountManager {
        int createAccount( Account account);
    }

    public static class AccountManagerImpl implements AccountManager {

        AccountDAO accountDAO;

        public void setAccountDAO( AccountDAO accountDAO) {
            this.accountDAO = accountDAO;
        }

        @Override
        public int createAccount( Account account) {
            return accountDAO.create( account);
        }
    }

    /**********************************************/
    static class Profiler implements Ordered {

        private int order;

        // allows us to control the ordering of advice
        @Override
        public int getOrder() {
            return this.order;
        }

        public void setOrder( int order) {
            this.order = order;
        }

        // this method is the around advice
        public Object profile( ProceedingJoinPoint call) throws Throwable {
            Object returnValue;
            StopWatch clock = new StopWatch( getClass().getName());
            try {
                clock.start( call.toShortString());
                returnValue = call.proceed();
            } finally {
                clock.stop();
                log.info( clock.prettyPrint());
            }
            return returnValue;
        }
    }

    public static void main( String[] args) {

        ClassPathXmlApplicationContext ctx = null;
        try {
            log.info( "main:start");

            ctx = new ClassPathXmlApplicationContext( "profiler1.xml");
            AccountManager accountManager = ctx.getBean( "accountManager", AccountManager.class);
            Account account = new Account();
            account.setAccountNumber( String.valueOf( (int) (10000 * Math.random())));
            accountManager.createAccount( account);
        } catch( Exception e) {
            log.error( e.getMessage(), e);
        } finally {
            ctx.close();
            log.info( "main:finish");
        }
    }
}
