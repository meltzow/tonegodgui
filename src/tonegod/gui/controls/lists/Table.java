package tonegod.gui.controls.lists;

import com.jme3.font.BitmapFont;
import com.jme3.font.LineWrapMode;
import com.jme3.input.KeyInput;
import com.jme3.input.event.KeyInputEvent;
import com.jme3.input.event.MouseButtonEvent;
import com.jme3.input.event.MouseMotionEvent;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector4f;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import tonegod.gui.controls.buttons.ButtonAdapter;
import tonegod.gui.controls.scrolling.ScrollArea;
import tonegod.gui.core.Element;
import tonegod.gui.core.ElementManager;
import tonegod.gui.core.utils.UIDUtil;
import tonegod.gui.listeners.KeyboardListener;
import tonegod.gui.listeners.MouseButtonListener;
import tonegod.gui.listeners.MouseMovementListener;
import tonegod.gui.listeners.MouseWheelListener;
import tonegod.gui.listeners.TabFocusListener;

/**
 *
 * @author rockfire
 * @author t0neg0d
 */
public abstract class Table extends ScrollArea implements MouseMovementListener, MouseWheelListener, MouseButtonListener, TabFocusListener, KeyboardListener {

    private final Vector2f arrowSize;

    public enum ColumnResizeMode {

        NONE, AUTO_ALL, AUTO_FIRST, AUTO_LAST;
    }

    public enum SelectionMode {

        NONE, ROW, MULTIPLE_ROWS, CELL, MULTIPLE_CELLS;

        public boolean isEnabled() {
            return !this.equals(NONE);
        }

        public boolean isSingle() {
            return this.equals(ROW) || this.equals(CELL);
        }

        public boolean isMultiple() {
            return this.equals(MULTIPLE_CELLS) || this.equals(MULTIPLE_ROWS);
        }
    }
    private List<TableRow> rows = new ArrayList();
    private List<Integer> selectedRows = new ArrayList();
    private Map<Integer, List<Integer>> selectedCells = new HashMap();
    private List<Element> highlights = new ArrayList();
    private SelectionMode selectionMode = SelectionMode.ROW;
    private float tablePadding = 1;
    private ColorRGBA highlightColor;
    protected int currentRowIndex = -1;
    protected int currentColumnIndex = -1;
    private boolean shift = false, ctrl = false;
    private final List<TableColumn> columns = new ArrayList<TableColumn>();
    private final float headerHeight;
    private final float rowHeight;
    private Element clipLayer;
    private ColumnResizeMode columnResizeMode = ColumnResizeMode.NONE;
    private boolean sortable;
    private String arrowUpImg;
    private String arrowDownImg;
    private String noArrowImg;

    public static class TableCell extends Element implements Comparable<TableCell> {

        private Object value;

        public TableCell(ElementManager screen, String label, Object value) {
            super(screen, UIDUtil.getUID(), Vector2f.ZERO, screen.getStyle("Table#Cell").getVector2f("defaultSize"), screen.getStyle("Table#Cell").getVector4f("resizeBorders"), screen.getStyle("Table#Cell").getString("defaultImg"));
            init(label, value);
        }

        public TableCell(ElementManager screen, String label, Object value, Vector2f dimensions, Vector4f resizeBorders, String texturePath) {
            super(screen, UIDUtil.getUID(), Vector2f.ZERO, dimensions, resizeBorders, texturePath);
            init(label, value);
        }

        private void init(String label, Object value) {

            // Load default font info
            setFontColor(screen.getStyle("Table#Cell").getColorRGBA("fontColor"));
            setFontSize(screen.getStyle("Table#Cell").getFloat("fontSize"));
            setTextAlign(BitmapFont.Align.valueOf(screen.getStyle("Table#Cell").getString("textAlign")));
            setTextVAlign(BitmapFont.VAlign.valueOf(screen.getStyle("Table#Cell").getString("textVAlign")));
            setTextWrap(LineWrapMode.valueOf(screen.getStyle("Table#Cell").getString("textWrap")));
            setTextPadding(screen.getStyle("Table#Cell").getFloat("textPadding"));
            setTextClipPadding(screen.getStyle("Table#Cell").getFloat("textPadding"));

            setText(label);
            setIgnoreMouse(true);
            this.value = value;
            setDocking(null);
            setScaleEW(false);
            setScaleNS(false);
        }

        @Override
        public int compareTo(TableCell o) {
            if (value instanceof Comparable && o.value instanceof Comparable) {
                return ((Comparable) value).compareTo((Comparable) o.value);
            }
            return toString().compareTo(o.toString());
        }
    }

    public static class TableColumn extends ButtonAdapter {

        private Table table;
        private Boolean sort;
        private boolean resized;

        public TableColumn(Table table, ElementManager screen, String UID) {
            super(screen, UID, Vector2f.ZERO, screen.getStyle("Table#Header").getVector2f("defaultSize"), screen.getStyle("Table#Header").getVector4f("resizeBorders"), screen.getStyle("Table#Header").getString("defaultImg"));
            init(table);
        }

        public TableColumn(Table table, ElementManager screen, String UID, Vector2f dimensions, Vector4f resizeBorders, String texturePath) {
            super(screen, UID, Vector2f.ZERO, dimensions, resizeBorders, texturePath);
            init(table);
        }

        @Override
        public void controlResizeHook() {
            // This flag is to stop sort events when actually resizing
            resized = true;
            super.controlResizeHook();

            // Adjust table columns to new size
            table.displayHighlights();
            table.sizeColumns();

        }

        @Override
        public void onMouseLeftPressed(MouseButtonEvent evt) {
            resized = false;
            super.onMouseLeftPressed(evt);
        }

        @Override
        public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
            super.onButtonMouseLeftUp(evt, toggled);
            if (!resized) {
                if (sort == null) {
                    sort = true;
                } else {
                    sort = !sort;
                }
                table.sort(this, sort);
            }

        }

