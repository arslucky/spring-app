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
 * Declarative transaction
 */
public class TransactionDeclarative1 {
    static Logger log = LogManager.getLogger( TransactionDeclarative1.class);

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
        void createAccount( Account account);
    }

    public static class AccountManagerImpl implements AccountManager {

        AccountDAO accountDAO;

        public void setAccountDAO( AccountDAO accountDAO) {
            this.accountDAO = accountDAO;
        }

        @Override
        public void createAccount( Account account) {
            accountDAO.create( account);
        }
    }

    public static void main( String[] args) {

        ClassPathXmlApplicationContext ctx = null;
        try {
            log.info( "main:start");

            ctx = new ClassPathXmlApplicationContext( "transactionDeclarative1.xml");
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
