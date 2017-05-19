// License: GPL. For details, see Readme.txt file.
package com.poidevin.beans.org.openstreetmap.gui.jmapviewer.interfaces;

import java.awt.Color;
import java.awt.Font;
import java.awt.Stroke;

import com.poidevin.beans.org.openstreetmap.gui.jmapviewer.Layer;
import com.poidevin.beans.org.openstreetmap.gui.jmapviewer.Style;

public interface MapObject {

    Layer getLayer();

    void setLayer(Layer layer);

    Style getStyle();

    Style getStyleAssigned();

    Color getColor();

    Color getBackColor();

    Stroke getStroke();

    Font getFont();

    String getName();

    boolean isVisible();
}
