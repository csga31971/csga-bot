package com.moebuff.discord;

import com.moebuff.discord.utils.UnhandledException;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.FileBasedConfiguration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.ex.ConfigurationException;

/**
 * 配置信息
 *
 * @author muto
 */
public class Settings {
    public static final String ID;
    public static final String SECRET;
    public static final String USERNAME;
    public static final String TOKEN;

    static {
        Parameters params = new Parameters();
        FileBasedConfigurationBuilder<FileBasedConfiguration> builder =
                new FileBasedConfigurationBuilder<FileBasedConfiguration>(PropertiesConfiguration.class)
                        .configure(
                                params.properties().setFileName("moe.properties")
                        );
        try {
            Configuration config = builder.getConfiguration();
            ID = config.getString("client.id");
            SECRET = config.getString("client.secret");
            USERNAME = config.getString("bot.username");
            TOKEN = config.getString("bot.token");
        } catch (ConfigurationException e) {
            throw new UnhandledException(e);
        }
    }
}
