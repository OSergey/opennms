/*******************************************************************************
 * This file is part of OpenNMS(R).
 *
 * Copyright (C) 2010-2011 The OpenNMS Group, Inc.
 * OpenNMS(R) is Copyright (C) 1999-2011 The OpenNMS Group, Inc.
 *
 * OpenNMS(R) is a registered trademark of The OpenNMS Group, Inc.
 *
 * OpenNMS(R) is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published
 * by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * OpenNMS(R) is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OpenNMS(R).  If not, see:
 *      http://www.gnu.org/licenses/
 *
 * For more information contact:
 *     OpenNMS(R) Licensing <license@opennms.org>
 *     http://www.opennms.org/
 *     http://www.opennms.com/
 *******************************************************************************/

package org.opennms.reporting.core.svclayer.support;

import java.io.File;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.hibernate.criterion.Order;
import org.opennms.api.reporting.ReportException;
import org.opennms.api.reporting.ReportFormat;
import org.opennms.api.reporting.ReportService;
import org.opennms.core.utils.ThreadCategory;
import org.opennms.netmgt.config.databaseReports.Report;
import org.opennms.netmgt.dao.DatabaseReportConfigDao;
import org.opennms.netmgt.dao.ReportCatalogDao;
import org.opennms.netmgt.model.OnmsCriteria;
import org.opennms.netmgt.model.ReportCatalogEntry;
import org.opennms.reporting.core.svclayer.ReportServiceLocator;
import org.opennms.reporting.core.svclayer.ReportStoreService;

/**
 * <p>DefaultReportStoreService class.</p>
 */
public class DefaultReportStoreService implements ReportStoreService {
    
    private ReportCatalogDao m_reportCatalogDao;
    private ReportServiceLocator m_reportServiceLocator;
    private DatabaseReportConfigDao m_databaseReportConfigDao;
    
    private static final String LOG4J_CATEGORY = "OpenNMS.Report";
    
    private final ThreadCategory log;
    
    /**
     * <p>Constructor for DefaultReportStoreService.</p>
     */
    public DefaultReportStoreService () {
        String oldPrefix = ThreadCategory.getPrefix();
        ThreadCategory.setPrefix(LOG4J_CATEGORY);
        log = ThreadCategory.getInstance(DefaultReportStoreService.class);
        ThreadCategory.setPrefix(oldPrefix);
    }

    /**
     * <p>delete</p>
     *
     * @param ids an array of {@link java.lang.Integer} objects.
     */
    public void delete(Integer[] ids) {
        for (Integer id : ids) {
            delete(id); 
        }
    }
    
    /**
     * <p>delete</p>
     *
     * @param id a {@link java.lang.Integer} object.
     */
    public void delete(Integer id) {
        String deleteFile = new String(m_reportCatalogDao.get(id).getLocation());
        boolean success = (new File(deleteFile).delete());
        if (success) {
            log().debug("deleted report XML file: " + deleteFile);
        } else {
            log().warn("unable to delete report XML file: " + deleteFile + " will delete reportCatalogEntry anyway");
        }
        m_reportCatalogDao.delete(id);
    }

    /**
     * <p>getAll</p>
     *
     * @return a {@link java.util.List} object.
     */
    public List<ReportCatalogEntry> getAll() {
        OnmsCriteria onmsCrit = new OnmsCriteria(ReportCatalogEntry.class);
        onmsCrit.addOrder(Order.desc("date"));
        return m_reportCatalogDao.findMatching(onmsCrit);
    }
    
    /**
     * <p>getFormatMap</p>
     *
     * @return a {@link java.util.Map} object.
     */
    public Map<String, Object> getFormatMap() {
        HashMap <String, Object> formatMap = new HashMap<String, Object>();
        List <Report> reports = m_databaseReportConfigDao.getReports();
        Iterator<Report> reportIter = reports.iterator();
        while (reportIter.hasNext()) {
            Report report = reportIter.next();
            String id = report.getId();
            String service = report.getReportService();
            List <ReportFormat> formats = m_reportServiceLocator.getReportService(service).getFormats(id);
            formatMap.put(id, formats);
        }
        return formatMap;
    }
    
    /** {@inheritDoc} */
    public void render(Integer id, ReportFormat format, OutputStream outputStream) {
        ReportCatalogEntry catalogEntry = m_reportCatalogDao.get(id);
        String reportServiceName = m_databaseReportConfigDao.getReportService(catalogEntry.getReportId());
        ReportService reportService = m_reportServiceLocator.getReportService(reportServiceName);
        log().debug("attempting to rended the report as " + format.toString() + " using " + reportServiceName );
        try {
            reportService.render(catalogEntry.getReportId(), catalogEntry.getLocation(), format, outputStream);
        } catch (ReportException e) {
            log.error("unable to render report", e);
        }
    }
    
    private ThreadCategory log() {
        return ThreadCategory.getInstance(getClass());
    }

    /** {@inheritDoc} */
    public void save(final ReportCatalogEntry reportCatalogEntry) {
        m_reportCatalogDao.save(reportCatalogEntry);
        m_reportCatalogDao.flush();
    }

    /** {@inheritDoc} */
    public void setReportCatalogDao(ReportCatalogDao reportCatalogDao) {
        m_reportCatalogDao = reportCatalogDao;
    }
    
    /**
     * <p>setDatabaseReportConfigDao</p>
     *
     * @param databaseReportConfigDao a {@link org.opennms.netmgt.dao.DatabaseReportConfigDao} object.
     */
    public void setDatabaseReportConfigDao(DatabaseReportConfigDao databaseReportConfigDao) {
        m_databaseReportConfigDao = databaseReportConfigDao;
    }
    
    /** {@inheritDoc} */
    public void setReportServiceLocator(ReportServiceLocator reportServiceLocator) {
        m_reportServiceLocator = reportServiceLocator;
    }

}
