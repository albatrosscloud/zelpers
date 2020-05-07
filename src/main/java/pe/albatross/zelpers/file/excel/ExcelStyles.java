package pe.albatross.zelpers.file.excel;

import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.hssf.util.HSSFColor.HSSFColorPredefined;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;

public class ExcelStyles {

    public static CellStyle getStyleHeader(Workbook workBook) {

        Font fontTitle = workBook.createFont();
        fontTitle.setFontName("Arial");
        fontTitle.setBold(true);
        fontTitle.setColor(IndexedColors.WHITE.getIndex());

        CellStyle cellHeader = workBook.createCellStyle();
        cellHeader.setFillForegroundColor(IndexedColors.BLACK.getIndex());
        cellHeader.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        cellHeader.setAlignment(HorizontalAlignment.CENTER);
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
        cellBody.setVerticalAlignment(VerticalAlignment.CENTER);

        return cellBody;
    }

    public static CellStyle getStyleCellHeaderGrey(Workbook workBook) {
        Font font = workBook.createFont();
        font.setFontName("Arial");
        font.setBold(true);
        font.setColor(HSSFColorPredefined.WHITE.getIndex());

        CellStyle cell = workBook.createCellStyle();
        cell.setAlignment(HorizontalAlignment.CENTER);
        cell.setFont(font);
        cell.setBorderTop(BorderStyle.THIN);
        cell.setBorderBottom(BorderStyle.THIN);
        cell.setBorderRight(BorderStyle.THIN);
        cell.setBorderLeft(BorderStyle.THIN);

        cell.setFillForegroundColor(IndexedColors.GREY_50_PERCENT.getIndex());
        cell.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return cell;
    }

    public static CellStyle getCellTitle1Green(Workbook workBook) {
        Font font = workBook.createFont();
        font.setFontName("Arial");
        font.setBold(true);
        font.setColor(HSSFColorPredefined.GREEN.getIndex());
        font.setFontHeightInPoints((short) 15);

        CellStyle cell = workBook.createCellStyle();
        cell.setAlignment(HorizontalAlignment.CENTER);
        cell.setFont(font);
        cell.setBorderTop(BorderStyle.NONE);
        cell.setBorderBottom(BorderStyle.NONE);
        cell.setBorderRight(BorderStyle.NONE);
        cell.setBorderLeft(BorderStyle.NONE);

        return cell;
    }

    public static CellStyle getCellTitle2Green(Workbook workBook) {
        Font font = workBook.createFont();
        font.setFontName("Arial");
        font.setBold(true);

        font.setColor(HSSFColorPredefined.GREEN.getIndex());
        font.setFontHeightInPoints((short) 12);

        CellStyle cell = workBook.createCellStyle();
        cell.setAlignment(HorizontalAlignment.CENTER);
        cell.setFont(font);
        cell.setBorderTop(BorderStyle.NONE);
        cell.setBorderBottom(BorderStyle.NONE);
        cell.setBorderRight(BorderStyle.NONE);
        cell.setBorderLeft(BorderStyle.NONE);

        return cell;
    }

    public static CellStyle getCellTitle3Green(Workbook workBook) {
        Font font = workBook.createFont();
        font.setFontName("Arial");
        font.setBold(true);
        font.setColor(HSSFColorPredefined.GREEN.getIndex());
        font.setFontHeightInPoints((short) 11);

        CellStyle cell = workBook.createCellStyle();
        cell.setAlignment(HorizontalAlignment.CENTER);
        cell.setFont(font);
        cell.setBorderTop(BorderStyle.NONE);
        cell.setBorderBottom(BorderStyle.NONE);
        cell.setBorderRight(BorderStyle.NONE);
        cell.setBorderLeft(BorderStyle.NONE);

        return cell;
    }

}
