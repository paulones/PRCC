/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package reports;

import Beans.ExtractBean;
import com.itextpdf.text.Document;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import java.io.OutputStream;

/**
 *
 * @author ppccardoso
 */
public class RelatorioMensal {

    private ExtractBean bean;
    
    public RelatorioMensal(){
    }

    public void Generate() throws Exception {
        PdfWriter writer = null;
        Document doc = null;
        OutputStream os = null;
//      cria o documento tamanho A4, margens de 2,54cm
        doc = new Document(PageSize.A4, 72, 72, 72, 72);
        String nomedoarquivo = "";

        try {

            nomedoarquivo = "Extrato de " + bean.getMesStr() + " - " + bean.getAnoStr() + ".pdf";
            os = new FileOutputStream(nomedoarquivo);

            PdfWriter.getInstance(doc, os);

            doc.open();

            doc.add(new Paragraph("Hello World!"));

        } finally {
            if ((doc != null) || (os != null)) {
                doc.close();
            }
            String pdfopen = "C:"+File.separator+"Relatorio"+File.separator+ nomedoarquivo;
            //java.awt.Desktop.getDesktop().open(new File(pdfopen));
        }


    }

    public ExtractBean getBean() {
        return bean;
    }

    public void setBean(ExtractBean bean) {
        this.bean = bean;
    }
}
