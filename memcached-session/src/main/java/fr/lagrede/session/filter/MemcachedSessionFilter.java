package fr.lagrede.session.filter;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;
import org.springframework.web.filter.GenericFilterBean;

import fr.lagrede.session.client.MemcachedClientController;
import fr.lagrede.session.exception.CommunicationException;
import fr.lagrede.session.listener.MemcachedSessionAttributeListener;
import fr.lagrede.session.listener.MemcachedSessionBindingListener;

/**
 * Filtre de sauvegarde/lecture de la session dans un memcached<br/>
 * Si la session n'existe pas, le filtre commence par essayer de trouver l'identifiant de session dans memcached contenu dans le cookie {@value #MEMCACHED_ID}<br/>
 * Si la cl� ne correspond � aucune donn�e ou si l'ip du client est diff�rente de celle contenue dans la session sauvegard�e, on g�n�re un nouvel identifiant.<br/>
 * La session sera sauvegard� � chaque fois que l'attribut de session {@link MemcachedSessionAttributeListener#DIRTY_SESSION} sera modifi�
 * 
 * @author anthony.lagrede
 *
 */
public class MemcachedSessionFilter extends GenericFilterBean implements InitializingBean {

    private static final String HEAD = "HEAD";

    private static final String REMOTE_IP = "remote_ip";

    final Logger logger = LoggerFactory.getLogger(MemcachedSessionFilter.class);

    private static final String MEMCACHED_LISTENER = "memcachedListener";
    
    public static final String MEMCACHED_ID = "MEM_ID";
    
    
    private MemcachedClientController pmMemcached = null;
    
    /**
     * Impl�mentation du client memcached
     * @param clazz classe h�ritant de {@link MemcachedClientController}
     */
    public void setCachedServer(MemcachedClientController cachedServer) {
        this.pmMemcached = cachedServer;
    }


    @Override
    public void afterPropertiesSet() throws ServletException {
        super.afterPropertiesSet();
        Assert.notNull(pmMemcached, "Missing CachedServer implementation");
    }


    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        createMemcachedClient();
        
        if ( request.getSession(false) == null && ! HEAD.equals(request.getMethod()) ) {
            loadSessionFromMemcached(request, response);
        }
        
        chain.doFilter(req, res); // ex�cution de la request
        
        backupSessionToMemcached(request);

    }

    
    /**
     * Cr�e le client memcached si la propri�t� d'activation est pr�sente
     * @throws IOException
     */
    private void createMemcachedClient() throws IOException {
        
        if (pmMemcached.isMemcachedActivated() && ! pmMemcached.isRunning()) {
            pmMemcached.init();
        
        } else if (Boolean.FALSE.equals(pmMemcached.isMemcachedActivated()) && pmMemcached.isRunning()){
            pmMemcached.shutdown();
        }
        
    }


    /**
     * Charge la session depuis un serveur externe de Cache
     * @param request
     */
    private void loadSessionFromMemcached(HttpServletRequest request, HttpServletResponse response) {
        
        String memcachedId = extractMemcachedId(request);
        
        if (memcachedId == null) {
            initializeClientForClusteredSession(request, response);
            return;
        } 
        
        try {
            
            // r�cup�ration de la session depuis memcached
            @SuppressWarnings("unchecked")
            Map<String, Object> params = (Map<String, Object>) pmMemcached.get(memcachedId);
            
            if (params == null || params.isEmpty() || isHijackingSession(request, params)) {
                resetMemcachedCookie(request);
                loadSessionFromMemcached(request, response);
            }

            loadParamsInSession(request, params);

        } catch (CommunicationException e) {
            logger.error("Any response from memcached servers: {}", e.getMessage());
        }
        
    }


    /**
     * Permet de parer aux tentatives d'<i>hijacking</i> de session par <i>brute force</i> en emp�chant l'�ventuel
     * voleur de r�cup�rer une session 
     * @param request
     * @param params
     * @return true si l'IP enregistr�e n'est pas la m�me que celle de la request
     */
    private boolean isHijackingSession(HttpServletRequest request, Map<String, Object> params) {
        return (! ("" + request.getRemoteAddr()).equals("" + params.get(REMOTE_IP)) );
    }


    /**
     * Charge la map de param�tres dans la session
     * @param request
     * @param params liste � charger en session
     */
    private void loadParamsInSession(HttpServletRequest request, Map<String, Object> params) {
        if (params != null) {
            for (Entry<String, Object> entry : params.entrySet()) {
                logger.debug("Read from memcached: " + entry.getKey() + ":" + entry.getValue());
                request.getSession().setAttribute(entry.getKey(), entry.getValue());
            }
            
            logger.info("Session: " + request.getRequestedSessionId() + " restored from memcached");
        }
    }


    /**
     * Initialise la nouvelle connexion pour g�rer la session en cluster
     * @param request
     * @param response
     */
    private void initializeClientForClusteredSession(HttpServletRequest request, HttpServletResponse response) {
        if (logger.isDebugEnabled()) {
            logger.debug("New Memcached Identifier created");
        }
        Cookie memcachedCookie = createNewMemcachedCookie();
        response.addCookie(memcachedCookie);
        request.getSession().setAttribute(MEMCACHED_LISTENER, new MemcachedSessionBindingListener(memcachedCookie.getValue()));
    }
    
    /**
     * Sauvegarde la session contenue dans la request dans un serveur de cache externe 
     * @param request
     */
    private void backupSessionToMemcached(HttpServletRequest request) {
        
        if (Boolean.TRUE.equals(request.getSession().getAttribute(MemcachedSessionAttributeListener.DIRTY_SESSION))) {
            
            Map<String, Object> backupMap = extractSessionParams(request);

            
            String memcachedId = extractMemcachedId(request);
            
            if (memcachedId != null) {
            
                pmMemcached.set(memcachedId, backupMap);
            
            } else if (logger.isDebugEnabled()){
                logger.debug("No memcachedId found");
            }

            if (request.getSession(false) != null) {
                request.getSession().removeAttribute(MemcachedSessionAttributeListener.DIRTY_SESSION);
            }

            //backupInFile(backupMap, memcachedId);
        }
        
    }

