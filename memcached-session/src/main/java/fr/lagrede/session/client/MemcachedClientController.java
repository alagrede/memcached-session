package fr.lagrede.session.client;

import java.io.IOException;

import net.spy.memcached.transcoders.Transcoder;
import fr.lagrede.session.configuration.MemcachedConfigurator;
import fr.lagrede.session.exception.CommunicationException;

/**
 * M�thodes n�cessaires � l'utilisation d'un client memcached 
 * @author anthony.lagrede
 *
 */
public interface MemcachedClientController {

    //~ M�thodes du cycle de vie du client memcached ==============================================
    
    /**
     * Cr�ation du pool de connexion.
     */
    void init() throws IOException;

    /**
     * Destruction du pool et fermeture des connexions 
     */
    void shutdown();

    /**
     * @return true si le client est d�marr�
     */
    boolean isRunning();
    
    
    //~ M�thodes d'acc�s aux donn�es ==============================================================

    /**
     * Ordre <b>set</b> en asynchrone 
     */
    void set(String key, Object o);
    
    /**
     * Ordre de <b>get</b> en synchrone
     * @param key � r�cup�rer
     * @return l'objet correspondant � la cl� dans memcached
     * @throws CommunicationException si le client n'est pas accessible ou ne r�pond pas assez vite
     */
    Object get(String key) throws CommunicationException;
    
    /**
     * Ordre de suppression 
     * @param key cl� � supprimer
     */
    void delete(String key);
    
  
    //~ M�thodes de param�trage ===================================================================
    
    /**
     * @return true si la propri�t� d'activation memcached est active
     */
    boolean isMemcachedActivated();
    
    /**
     * Objet contenant toutes les configurations pour le client memcached
     * @param configurator
     */
    void setConfigurator(MemcachedConfigurator configurator);

    /**
     * Strat�gie de serialisation des objets java
     * @param transcoder
     */
    void setTranscoder(Transcoder<Object> transcoder);
}
