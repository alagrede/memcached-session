package fr.lagrede.session.listener;

import java.io.Serializable;

import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.lagrede.session.beanutils.MemcachedContextAware;
import fr.lagrede.session.client.MemcachedClientController;


/**
 * Listener de session contenant l'identifiant de la session dans memcached
 * 
 * @author mustapha.ait-alla
 *
 */
@SuppressWarnings("serial")
public class MemcachedSessionBindingListener implements HttpSessionBindingListener, Serializable {

    final Logger logger = LoggerFactory.getLogger(MemcachedSessionBindingListener.class);

    private String memcachedSessionId;
    
    public MemcachedSessionBindingListener(String memcachedSessionId) {
        this.memcachedSessionId = memcachedSessionId;
    }

    public void valueBound(HttpSessionBindingEvent event) {
        // nothing here
    }

    /**
     * Lorsque la session du serveur d'application expire, les donn�es pr�sentes dans memcached seront �galement effac�es
     */
    public void valueUnbound(HttpSessionBindingEvent event) {
        
        if (memcachedSessionId != null) {
            if (MemcachedContextAware.getContext() != null) {
                MemcachedClientController memcachedClient = MemcachedContextAware.getContext().getBean(MemcachedClientController.class);
                memcachedClient.delete(memcachedSessionId);

                if (logger.isDebugEnabled()) {
                    logger.debug("Erase unBound Session: " + memcachedSessionId + " in memcached");
                }
            }
        }        

    }
 

}
