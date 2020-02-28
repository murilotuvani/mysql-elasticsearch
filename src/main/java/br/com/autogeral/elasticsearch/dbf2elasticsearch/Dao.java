package br.com.autogeral.elasticsearch.dbf2elasticsearch;

import java.util.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * 27/02/2020 23:49:32
 *
 * @author murilotuvani
 */
public class Dao {

    protected Integer getIntOrNull(ResultSet rs, String string) throws SQLException {
        int value = rs.getInt(string);
        if (rs.wasNull()) {
            return null;
        }
        return value;
    }

    protected Long getLongOrNull(ResultSet rs, String string) throws SQLException {
        long value = rs.getLong(string);
        if (rs.wasNull()) {
            return null;
        }
        return value;
    }

    protected Float getFloatOrNull(ResultSet rs, String string) throws SQLException {
        Float value = rs.getFloat(string);
        if (rs.wasNull()) {
            return null;
        }
        return value;
    }

    protected Double getDoubleOrNull(ResultSet rs, String string) throws SQLException {
        Double value = rs.getDouble(string);
        if (rs.wasNull()) {
            return null;
        }
        return value;
    }

    protected String getStringOrNull(ResultSet rs, String string) throws SQLException {
        String value = rs.getString(string);
        if (rs.wasNull()) {
            return null;
        }
        return value;
    }

    protected Date getDateOrNull(ResultSet rs, String string) throws SQLException {
        boolean isZeroDate = verifyZeroDate(rs, string);
        if (isZeroDate) {
            return null;
        }
        Date value = rs.getDate(string);
        if (rs.wasNull()) {
            return null;
        }
        return value;
    }

    protected Date getTimestampOrNull(ResultSet rs, String string) throws SQLException {
        boolean isZeroDate = verifyZeroDate(rs, string);
        if (isZeroDate) {
            return null;
        }
        Date value = rs.getTimestamp(string);
        if (rs.wasNull()) {
            return null;
        }
        return value;
    }

    protected LocalDateTime getLocalDateTimeOrNull(ResultSet rs, String string) throws SQLException {
        boolean isZeroDate = verifyZeroDate(rs, string);
        if (isZeroDate) {
            return null;
        }
        if (rs.wasNull()) {
            return null;
        }
        LocalDateTime value = rs.getTimestamp(string).toLocalDateTime();
        return value;
    }

    protected java.time.LocalDate getLocalDateOrNull(ResultSet rs, String string) throws SQLException {
        boolean isZeroDate = verifyZeroDate(rs, string);
        if (isZeroDate) {
            return null;
        }
        if (rs.wasNull()) {
            return null;
        }
        java.time.LocalDate value = rs.getDate(string).toLocalDate();
        return value;
    }

    protected LocalTime getLocalTimeOrNull(ResultSet rs, String string) throws SQLException {
        boolean isZeroDate = verifyZeroDate(rs, string);
        if (isZeroDate) {
            return null;
        }
        if (rs.wasNull()) {
            return null;
        }
        LocalTime value = rs.getTime(string).toLocalTime();
        return value;
    }

    private boolean verifyZeroDate(ResultSet rs, String string) throws SQLException {
        String verify = rs.getString(string);
        if (verify != null && verify.contains("0000-00-00")) {
            return true;
        }
        return false;
    }

    protected Time getTimeOrNull(ResultSet rs, String string) throws SQLException {
        Time value = rs.getTime(string);
        if (rs.wasNull()) {
            return null;
        }
        return value;
    }

    protected Boolean getBooleanOrNull(ResultSet rs, String string) throws SQLException {
        Boolean value = rs.getBoolean(string);
        if (rs.wasNull()) {
            value = null;
        }
        return value;
    }
}
