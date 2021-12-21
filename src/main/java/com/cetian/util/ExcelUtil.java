/**
 * @Copyright: 2018 jiayun.com Inc. All rights reserved.
 * @Title: ExcelUtil.java
 * @date 2018/7/27 11:55
 * @version V1.0
 * @author zangrong
 */
package com.cetian.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;

import java.util.List;

/**
 * @ClassName: ExcelUtil
 * @Description: TODO
 * @date 2018/7/27 11:55
 * @author zangrong
 */
@Slf4j
public class ExcelUtil {

    private static final String FORMULA_LINK_TEMPLATE = "HYPERLINK(\"%s\")";

    public static String getStringValue(Row row, int i) {
        String value = null;
        if (row == null) {
            return value;
        }
        Cell cell = row.getCell(i);
        if (cell == null) {
            return value;
        }
        try{
            value = StringUtils.trimToNull(cell.getStringCellValue());
        }catch(IllegalStateException e){
            // 有可能单元格格式是数字格式，这里尝试用数字格式试一下
            try {
                double numericCellValue = cell.getNumericCellValue();
                value = (long)numericCellValue + "";
            }catch (Exception e2){
                log.warn("", e2);
            }
        }catch (Exception e){
            log.warn("", e);
        }
        return value;
    }

    public static double getDoubleValue(Row row, int i) {
        double value = 0.00d;
        if (row == null) {
            return value;
        }
        Cell cell = row.getCell(i);
        if (cell == null) {
            return value;
        }
        try{
            String temp = StringUtils.trimToNull(cell.getStringCellValue());
            value = Double.parseDouble(temp);
        }catch(Exception e){
            log.warn("", e);
            try{
                value = cell.getNumericCellValue();
            }catch(Exception ex){
                log.warn("", ex);
            }
        }
        return value;
    }

    public static <E extends Enum<E>> E getEnumCellValue(Row row, int i, Class<E> clazz) {
        if (row == null) {
            return null;
        }
         Cell cell = row.getCell(i);
        if (cell == null) {
            return null;
        }
        String cellValue = StringUtils.trimToEmpty(cell.getStringCellValue());
        List<E> enumList = EnumUtils.getEnumList(clazz);
        for(E e : enumList){
            String name = ReflectionUtil.getFieldValueString(e, "name");
            if (StringUtils.equals(cellValue, name)) {
                return e;
            }
        }
        return null;
    }

    public static void writeComment(Cell cell, String message, Drawing<?> patriarch, CellStyle style) {
        try{
            // 先清除单元格已有的注释
            cell.removeCellComment();
            Comment comment = patriarch.createCellComment(new XSSFClientAnchor(0, 0, 0, 0, (short) 4, 2, (short) 6, 5));
            comment.setString(new XSSFRichTextString(message));
            cell.setCellComment(comment);
            cell.setCellStyle(style);
        }catch (Exception e){
            log.warn("excel 添加注释异常");
        }
    }

    /**
     * 删除一行，并且上移下面的行
     * @param sheet
     * @param rowIndex
     */
    public static void removeRow(Sheet sheet, int rowIndex) {
        int lastRowNum=sheet.getLastRowNum();
        if(rowIndex>=0&&rowIndex<lastRowNum)
            sheet.shiftRows(rowIndex+1,lastRowNum,-1);//将行号为rowIndex+1一直到行号为lastRowNum的单元格全部上移一行，以便删除rowIndex行
        if(rowIndex==lastRowNum){
            Row removingRow=sheet.getRow(rowIndex);
            if(removingRow!=null)
                sheet.removeRow(removingRow);
        }
    }

    /**
     * 向指定行的指定列写入Double
     * @param row
     * @param columnIndex
     * @param content
     */
    public static void writeToCellDouble(Row row, int columnIndex, Double content){
        if (content != null){
             Cell cell = row.createCell(columnIndex);
            cell.setCellValue(content);
        }
    }

    /**
     * 向指定行的指定列写入Integer
     * @param row
     * @param columnIndex
     * @param content
     */
    public static void writeToCellInteger(Row row, int columnIndex, Integer content){
        if (content != null){
             Cell cell = row.createCell(columnIndex);
            cell.setCellValue(content);
        }
    }

    /**
     * 向指定行的指定列写入文本
     * @param row
     * @param columnIndex
     * @param content
     */
    public static void writeToCell(Row row, int columnIndex, Object content){
         Cell cell = row.createCell(columnIndex);
        cell.setCellValue(ObjectUtil.trimToEmpty(content));
    }

    /**
     * 向指定行的指定列写入文本
     * @param row
     * @param columnIndex
     * @param content
     */
    public static void writeToCellFormular(Row row, int columnIndex, Object content){
        Cell cell = row.createCell(columnIndex);
        cell.setCellFormula(CellType.FORMULA.name());
        String str = ObjectUtil.trimToEmpty(content);
        str = String.format(FORMULA_LINK_TEMPLATE, str);
        cell.setCellFormula(str);
    }
}
