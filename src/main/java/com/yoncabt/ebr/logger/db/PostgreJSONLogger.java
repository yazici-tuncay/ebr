/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.yoncabt.ebr.logger.db;

import com.yoncabt.ebr.ReportOutputFormat;
import com.yoncabt.ebr.logger.ReportLogger;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import com.yoncabt.ebr.jdbcbridge.JDBCUtil;

/**
 *
 * @author myururdurmaz
 */
public class PostgreJSONLogger implements ReportLogger {

    private Connection connection;

    @Autowired
    private JDBCUtil jdbcutil;

    @PostConstruct
    private void init() {
        try {
            connection = jdbcutil.connect("logger");
        } catch (SQLException ex) {
            throw new Error(ex);
        }
    }

    @PreDestroy
    private void destroy() throws SQLException {
        connection.close();
    }
    @Override
    public void logReport(String uuid, Map<String, Object> reportParams, ReportOutputFormat outputFormat, InputStream reportData) throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public byte[] getReportData(String uuid) throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
