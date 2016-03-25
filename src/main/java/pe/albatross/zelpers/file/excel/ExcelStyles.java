package pe.albatross.zelpers.file.excel;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
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

}
