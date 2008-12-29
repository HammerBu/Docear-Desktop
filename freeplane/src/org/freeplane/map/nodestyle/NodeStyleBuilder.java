/*
 *  Freeplane - mind map editor
 *  Copyright (C) 2008 Joerg Mueller, Daniel Polansky, Christian Foltin, Dimitry Polivaev
 *
 *  This file is created by Dimitry Polivaev in 2008.
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.freeplane.map.nodestyle;

import java.io.IOException;

import org.freeplane.extension.IExtension;
import org.freeplane.io.IAttributeHandler;
import org.freeplane.io.IElementDOMHandler;
import org.freeplane.io.IExtensionAttributeWriter;
import org.freeplane.io.IExtensionElementWriter;
import org.freeplane.io.ITreeWriter;
import org.freeplane.io.ReadManager;
import org.freeplane.io.WriteManager;
import org.freeplane.io.xml.TreeXmlReader;
import org.freeplane.io.xml.TreeXmlWriter;
import org.freeplane.io.xml.n3.nanoxml.IXMLElement;
import org.freeplane.io.xml.n3.nanoxml.XMLElement;
import org.freeplane.map.tree.NodeBuilder;
import org.freeplane.map.tree.NodeModel;

public class NodeStyleBuilder implements IElementDOMHandler, IExtensionElementWriter,
        IExtensionAttributeWriter {
	static class FontProperties {
		String fontName;
		Integer fontSize;
		Boolean isBold;
		Boolean isItalic;
	}

	public NodeStyleBuilder() {
	}

	public Object createElement(final Object parent, final String tag, final IXMLElement attributes) {
		if (tag.equals("font")) {
			return new FontProperties();
		}
		return null;
	}

	public void endElement(final Object parent, final String tag, final Object userObject,
	                       final IXMLElement dom) {
		if (parent instanceof NodeModel) {
			final NodeModel node = (NodeModel) parent;
			if (tag.equals("font")) {
				final FontProperties fp = (FontProperties) userObject;
				NodeStyleModel nodeStyleModel = node.getNodeStyleModel();
				if (nodeStyleModel == null) {
					nodeStyleModel = new NodeStyleModel();
					node.addExtension(nodeStyleModel);
				}
				nodeStyleModel.setFontFamilyName(fp.fontName);
				nodeStyleModel.setFontSize(fp.fontSize);
				nodeStyleModel.setItalic(fp.isItalic);
				nodeStyleModel.setBold(fp.isBold);
				return;
			}
			return;
		}
	}

	private void registerAttributeHandlers(final ReadManager reader) {
		reader.addAttributeHandler(NodeBuilder.XML_NODE, "COLOR", new IAttributeHandler() {
			public void setAttribute(final Object userObject, final String value) {
				if (value.length() == 7) {
					final NodeModel node = (NodeModel) userObject;
					node.setColor(TreeXmlReader.xmlToColor(value));
				}
			}
		});
		reader.addAttributeHandler(NodeBuilder.XML_NODE, "BACKGROUND_COLOR",
		    new IAttributeHandler() {
			    public void setAttribute(final Object userObject, final String value) {
				    if (value.length() == 7) {
					    final NodeModel node = (NodeModel) userObject;
					    node.setBackgroundColor(TreeXmlReader.xmlToColor(value));
				    }
			    }
		    });
		reader.addAttributeHandler(NodeBuilder.XML_NODE, "STYLE", new IAttributeHandler() {
			public void setAttribute(final Object userObject, final String value) {
				final NodeModel node = (NodeModel) userObject;
				node.setShape(value);
			}
		});
		reader.addAttributeHandler("font", "SIZE", new IAttributeHandler() {
			public void setAttribute(final Object userObject, final String value) {
				final FontProperties fp = (FontProperties) userObject;
				fp.fontSize = Integer.parseInt(value.toString());
			}
		});
		reader.addAttributeHandler("font", "NAME", new IAttributeHandler() {
			public void setAttribute(final Object userObject, final String value) {
				final FontProperties fp = (FontProperties) userObject;
				fp.fontName = value.toString();
			}
		});
		reader.addAttributeHandler("font", "BOLD", new IAttributeHandler() {
			public void setAttribute(final Object userObject, final String value) {
				final FontProperties fp = (FontProperties) userObject;
				fp.isBold = value.toString().equals("true");
			}
		});
		reader.addAttributeHandler("font", "ITALIC", new IAttributeHandler() {
			public void setAttribute(final Object userObject, final String value) {
				final FontProperties fp = (FontProperties) userObject;
				fp.isItalic = value.toString().equals("true");
			}
		});
	}

	/**
	 */
	public void registerBy(final ReadManager reader, final WriteManager writer) {
		reader.addElementHandler("font", this);
		registerAttributeHandlers(reader);
		writer.addExtensionElementWriter(NodeStyleModel.class, this);
		writer.addExtensionAttributeWriter(NodeStyleModel.class, this);
	}

	public void setAttributes(final String tag, final Object node, final IXMLElement attributes) {
	}

	public void writeAttributes(final ITreeWriter writer, final Object userObject,
	                            final IExtension extension) {
		final NodeStyleModel style = (NodeStyleModel) extension;
		if (style.getColor() != null) {
			writer.addAttribute("COLOR", TreeXmlWriter.colorToXml(style.getColor()));
		}
		if (style.getBackgroundColor() != null) {
			writer.addAttribute("BACKGROUND_COLOR", TreeXmlWriter.colorToXml(style.getBackgroundColor()));
		}
		if (style.getShape() != null) {
			writer.addAttribute("STYLE", style.getShape());
		}
	}

	public void writeContent(final ITreeWriter writer, final Object node, final IExtension extension)
	        throws IOException {
		final NodeStyleModel style = (NodeStyleModel) extension;
		if (style != null) {
			final XMLElement fontElement = new XMLElement();
			fontElement.setName("font");
			boolean isRelevant = false;
			if (style.getFontFamilyName() != null) {
				fontElement.setAttribute("NAME", style.getFontFamilyName());
				isRelevant = true;
			}
			if (style.getFontSize() != null) {
				fontElement.setAttribute("SIZE", Integer.toString(style.getFontSize()));
				isRelevant = true;
			}
			if (style.isBold() != null) {
				fontElement.setAttribute("BOLD", style.isBold() ? "true" : "false");
				isRelevant = true;
			}
			if (style.isItalic() != null) {
				fontElement.setAttribute("ITALIC", style.isItalic() ? "true" : "false");
				isRelevant = true;
			}
			if (isRelevant) {
				writer.addElement(style, fontElement);
			}
		}
	}
}