        private void init(Table table) {

            // Load default font info
            setFontColor(screen.getStyle("Table#Header").getColorRGBA("fontColor"));
            setFontSize(screen.getStyle("Table#Header").getFloat("fontSize"));
            setTextAlign(BitmapFont.Align.valueOf(screen.getStyle("Table#Header").getString("textAlign")));
            setTextVAlign(BitmapFont.VAlign.valueOf(screen.getStyle("Table#Header").getString("textVAlign")));
            setTextWrap(LineWrapMode.valueOf(screen.getStyle("Table#Header").getString("textWrap")));
            setTextPadding(screen.getStyle("Table#Header").getFloat("textPadding"));
            setTextClipPadding(screen.getStyle("Table#Header").getFloat("textPadding"));

            setButtonIcon(table.arrowSize.x, table.arrowSize.y, table.noArrowImg); // start with the blank icon
            getButtonIcon().setX(getWidth() - getButtonIcon().getWidth() - borders.z - getTextPadding());

            this.table = table;
            if (screen.getStyle("Table#Header").getString("hoverImg") != null) {
                setButtonHoverInfo(
                        screen.getStyle("Table#Header").getString("hoverImg"),
                        screen.getStyle("Table#Header").getColorRGBA("hoverColor"));
            }
            if (screen.getStyle("Table#Header").getString("pressedImg") != null) {
                setButtonPressedInfo(
                        screen.getStyle("Table#Header").getString("pressedImg"),
                        screen.getStyle("Table#Header").getColorRGBA("pressedColor"));
            }
            setResizeN(false);
            setResizeS(false);
            setClippingLayer(table);
            reconfigure();
        }

