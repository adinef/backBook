package net.fp.backBook.config;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.ServerAddress;
import de.flapdoodle.embed.mongo.config.IMongodConfig;
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.distribution.Version;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class MongoTestingConfig {

    @Bean
    public MongoClient mongoClient() {
        MongoClientOptions mongoClientOptions = MongoClientOptions.builder().connectTimeout(20000).build();
        return new MongoClient(new ServerAddress("localhost", 7070), mongoClientOptions);
    }

    @Bean
    public IMongodConfig mongodConfig() throws IOException {
        return new MongodConfigBuilder()
                .net(new Net(7070, false))
                .version(Version.V3_2_20)
                .build();
    }
}
