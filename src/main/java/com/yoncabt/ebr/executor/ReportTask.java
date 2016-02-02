/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.yoncabt.ebr.executor;

import com.yoncabt.ebr.jdbcbridge.JDBCUtil;
import com.yoncabt.ebr.ReportOutputFormat;
import com.yoncabt.ebr.ReportRequest;
import com.yoncabt.ebr.ReportResponse;
import com.yoncabt.ebr.executor.jasper.YoncaJasperReports;
import com.yoncabt.ebr.jdbcbridge.YoncaConnection;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Collections;
import javax.mail.MessagingException;
import net.sf.jasperreports.engine.JRException;
import org.apache.commons.lang.StringUtils;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 *
 * @author myururdurmaz
 */
@Component
@Scope("prototype")
public class ReportTask implements Runnable {

    @Autowired
    private YoncaJasperReports jasperReports;

    @Autowired
    private YoncaMailSender mailSender;

    @Autowired
    private JDBCUtil jdbcutil;

    private ReportRequest request;

    private ReportResponse response;

    private Exception exception;

    private YoncaConnection connection;

    private long started;

    private long ended;

    @Override
    public void run() {
        started = System.currentTimeMillis();
        response = new ReportResponse();
        response.setUuid(request.getUuid());
        try {
            connection = jdbcutil.connect(request.getDatasourceName());
            try (InputStream is = jasperReports.exportTo(request.getReport(), request.getReportParams(), ReportOutputFormat.valueOf(request.getExtension()), connection, request.getLocale(), request.getUuid());) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                IOUtils.copy(is, baos);
                if (!request.isAsync()) { //sadece senkron ise buraya keydetsin
                    response.setOutput(baos.toByteArray());//burası RAMi doldurabilir dikakt etmek lazım. bunun yerine diske bir yere yazıp istenince vermek daha mantıklı olabilir
                }
                if (!StringUtils.isBlank(request.getEmail())) {
                    mailSender.send(request.getEmail(), "Raporunuz ektedir", Collections.singletonMap(request.getUuid() + "." + request.getExtension(), baos.toByteArray()));
                }
            }
        } catch (JRException | IOException | MessagingException | SQLException ex) {
            ex.printStackTrace();
            exception = ex;
        } finally {
            if (connection != null) {
                connection.forceClose();
            }
        }
        ended = System.currentTimeMillis();
    }

    public ReportRequest getRequest() {
        return request;
    }

    public void setRequest(ReportRequest request) {
        this.request = request;
    }

    /**
     * @return the exception
     */
    public Exception getException() {
        return exception;
    }

    public ReportResponse getResponse() {
        return response;
    }

    public YoncaConnection getYoncaConnection() {
        return connection;
    }

    public long getStarted() {
        return started;
    }

    public long getEnded() {
        return ended;
    }
}