        private void reconfigure() {
            setIsResizable(!table.columnResizeMode.equals(ColumnResizeMode.AUTO_ALL));
            int index = table.columns.indexOf(this);
            if (index != -1) {
                switch (table.columnResizeMode) {
                    case AUTO_FIRST:
                        setResizeE(false);
                        setResizeW(index > 0 && index < table.columns.size());
                        break;
                    case AUTO_LAST:
                        setResizeE(true);
                        setResizeW(index > 1 && index < table.columns.size());
                        break;
                    case NONE:
                        setResizeE(true);
                        setResizeW(false);
                        break;
                }
            }
        }
    }

    /**
     * Creates a new instance of the Table control
     *
     * @param screen The screen control the Element is to be added to
     * @param position A Vector2f containing the x/y position of the Element
     */
    public Table(ElementManager screen, Vector2f position) {
        this(screen, UIDUtil.getUID(), position,
                screen.getStyle("Table").getVector2f("defaultSize"),
                screen.getStyle("Table").getVector4f("resizeBorders"),
                screen.getStyle("Table").getString("defaultImg"));
    }

    /**
     * Creates a new instance of the Table control
     *
     * @param screen The screen control the Table is to be added to
     * @param position A Vector2f containing the x/y position of the Element
     * @param dimensions A Vector2f containing the width/height dimensions of the Element
     */
    public Table(ElementManager screen, Vector2f position, Vector2f dimensions) {
        this(screen, UIDUtil.getUID(), position, dimensions,
                screen.getStyle("Table").getVector4f("resizeBorders"),
                screen.getStyle("Table").getString("defaultImg"));
    }

    /**
     * Creates a new instance of the Table control
     *
     * @param screen The screen control the Element is to be added to
     * @param position A Vector2f containing the x/y position of the Element
     * @param dimensions A Vector2f containing the width/height dimensions of the Element
     * @param resizeBorders A Vector4f containg the border information used when resizing
     * the default image (x = N, y = W, z = E, w = S)
     * @param defaultImg The default image to use for the Menu
     */
    public Table(ElementManager screen, Vector2f position, Vector2f dimensions, Vector4f resizeBorders, String defaultImg) {
        this(screen, UIDUtil.getUID(), position, dimensions, resizeBorders, defaultImg);
    }

    /**
     * Creates a new instance of the Table control
     *
     * @param screen The screen control the Element is to be added to
     * @param UID A unique String identifier for the Element
     * @param position A Vector2f containing the x/y position of the Element
     */
    public Table(ElementManager screen, String UID, Vector2f position) {
        this(screen, UID, position,
                screen.getStyle("Table").getVector2f("defaultSize"),
                screen.getStyle("Table").getVector4f("resizeBorders"),
                screen.getStyle("Table").getString("defaultImg"));
    }

    /**
     * Creates a new instance of the Table control
     *
     * @param screen The screen control the Element is to be added to
     * @param UID A unique String identifier for the Element
     * @param position A Vector2f containing the x/y position of the Element
     * @param dimensions A Vector2f containing the width/height dimensions of the Element
     */
    public Table(ElementManager screen, String UID, Vector2f position, Vector2f dimensions) {
        this(screen, UID, position, dimensions,
                screen.getStyle("Table").getVector4f("resizeBorders"),
                screen.getStyle("Table").getString("defaultImg"));
    }

    /**
     * Creates a new instance of the Table control
     *
     * @param screen The screen control the Element is to be added to
     * @param UID A unique String identifier for the Element
     * @param position A Vector2f containing the x/y position of the Element
     * @param dimensions A Vector2f containing the width/height dimensions of the Element
     * @param resizeBorders A Vector4f containg the border information used when resizing
     * the default image (x = N, y = W, z = E, w = S)
     * @param defaultImg The default image to use for the Slider's track
     */
    public Table(ElementManager screen, String UID, Vector2f position, Vector2f dimensions, Vector4f resizeBorders, String defaultImg) {
        super(screen, UID, position, dimensions, resizeBorders, defaultImg, false);

        arrowSize = screen.getStyle("Table#Header").getVector2f("arrowSize");
        arrowUpImg = screen.getStyle("Table#Header").getString("arrowUpImg");
        arrowDownImg = screen.getStyle("Table#Header").getString("arrowDownImg");
        noArrowImg = screen.getStyle("Table#Header").getString("noArrowImg");

        tablePadding = screen.getStyle("Table").getFloat("tablePadding");
        headerHeight = screen.getStyle("Table#Header").getVector2f("defaultSize").y;
        highlightColor = screen.getStyle("Table").getColorRGBA("highlightColor");
        // Load default font info
        scrollableArea.setScaleEW(false);
        scrollableArea.setScaleNS(false);

        rowHeight = screen.getStyle("Table#Cell").getVector2f("defaultSize").y;

        scrollableArea.setScaleEW(true);
        scrollableArea.setText(" ");
        scrollableArea.setIgnoreMouse(true);
        scrollableArea.setHeight(rowHeight);
        scrollableArea.setX(tablePadding);

        // Dedicated clip layer
        clipLayer = new Element(screen, getUID() + ":clipLayer", new Vector2f(tablePadding, tablePadding + headerHeight), getViewPortSize(), Vector4f.ZERO, null);
        clipLayer.setAsContainerOnly();
        clipLayer.setScaleEW(true);
        clipLayer.setScaleNS(true);

        addChild(clipLayer);
    }

    /**
     * Get the size of the visible scrolling area
     *
     * @return viewport size
     */
    public final Vector2f getViewPortSize() {
        return new Vector2f(getWidth() - (tablePadding * 2), getHeight() - (tablePadding * 2) - headerHeight);
    }

    /**
     * Get if the table is sortable.
     *
     * @return sortable
     */
    public boolean getIsSortable() {
        return sortable;
    }

    public void setSortable(boolean sortable) {
        this.sortable = sortable;
        for (TableColumn column : columns) {
            column.setIsEnabled(sortable);
        }
    }

    /**
     * Get the columns.
     *
     * @return columns
     */
    public List<TableColumn> getColumns() {
        return columns;
    }

    /**
     * Sort a column.
     *
     * @param column
     * @param ascending
     */
    public void sort(TableColumn column, boolean ascending) {
        // Sort rows
        final int columnIndex = columns.indexOf(column);
        selectedRows.clear();
        Collections.sort(rows, new Comparator<TableRow>() {
            @Override
            public int compare(TableRow o1, TableRow o2) {
                Element e1 = new ArrayList<Element>(o1.getElements()).get(columnIndex);
                Element e2 = new ArrayList<Element>(o2.getElements()).get(columnIndex);
                if (e1 instanceof Comparable) {
                    return ((Comparable) e1).compareTo((Comparable) e2);
                }
                return e1.toString().compareTo(e2.toString());
            }
        });
        if (!ascending) {
            Collections.reverse(rows);
        }

        // Set header button images
        for (TableColumn tc : columns) {
            if (tc == column) {
                tc.getButtonIcon().setColorMap((ascending) ? arrowDownImg : arrowUpImg);
            } else {
                tc.getButtonIcon().setColorMap(noArrowImg);
            }
        }

        pack();
    }

    /**
     * Get the column resize mode.
     *
     * @return column resize mode
     */
    public ColumnResizeMode getColumnResizeMode() {
        return columnResizeMode;
    }

    /**
     * Set the column resize mode.
     *
     * @param columnResizeMode column resize mode
     */
    public void setColumnResizeMode(ColumnResizeMode columnResizeMode) {
        this.columnResizeMode = columnResizeMode;
        reconfigureHeaders();
        sizeColumns();
        displayHighlights();
    }

    /**
     * Add a new column.
     *
     * @param columnName column name
     */
    public void addColumn(String columnName) {
        TableColumn header = new TableColumn(this, screen, getUID() + ":col");
        header.setText(columnName);
        addColumn(header);
    }

    /**
     * Add a new column control. Using this as opposed the simple string varient allows
     * custom controls to be used for the header.
     *
     * @param column column
     */
    public void addColumn(TableColumn column) {
        column.getButtonIcon().setColorMap(noArrowImg);
        columns.add(column);
        column.setIsEnabled(sortable);
        column.setControlClippingLayer(this);
        addChild(column);
        reconfigureHeaders();
        sizeColumns();
    }

    @Override
    public void controlResizeHook() {
        super.controlResizeHook();
        sizeColumns();
    }

    public void setSelectionMode(SelectionMode selectionMode) {
        this.selectionMode = selectionMode;
        selectedRows.clear();
        selectedCells.clear();
        displayHighlights();
    }

    public SelectionMode getSelectionMode() {
        return selectionMode;
    }

    /**
     * Adds a TableRow to the Table
     *
     * @param row row
     */
    public int addRow(TableRow row) {
        this.getVScrollBar().hide();
        row.setControlClippingLayer(clipLayer);
        this.rows.add(row);
        pack();
        return rows.size() - 1;
    }

    /**
     * Inserts a new row at the provided index
     *
     * @param index The index to insert into
     * @param row The row to insert
     */
    public void insertRow(int index, TableRow row) {
        if (!rows.isEmpty()) {
            if (index >= 0 && index < rows.size()) {
                this.getVScrollBar().hide();
                this.rows.add(index, row);
                pack();
            }
        }
    }

    /**
     * Remove the row at the provided index
     *
     * @param index int
     */
    public void removeRow(int index) {
        selectedCells.remove(index);
        selectedRows.remove(index);
        this.getVScrollBar().hide();
        if (!rows.isEmpty()) {
            if (index >= 0 && index < rows.size()) {
                rows.remove(index);
                pack();
            }
        }
    }

    /**
     * Removes the first row in the Table
     */
    public int removeFirstRow() {
        if (!rows.isEmpty()) {
            removeRow(0);
            return 0;
        } else {
            return -1;
        }
    }

    /**
     * Removes the last TableRow in the Table
     */
    public int removeLastRow() {
        if (!rows.isEmpty()) {
            removeRow(rows.size() - 1);
            return rows.size();
        } else {
            return -1;
        }
    }

    /**
     * Remove all rows.
     */
    public void removeAllRows() {
        rows.clear();
        selectedRows.clear();
        selectedCells.clear();
        pack();
    }

    /**
     * Select an entire column
     *
     * @param column column
     */
    public void setSelectColumn(int column) {
        selectedCells.clear();
        selectedRows.clear();
        for (int i = 0; i < rows.size(); i++) {
            selectedRows.add(i);
            selectedCells.put(i, new ArrayList<Integer>(Arrays.asList(column)));
        }
        displayHighlights();
    }

    /**
     * Sets the current selected row index for single select Table
     *
     * @param index int
     */
    public void setSelectedRowIndex(Integer index) {
        if (index < 0) {
            index = 0;
        } else if (index >= rows.size()) {
            index = rows.size() - 1;
        }
        selectedRows.clear();
        selectedRows.add(index);
        selectedCells.clear();
        displayHighlights();
        onChange();
    }

    /**
     * Sets the current selected row and colum indexes
     *
     * @param index int
     */
    public void setSelectedCellIndexes(Integer rowIndex, Integer... columnIndexes) {
        if (rowIndex < 0) {
            rowIndex = 0;
        } else if (rowIndex >= rows.size()) {
            rowIndex = rows.size() - 1;
        }
        selectedRows.clear();
        selectedCells.clear();
        if (columnIndexes.length > 0) {
            selectedCells.put(rowIndex, new ArrayList(Arrays.asList(columnIndexes)));
            selectedRows.add(rowIndex);
        }
        displayHighlights();
        onChange();
    }

    /**
     * Sets the current list of selected indexes to the specified indexes
     *
     * @param indexes
     */
    public void setSelectedRowIndexes(Integer... indexes) {
        selectedCells.clear();
        for (int i = 0; i < indexes.length; i++) {
            if (!selectedRows.contains(indexes[i])) {
                selectedRows.add(indexes[i]);
            }
        }
        displayHighlights();
        onChange();
    }

    /**
     * Adds specific cells of the specified row to the list of selected indexes
     *
     * @param rowIndex row index
     * @param columnIndex column
     */
    public void addSelectedCellIndexes(Integer rowIndex, Integer... columnIndexes) {
        if (columnIndexes.length == 0) {
            throw new IllegalArgumentException("Must supply at least one column index.");
        }
        List<Integer> selectedColumns = selectedCells.get(rowIndex);
        if (selectedColumns == null) {
            selectedColumns = new ArrayList<Integer>();
            selectedCells.put(rowIndex, selectedColumns);
        }
        for (Integer col : columnIndexes) {
            if (!selectedColumns.contains(col)) {
                selectedColumns.add(col);
            }
        }
        if (!selectedRows.contains(rowIndex) && !selectedColumns.isEmpty()) {
            selectedRows.add(rowIndex);
        }
        displayHighlights();
        onChange();
    }

    /**
     * Adds all cells of the specified row to the list of selected indexes
     *
     * @param row row index
     */
    public void addSelectedRowIndex(Integer row) {
        selectedCells.remove(row);
        if (!selectedRows.contains(row) && row > -1) {
            selectedRows.add(row);
        }
        displayHighlights();
        onChange();
    }

    /**
     * Removes the specified index from the list of selected indexes
     *
     * @param index int
     */
    public void removeSelectedRowIndex(Integer index) {
        selectedCells.remove(index);
        selectedRows.remove(index);
        displayHighlights();
        onChange();
    }

    /**
     * Removes the specified cells from the list of selected indexes
     *
     * @param index int
     */
    public void removeSelectedCellIndexes(Integer rowIndex, Integer... columnIndexes) {
        if (columnIndexes.length == 0) {
            throw new IllegalArgumentException("Must supply at least one column index.");
        }
        List<Integer> selectedColumns = selectedCells.get(rowIndex);
        if (selectedColumns != null) {
            selectedColumns.removeAll(Arrays.asList(columnIndexes));
            if (selectedColumns.isEmpty()) {
                selectedCells.remove(rowIndex);
            }
            if (selectedColumns.isEmpty()) {
                selectedRows.remove(rowIndex);
            }
        } else {
            if (columnIndexes.length == columns.size()) {
                selectedRows.remove(rowIndex);
            }
        }
        displayHighlights();
        onChange();
    }

    /**
     * Get if anything is selected (rows or cells)
     *
     * @return select
     */
    public boolean isAnythingSelected() {
        return !selectedRows.isEmpty();
    }

    /**
     * Returns the first (or only) row in the list of selected indexes
     *
     * @return int
     */
    public int getSelectedRowIndex() {
        if (selectedRows.isEmpty()) {
            return -1;
        } else {
            return selectedRows.get(0);
        }
    }

    /**
     * Get the list of column indexes that are selected for the row.
     *
     * @return List<Integer>
     */
    public List<Integer> getSelectedColumnIndexes(int rowIndex) {
        if (selectedCells.containsKey(rowIndex)) {
            return selectedCells.get(rowIndex);
        } else if (selectedRows.contains(rowIndex)) {
            return getAllColumnIndexes();
        }
        return Collections.emptyList();
    }

    /**
     * Returns the entire list of selected indexes
     *
     * @return List<Integer>
     */
    public List<Integer> getSelectedRowIndexes() {
        return this.selectedRows;
    }

    /**
     * Returns the TableRow at the specified index
     *
     * @param index int
     * @return TableRow
     */
    public TableRow getRow(int index) {
        if (!rows.isEmpty()) {
            if (index >= 0 && index < rows.size()) {
                return rows.get(index);
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    /**
     * Get the co-ordinates of the first selected cell. First element in array is the row,
     * the second is the column.
     * <code>null</code> will be returned if nothing is selected.
     *
     * @return first selected cell
     */
    public int[] getSelectedCell() {
        int r = getSelectedRowIndex();
        if (r == -1) {
            return null;
        }
        List<Integer> cols = getSelectedColumnIndexes(r);
        if (cols.isEmpty()) {
            return null;
        }
        return new int[]{r, cols.get(0)};
    }

    /**
     * Get the co-ordinates of the last selected cell. First element in array is the row,
     * the second is the column.
     * <code>null</code> will be returned if nothing is selected.
     *
     * @return first selected cell
     */
    public int[] getLastSelectedCell() {
        int r = selectedRows.get(selectedRows.size() - 1);
        if (r == -1) {
            return null;
        }
        List<Integer> cols = getSelectedColumnIndexes(r);
        if (cols.isEmpty()) {
            return null;
        }
        return new int[]{r, cols.get(cols.size() - 1)};
    }

    /**
     * Returns a List containing all ListItems corresponding to the list of
     * selectedIndexes
     *
     * @return List<ListItem>
     */
    public List<TableRow> getSelectedRows() {
        List<TableRow> ret = new ArrayList();
        for (Integer i : selectedRows) {
            ret.add(getRow(i));
        }
        return ret;
    }

    public int getRowCount() {
        return rows.size();
    }

    public List<TableRow> getRows() {
        return this.rows;
    }

    public float getRowHeight() {
        return this.rowHeight;
    }

    /**
     * Forces the Table to rebuild all TableRows. This does not need to be called, however
     * it will not effect anything negatively if it is.
     */
    public void pack() {

        scrollableArea.removeAllChildren();
        highlights.clear();
        scrollableArea.setHeight(headerHeight);

        int index = 0;
        float currentHeight = headerHeight;
        float width = getWidth();

        // Get the scrollable height
        for (TableRow mi : rows) {
            mi.setWidth(scrollableArea.getWidth());
            mi.pack();
            currentHeight += mi.getHeight();
        }

        scrollableArea.setWidth(width);
        scrollableArea.setHeight(currentHeight);

        float y = tablePadding;
        for (int i = rows.size() - 1; i >= 0; i--) {
            TableRow mi = rows.get(i);
            mi.setInitialized();
            mi.setPosition(0, y);
            addScrollableChild(mi);
            List<Integer> cells = selectedCells.get(i);
            if (cells != null) {
                for (Integer columnIndex : cells) {
                    TableColumn column = columns.get(columnIndex);
                    Element highlight = createHighlight(index, columnIndex);
                    highlight.setWidth(column.getWidth());
                    highlight.setHeight(rowHeight);
                    highlight.getElementMaterial().setColor("Color", highlightColor);
                    highlight.setClippingLayer(clipLayer);
                    highlight.setPosition(column.getX() - tablePadding, y);
                    scrollableArea.addChild(highlight);
                    highlights.add(highlight);
                }
            } else if (selectedRows.contains(index)) {
                Element highlight = createHighlight(index, 0);
                highlight.setWidth(getWidth() - (tablePadding * 2));
                highlight.setHeight(rowHeight);
                highlight.getElementMaterial().setColor("Color", highlightColor);
                highlight.setClippingLayer(clipLayer);
                highlight.setPosition(0, y);
                scrollableArea.addChild(highlight);
                highlights.add(highlight);
            }
            index++;
            y += mi.getHeight();
        }

        if (getScrollableHeight() > getHeight() - (tablePadding * 2)) {
            scrollToTop();
            setWidth(getWidth());
            getVScrollBar().setX(getWidth() + scrollBarGap);
            getVScrollBar().show();
        }


        scrollToTop();
    }

    @Override
    public float getScrollableHeight() {
        // TODO super.getScrollableHeight() looks like it has a bug? text padding is used even though there is none?
        return scrollableArea.getHeight();
    }

    protected void sizeColumns() {

        float x = tablePadding;
        int tw = (int) (getWidth() - (tablePadding * 2));
        final float y = getHeight() - headerHeight - tablePadding;
        switch (columnResizeMode) {
            case AUTO_ALL:
                int cw = (int) (getWidth() - (tablePadding * 2)) / columns.size();
                for (TableColumn header : columns) {
                    header.setPosition(x, y);
                    header.setDimensions(cw, headerHeight);
                    x += cw;
                }
                break;
            case AUTO_FIRST:
                if (columns.size() > 0) {
                    for (int i = 1; i < columns.size(); i++) {
                        tw -= columns.get(i).getWidth();
                    }
                    TableColumn header = columns.get(0);
                    header.setPosition(x, y);
                    header.setDimensions(tw, headerHeight);
                    x += tw;
                    for (int i = 1; i < columns.size(); i++) {
                        header = columns.get(i);
                        header.setPosition(x, y);
                        x += header.getWidth();
                    }
                }
                break;
            case AUTO_LAST:
                if (columns.size() > 0) {
                    for (int i = 0; i < columns.size() - 1; i++) {
                        final TableColumn header = columns.get(i);
                        header.setPosition(x, y);
                        x += header.getWidth();
                        tw -= header.getWidth();
                    }
                    TableColumn header = columns.get(columns.size() - 1);
                    header.setPosition(x, y);
                    header.setDimensions(tw, headerHeight);
                }
                break;
            case NONE:
                for (TableColumn header : columns) {
                    header.setPosition(x, y);
                    x += header.getWidth();
                }
                break;
        }

        for (TableColumn col : columns) {
            col.getButtonIcon().setX(col.getWidth() - col.getButtonIcon().getWidth() - col.borders.z - col.getTextPadding());
        }

        for (TableRow r : rows) {
            r.pack();
            r.updateClipping();
        }
    }

    private void displayHighlights() {
        for (Element h : highlights) {
            scrollableArea.removeChild(h);
        }
        highlights.clear();
        int index = 0;
        float y = headerHeight - tablePadding;
        for (TableRow mi : rows) {
            List<Integer> cells = selectedCells.get(index);
            if (cells != null) {
                for (Integer columnIndex : cells) {
                    TableColumn column = columns.get(columnIndex);
                    Element highlight = createHighlight(index, columnIndex);
                    highlight.setWidth(column.getWidth());
                    highlight.setHeight(rowHeight);
                    highlight.getElementMaterial().setColor("Color", highlightColor);
                    highlight.setClippingLayer(clipLayer);
                    highlight.setPosition(column.getX() - tablePadding, y);
                    scrollableArea.addChild(highlight);
                    highlights.add(highlight);
                }
            } else if (selectedRows.contains(index)) {
                Element highlight = createHighlight(index, 0);
                highlight.setWidth(getWidth() - (tablePadding * 2));
                highlight.setHeight(rowHeight);
                highlight.getElementMaterial().setColor("Color", highlightColor);
                highlight.setClippingLayer(clipLayer);
                highlight.setPosition(0, y);
                scrollableArea.addChild(highlight);
                highlights.add(highlight);
            }
            y += rowHeight;
            index++;
        }
    }

    private Element createHighlight(int index, int index2) {
        Element highlight = new Element(
                screen,
                getUID() + ":Highlight" + index + ":" + index2,
                new Vector2f(0, 0),
                new Vector2f(rowHeight, rowHeight),
                new Vector4f(0, 0, 0, 0),
                null);
        highlight.setScaleEW(true);
        highlight.setScaleNS(false);
        highlight.setDocking(Docking.SW);
        highlight.setIgnoreMouse(true);

        return highlight;
    }

    @Override
    public void onMouseMove(MouseMotionEvent evt) {
        float x = evt.getX() - getX();
        float y = scrollableArea.getAbsoluteHeight() - headerHeight - evt.getY();

        for (int i = 0; i < columns.size(); i++) {
            TableColumn header = columns.get(i);
            if (x >= header.getX() && x <= header.getX() + header.getWidth()) {
                currentColumnIndex = i;
                break;
            }
        }
        if (currentRowIndex != (int) Math.floor(y / rowHeight)) {
            currentRowIndex = (int) Math.floor(y / rowHeight);
        }
    }

    @Override
    public void onMouseLeftPressed(MouseButtonEvent evt) {
        evt.setConsumed();
    }

    @Override
    public void onMouseLeftReleased(MouseButtonEvent evt) {
        setTabFocus();
        switch (selectionMode) {
            case MULTIPLE_ROWS:
                if (ctrl) {
                    if (!selectedRows.contains(currentRowIndex)) {
                        addSelectedRowIndex(currentRowIndex);
                    } else {
                        removeSelectedRowIndex(currentRowIndex);
                    }
                } else if (shift) {
                    int lastRow = selectedRows.get(selectedRows.size() - 1);
                    if (currentRowIndex > lastRow) {
                        for (int i = lastRow + 1; i <= currentRowIndex; i++) {
                            addSelectedRowIndex(i);
                        }
                    } else {
                        for (int i = lastRow - 1; i >= currentRowIndex; i--) {
                            addSelectedRowIndex(i);
                        }
                    }
                } else {
                    setSelectedRowIndex(currentRowIndex);
                }
                break;
            case ROW:
                if (currentRowIndex >= 0 && currentRowIndex < rows.size()) {
                    setSelectedRowIndex(currentRowIndex);
                } else {
                    selectedRows.clear();
                }
                break;
            case MULTIPLE_CELLS:
                if (ctrl) {
                    if (!getSelectedColumnIndexes(currentRowIndex).contains(currentColumnIndex)) {
                        addSelectedCellIndexes(currentRowIndex, currentColumnIndex);
                    } else {
                        removeSelectedCellIndexes(currentRowIndex, currentColumnIndex);
                    }
                } else if (shift) {
                    int[] lastSel = getLastSelectedCell();
                    int lastRow = lastSel[0];
                    List<Integer> cols = new ArrayList<Integer>(getSelectedColumnIndexes(lastRow));
                    if (currentColumnIndex > lastSel[1]) {
                        for (int i = lastSel[1] + 1; i <= currentColumnIndex; i++) {
                            cols.add(i);
                        }
                    } else if (currentColumnIndex < lastSel[1]) {
                        for (int i = currentColumnIndex; i <= lastSel[1] - 1; i++) {
                            cols.add(i);
                        }
                    }
                    int startRow = Math.min(Math.min(getSelectedRowIndex(), lastRow), currentRowIndex);
                    int endRow = Math.max(Math.max(getSelectedRowIndex(), lastRow), currentRowIndex);
                    for (int i = startRow; i <= endRow; i++) {
                        addSelectedCellIndexes(i, cols.toArray(new Integer[0]));
                    }
                } else {
                    setSelectedCellIndexes(currentRowIndex, currentColumnIndex);
                }
                break;
            case CELL:
                if (currentColumnIndex >= 0 && currentColumnIndex < columns.size()
                        && currentRowIndex >= 0 && currentRowIndex < rows.size()) {
                    setSelectedCellIndexes(currentRowIndex, currentColumnIndex);
                } else {
                    selectedCells.clear();
                }
                break;
        }
        evt.setConsumed();
    }

    /**
     * Select everything
     */
    public void selectAll() {
        selectedCells.clear();
        selectedRows.clear();
        List<Integer> l = new ArrayList();
        for (int i = 0; i < rows.size(); i++) {
            l.add(i);
        }
        selectedRows.addAll(l);
        displayHighlights();
    }

    @Override
    public void onMouseRightPressed(MouseButtonEvent evt) {
        evt.setConsumed();
    }

    @Override
    public void onMouseRightReleased(MouseButtonEvent evt) {
        evt.setConsumed();
    }

    @Override
    public void onKeyPress(KeyInputEvent evt) {
        if (selectionMode.equals(SelectionMode.NONE)) {
            return;
        }

        if (evt.getKeyCode() == KeyInput.KEY_LCONTROL || evt.getKeyCode() == KeyInput.KEY_RCONTROL) {
            ctrl = true;
        } else if (evt.getKeyCode() == KeyInput.KEY_LSHIFT || evt.getKeyCode() == KeyInput.KEY_RSHIFT) {
            shift = true;
        }
        evt.setConsumed();
    }

    @Override
    public void onKeyRelease(KeyInputEvent evt) {
        if (selectionMode.equals(SelectionMode.NONE)) {
            return;
        }

        int newRow = -1;
        if (evt.getKeyCode() == KeyInput.KEY_LCONTROL || evt.getKeyCode() == KeyInput.KEY_RCONTROL) {
            ctrl = false;
        } else if (evt.getKeyCode() == KeyInput.KEY_LSHIFT || evt.getKeyCode() == KeyInput.KEY_RSHIFT) {
            shift = false;
        } else if (evt.getKeyCode() == KeyInput.KEY_A && ctrl && selectionMode.isEnabled()) {
            selectAll();
        } else {
            if (evt.getKeyCode() == KeyInput.KEY_LEFT) {
                newRow = selectLeft(evt);
            } else if (evt.getKeyCode() == KeyInput.KEY_RIGHT) {
                newRow = selectRight(evt);
            } else if (evt.getKeyCode() == KeyInput.KEY_DOWN) {
                newRow = selectDown(evt);
            } else if (evt.getKeyCode() == KeyInput.KEY_UP) {
                newRow = selectUp(evt);
            }

            if (newRow == -1) {
                // Return now se we don't consume
                return;
            }
        }

        // If new row is selected, scroll to it
        if (newRow >= 0 && newRow < getRowCount()) {
            TableRow row = rows.get(newRow);
            final float scrolledAmount = getScrolledAmount();
            final float viewPortHeight = getViewPortSize().y;
            final float maxY = scrolledAmount + viewPortHeight;
            final float rowY = getScrollableHeight() - row.getY() - row.getHeight() - headerHeight + tablePadding;
            final float rowBottom = rowY + row.getHeight();
            if (rowBottom >= maxY) {
                scrollYBy(rowBottom - maxY);
                setScrollThumb();
            } else if (rowY < scrolledAmount) {
                scrollYBy(rowY - scrolledAmount);
                setScrollThumb();
            }
        }

        evt.setConsumed();
    }

    public float getScrolledAmount() {
        return getScrollableHeight() - getHeight() + getScrollableArea().getY() + (tablePadding * 2);
    }

    @Override
    public void setTabFocus() {
        screen.setKeyboardElement(this);
    }

    @Override
    public void resetTabFocus() {
        screen.setKeyboardElement(null);
    }

    @Override
    protected void onAdjustWidthForScroll() {
        super.onAdjustWidthForScroll();
        clipLayer.setDimensions(getViewPortSize());
        displayHighlights();
    }

    protected int selectUp(KeyInputEvent evt) {
        int selRow = getSelectedRowIndex();
        int lastRow = selectedRows.get(selectedRows.size() - 1);
        int newRow = lastRow - 1;
        switch (selectionMode) {
            case ROW:
            case MULTIPLE_ROWS:
                if (shift && selectionMode.equals(SelectionMode.MULTIPLE_ROWS)) {
                    if (selRow >= lastRow) {
                        addSelectedRowIndex(newRow);
                    } else {
                        removeSelectedRowIndex(lastRow);
                    }
                } else {
                    setSelectedRowIndex(newRow);
                }
                break;
            case MULTIPLE_CELLS:
            case CELL:
                final List<Integer> selectedColumnIndexes = getSelectedColumnIndexes(lastRow);
                if (shift && selectionMode.equals(SelectionMode.MULTIPLE_CELLS)) {
                    if (selRow >= lastRow) {
                        addSelectedCellIndexes(newRow, selectedColumnIndexes.toArray(new Integer[0]));
                    } else {
                        removeSelectedCellIndexes(lastRow, selectedColumnIndexes.toArray(new Integer[0]));
                    }
                } else {
                    setSelectedCellIndexes(newRow, selectedColumnIndexes.toArray(new Integer[0]));
                }
                break;
        }
        return newRow;
    }

    protected int selectDown(KeyInputEvent evt) {
        int newRow = -1;
        switch (selectionMode) {
            case ROW:
            case MULTIPLE_ROWS:
                int selRow = getSelectedRowIndex();
                int lastRow = selectedRows.get(selectedRows.size() - 1);
                newRow = lastRow + 1;
                if (shift && selectionMode.equals(SelectionMode.MULTIPLE_ROWS)) {
                    if (lastRow >= selRow) {
                        addSelectedRowIndex(newRow);
                    } else {
                        if (selRow > lastRow) {
                            removeSelectedRowIndex(lastRow);
                        } else {
                            removeSelectedRowIndex(selRow);
                        }
                    }
                } else {
                    setSelectedRowIndex(newRow);
                }
                break;
            case MULTIPLE_CELLS:
            case CELL:
                lastRow = selectedRows.get(selectedRows.size() - 1);
                final List<Integer> selectedColumnIndexes = getSelectedColumnIndexes(lastRow);
                if (shift && selectionMode.equals(SelectionMode.MULTIPLE_CELLS)) {
                    selRow = getSelectedRowIndex();
                    if (lastRow >= selRow) {
                        newRow = lastRow + 1;
                        addSelectedCellIndexes(newRow, selectedColumnIndexes.toArray(new Integer[0]));
                    } else {
                        if (selRow > lastRow) {
                            removeSelectedCellIndexes(lastRow, selectedColumnIndexes.toArray(new Integer[0]));
                        } else {
                            removeSelectedCellIndexes(selRow, selectedColumnIndexes.toArray(new Integer[0]));
                        }
                    }
                } else {
                    newRow = lastRow + 1;
                    setSelectedCellIndexes(newRow, selectedColumnIndexes.toArray(new Integer[0]));
                }
                break;
        }
        return newRow;
    }

    protected int selectRight(KeyInputEvent evt) {
        int newRow = -1;
        switch (selectionMode) {
            case ROW:
            case MULTIPLE_ROWS:
                // Return now se we don't consume
                return newRow;
            case CELL:
            case MULTIPLE_CELLS:
                if (isAnythingSelected()) {

                    int[] sel = getSelectedCell();
                    int[] lastSel = getLastSelectedCell();
                    newRow = sel[0];
                    if (sel[1] > lastSel[1]) {
                        for (int r : getSelectedRowIndexes()) {
                            removeSelectedCellIndexes(r, lastSel[1]);
                        }
                    } else {
                        int col = lastSel[1];
                        col++;
                        if (selectionMode.equals(SelectionMode.CELL) || !shift) {
                            if (col >= columns.size()) {
                                col = 0;
                                newRow++;
                            }
                            if (newRow >= rows.size()) {
                                newRow = rows.size() - 1;
                                col = 0;
                            }
                        } else {
                            if (col >= columns.size()) {
                                col = columns.size() - 1;
                            }
                        }
                        if (shift && selectionMode.equals(SelectionMode.MULTIPLE_CELLS)) {
                            for (int r : getSelectedRowIndexes()) {
                                addSelectedCellIndexes(r, col);
                            }
                        } else {
                            setSelectedCellIndexes(newRow, col);
                        }
                    }
                } else if (getRowCount() > 0) {
                    newRow = 0;
                    setSelectedCellIndexes(0, 0);
                }
                break;
        }
        return newRow;
    }

    protected int selectLeft(KeyInputEvent evt) {
        int newRow = -1;
        switch (selectionMode) {
            case ROW:
            case MULTIPLE_ROWS:
                // Return now se we don't consume
                return newRow;
            case MULTIPLE_CELLS:
            case CELL:
                if (isAnythingSelected()) {
                    int[] sel = getSelectedCell();
                    int[] lastSel = getLastSelectedCell();
                    newRow = sel[0];

                    // Work out which side of the selection we adjust
                    if (sel[1] < lastSel[1]) {
                        for (int r : getSelectedRowIndexes()) {
                            removeSelectedCellIndexes(r, lastSel[1]);
                        }
                    } else {
                        int col = lastSel[1];
                        col--;
                        if (selectionMode.equals(SelectionMode.CELL) || !shift) {
                            if (col < 0) {
                                col = columns.size() - 1;
                                newRow--;
                            }
                            if (newRow < 0) {
                                newRow = 0;
                                col = 0;
                            }
                        } else {
                            if (col < 0) {
                                col = 0;
                            }
                        }
                        if (shift && selectionMode.equals(SelectionMode.MULTIPLE_CELLS)) {
                            for (int r : getSelectedRowIndexes()) {
                                addSelectedCellIndexes(r, col);
                            }
                        } else {
                            setSelectedCellIndexes(newRow, col);
                        }
                    }


                } else if (getRowCount() > 0) {
                    newRow = 0;
                    setSelectedCellIndexes(0, 0);
                }
                break;
        }
        return newRow;
    }

    public abstract void onChange();

    public static class TableRow extends Element {

        private Table table;

        public TableRow(ElementManager screen, Table table) {
            this(screen, table, UIDUtil.getUID());
        }

        public TableRow(ElementManager screen, Table table, String UID) {
            super(screen, UID, Vector2f.ZERO, screen.getStyle("Table#Row").getVector2f("defaultSize"), screen.getStyle("Table#Row").getVector4f("resizeBorders"), screen.getStyle("Table#Row").getString("defaultImg"));
            init(table);
        }

        public TableRow(ElementManager screen, Table table, String UID, Vector2f dimensions, Vector4f resizeBorders, String texturePath) {
            super(screen, UID, Vector2f.ZERO, dimensions, resizeBorders, texturePath);
            init(table);
        }

        private void init(Table table) {
            this.table = table;
            setIgnoreMouse(true);
            setDocking(null);
            setScaleEW(false);
            setScaleNS(false);

        }

        public TableCell addCell(String label, Object value) {
            final TableCell tableCell = new TableCell(screen, label, value);
            addChild(tableCell);
            return tableCell;
        }

        public void pack() {
            Iterator<Element> el = getElements().iterator();
            float x = 0;
            setHeight(0);
            for (TableColumn header : table.columns) {
                Element cell = el.next();
                final float width = header.getWidth();
                cell.setX(x);
                cell.setWidth(width);
                x += width;
                setHeight(Math.max(getHeight(), cell.getHeight()));
            }
        }
    }

    public void scrollToRow(int rIndex) {
        float diff = (rIndex + 1) * getRowHeight();

        float y = -(getScrollableHeight() - diff);

        if (FastMath.abs(y) > getScrollableHeight()) {
            y = getScrollableHeight();
        }
        scrollThumbYTo(y);
    }

    public void scrollToSelected() {
        scrollToRow(getSelectedRowIndex());
    }

    private void reconfigureHeaders() {
        for (TableColumn header : columns) {
            header.reconfigure();
        }
    }

    private void setScrollThumb() {
        /* All this is to update the scroll thumb to the current scroll position. 
         Im sure something like this would be better*/
        final float trackLength = getVScrollBar().getScrollTrack().getHeight();
        float scale = (getScrollableHeight()) / (trackLength);
        float diff = getScrolledAmount();
        float thumbHeight = trackLength - getVScrollBar().getScrollThumb().getHeight();
        final int y = (int) (diff / scale);
        getVScrollBar().getScrollThumb().setY(thumbHeight - y);
    }

    private List<Integer> getAllColumnIndexes() {
        List<Integer> l = new ArrayList();
        for (int i = 0; i < columns.size(); i++) {
            l.add(i);
        }
        return l;
    }
}
