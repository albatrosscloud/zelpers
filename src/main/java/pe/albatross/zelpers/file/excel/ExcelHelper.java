package pe.albatross.zelpers.file.excel;

import java.math.BigDecimal;
import java.util.Date;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;

public class ExcelHelper {

    private Sheet sheet;
    private Workbook workBook;

    public ExcelHelper() {
    }

    public ExcelHelper(Sheet sheet, Workbook workBook) {
        this.sheet = sheet;
        this.workBook = workBook;
    }

    public static Cell findCell(Sheet sheet, int nroRow, int nroCell) {
        Row row = sheet.getRow(nroRow);
        if (row == null) {
            row = sheet.createRow(nroRow);
        }

        Cell cell = row.getCell(nroCell);
        if (cell == null) {
            cell = row.createCell(nroCell);
        }

        return cell;
    }

    public static void formatCell(Sheet sheet, int nroRow, int nroCell, String formato) {
        Cell cell = findCell(sheet, nroRow, nroCell);
        CellStyle cellStyle = cell.getCellStyle();
        cellStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat(formato));
    }

    public static void replaceVal(Sheet sheet, int nroRow, int nroCell, Date valor) {
        Cell cell = findCell(sheet, nroRow, nroCell);
        if (valor == null) {
            cell.setCellType(Cell.CELL_TYPE_BLANK);
            return;
        } else {
            cell.setCellValue(valor);
        }

        CellStyle cellStyle = cell.getCellStyle();
        cellStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("dd/mm/yyyy"));
    }

    public static void replaceVal(Sheet sheet, int nroRow, int nroCell, Integer valor) {
        Cell cell = findCell(sheet, nroRow, nroCell);
        if (valor == null) {
            cell.setCellType(Cell.CELL_TYPE_BLANK);
        } else {
            cell.setCellValue(valor);
        }
    }

    public static void replaceVal(Sheet sheet, int nroRow, int nroCell, Long valor) {
        Cell cell = findCell(sheet, nroRow, nroCell);
        if (valor == null) {
            cell.setCellType(Cell.CELL_TYPE_BLANK);
        } else {
            cell.setCellValue(valor);
        }
    }

    public static void replaceVal(Sheet sheet, int nroRow, int nroCell, String valor) {
        Cell cell = findCell(sheet, nroRow, nroCell);
        if (valor == null) {
            cell.setCellType(Cell.CELL_TYPE_BLANK);
        } else {
            cell.setCellType(Cell.CELL_TYPE_STRING);
            cell.setCellValue(valor);
        }
    }

    public static void replaceVal(Sheet sheet, int nroRow, int nroCell, Double valor) {
        Cell cell = findCell(sheet, nroRow, nroCell);
        if (valor == null) {
            cell.setCellType(Cell.CELL_TYPE_BLANK);
        } else {
            cell.setCellValue(valor);
        }
    }

    public static void replaceVal(Sheet sheet, int nroRow, int nroCell, BigDecimal valor) {
        Cell cell = findCell(sheet, nroRow, nroCell);
        if (valor == null) {
            cell.setCellType(Cell.CELL_TYPE_BLANK);
        } else {
            cell.setCellValue(valor.doubleValue());
        }
    }

    public Cell findCell(int nroRow, int nroCell) {
        Row row = sheet.getRow(nroRow);
        if (row == null) {
            row = sheet.createRow(nroRow);
        }

        Cell cell = row.getCell(nroCell);
        if (cell == null) {
            cell = row.createCell(nroCell);
        }

        return cell;
    }

    public CellStyle getCellStyle(int nroRow, int nroCell) {
        Cell cell = findCell(nroRow, nroCell);
        CellStyle cellStyle = cell.getCellStyle();

        CellStyle newCellStyle = workBook.createCellStyle();
        newCellStyle.cloneStyleFrom(cellStyle);
        return newCellStyle;
    }

    public void replaceStyle(int nroRow, int nroCell, CellStyle style) {
        Cell cell = findCell(nroRow, nroCell);
        cell.setCellStyle(style);
    }

    public void replaceVal(int nroRow, int nroCell, Date valor) {
        Cell cell = findCell(nroRow, nroCell);
        cell.setCellValue(valor);
    }

    public void replaceVal(int nroRow, int nroCell, Date valor, String formato, CellStyle cellStyle) {
        Cell cell = findCell(nroRow, nroCell);
        if (valor != null) {
            replaceVal(nroRow, nroCell, valor);
        }

        cell.setCellStyle(cellStyle);
        DataFormat df = workBook.createDataFormat();
        cellStyle.setDataFormat(df.getFormat(formato));
        cell.setCellStyle(cellStyle);
    }

    public void replaceVal(int nroRow, int nroCell, Date valor, String formato) {
        Cell cell = findCell(nroRow, nroCell);
        if (valor != null) {
            replaceVal(nroRow, nroCell, valor);
        }

        CellStyle cellStyle = getCellStyle(nroRow, nroCell);
        DataFormat df = workBook.createDataFormat();
        cellStyle.setDataFormat(df.getFormat(formato));
        cell.setCellStyle(cellStyle);
    }

    public void replaceVal(int nroRow, int nroCell, Date valor, CellStyle cellStyle) {
        Cell cell = findCell(nroRow, nroCell);
        if (valor != null) {
            replaceVal(nroRow, nroCell, valor);
        }
        cell.setCellStyle(cellStyle);
    }

    public void replaceVal(int nroRow, int nroCell, Integer valor) {
        Cell cell = findCell(nroRow, nroCell);
        if (valor == null) {
            cell.setCellType(Cell.CELL_TYPE_BLANK);
        } else {
            cell.setCellValue(valor);
        }
    }

    public void replaceVal(int nroRow, int nroCell, Integer valor, CellStyle cellStyle) {
        Cell cell = findCell(nroRow, nroCell);
        if (valor == null) {
            cell.setCellType(Cell.CELL_TYPE_BLANK);
        } else {
            cell.setCellValue(valor);
        }
        cell.setCellStyle(cellStyle);
    }

    public void replaceVal(int nroRow, int nroCell, Long valor) {
        Cell cell = findCell(nroRow, nroCell);
        if (valor == null) {
            cell.setCellType(Cell.CELL_TYPE_BLANK);
        } else {
            cell.setCellValue(valor);
        }
    }

    public void replaceVal(int nroRow, int nroCell, Long valor, CellStyle cellStyle) {
        Cell cell = findCell(nroRow, nroCell);
        if (valor == null) {
            cell.setCellType(Cell.CELL_TYPE_BLANK);
        } else {
            cell.setCellValue(valor);
        }
        cell.setCellStyle(cellStyle);
    }

    public void replaceVal(int nroRow, int nroCell, String valor) {
        Cell cell = findCell(nroRow, nroCell);
        if (valor == null) {
            cell.setCellType(Cell.CELL_TYPE_BLANK);
        } else {
            cell.setCellType(Cell.CELL_TYPE_STRING);
            cell.setCellValue(valor);
        }
        CellStyle cellStyle = getCellStyle(nroRow, nroCell);
        cellStyle.setWrapText(true);
        cell.setCellStyle(cellStyle);
    }

    public void replaceVal(int nroRow, int nroCell, String valor, String formato) {
        Cell cell = findCell(nroRow, nroCell);
        replaceVal(nroRow, nroCell, valor);

        CellStyle cellStyle = getCellStyle(nroRow, nroCell);
        DataFormat df = workBook.createDataFormat();
        cellStyle.setDataFormat(df.getFormat(formato));
        cell.setCellStyle(cellStyle);
    }

    public void replaceVal(int nroRow, int nroCell, String valor, CellStyle cellStyle) {
        Cell cell = findCell(nroRow, nroCell);
        replaceVal(nroRow, nroCell, valor);
        cell.setCellStyle(cellStyle);
    }

    public void replaceVal(int nroRow, int nroCell, Double valor) {
        Cell cell = findCell(nroRow, nroCell);
        cell.setCellValue(valor);
    }

    public void replaceVal(int nroRow, int nroCell, BigDecimal valor) {
        Cell cell = findCell(nroRow, nroCell);
        if (valor == null) {
            cell.setCellType(Cell.CELL_TYPE_BLANK);
        } else {
            cell.setCellValue(valor.doubleValue());
        }
    }

    public void replaceVal(int nroRow, int nroCell, BigDecimal valor, String formato) {
        Cell cell = findCell(nroRow, nroCell);
        replaceVal(nroRow, nroCell, valor);

        CellStyle cellStyle = getCellStyle(nroRow, nroCell);
        DataFormat df = workBook.createDataFormat();
        cellStyle.setDataFormat(df.getFormat(formato));
        cell.setCellType(Cell.CELL_TYPE_NUMERIC);
        cell.setCellStyle(cellStyle);
    }

    public void replaceVal(int nroRow, int nroCell, BigDecimal valor, CellStyle cellStyle) {
        Cell cell = findCell(nroRow, nroCell);
        replaceVal(nroRow, nroCell, valor);
        cell.setCellType(Cell.CELL_TYPE_NUMERIC);
        cell.setCellStyle(cellStyle);
    }

    public static void mergeCell(Sheet sheet, int firstRow, int lastRow, int firstCol, int lastCol) {

        sheet.addMergedRegion(new CellRangeAddress(firstRow, lastRow, firstCol, lastCol));
    }

    public static void createCell(Row row, int cellNumber, String value, CellStyle style) {
        Cell cell = row.createCell(cellNumber);
        cell.setCellValue(value + "");
        if (style != null) {
            cell.setCellStyle(style);
        }
    }

    public Sheet getSheet() {
        return sheet;
    }

    public void setSheet(Sheet sheet) {
        this.sheet = sheet;
    }

    public Workbook getWorkBook() {
        return workBook;
    }

    public void setWorkBook(Workbook workBook) {
        this.workBook = workBook;
    }

}
