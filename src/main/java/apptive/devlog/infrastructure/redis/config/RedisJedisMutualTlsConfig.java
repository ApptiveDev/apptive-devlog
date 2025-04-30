package apptive.devlog.infrastructure.redis.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import javax.net.ssl.*;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyStore;

@Configuration
@Profile("prod")
public class RedisJedisMutualTlsConfig {

    @Value("${spring.data.redis.host}")
    private String redisHost;

    @Value("${spring.data.redis.port}")
    private int redisPort;

    @Value("${spring.data.redis.password}")
    private String redisPassword;

    @Value("${spring.redis.ssl.keystore-path}")
    private String keyStorePath;
    @Value("${spring.redis.ssl.keystore-password}")
    private String keyStorePassword;
    @Value("${spring.redis.ssl.keystore-type}")
    private String keyStoreType;

    @Value("${spring.redis.ssl.truststore-path}")
    private String trustStorePath;
    @Value("${spring.redis.ssl.truststore-password}")
    private String trustStorePassword;
    @Value("${spring.redis.ssl.truststore-type}")
    private String trustStoreType;

    @Bean
    public JedisConnectionFactory jedisConnectionFactory() throws Exception {

        // --- SSLContext 생성 (keystore + truststore 로 mutual TLS) ---
        char[] keyPass = keyStorePassword.toCharArray();
        KeyStore ks = KeyStore.getInstance(keyStoreType);
        try (InputStream ksStream = new FileInputStream(keyStorePath)) {
            ks.load(ksStream, keyPass);
        }
        KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        kmf.init(ks, keyPass);

        char[] trustPass = trustStorePassword.toCharArray();
        KeyStore ts = KeyStore.getInstance(trustStoreType);
        try (InputStream tsStream = new FileInputStream(trustStorePath)) {
            ts.load(tsStream, trustPass);
        }
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(ts);

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);

        // --- JedisClientConfiguration 설정 ---
        JedisClientConfiguration jedisClientConfig = JedisClientConfiguration.builder()
                .useSsl()
                .sslSocketFactory(sslContext.getSocketFactory())
                .build();

        // --- RedisStandaloneConfiguration 설정 ---
        RedisStandaloneConfiguration serverConfig = new RedisStandaloneConfiguration(redisHost, redisPort);
        serverConfig.setPassword(RedisPassword.of(redisPassword));

        return new JedisConnectionFactory(serverConfig, jedisClientConfig);
    }

    @Bean
    public RedisTemplate<String, String> redisTemplate(JedisConnectionFactory cf) {
        RedisTemplate<String, String> template = new RedisTemplate<>();
        template.setConnectionFactory(cf);
        StringRedisSerializer s = new StringRedisSerializer();
        template.setKeySerializer(s);
        template.setValueSerializer(s);
        template.setHashKeySerializer(s);
        template.setHashValueSerializer(s);
        template.afterPropertiesSet();
        return template;
    }
}
