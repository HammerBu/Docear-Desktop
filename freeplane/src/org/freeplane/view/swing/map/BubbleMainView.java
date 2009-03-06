/*
 *  Freeplane - mind map editor
 *  Copyright (C) 2008 Joerg Mueller, Daniel Polansky, Christian Foltin, Dimitry Polivaev
 *
 *  This file is modified by Dimitry Polivaev in 2008.
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
package org.freeplane.view.swing.map;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Stroke;

import org.freeplane.core.model.NodeModel;
import org.freeplane.features.common.edge.EdgeController;
import org.freeplane.features.common.nodestyle.NodeStyleModel;

class BubbleMainView extends MainView {
	final static Stroke DEF_STROKE = new BasicStroke();

	/**
	 * Returns the relative position of the Edge
	 */
	@Override
	int getAlignment() {
		return NodeView.ALIGN_CENTER;
	}

	@Override
	Point getCenterPoint() {
		final Point in = getLeftPoint();
		in.x = getWidth() / 2;
		return in;
	}

	@Override
	public int getDeltaX() {
		final NodeModel model = getNodeView().getModel();
		if (getNodeView().getMap().getModeController().getMapController().isFolded(model) && getNodeView().isLeft()) {
			return super.getDeltaX() + getZoomedFoldingSymbolHalfWidth() * 2;
		}
		return super.getDeltaX();
	}

	@Override
	Point getLeftPoint() {
		final Point in = new Point(0, getHeight() / 2);
		return in;
	}

	@Override
	protected int getMainViewWidthWithFoldingMark() {
		int width = getWidth();
		final int dW = getZoomedFoldingSymbolHalfWidth() * 2;
		final NodeModel model = getNodeView().getModel();
		if (getNodeView().getMap().getModeController().getMapController().isFolded(model)) {
			width += dW;
		}
		return width + dW;
	}

	/*
	 * (non-Javadoc)
	 * @see freeplane.view.mindmapview.NodeView.MainView#getPreferredSize()
	 */
	@Override
	public Dimension getPreferredSize() {
		final Dimension prefSize = super.getPreferredSize();
		prefSize.width += getNodeView().getMap().getZoomed(5);
		return prefSize;
	}

	@Override
	Point getRightPoint() {
		final Point in = getLeftPoint();
		in.x = getWidth() - 1;
		return in;
	}

	/*
	 * (non-Javadoc)
	 * @see freeplane.view.mindmapview.NodeView#getStyle()
	 */
	@Override
	String getStyle() {
		return NodeStyleModel.STYLE_BUBBLE;
	}

	/*
	 * (non-Javadoc)
	 * @see freeplane.view.mindmapview.NodeView#getTextWidth()
	 */
	@Override
	public int getTextWidth() {
		return super.getTextWidth() + getNodeView().getMap().getZoomed(5);
	}

	/*
	 * (non-Javadoc)
	 * @see freeplane.view.mindmapview.NodeView#getTextX()
	 */
	@Override
	public int getTextX() {
		return super.getTextX() + getNodeView().getMap().getZoomed(2);
	}

	@Override
	public void paint(final Graphics graphics) {
		final Graphics2D g = (Graphics2D) graphics;
		final NodeView nodeView = getNodeView();
		final NodeModel model = nodeView.getModel();
		if (model == null) {
			return;
		}
		paintSelected(g);
		paintDragOver(g);
		final Color edgeColor = EdgeController.getController(getNodeView().getMap().getModeController())
		    .getColor(model);
		g.setColor(edgeColor);
		g.setStroke(BubbleMainView.DEF_STROKE);
		g.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
		super.paint(g);
	}

	@Override
	protected void paintBackground(final Graphics2D graphics, final Color color) {
		graphics.setColor(color);
		graphics.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
	}

	@Override
	public void paintSelected(final Graphics2D graphics) {
		super.paintSelected(graphics);
		if (getNodeView().useSelectionColors()) {
			graphics.setColor(MapView.standardSelectColor);
			graphics.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
		}
	}
}
