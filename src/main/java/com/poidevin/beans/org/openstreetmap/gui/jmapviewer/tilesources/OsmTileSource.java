// License: GPL. For details, see Readme.txt file.
package com.poidevin.beans.org.openstreetmap.gui.jmapviewer.tilesources;

/**
 * OSM Tile source.
 */
public class OsmTileSource {

    /**
     * The default "Mapnik" OSM tile source.
     */
    public static class Mapnik extends AbstractOsmTileSource {

    	/**
    	 * Other OSM servers :
    	 * http://Corvallis.tile.openstreetmap.org
    	 * http://London.tile.openstreetmap.org
    	 */
        private static final String PATTERN = "http://%s.tile.openstreetmap.org";

        private static final String[] SERVER = {"a", "b", "c"};

        private int serverNum;

        /**
         * Constructs a new {@code "Mapnik"} tile source.
         */
        public Mapnik() {
            super("Mapnik", PATTERN, "MAPNIK");
        }

        @Override
        public String getBaseUrl() {
            String url = String.format(this.baseUrl, new Object[] {SERVER[serverNum]});
            serverNum = (serverNum + 1) % SERVER.length;
            return url;
        }
    }

    /**
     * The "Cycle Map" OSM tile source.
     */
    public static class CycleMap extends AbstractOsmTileSource {

        private static final String PATTERN = "http://%s.tile.opencyclemap.org/cycle";

        private static final String[] SERVER = {"a", "b", "c"};

        private int serverNum;

        /**
         * Constructs a new {@code CycleMap} tile source.
         */
        public CycleMap() {
            super("Cyclemap", PATTERN, "opencyclemap");
        }

        @Override
        public String getBaseUrl() {
            String url = String.format(this.baseUrl, new Object[] {SERVER[serverNum]});
            serverNum = (serverNum + 1) % SERVER.length;
            return url;
        }

        @Override
        public int getMaxZoom() {
            return 18;
        }
    }
}
