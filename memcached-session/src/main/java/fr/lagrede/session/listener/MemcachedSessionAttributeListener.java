package fr.lagrede.session.listener;

import java.util.List;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionBindingEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ClassUtils;

import fr.lagrede.session.beanutils.DirtyBean;
import fr.lagrede.session.configuration.MemcachedConfigurator;
import fr.lagrede.session.configuration.implementation.DefaultMemcachedConfigurator;

/**
 * Listener de session permettant de d�tecter tous les changements d'attributs de la session
 * 
 * @author anthony.lagrede
 *
 */
public class MemcachedSessionAttributeListener implements HttpSessionAttributeListener, ServletContextListener {

    
    final Logger logger = LoggerFactory.getLogger(MemcachedSessionAttributeListener.class);
    
    public static final String DIRTY_SESSION = "dirtySession";
    
    private MemcachedConfigurator configurator = new DefaultMemcachedConfigurator();
    

    //--------------------------------------------------------
    // ServletContextListener
    //--------------------------------------------------------

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        // Nothing here
    }
    
    @Override
    public void contextInitialized(ServletContextEvent event) {
        String configuratorClassName = event.getServletContext().getInitParameter("memcachedConfiguratorClass");
        
        if(ClassUtils.isPresent(configuratorClassName, getClass().getClassLoader())) {
            try {
                
                Class<?> clazz = ClassUtils.forName(configuratorClassName, getClass().getClassLoader());
                
                if(!ClassUtils.isAssignable(MemcachedConfigurator.class, clazz)) {
                    logger.error("Class {} is not assignable to {}. Use default implementation {}", clazz.getCanonicalName(), MemcachedConfigurator.class.getCanonicalName());
                    return;
                }
                
                configurator = (MemcachedConfigurator) ClassUtils.forName(configuratorClassName, getClass().getClassLoader()).newInstance();
                
                
            } catch (ClassNotFoundException e) {
                logger.error("error", e);
            } catch (LinkageError e) {
                logger.error("error", e);
            } catch (InstantiationException e) {
                logger.error("error", e);
            } catch (IllegalAccessException e) {
                logger.error("error", e);
            }
                    
        }
        
    }
    
    
    //--------------------------------------------------------
    // HttpSessionAttributeListener
    //--------------------------------------------------------
    

    @Override
    public void attributeAdded(HttpSessionBindingEvent event) {
        
        if (!"dirtySession".equals(event.getName()) && ! isExcludedSessionAttribute(event)) {
            sessionIsDirty(event, "attribute added in session");
        }
        
    }

    @Override
    public void attributeRemoved(HttpSessionBindingEvent event) {
        
        if (!"dirtySession".equals(event.getName()) && ! isExcludedSessionAttribute(event)) {
            sessionIsDirty(event, "attribute removed in session");
        }
        
    }

    /**
     * Le cas remplacement est plus complexe car des �v�nements de modifications d'attributs sont lev�s sans que l'attribut n'est �t� modifi�.<br/>
     * <i>Ex: injection d'un bean session spring dans un singleton, m�me effectuant que de la lecture</i>
     */
    @Override
    public void attributeReplaced(HttpSessionBindingEvent event) {
        if (!"dirtySession".equals(event.getName()) && ! isExcludedSessionAttribute(event)) {

            if (DirtyBean.class.isAssignableFrom(event.getValue().getClass())) {
                if (!((DirtyBean)event.getValue()).isDirty()) {
                    return; // le bean n'a pas �t� modifi�
                }
                ((DirtyBean)event.getValue()).clean();
            }

            sessionIsDirty(event, "attribute replaced in session");
        }
        
    }

    /**
     * Retourne vrai si l'attribut de session contenu dans l'�v�nement appartient � la liste des donn�es � ne pas surveiller
     * 
     * @param event
     * @return
     */
    private boolean isExcludedSessionAttribute(HttpSessionBindingEvent event) {
        
        List<String> excludedAttributes = configurator.getMemcachedExcludedSessionAttributes();
        for (String attribute : excludedAttributes) {
            if (attribute != null && attribute.equals(event.getName())) {
                return true;
            }
        }
        
        return false;
    }

    /**
     * Modifie l'attribut de session {@link #DIRTY_SESSION} � true.<br/>
     * G�re le cas de la session expir�e
     * @param event
     * @param message
     */
    private void sessionIsDirty(HttpSessionBindingEvent event, String message) {
        
        try {
            event.getSession().setAttribute(DIRTY_SESSION, true);
        } catch (IllegalStateException ex) {
            // la session est probablement expir�e
            return;
        }

        if (logger.isDebugEnabled()) {
            logger.debug(message + ": " + event.getName() + ":" + event.getValue() + "::" + event.getSource());
        }
    }
    
}
