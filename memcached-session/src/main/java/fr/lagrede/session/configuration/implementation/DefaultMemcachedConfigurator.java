package fr.lagrede.session.configuration.implementation;

import java.util.List;

import net.spy.memcached.ConnectionFactoryBuilder.Protocol;
import fr.lagrede.session.configuration.MemcachedConfigurator;

/**
 * Impl�mentation par d�faut du {@link MemcachedConfigurator}<br/>
 * Les propri�t�s sont charg�es depuis <b>{@link System#getProperty(String)}</b> sinon on utilise la d�finition par d�faut interne
 *  
 * @author anthony.lagrede
 *
 */
public class DefaultMemcachedConfigurator extends AbstractMemcachedConfigurator implements MemcachedConfigurator {

    private static final int DEFAULT_MAX_TIMEOUTEXCEPTION_THRESHOLD = 998;
    private static final int DEFAULT_MAX_RECONNECT_DELAY = 30;
    private static final int DEFAULT_OPERATION_TIMEOUT = 2500;
    private static final int DATA_LIFE_TIME = 3600; // en seconds
    private static final int NBR_CLIENTS_IN_POOL = 1;
    private static final String SERVER_ADDRESS = "localhost:11211";
    private static final int DEFAULT_COMPRESSION_THRESHOLD = 16384;

    public DefaultMemcachedConfigurator(){}
    
    /**
     * {@inheritDoc}<br/>
     * <i>memcached.is_actived</i><br/>
     * default value: <b>true</b>
     */
    @Override
    public boolean isMemcachedActivated() {
        return getboolean("memcached.is_actived", true);
    }

    /**
     * {@inheritDoc}<br/>
     * <i>memcached.excluded_session_attributes</i><br/>
     * default separator <b>;</b>
     * default value: <b>""</b>
     */
    @Override
    public List<String> getMemcachedExcludedSessionAttributes() {
        return getList("memcached.excluded_session_attributes", ";");
    }

    /**
     * {@inheritDoc}<br/>
     * <i>memcached.data_life_time</i><br/>
     * default value: <b>{@value #DATA_LIFE_TIME}</b>
     */
    @Override
    public int getMemcachedDataLifeTime() {
        return getInteger("memcached.data_life_time", DATA_LIFE_TIME);
    }

    /**
     * {@inheritDoc}<br/>
     * <i>memcached.clients_in_pool</i><br/>
     * default value: {@value #NBR_CLIENTS_IN_POOL}
     */
    @Override
    public int getNbrMemcachedClientsInPool() {
        return getInteger("memcached.clients_in_pool", NBR_CLIENTS_IN_POOL);
    }

    /**
     * {@inheritDoc}<br/>
     * <i>memcached.operation.timeout</i><br/>
     * default value: {@value #DEFAULT_OPERATION_TIMEOUT}
     */
    @Override
    public long getMemcachedOperationTimeout() {
        return getLong("memcached.operation.timeout", DEFAULT_OPERATION_TIMEOUT);
    }

    /**
     * {@inheritDoc}<br/>
     * <i>memcached.reconnect.delay</i><br/>
     * default value: {@value #DEFAULT_MAX_RECONNECT_DELAY}
     */
    @Override
    public long getMemcachedReconnectDelay() {
        return getLong("memcached.reconnect.delay", DEFAULT_MAX_RECONNECT_DELAY);
    }

    /**
     * {@inheritDoc}<br/>
     * <i>memcached.threshold.timeout</i><br/>
     * default value: {@value #DEFAULT_MAX_TIMEOUTEXCEPTION_THRESHOLD}
     */
    @Override
    public int getMemcachedThresholdTimeout() {
        return getInteger("memcached.threshold.timeout", DEFAULT_MAX_TIMEOUTEXCEPTION_THRESHOLD);
    }

    /**
     * {@inheritDoc}<br/>
     * <i>memcached.servers.list</i><br/>
     * default value: {@value #SERVER_ADDRESS}
     */
    @Override
    public String getMemcachedServersList() {
        return getProperty("memcached.servers.list", SERVER_ADDRESS);
    }

    /**
     * {@inheritDoc}<br/>
     * <i>memcached.protocol</i><br/>
     * default value: TEXT
     */
    @Override
    public Protocol getMemcachedProtocolMode() {
        return getEnum(Protocol.class, "memcached.protocol", Protocol.TEXT);
    }

    /**
     * {@inheritDoc}<br/>
     * <i>memcached.compressionThreshold</i>
     * default value: {@value #DEFAULT_COMPRESSION_THRESHOLD}
     */
    @Override
    public int getCompressionThreshold() {
        return getInteger("memcached.compressionThreshold", DEFAULT_COMPRESSION_THRESHOLD);
    }

}