//    private SerializingTranscoder getTranscoder() {
//        SerializingTranscoder tr = new SerializingTranscoder();
//        tr.setCompressionThreshold(1024);
//        return tr;
//    }
//    
//    private void backupInFile(Map<String, Object> backupMap, String memcachedId) {
//        try {
//
//            byte[] obj = SerializationUtils.serialize(backupMap);
//            new FileOutputStream(new File("/ramfs/" + memcachedId)).write(obj);
//
//            CachedData cachedData = getTranscoder().encode(backupMap);
//            new FileOutputStream(new File("/ramfs/" + memcachedId + "_compress")).write(cachedData.getData());
//            
//        
//        } catch (IOException e) {
//            logger.error("error:", e);
//        }
//    }


    /**
     * Extrait les param�tres de la session
     * @param request
     * @return tous les param�tres de la session
     */
    private Map<String, Object> extractSessionParams(HttpServletRequest request) {
        Map<String, Object> backupMap = new HashMap<String, Object>();
        @SuppressWarnings("unchecked")
        Enumeration<String> enumeration = request.getSession().getAttributeNames();
        
        while(enumeration.hasMoreElements()) {
            String key = enumeration.nextElement();
            backupMap.put(key, request.getSession().getAttribute(key));
        }
        
        backupMap.put(REMOTE_IP, "" + request.getRemoteAddr()); // ajout de l'ip pour contr�le
        
        return backupMap;
    }
    
    
    /**
     * Cr�ation du cookie d'identification Memcached
     */
    private Cookie createNewMemcachedCookie() {
        Cookie sessionCookie = new Cookie(MEMCACHED_ID, generateRandomId());
        sessionCookie.setMaxAge(-1);
        sessionCookie.setPath("/");
        return sessionCookie;
    }


    /**
     * G�n�re un Id unique pour l'identifiant de session dans memcached
     * @return
     */
    private String generateRandomId() {
        return UUID.randomUUID().toString().replace("-", "");
    }


    /**
     * R�cup�re depuis le {@link Cookie} l'identifiant de la session dans memcached
     * @param request
     * @return identifiant de session dans <b>Memcached</b>
     */
    private String extractMemcachedId(HttpServletRequest request) {
        
        if (request.getCookies() == null)
            return null;
        
        for (Cookie cookie : request.getCookies()) {
            if (MEMCACHED_ID.equals(cookie.getName())) {
                if (cookie.getValue() != null && ! "".equals(cookie.getValue()))
                    return cookie.getValue();
            }
        }
        
        return null;
    }

    
    /**
     * Remise � 0 du cookie de sesion memcached
     * @param request
     */
    private void resetMemcachedCookie(HttpServletRequest request) {

        if (request.getCookies() == null)
            return;
        
        for (Cookie cookie : request.getCookies()) {
            if (MEMCACHED_ID.equals(cookie.getName())) {
                cookie.setValue("");
                cookie.setMaxAge(0);
            }
        }

    }
    
}
