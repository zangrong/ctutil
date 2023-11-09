/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * Copyright [2014] [zangrong CetianTech]
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
 * @Description
 *
 *
 *
 * @author zangrong
 * @Date 2020-01-20 06:02
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
            // 先尝试直接用数字获取
            value = cell.getNumericCellValue();
        }catch(Exception e){
            try{
                // 再尝试尝试用字符串解析
                String temp = StringUtils.trimToNull(cell.getStringCellValue());
                value = Double.parseDouble(temp);
            }catch(Exception ex){
                log.warn("", ex);
            }
        }
        return value;
    }

    public static Integer getIntegerValue(Row row, int i) {
        Integer value = null;
        if (row == null) {
            return value;
        }
        Cell cell = row.getCell(i);
        if (cell == null) {
            return value;
        }
        try{
            double doubleValue = cell.getNumericCellValue();
            value = (int)doubleValue;
        }catch(Exception e){
            try{
                String temp = StringUtils.trimToNull(cell.getStringCellValue());
                value = Integer.parseInt(temp);
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

    public static void writeToCellWithWrap(Row row, int columnIndex, Object content){
        Cell cell = row.createCell(columnIndex);
        CellStyle cellStyle = cell.getCellStyle();
        if (cellStyle != null){
            cellStyle.setWrapText(true);
        }
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
