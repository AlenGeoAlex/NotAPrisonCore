package me.alenalex.notaprisoncore.paper.constants;

public class DbConstants {

    public static final class TableNames {
        public static final String MINE_META = "mine_metas";
        public static final String MINES = "mines";
        public static final String USER_SOCIALS = "user_socials";
        public static final String USER_PROFILE = "user_profile";

    }

    public static final class UserProfile {
        public static final String GET_OR_CREATE_PROCEDURE = "{CALL GetOrCreateUserProfile(?)}";
        public static final String UPDATE_PROCEDURE = "";
    }
    public static final class MineMeta{
        public static final String INSERT_QUERY = "INSERT INTO `mine_metas` " +
                "(" +
                "spawn_point, " +
                "lower_mine_point, " +
                "upper_mine_point, " +
                "lower_mine_region, " +
                "upper_mine_region, " +
                "additional_position_map, " +
                "id" +
                ") " +
                "VALUES " +
                "(?, ?, ?, ?, ?, ?, ?);";

        public static final String UPDATE_QUERY = "UPDATE mine_metas " +
                "SET `spawn_point` = ?, " +
                "`lower_mine_point` = ?, " +
                "`upper_mine_point` = ?, " +
                "`lower_mine_region` = ?, " +
                "`upper_mine_region` = ?, " +
                "`additional_position_map` = ?" +
                "WHERE `id` = ?";
    }

}
