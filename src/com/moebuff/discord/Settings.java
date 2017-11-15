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

    public static final String BOT_TOKEN;
    public static final String URL_AGENT;
    public static final String TL_APIKEY;
    public static final String TL_SECRET;
    public static final String OSU_APIKEY;
    public static final String BOT_ID_STRING = "270475536533291008";

    static {
        Parameters params = new Parameters();
        FileBasedConfigurationBuilder<FileBasedConfiguration> builder =
                new FileBasedConfigurationBuilder<FileBasedConfiguration>(PropertiesConfiguration.class)
                        .configure(
                                params.properties().setFileName("moe.properties")
                        );
        try {
            Configuration config = builder.getConfiguration();
            BOT_TOKEN = config.getString("bot.token");
            URL_AGENT = config.getString("url.agent");
            TL_APIKEY = config.getString("tl.APIkey");
            TL_SECRET = config.getString("tl.secret");
            OSU_APIKEY = config.getString("osu.apikey");
        } catch (ConfigurationException e) {
            throw new UnhandledException(e);
        }
    }
}
