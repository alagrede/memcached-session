#################
# Requirements
#################

 - A properly configured Spring security environment
 - Spring installation >= 3.0

##################################################################################
# Installation
##################################################################################
 - First add memcached-session.jar in your application classpath


 - Add a new listener in web.xml

  <!-- Session Listener -->
  <listener>
    <listener-class>fr.lagrede.session.listener.MemcachedSessionAttributeListener</listener-class>
  </listener>


    ## Optionally you can define a context parameter for override default properties definition

    <!-- Default memcached configuration --> 
    <context-param>
      <param-name>memcachedConfiguratorClass</param-name> 
      <param-value>fr.lagrede.session.configuration.implementation.DefaultMemcachedConfigurator</param-value> 
     </context-param>



 - Import internal spring memcached-session configuration in your project

    <!-- Internal spring configuration -->
    <import resource="classpath*:memcached-session-shared.xml" />



 -  Add a spring security filter named: memcachedSessionFilter

          <!-- Spring Filters -->
          <security:filter-chain pattern="/**" filters="
               memcachedSessionFilter,
               securityContextPersistenceFilter,
               UsernamePasswordAuthenticationFilter,
               ...
               " />

         Or with security namespace:
          <custom-filter position="FIRST" ref="memcachedSessionFilter" />


####################################################################################
# Configuration
####################################################################################


# Memcached activation
# stop and run for refresh all properties
memcached.is_actived = true

# list of session fields to exclude
# [refreshable property]
memcached.excluded_session_attributes = headerAccountHolder

# Time to live of datas in memcached
# [refreshable property]
memcached.data_life_time = 3600


# Nbr of memcached client in pool
# [static property]
memcached.clients_in_pool = 1

# List of memcached servers (ex: localhost:11211 localhost:11212)
# [static property]
memcached.servers.list = localhost:11211

# Communication protocol (default TEXT)
# [static property]
memcached.protocol = BINARY

# Max nbr of connection retries before closing connection to server
# [static property]
memcached.threshold.timeout = 998

# Time between each memcached connection attempt
# [static property]
memcached.reconnect.delay = 30

# Max datas size in bytes before trigger compression
# [static property]
memcached.compressionThreshold = 16384



