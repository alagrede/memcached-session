package fr.lagrede.session.configuration;

import java.util.List;

import net.spy.memcached.ConnectionFactoryBuilder.Protocol;


public interface MemcachedConfigurator {

    /**
     * Activation de memcached
     * <b>Il faut d�sactiver puis relancer les clients memcached pour que toutes les propri�t�s soient prises en compte</b>
     */
    boolean isMemcachedActivated();
    
    /**
     * Liste des propri�t�s � ne pas surveiller<br/>
     * <b>modifiable � chaud</b>
     */
    List<String> getMemcachedExcludedSessionAttributes();

    /**
     * Dur�e de vie des donn�es dans le memcached<br/>
     * <b>modifiable � chaud</b>
     */
    int getMemcachedDataLifeTime();
    
    /**
     * Nombre de clients memcached dans le pool de connexion<br/>
     * <b>modifiable quand memcached est d�sactiv�</b> 
     */
    int getNbrMemcachedClientsInPool();
    
    /**
     * Timeout d'une op�ration<br/>
     * <b>modifiable quand memcached est d�sactiv�</b> 
     */
    long getMemcachedOperationTimeout();

    /**
     * Temps entre chaque tentative de reconnexion<br/>
     * <b>modifiable quand memcached est d�sactiv�</b>
     */
    long getMemcachedReconnectDelay();

    /**
     * Nombre de timeout de connexion avant la fermeture d�finitive du serveur<br/>
     * <b>modifiable quand memcached est d�sactiv�</b>  
     */
    int getMemcachedThresholdTimeout();

    /**
     * Liste des serveurs memcached <i>ex: "localhost:11211 localhost:11212"</i><br/>
     * <b>modifiable quand memcached est d�sactiv�</b>
     */
    String getMemcachedServersList();

    /**
     * Protocol de communication avec le serveur Memcached (TEXT ou BINARY)<br/>
     * <b>modifiable quand memcached est d�sactiv�</b>
     */
    Protocol getMemcachedProtocolMode();

    /**
     * size en bytes � partir de laquelle il faut compresser les donn�es
     * <b>modifiable quand memcached est d�sactiv�</b> 
     */
    int getCompressionThreshold();
    
}
