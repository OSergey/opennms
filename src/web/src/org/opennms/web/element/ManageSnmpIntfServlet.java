/*
 * Creato il 17-feb-2004
 *
 * Per modificare il modello associato a questo file generato, aprire
 * Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
package org.opennms.web.element;

import javax.servlet.ServletException;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.opennms.netmgt.config.SnmpPeerFactory;
import org.opennms.netmgt.utils.IPSorter;
import org.opennms.protocols.snmp.SnmpBadConversionException;
import org.opennms.protocols.snmp.SnmpPeer;
import org.opennms.core.utils.SnmpIfAdmin;

import java.sql.SQLException;
import java.util.ArrayList;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author micmas
 * 
 * Per modificare il modello associato al commento di questo tipo generato,
 * aprire Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e
 * commenti
 * 
 * La servlet prende i seguenti parametri dal file web.xml
 */
public final class ManageSnmpIntfServlet extends HttpServlet {
    protected int snmpServiceId;

    protected SnmpPeerFactory snmpPeerFactory;

    protected String pageToRedirect;

    public void init() throws ServletException {
        try {
            this.snmpServiceId = NetworkElementFactory
                    .getServiceIdFromName("SNMP");
            SnmpPeerFactory.init();
        } catch (Exception e) {
            throw new ServletException(
                    "Could not determine the snmp service ID", e);
        }
    }

    protected void doGet(HttpServletRequest request,
            HttpServletResponse response) throws ServletException {
        doPost(request, response);
    }

    protected void doPost(HttpServletRequest request,
            HttpServletResponse response) throws ServletException {

        HttpSession userSession = request.getSession(false);
        if (userSession == null)
            throw new ServletException("Session exceeded");

        String nodeIdString = request.getParameter("node");
        if (nodeIdString == null) {
            throw new org.opennms.web.MissingParameterException("node");
        }
        int nodeId = Integer.parseInt(nodeIdString);

        String intfIdString = request.getParameter("intf");
        if (intfIdString == null) {
            throw new org.opennms.web.MissingParameterException("intf");
        }
        int intfId = Integer.parseInt(intfIdString);

        String statusString = request.getParameter("status");
        if (statusString == null) {
            throw new org.opennms.web.MissingParameterException("status");
        }
        int status = Integer.parseInt(statusString);

        String snmpIp = null;
        Service[] snmpServices = null;
        try {
            snmpServices = NetworkElementFactory.getServicesOnNode(nodeId,
                    this.snmpServiceId);
            if (snmpServices != null && snmpServices.length > 0) {
                ArrayList ips = new ArrayList();
                for (int i = 0; i < snmpServices.length; i++) {
                    ips.add(InetAddress.getByName(snmpServices[i]
                            .getIpAddress()));
                }

                InetAddress lowest = IPSorter.getLowestInetAddress(ips);

                if (lowest != null) {
                    snmpIp = lowest.getHostAddress();
                }
            }

            InetAddress[] inetAddress = InetAddress.getAllByName(snmpIp);
            SnmpPeer m_snmpPeer = SnmpPeerFactory.getInstance().getPeer(
                    inetAddress[0]);
            SnmpIfAdmin snmpIfAdmin = new SnmpIfAdmin(nodeId, m_snmpPeer);
            snmpIfAdmin.setIfAdmin(intfId, status);
            redirect(request, response);
            
        } catch (SQLException e) {
            throw new ServletException(e);
        } catch (UnknownHostException e) {
            throw new ServletException(e);
        } catch (IOException e) {
            throw new ServletException(e);
        } catch (SnmpBadConversionException e) {
            throw new ServletException(e);
        }
    }

    private void redirect(HttpServletRequest request,
            HttpServletResponse response) throws IOException {
        String redirectURL = request.getHeader("Referer");
        response.sendRedirect(redirectURL);
    }

}
