package com.moebuff.discord.dao;

import java.util.Map;

public interface User_osu_id_DAO {
    boolean setosuid(Map<String, String> map);
    String getosuid(String userid);
}
