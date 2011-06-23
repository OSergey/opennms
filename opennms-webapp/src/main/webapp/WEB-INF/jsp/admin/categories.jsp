<%--
/*******************************************************************************
 * This file is part of OpenNMS(R).
 *
 * Copyright (C) 2006-2011 The OpenNMS Group, Inc.
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

--%>
<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<jsp:include page="/includes/header.jsp" flush="false">
	<jsp:param name="title" value="Categories" />
	<jsp:param name="headTitle" value="Categories" />
	<jsp:param name="breadcrumb"
               value="<a href='admin/index.jsp'>Admin</a>" />
	<jsp:param name="breadcrumb" value="Categories" />
</jsp:include>

<h3>Surveillance Categories</h3>

<table>
  <tr>
    <th>Delete</th>
    <th>Edit</th>
    <th>Category</th>
  </tr>
  <c:forEach items="${categories}" var="category">
	  <tr>
	    <td><a href="admin/categories.htm?removeCategoryId=${category.id}"><img src="images/trash.gif" alt="Delete Category"/></a></td>
	    <td><a href="admin/categories.htm?categoryid=${category.id}&edit"><img src="images/modify.gif" alt="Edit Category"/></a></td>
	    <td><a href="admin/categories.htm?categoryid=${category.id}">${fn:escapeXml(category.name)}</a></td> 
  	  </tr>
  </c:forEach>
  <tr>
    <td></td>
    <td></td>
    <td>
      <form action="admin/categories.htm">
        <input type="textfield" name="newCategoryName" size="40"/>
        <input type="submit" value="Add New Category"/>
      </form>
  </tr>
</table>

<jsp:include page="/includes/footer.jsp" flush="false"/>
