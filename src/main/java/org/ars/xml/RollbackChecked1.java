package org.ars.xml;

import javax.sql.DataSource;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * @author arsen.ibragimov
 * Roll back declarative transaction for checked exception
 */
public class RollbackChecked1 {
    static Logger log = LogManager.getLogger( RollbackChecked1.class);

    static {
        Configurator.setRootLevel( Level.DEBUG);
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
        void create( Account account);
    }

    static class AccountDAOImpl implements AccountDAO {

        DataSource dataSource;

        public void setDataSource( DataSource dataSource) {
            this.dataSource = dataSource;
        }

        @Override
        public void create( Account account) {
            String insert = "insert into account(account_number) values(?)";
            JdbcTemplate jdbcTemplate = new JdbcTemplate( dataSource);
            jdbcTemplate.update( insert, new Object[] { account.getAccountNumber() });
        }
    }

    /**********************************************/
    public static interface AccountManager {
        void createAccount( Account account) throws CheckedException;
    }

    public static class AccountManagerImpl implements AccountManager {

        AccountDAO accountDAO;

        public void setAccountDAO( AccountDAO accountDAO) {
            this.accountDAO = accountDAO;
        }

        @Override
        public void createAccount( Account account) throws CheckedException {
            if( true) {
                throw new CheckedException( "test");
            }
            accountDAO.create( account);
        }
    }
    /**********************************************/
    public static class CheckedException extends Exception {

        private static final long serialVersionUID = 3780114280358252271L;

        public CheckedException( String message) {
            super( message);
        }
    }
    /**********************************************/
    public static void main( String[] args) {

        ClassPathXmlApplicationContext ctx = null;
        try {
            log.info( "main:start");

            ctx = new ClassPathXmlApplicationContext( "RollbackChecked1.xml");
            AccountManager accountManager = ctx.getBean( "accountManager", AccountManager.class);
            Account account = new Account();
            account.setAccountNumber( "987654");
            accountManager.createAccount( account);
        } catch( Exception e) {
            log.error( e.getMessage(), e);
        } finally {
            ctx.close();
            log.info( "main:finish");
        }
    }
}
