package ru.mail.park.jdbc.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mail.park.jdbc.IBaseService;
import ru.mail.park.responses.DBResponse;
import ru.mail.park.responses.Status;

import javax.sql.DataSource;
import java.sql.*;

/**
 * Created by Andry on 06.11.16.
 */
@Service
@Transactional
public class BaseServiceImpl implements IBaseService {

    public static final int ALREADY_EXIST = 1062;
    protected String tableName = "";
    private final DataSource dataSource;

    public BaseServiceImpl(DataSource ds) {
        this.dataSource = ds;
    }

    @Override
    public void truncateTable() {
        try (Connection connection = dataSource.getConnection()) {

            try (Statement ps = connection.createStatement()) {

                ps.execute("SET FOREIGN_KEY_CHECKS = 0;");
                ps.execute("TRUNCATE TABLE " + tableName);

                if (tableName == "User") {
                    ps.execute("TRUNCATE TABLE Followers");
                }
                if (tableName == "Thread") {
                    ps.execute("TRUNCATE TABLE Subscriptions");
                }
                ps.execute("SET FOREIGN_KEY_CHECKS = 1;");
            }


        } catch (Exception e) {
            new DBResponse(Status.UNKNOWN_ERROR);
        }
    }

    @Override
    public long getAmount() {
        long count;
        try (Connection connection = dataSource.getConnection()) {
            String query = new StringBuilder("SELECT COUNT(*) AS Number FROM ")
                    .append(tableName).toString();
            try (PreparedStatement ps = connection.prepareStatement(query)) {
                try (ResultSet resultSet = ps.executeQuery()) {
                    resultSet.next();
                    count = resultSet.getLong("Number");
                }
            } catch (SQLException e) {
                return -1;
            }
        } catch (Exception e) {
            return -1;
        }

        return count;
    }

    protected DBResponse handeSQLException(SQLException e) {
        if (e.getErrorCode() == ALREADY_EXIST) {
            return new DBResponse(Status.ALREADY_EXIST);
        } else {
            return new DBResponse(Status.UNKNOWN_ERROR);
        }
    }
}
