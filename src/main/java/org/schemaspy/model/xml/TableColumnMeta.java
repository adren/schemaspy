/*
 * This file is a part of the SchemaSpy project (http://schemaspy.org).
 * Copyright (C) 2004, 2005, 2006, 2007, 2008, 2009, 2010, 2011 John Currier
 *
 * SchemaSpy is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * SchemaSpy is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package org.schemaspy.model.xml;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Additional metadata about a column as expressed in XML instead of from
 * the database.
 *
 * @author John Currier
 */
public class TableColumnMeta {
    private final String name;
    private final String type;
    private final boolean isPrimary;
    private final String id;
    private final int size;
    private final int digits;
    private final boolean isNullable;
    private final String comments;
    private final String defaultValue;
    private final boolean isAutoUpdated;
    private final List<ForeignKeyMeta> foreignKeys = new ArrayList<ForeignKeyMeta>();
    private final boolean isExcluded;
    private final boolean isAllExcluded;
    private final boolean isImpliedParentsDisabled;
    private final boolean isImpliedChildrenDisabled;
    private static final Logger logger = Logger.getLogger(TableColumnMeta.class.getName());

    public TableColumnMeta(Node colNode) {
        NamedNodeMap attribs = colNode.getAttributes();
        String tmp;

        name = attribs.getNamedItem("name").getNodeValue();

        Node node = attribs.getNamedItem("comments");
        if (node == null)
            node = attribs.getNamedItem("remarks");
        if (node != null) {
            tmp = node.getNodeValue().trim();
            comments = tmp.length() == 0 ? null : tmp;
        } else {
            comments = null;
        }

        node = attribs.getNamedItem("type");
        type = node == null ? "Unknown" : node.getNodeValue();

        node = attribs.getNamedItem("id");
        id = node == null ? null : node.getNodeValue();

        node = attribs.getNamedItem("size");
        size = node == null ? 0 : Integer.parseInt(node.getNodeValue());

        node = attribs.getNamedItem("digits");
        digits = node == null ? 0 : Integer.parseInt(node.getNodeValue());
        
        node = attribs.getNamedItem("nullable");
        isNullable = node != null && evalBoolean(node.getNodeValue());

        node = attribs.getNamedItem("autoUpdated");
        isAutoUpdated = node != null && evalBoolean(node.getNodeValue());
        
        node = attribs.getNamedItem("primaryKey");
        isPrimary = node != null && evalBoolean(node.getNodeValue());
        
        node = attribs.getNamedItem("defaultValue");
        defaultValue = node == null ? null : node.getNodeValue();
        
        node = attribs.getNamedItem("disableImpliedKeys");
        if (node != null) {
            tmp = node.getNodeValue().trim().toLowerCase();
            switch (tmp) {
                case "to":
                    isImpliedChildrenDisabled = true;
                    isImpliedParentsDisabled = false;
                    break;
                case "from":
                    isImpliedParentsDisabled = true;
                    isImpliedChildrenDisabled = false;
                    break;
                case "all":
                    isImpliedChildrenDisabled = isImpliedParentsDisabled = true;
                    break;
                default:
                    isImpliedChildrenDisabled = isImpliedParentsDisabled = false;
                    break;
            }
        } else {
            isImpliedChildrenDisabled = isImpliedParentsDisabled = false;
        }

        node = attribs.getNamedItem("disableDiagramAssociations");
        if (node != null) {
            tmp = node.getNodeValue().trim().toLowerCase();
            switch (tmp) {
                case "all":
                    isAllExcluded = true;
                    isExcluded = true;
                    break;
                case "exceptdirect":
                    isAllExcluded = false;
                    isExcluded = true;
                    break;
                default:
                    isAllExcluded = false;
                    isExcluded = false;
                    break;
            }
        } else {
            isAllExcluded = false;
            isExcluded = false;
        }

        logger.finer("Found XML column metadata for " + name +
                    " isPrimaryKey: " + isPrimary +
                    " comments: " + comments);

        NodeList fkNodes = ((Element)colNode.getChildNodes()).getElementsByTagName("foreignKey");

        for (int i = 0; i < fkNodes.getLength(); ++i) {
            Node fkNode = fkNodes.item(i);
            foreignKeys.add(new ForeignKeyMeta(fkNode));
        }
    }

    private boolean evalBoolean(String exp) {
        if (exp == null)
            return false;

        String returnExp = exp.trim().toLowerCase();
        return "true".equals(returnExp) || "yes".equals(returnExp) || "1".equals(returnExp);
    }

    public String getName() {
        return name;
    }
    
    public String getType() {
        return type;
    }
    
    public String getId() {
        return id;
    }
    
    public int getSize() {
        return size;
    }
    
    public int getDigits() {
        return digits;
    }
    
    public boolean isPrimary() {
        return isPrimary;
    }

    public boolean isNullable() {
        return isNullable;
    }
    
    public boolean isAutoUpdated() {
        return isAutoUpdated;
    }

    public String getComments() {
        return comments;
    }
    
    public String getDefaultValue() {
        return defaultValue;
    }

    public List<ForeignKeyMeta> getForeignKeys() {
        return foreignKeys;
    }

    public boolean isExcluded() {
        return isExcluded;
    }

    public boolean isAllExcluded() {
        return isAllExcluded;
    }

    public boolean isImpliedParentsDisabled() {
        return isImpliedParentsDisabled;
    }

    public boolean isImpliedChildrenDisabled() {
        return isImpliedChildrenDisabled;
    }
}