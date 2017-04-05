/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package AbdullahWeka;
//<p id="textOutput">Uploaded file Status:   #{fileUploadBean.text}</p>

import Database.Access.DatabaseAccess;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import javax.faces.bean.ManagedBean;
import java.io.IOException;
import java.io.InputStream;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetProvider;

/**
 *
 * @author Abdullah
 */
@ManagedBean
@SessionScoped
public class FileUploadBean {

    private Part uploadedFile;
    private String text;
    private String filename;
    private String filetype;
    private String description;
    private String predicts;
    private CachedRowSet crs = null;

    public FileUploadBean() {
    }

    public Part getUploadedFile() {
        return uploadedFile;
    }

    public String getFiletype() {
        return filetype;
    }

    public void setFiletype(String filetype) {
        this.filetype = filetype;
    }

    public void setUploadedFile(Part uploadedFile) {
        this.uploadedFile = uploadedFile;
    }

    public String getDescription() {
        return description;
    }

    public String getPredicts() {
        return predicts;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPredicts(String predicts) {
        this.predicts = predicts;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public void updateDB() {
        try {
            DriverManager.registerDriver(new org.apache.derby.jdbc.ClientDriver());
            crs = RowSetProvider.newFactory().createCachedRowSet();
            crs.setUrl(DatabaseAccess.DBurl);
            crs.setUsername(DatabaseAccess.DBuser);
            crs.setPassword(DatabaseAccess.DBpass);
            crs.setCommand("insert into FILES (fileName,fileType,filePrediction,fileDescription) values (?,?,?,?)");
            crs.setString(1, filename);
            crs.setString(2, filetype);
            crs.setString(3, predicts);
            crs.setString(4, description);
            crs.execute();
        } catch (SQLException ex) {
            Logger.getLogger(FileUploadBean.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /*conversion from csv to arff or vv
    
package weka.api;
import weka.core.Instances;
import weka.core.converters.ArffSaver;
import weka.core.converters.CSVLoader;
import java.io.File;

    // load CSV
    CSVLoader loader = new CSVLoader();
    loader.setSource(new File("/home/likewise-open/ACADEMIC/csstnns/test/TS7/TS7-Venus.csv"));
    Instances data = loader.getDataSet();//get instances object

    // save ARFF
    ArffSaver saver = new ArffSaver();
    saver.setInstances(data);//set the dataset we want to convert
    //and save as ARFF
    saver.setFile(new File("/home/likewise-open/ACADEMIC/csstnns/test/TS7/TS7-Venus.arff"));
    saver.writeBatch();

     */
    public void upload() {

        FileOutputStream fop = null;
        File file;
        try {
            if (null != uploadedFile) {
                try {
                    InputStream is = uploadedFile.getInputStream();
                    text = new Scanner(is).useDelimiter("\\A").next();
                } catch (IOException ex) {
                }
            }
            file = new File("D:/Documents/University Docs/Spring 2017/CMP 491 - Senior Design II/Testing/AllfilesForProgram/" + filename + "." + filetype);
            fop = new FileOutputStream(file);
            if (!file.exists()) {
                file.createNewFile();
            }
            byte[] contentInBytes = text.getBytes();

            fop.write(contentInBytes);
            fop.flush();
            fop.close();

            text = "Uploaded";
        } catch (FileNotFoundException ex) {
            Logger.getLogger(FileUploadBean.class.getName()).log(Level.SEVERE, null, ex);
            text = "Error in uploading";
        } catch (IOException ex) {
            Logger.getLogger(FileUploadBean.class.getName()).log(Level.SEVERE, null, ex);
            text = "Error in uploading";

        } finally {
            try {
                if (fop != null) {
                    fop.close();
                }
                updateDB();
                FacesContext context = FacesContext.getCurrentInstance();
                HttpServletResponse response = (HttpServletResponse) context.getExternalContext().getResponse();
                initialAnalysis ia = new initialAnalysis();
                ia.analyze(filename);
                response.sendRedirect("doctorPatientAI.xhtml");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

}