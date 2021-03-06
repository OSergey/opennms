/*******************************************************************************
 * This file is part of OpenNMS(R).
 *
 * Copyright (C) 2011-2012 The OpenNMS Group, Inc.
 * OpenNMS(R) is Copyright (C) 1999-2012 The OpenNMS Group, Inc.
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

package org.opennms.core.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * This class is intended to be group some utility classes related with RDBMS
 * and the capsd and monitoring plugins.
 *
 * @author Jose Vicente Nunez Zuleta (josevnz@users.sourceforge.net) - RHCE, SJCD, SJCP
 * @version 0.1 - 07/22/2002
 * @since 0.1
 */
public class DBTools {

    private static int _counter;

    private static DBTools _instance;

    /**
     * The JDBC hostname. This token is replaced when the url is constructed.
     * 
     * @see #constructUrl
     */
    public static final String JDBC_HOST = "OPENNMS_JDBC_HOSTNAME";

    /**
     * Minimal port range
     */
    public static final int MIN_PORT_VALUE = 1024;

    /**
     * Maximum port range
     */
    public static final int MAX_PORT_VALUE = 65535;

    /**
     * Default Sybase JDBC driver to use. Defaults to
     * 'com.sybase.jdbc2.jdbc.SybDriver'
     */
    public static final String DEFAULT_JDBC_DRIVER = "com.sybase.jdbc2.jdbc.SybDriver";

    /**
     * Default user to use when connecting to the database. Defaults to 'sa'
     */
    public static final String DEFAULT_DATABASE_USER = "sa";

    /**
     * Default port to use to check this service. Defaults to '4100' Make sure
     * than is less than MAX_PORT_VALUE and greater than MIN_PORT_VALUE
     * 
     * @see #MIN_PORT_VALUE
     * @see #MAX_PORT_VALUE
     */
    public static final int DEFAULT_PORT = 4100;

    /**
     * Default database password. Should be empty. You should not put a database
     * password here (or event worst, hardcode it in the code) Instead call the
     * class method that accepts a map
     */
    public static final String DEFAULT_DATABASE_PASSWORD = "";

    /**
     * Default vendor protocol, like jdbc:sybase:Tds:
     */
    public static final String DEFAULT_URL = "jdbc:sybase:Tds:" + JDBC_HOST + "/tempdb";

    // Pattern for the JDBC_HOST
    private static final Pattern _pattern = Pattern.compile(JDBC_HOST);

    /**
     * Hide the constructor, this class follows the "Singleton" pattern.
     */
    private DBTools() {
        // do nothing
    }

    /**
     * Returns a single instance of this class to the caller. We do not want
     * multiple copies of this class loaded, just one.
     *
     * @return DBTools A class instance
     */
    public synchronized static DBTools getInstance() {
        if (_instance == null) {
            _instance = new DBTools();
        }
        _counter++;
        return _instance;
    }

    /**
     * Return how many instances of this objects are loaded now
     *
     * @return int Number of instances on this JVM
     */
    public int getNumberOfInstances() {
        return _counter;
    };

    /**
     * Constructs a JDBC url given a set of fragments. The resulting Url will
     * have the form: <br>
     * <code>jdbc:<protocol:<b>hostname</b>:<b>4100</b></code>
     *
     * @param hostname_
     *            The hostname where the database server is
     * @param url_
     *            (for example jdbc:sybase:Tds:@{link #JDBC_HOST
     *            JDBC_HOST}:4100/tempdb). The JDBC_HOST is replaced by the real
     *            hostname
     * @throws java.lang.NullPointerException
     *             If one of the arguments is null
     * @throws java.lang.IllegalArgumentException
     *             If the JDBC_HOST is not part of the JDBC url
     * @return a {@link java.lang.String} object.
     */
    public static String constructUrl(String url_, String hostname_) throws IllegalArgumentException, NullPointerException {
        String url = null;

        if (url_ == null) {
            throw new NullPointerException(DBTools.class.getName() + ": url cannot be null");
        }
        if (hostname_ == null) {
            throw new NullPointerException(DBTools.class.getName() + ": hostname cannot be null");
        }
        try {
            Matcher match = _pattern.matcher(url_);
            url = match.replaceFirst(hostname_);
        } catch (PatternSyntaxException patternExp) {
            throw patternExp;
        }
        return url;
    }

} // End of class
