package xbear.javaopenrasp.filters.jndi;

import com.sun.jndi.ldap.LdapURL;
import xbear.javaopenrasp.config.Config;
import xbear.javaopenrasp.filters.SecurityFilterI;
import xbear.javaopenrasp.util.Console;
import xbear.javaopenrasp.util.StackTrace;

import javax.naming.NamingException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.Set;

/**
 * @author guo.chen
 * @date: 2022-03-08
 */
public class Log4JFilter implements SecurityFilterI {

    private static final Set<String> allowedHosts = new HashSet<String>() {{
        add("localhost");
        add("127.0.0.1");
        add("0:0:0:0:0:0:0:1");
        try {
            add(InetAddress.getLocalHost().getHostName());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }};

    @Override
    public boolean filter(Object forCheck) {
        String moduleName = "org/apache/logging/log4j/core/net/JndiManager";
        String logContent = (String) forCheck;
        Console.log("prepare [jndi invoke] filter:" + logContent);
        String mode = (String) Config.moudleMap.get(moduleName).get("mode");
        switch (mode) {
            case "check":
                Console.log("check: " + logContent);
                // jndi:ldap://172.16.163.138:9999/Hacker
                try {
                    LdapURL ldapURL = new LdapURL(logContent);
                    Console.log("check: 1" );
                    if (!allowedHosts.contains(ldapURL.getHost())) {
                        Console.log("check: 2" );
                        Console.log("Warning: JNDI resource is not allowed..");
                        return false;
                    }
                } catch (NamingException e) {
                    Console.log("check: 3" );
                    e.printStackTrace();
                }

                return true;
            case "white":
                if (Config.isWhite(moduleName, logContent)) {
                    Console.log("parse log4J:" + logContent);
                    return true;
                }
                Console.log("block" + logContent);
                return false;
            case "black":
                if (Config.isBlack(moduleName, logContent)) {
                    Console.log("block parse log4J" + logContent);
                    return false;
                }
                Console.log("exec command:" + logContent);
                return true;
            case "log":
            default:
                Console.log("parse log4J" + logContent);
                Console.log("log stack trace:\r\n" + StackTrace.getStackTrace());
                return true;
        }

    }
}
