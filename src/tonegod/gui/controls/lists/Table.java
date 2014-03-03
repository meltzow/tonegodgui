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
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
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

    public enum ColumnResizeMode {

        NONE, AUTO_ALL, AUTO_FIRST, AUTO_LAST;
    }
    private List<TableRow> rows = new ArrayList();
    private List<Integer> selectedIndexes = new ArrayList();
    private List<Element> highlights = new ArrayList();
    private boolean isMultiselect = false;
    private float initWidth;
    private float listPadding = 1;
    private ColorRGBA highlightColor;
    protected int currentListItemIndex = -1;
    protected int currentColumnIndex = -1;
    private boolean shift = false, ctrl = false;
    private final List<TableColumn> columns = new ArrayList<TableColumn>();
    private final float headerHeight;
    private final float rowHeight;
    private Element clipLayer;
    private ColumnResizeMode columnResizeMode = ColumnResizeMode.NONE;

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
            super.controlResizeHook();
            table.sizeColumns();
        }

        @Override
        public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
            super.onButtonMouseLeftUp(evt, toggled);
            if (sort == null) {
                sort = true;
            } else {
                sort = !sort;
            }
            table.sort(this, sort);

        }

        private void init(Table table) {
            this.table = table;
            setResizeN(false);
            setResizeS(false);
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

        listPadding = screen.getStyle("Table").getFloat("tablePadding");
        headerHeight = screen.getStyle("Table#Header").getVector2f("defaultSize").y;
        highlightColor = screen.getStyle("Table").getColorRGBA("highlightColor");
        // Load default font info
        setFontColor(screen.getStyle("Table").getColorRGBA("fontColor"));
        setFontSize(screen.getStyle("Table").getFloat("fontSize"));
        setTextAlign(BitmapFont.Align.valueOf(screen.getStyle("Table").getString("textAlign")));
        setTextVAlign(BitmapFont.VAlign.valueOf(screen.getStyle("Table").getString("textVAlign")));
        setTextWrap(LineWrapMode.valueOf(screen.getStyle("Table").getString("textWrap")));
        setTextPadding(screen.getStyle("Table").getFloat("textPadding"));
        setTextClipPadding(screen.getStyle("Table").getFloat("textPadding"));
        scrollableArea.setScaleEW(false);
        scrollableArea.setScaleNS(false);
        

        rowHeight = screen.getStyle("Table#Header").getVector2f("defaultSize").y;

        scrollableArea.setText(" ");
        scrollableArea.setIgnoreMouse(true);
        scrollableArea.setHeight(rowHeight);
        scrollableArea.setY(headerHeight);

        initWidth = rowHeight * 3;

        // Dedicated clip layer
        clipLayer = new Element(screen, getUID() + ":clipLayer", new Vector2f(listPadding, listPadding + headerHeight), new Vector2f(getWidth() - (listPadding * 2), getHeight() - (listPadding * 2) - headerHeight), Vector4f.ZERO, null);
        clipLayer.setAsContainerOnly();
        clipLayer.setScaleEW(true);
        clipLayer.setScaleNS(true);
        addChild(clipLayer);
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
     * @param column
     * @param ascending 
     */
    public void sort(TableColumn column, boolean ascending) {
        final int columnIndex = columns.indexOf(column);
        selectedIndexes.clear();
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
        pack();
    }

    /**
     * Get the column resize mode.
     * 
     * @return  column resize mode
     */
    public ColumnResizeMode getColumnResizeMode() {
        return columnResizeMode;
    }

    /**
     * Set the column resize mode.
     * 
     * @param columnResizeMode  column resize mode
     */
    public void setColumnResizeMode(ColumnResizeMode columnResizeMode) {
        this.columnResizeMode = columnResizeMode;
        reconfigureHeaders();
        sizeColumns();
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
     * Add a new column control. Using this as opposed the simple string varient
     * allows custom controls to be used for the header.
     * 
     * @param column column 
     */
    public void addColumn(TableColumn column) {
        columns.add(column);
        addChild(column);
        reconfigureHeaders();
        sizeColumns();
    }

    @Override
    public void controlResizeHook() {
        super.controlResizeHook();
        sizeColumns();
    }

    public void setIsMultiselect(boolean isMultiselect) {
        this.isMultiselect = isMultiselect;
    }

    public boolean getIsMultiselect() {
        return this.isMultiselect;
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
        this.rows = new ArrayList();
        this.selectedIndexes = new ArrayList();
        pack();
    }

    /**
     * Sets the current selected index for single select Table
     *
     * @param index int
     */
    public void setSelectedRowIndex(Integer index) {
        if (index < 0) {
            index = 0;
        } else if (index >= rows.size()) {
            index = rows.size() - 1;
        }
        selectedIndexes = new ArrayList();
        selectedIndexes.add(index);
        displayHighlights();
        onChange();
    }

    /**
     * Sets the current list of selected indexes to the specified indexes
     *
     * @param indexes
     */
    public void setSelectedIndexes(Integer... indexes) {
        for (int i = 0; i < indexes.length; i++) {
            if (!selectedIndexes.contains(indexes[i])) {
                selectedIndexes.add(indexes[i]);
            }
        }
        displayHighlights();
        onChange();
    }

    /**
     * Adds the specified index to the list of selected indexes
     *
     * @param index int
     */
    public void addSelectedIndex(Integer index) {
        if (!selectedIndexes.contains(index)) {
            selectedIndexes.add(index);
        }
        displayHighlights();
        onChange();
    }

    /**
     * Removes the specified index from the list of selected indexes
     *
     * @param index int
     */
    public void removeSelectedIndex(Integer index) {
        selectedIndexes.remove(index);
        displayHighlights();
        onChange();
    }

    /**
     * Returns the first (or only) index in the list of selected indexes
     *
     * @return int
     */
    public int getSelectedIndex() {
        if (selectedIndexes.isEmpty()) {
            return -1;
        } else {
            return selectedIndexes.get(0);
        }
    }

    /**
     * Returns the entire list of selected indexes
     *
     * @return List<Integer>
     */
    public List<Integer> getSelectedIndexes() {
        return this.selectedIndexes;
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
     * Returns a List containing all ListItems corresponding to the list of
     * selectedIndexes
     *
     * @return List<ListItem>
     */
    public List<TableRow> getSelectedRows() {
        List<TableRow> ret = new ArrayList();
        for (Integer i : selectedIndexes) {
            ret.add(getRow(i));
        }
        return ret;
    }

    public List<TableRow> getRows() {
        return this.rows;
    }

    public float getRowHeight() {
        return this.rowHeight;
    }

    /**
     * Forces the Table to rebuild all TableRows. This does not need to be called,
     * however it will not effect anything negatively if it is.
     */
    public void pack() {

        scrollableArea.removeAllChildren();
        highlights.clear();
        scrollableArea.setHeight(headerHeight);

        int index = 0;
        float currentHeight = 0;
        float width = rowHeight * 3;


        // Get the scrollable height
        scrollableArea.setWidth(((getWidth() > width) ? getWidth() : width) - (listPadding * 2));
        for (TableRow mi : rows) {
            mi.setWidth(scrollableArea.getWidth());
            mi.pack();
            currentHeight += mi.getHeight();
        }

        scrollableArea.setHeight(currentHeight);
        scrollableArea.setY(listPadding + headerHeight);


        float y = currentHeight - listPadding - headerHeight;
        for (TableRow mi : rows) {
        //    System.err.println();
            mi.setInitialized();
            mi.setPosition(listPadding, y);
         //   System.err.println("       " + y);
            addScrollableChild(mi);

            if (selectedIndexes.contains(index)) {
                Element highlight = createHighlight(index);
                highlight.setWidth(getWidth() - (listPadding * 2));
                highlight.setHeight(rowHeight);
                highlight.getElementMaterial().setColor("Color", highlightColor);
                highlight.setClippingLayer(clipLayer);
                highlight.setClipPadding(listPadding);
                highlight.setPosition(listPadding, y);
                scrollableArea.addChild(highlight);
                highlights.add(highlight);
            }
            index++;
            y -= mi.getHeight();
        }

        if (getScrollableHeight() > getHeight() - (listPadding * 2)) {
            scrollToTop();
            setWidth(getWidth());
            getVScrollBar().setX(getWidth());
            getVScrollBar().show();
        }


        scrollToTop();
    }

    protected void sizeColumns() {

        float x = listPadding;
        int tw = (int) (getWidth() - (listPadding * 2));
        final float y = getHeight() - headerHeight - listPadding;
        switch (columnResizeMode) {
            case AUTO_ALL:
                int cw = (int) (getWidth() - (listPadding * 2)) / columns.size();
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
        float currentHeight = 0;
        for (TableRow mi : rows) {
            if (selectedIndexes.contains(index)) {
                Element highlight = createHighlight(index);
                highlight.setWidth(getWidth() - (listPadding * 2));
                highlight.setHeight(rowHeight);
                highlight.getElementMaterial().setColor("Color", highlightColor);
                highlight.setClippingLayer(clipLayer);
                highlight.setPosition(listPadding, scrollableArea.getHeight() - ((rows.size() - index) * rowHeight) + listPadding);
                scrollableArea.addChild(highlight);
                highlights.add(highlight);
            }
            currentHeight += rowHeight;
            index++;
        }
    }

    private Element createHighlight(int index) {
        Element highlight = new Element(
                screen,
                getUID() + ":Highlight" + index,
                new Vector2f(0, 0),
                new Vector2f(listPadding, listPadding),
                new Vector4f(1, 1, 1, 1),
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
        float y = scrollableArea.getAbsoluteHeight() - listPadding - evt.getY();

        for(int i = 0 ; i < columns.size() ; i++) {
            TableColumn header = columns.get(i);
            if(x >= header.getX() && x <= header.getX() + header.getWidth()) {
                currentColumnIndex = i;
                break;
            }
        }
        if (currentListItemIndex != (int) Math.floor(y / rowHeight)) {
            currentListItemIndex = (int) Math.floor(y / rowHeight);
        }
    }

    @Override
    public void onMouseLeftPressed(MouseButtonEvent evt) {
        evt.setConsumed();
    }

    @Override
    public void onMouseLeftReleased(MouseButtonEvent evt) {
        if (isMultiselect) {
            if (shift || ctrl) {
                if (!selectedIndexes.contains(currentListItemIndex)) {
                    addSelectedIndex(currentListItemIndex);
                } else {
                    removeSelectedIndex(currentListItemIndex);
                }
            } else {
                setSelectedRowIndex(currentListItemIndex);
            }
        } else {
            if (currentListItemIndex >= 0 && currentListItemIndex < rows.size()) {
                setSelectedRowIndex(currentListItemIndex);
            } else {
                selectedIndexes = new ArrayList();
            }
        }
        evt.setConsumed();
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
        if (evt.getKeyCode() == KeyInput.KEY_LCONTROL || evt.getKeyCode() == KeyInput.KEY_RCONTROL) {
            ctrl = true;
        } else if (evt.getKeyCode() == KeyInput.KEY_LSHIFT || evt.getKeyCode() == KeyInput.KEY_RSHIFT) {
            shift = true;
        }
    }

    @Override
    public void onKeyRelease(KeyInputEvent evt) {
        if (evt.getKeyCode() == KeyInput.KEY_LCONTROL || evt.getKeyCode() == KeyInput.KEY_RCONTROL) {
            ctrl = false;
        } else if (evt.getKeyCode() == KeyInput.KEY_LSHIFT || evt.getKeyCode() == KeyInput.KEY_RSHIFT) {
            shift = false;
        }
    }

    @Override
    public void setTabFocus() {
        screen.setKeyboardElement(this);
    }

    @Override
    public void resetTabFocus() {
        screen.setKeyboardElement(null);
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

    public void scrollToSelected() {
        int rIndex = getSelectedIndex();
        float diff = (rIndex + 1) * getRowHeight();

        float y = -(getScrollableHeight() - diff);

        if (FastMath.abs(y) > getScrollableHeight()) {
            y = getScrollableHeight();
        }

        scrollThumbYTo(y);
    }

    private void reconfigureHeaders() {
        for (TableColumn header : columns) {
            header.reconfigure();
        }
    }
}
