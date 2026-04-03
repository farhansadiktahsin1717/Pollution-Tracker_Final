package com.example.pollutiontracker;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Locale;

public final class UserDAO {

    private static final String REGISTER_SQL = """
            INSERT INTO registrations (email, username, password_hash)
            VALUES (?, ?, ?)
            RETURNING id
            """;

    private static final String LOGIN_SQL = """
            SELECT id, username, email, password_hash
            FROM registrations
            WHERE LOWER(username) = LOWER(?)
               OR LOWER(email) = LOWER(?)
            """;

    private static final String PROFILE_SQL = """
            SELECT r.id,
                   r.username,
                   r.email,
                   ui.full_name,
                   ui.age,
                   ui.gender,
                   ui.phone_number,
                   ui.health_info,
                   ui.daily_outdoor_activity,
                   ui.division,
                   ui.district,
                   ui.city
            FROM registrations r
            LEFT JOIN user_info ui ON ui.registration_id = r.id
            WHERE r.id = ?
            """;

    private static final String UPSERT_USER_INFO_SQL = """
            INSERT INTO user_info (
                registration_id,
                full_name,
                age,
                gender,
                phone_number,
                health_info,
                daily_outdoor_activity,
                division,
                district,
                city
            )
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            ON CONFLICT (registration_id) DO UPDATE SET
                full_name = EXCLUDED.full_name,
                age = EXCLUDED.age,
                gender = EXCLUDED.gender,
                phone_number = EXCLUDED.phone_number,
                health_info = EXCLUDED.health_info,
                daily_outdoor_activity = EXCLUDED.daily_outdoor_activity,
                division = EXCLUDED.division,
                district = EXCLUDED.district,
                city = EXCLUDED.city,
                updated_at = CURRENT_TIMESTAMP
            """;

    private UserDAO() {
    }

    public static RegistrationResult register(String username, String password, String email) {
        String normalizedUsername = username == null ? "" : username.trim();
        String normalizedEmail = email == null ? "" : email.trim().toLowerCase(Locale.ROOT);

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(REGISTER_SQL)) {

            ps.setString(1, normalizedEmail);
            ps.setString(2, normalizedUsername);
            ps.setString(3, PasswordUtil.hashPassword(password));

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return RegistrationResult.success(rs.getInt("id"));
                }
            }
        } catch (SQLException e) {
            if ("23505".equals(e.getSQLState())) {
                return RegistrationResult.failure("That username or email is already registered.");
            }

            e.printStackTrace();
            return RegistrationResult.failure("Registration failed because the database is unavailable.");
        }

        return RegistrationResult.failure("Registration could not be completed.");
    }

    public static AuthenticationResult login(String usernameOrEmail, String password) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(LOGIN_SQL)) {

            String credential = usernameOrEmail == null ? "" : usernameOrEmail.trim();
            ps.setString(1, credential);
            ps.setString(2, credential);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return AuthenticationResult.failure();
                }

                if (!PasswordUtil.verifyPassword(password, rs.getString("password_hash"))) {
                    return AuthenticationResult.failure();
                }

                return AuthenticationResult.success(rs.getInt("id"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return AuthenticationResult.failure();
        }
    }

    public static UserProfile getUserProfile(int userId) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(PROFILE_SQL)) {

            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }

                Integer age = null;
                int ageValue = rs.getInt("age");
                if (!rs.wasNull()) {
                    age = ageValue;
                }

                return new UserProfile(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("email"),
                        rs.getString("full_name"),
                        age,
                        rs.getString("gender"),
                        rs.getString("phone_number"),
                        rs.getString("health_info"),
                        rs.getString("daily_outdoor_activity"),
                        rs.getString("division"),
                        rs.getString("district"),
                        rs.getString("city")
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean saveUserInfo(int userId, String fullName, int age, String gender, String phoneNumber,
                                       String healthInfo, String outdoor, String division,
                                       String district, String city) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(UPSERT_USER_INFO_SQL)) {

            ps.setInt(1, userId);
            ps.setString(2, fullName);
            ps.setInt(3, age);
            ps.setString(4, gender);
            setNullableText(ps, 5, phoneNumber);
            setNullableText(ps, 6, healthInfo);
            setNullableText(ps, 7, outdoor);
            ps.setString(8, division);
            ps.setString(9, district);
            ps.setString(10, city);

            ps.executeUpdate();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private static void setNullableText(PreparedStatement ps, int index, String value) throws SQLException {
        if (value == null || value.isBlank()) {
            ps.setNull(index, Types.VARCHAR);
            return;
        }

        ps.setString(index, value.trim());
    }

    public static final class RegistrationResult {
        private final int userId;
        private final String message;

        private RegistrationResult(int userId, String message) {
            this.userId = userId;
            this.message = message;
        }

        public static RegistrationResult success(int userId) {
            return new RegistrationResult(userId, null);
        }

        public static RegistrationResult failure(String message) {
            return new RegistrationResult(-1, message);
        }

        public boolean isSuccess() {
            return userId > 0;
        }

        public int getUserId() {
            return userId;
        }

        public String getMessage() {
            return message;
        }
    }

    public static final class AuthenticationResult {
        private final int userId;

        private AuthenticationResult(int userId) {
            this.userId = userId;
        }

        public static AuthenticationResult success(int userId) {
            return new AuthenticationResult(userId);
        }

        public static AuthenticationResult failure() {
            return new AuthenticationResult(-1);
        }

        public boolean isSuccess() {
            return userId > 0;
        }

        public int getUserId() {
            return userId;
        }
    }

    public static final class UserProfile {
        private final int userId;
        private final String username;
        private final String email;
        private final String fullName;
        private final Integer age;
        private final String gender;
        private final String phoneNumber;
        private final String healthInfo;
        private final String outdoorActivity;
        private final String division;
        private final String district;
        private final String city;

        private UserProfile(int userId, String username, String email, String fullName, Integer age,
                            String gender, String phoneNumber, String healthInfo, String outdoorActivity,
                            String division, String district, String city) {
            this.userId = userId;
            this.username = username;
            this.email = email;
            this.fullName = fullName;
            this.age = age;
            this.gender = gender;
            this.phoneNumber = phoneNumber;
            this.healthInfo = healthInfo;
            this.outdoorActivity = outdoorActivity;
            this.division = division;
            this.district = district;
            this.city = city;
        }

        public int getUserId() {
            return userId;
        }

        public String getUsername() {
            return username;
        }

        public String getEmail() {
            return email;
        }

        public String getFullName() {
            return fullName;
        }

        public Integer getAge() {
            return age;
        }

        public String getGender() {
            return gender;
        }

        public String getPhoneNumber() {
            return phoneNumber;
        }

        public String getHealthInfo() {
            return healthInfo;
        }

        public String getOutdoorActivity() {
            return outdoorActivity;
        }

        public String getDivision() {
            return division;
        }

        public String getDistrict() {
            return district;
        }

        public String getCity() {
            return city;
        }
    }
}
