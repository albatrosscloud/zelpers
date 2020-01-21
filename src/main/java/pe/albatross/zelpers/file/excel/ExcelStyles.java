package pe.albatross.zelpers.file.excel;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.PatternFormatting;
import org.apache.poi.ss.usermodel.Workbook;

public class ExcelStyles {

    public static CellStyle getStyleHeader(Workbook workBook) {

        Font fontTitle = workBook.createFont();
        fontTitle.setFontName("Arial");
        fontTitle.setBoldweight(Font.BOLDWEIGHT_BOLD);
        fontTitle.setColor(IndexedColors.WHITE.getIndex());

        CellStyle cellHeader = workBook.createCellStyle();
        cellHeader.setFillForegroundColor(IndexedColors.BLACK.getIndex());
        cellHeader.setFillPattern(CellStyle.SOLID_FOREGROUND);
        cellHeader.setAlignment(CellStyle.ALIGN_CENTER);
        cellHeader.setFont(fontTitle);

        return cellHeader;

    }

    public static CellStyle getStyleBody(Workbook workBook) {

        Font fontBody = workBook.createFont();
        fontBody.setFontName("Arial");
        fontBody.setColor(IndexedColors.BLACK.getIndex());

        CellStyle cellBody = workBook.createCellStyle();
        cellBody.setFont(fontBody);
        cellBody.setWrapText(true);
        cellBody.setVerticalAlignment(CellStyle.VERTICAL_CENTER);

        return cellBody;
    }

    public static CellStyle getStyleCellHeaderGrey(Workbook workBook) {
        Font font = workBook.createFont();
        font.setFontName("Arial");
        font.setBoldweight(Font.BOLDWEIGHT_BOLD);
        font.setColor(HSSFColor.WHITE.index);

        CellStyle cell = workBook.createCellStyle();
        cell.setAlignment(CellStyle.ALIGN_CENTER);
        cell.setFont(font);
        cell.setBorderTop(HSSFCellStyle.BORDER_THIN);
        cell.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        cell.setBorderRight(HSSFCellStyle.BORDER_THIN);
        cell.setBorderLeft(HSSFCellStyle.BORDER_THIN);

        cell.setFillForegroundColor(IndexedColors.GREY_50_PERCENT.getIndex());
        cell.setFillPattern(PatternFormatting.SOLID_FOREGROUND);
        return cell;
    }

    public static CellStyle getCellTitle1Green(Workbook workBook) {
        Font font = workBook.createFont();
        font.setFontName("Arial");
        font.setBoldweight(Font.BOLDWEIGHT_BOLD);
        font.setColor(HSSFColor.GREEN.index);
        font.setFontHeightInPoints((short) 15);

        CellStyle cell = workBook.createCellStyle();
        cell.setAlignment(CellStyle.ALIGN_CENTER);
        cell.setFont(font);
        cell.setBorderTop(HSSFCellStyle.BORDER_NONE);
        cell.setBorderBottom(HSSFCellStyle.BORDER_NONE);
        cell.setBorderRight(HSSFCellStyle.BORDER_NONE);
        cell.setBorderLeft(HSSFCellStyle.BORDER_NONE);

        return cell;
    }

    public static CellStyle getCellTitle2Green(Workbook workBook) {
        Font font = workBook.createFont();
        font.setFontName("Arial");
        font.setBoldweight(Font.BOLDWEIGHT_BOLD);
        font.setColor(HSSFColor.GREEN.index);
        font.setFontHeightInPoints((short) 12);

        CellStyle cell = workBook.createCellStyle();
        cell.setAlignment(CellStyle.ALIGN_CENTER);
        cell.setFont(font);
        cell.setBorderTop(HSSFCellStyle.BORDER_NONE);
        cell.setBorderBottom(HSSFCellStyle.BORDER_NONE);
        cell.setBorderRight(HSSFCellStyle.BORDER_NONE);
        cell.setBorderLeft(HSSFCellStyle.BORDER_NONE);

        return cell;
    }

    public static CellStyle getCellTitle3Green(Workbook workBook) {
        Font font = workBook.createFont();
        font.setFontName("Arial");
        font.setBoldweight(Font.BOLDWEIGHT_BOLD);
        font.setColor(HSSFColor.GREEN.index);
        font.setFontHeightInPoints((short) 11);

        CellStyle cell = workBook.createCellStyle();
        cell.setAlignment(CellStyle.ALIGN_CENTER);
        cell.setFont(font);
        cell.setBorderTop(HSSFCellStyle.BORDER_NONE);
        cell.setBorderBottom(HSSFCellStyle.BORDER_NONE);
        cell.setBorderRight(HSSFCellStyle.BORDER_NONE);
        cell.setBorderLeft(HSSFCellStyle.BORDER_NONE);

        return cell;
    }

}
