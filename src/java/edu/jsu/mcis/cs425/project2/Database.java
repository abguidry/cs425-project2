package edu.jsu.mcis.cs425.project2;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.sql.DataSource;

public class Database {

    
    public Connection getConnection() {
        Connection conn = null;
        try {
            
           Context  envContext = new InitialContext();
           Context initContext  = (Context)envContext.lookup("java:/comp/env");
           DataSource ds = (DataSource)initContext.lookup("jdbc/db_pool");
           conn = ds.getConnection();
            
        }
        
        catch (Exception e) {
            e.printStackTrace();
        } 
        
        return conn;
    }
    
    public HashMap getUserInfo(String username){ 
        HashMap<String, String> results = null;
    
        try{
               Connection conn = getConnection();
               
               String query = "SELECT * FROM user WHERE username = ?";
               PreparedStatement pstatement = conn.prepareStatement(query);
               pstatement.setString(1, username);
               
               boolean hasresults = pstatement.execute();
               
               if( hasresults){
                   ResultSet resultset = pstatement.getResultSet();
                   
                   if(resultset.next()){
                       
                       results = new HashMap<>();
                       String id = String.valueOf(resultset.getInt("id"));
                       String displayname = resultset.getString("displayname");
                       results.put("id", id);


                       results.put("displayname",displayname);
                       

                       
                       
                       
                   }
               }
        }
        
        catch(Exception e){
            e.printStackTrace();
        }
        return results; 

    
    }

    
    
    public String getSkillsListAsHTML(int userid){
        String skillList;
        StringBuilder skills = new StringBuilder();
       
        try{
               Connection conn = getConnection();
               
               String query = "SELECT skills.*, a.userid FROM skills LEFT JOIN ( SELECT * FROM applicants_to_skills WHERE userid = 1) AS a ON skills.id = a.skillsid; ";
               PreparedStatement pstatement = conn.prepareStatement(query);
               
               
               boolean hasresults = pstatement.execute();
               
                if( hasresults){
                   ResultSet resultset = pstatement.getResultSet();
                   
                   

                   while(resultset.next()){
                       String description = resultset.getString("description");
                       int id = resultset.getInt("id");
                       int user = resultset.getInt("userid");
                       //skills.append("<input type=\"checkbox\" name=\"skills\" value=\"1\" id=\"skills_\" checked>");
                       //skills.append("<label for=\"skills_\">.</label><br />");
                       
                       skills.append("<input type=\"checkbox\" name=\"skills\" value=\"");
                       skills.append(id);
                       skills.append("\" id=\"skills_").append(id).append("\" ");
                       if(user !=0){
                           skills.append("checked  ");
                       }
                       
                       
                       skills.append(">\n");
                       
                       skills.append("<label for=\"skills_").append(id).append("\">");
                       skills.append(description);
                       skills.append("</label><br />\n\n");
                       
                       
                   }
                }
        }
        catch(Exception e){
            e.printStackTrace();
        }      
        skillList = skills.toString();
        
        return skillList;
    }
    public void setSkillsList(int userid, String[] skills){
        
        try{
               Connection conn = getConnection();
               
       
        
               PreparedStatement stmt = conn.prepareStatement("DELETE FROM applicants_to_skills WHERE userid = ?");
               stmt.setInt(1, userid);
               stmt.execute();
               PreparedStatement pstatement = conn.prepareStatement("INSERT INTO applicants_to_skills (userid, skillsid) VALUES(?,?)" );
                for (String skill : skills) {
                    int skillsid = Integer.parseInt(skill);
                    pstatement.setInt(1, userid);
                    pstatement.setInt(2, skillsid);
                    pstatement.addBatch();
                }
                int[] r= pstatement.executeBatch();
                
        
        }
        catch(Exception e){
            e.printStackTrace();
        
        
        
        }
        
        
        
        
        
    }
    public String getJobsListAsHTML(int userid){
        StringBuilder jobsList = new StringBuilder();
        try{
               Connection conn = getConnection();
               //This is the point in the project where i bashed my head repeatedly 
               //into my keyboard and honestly thought about what life would be like if i just
               //dropped out of college and sought out life as a humble begger.
               
               String query = "SELECT jobs.id, jobs.name, a.userid FROM\n "+
                       "jobs LEFT JOIN (SELECT * FROM applicants_to_jobs WHERE userid= 1) AS a\n"+
                       "ON jobs.id = a.jobsid\n"+
                       "WHERE jobs.id  IN\n"+
                       "( SELECT jobsid AS id FROM\n"+
                       "(applicants_to_skills JOIN skills_to_jobs\n"+
                       "ON applicants_to_skills.skillsid = skills_to_jobs.skillsid)\n"+
                       "WHERE applicants_to_skills.userid= 1)\n"+
                       "ORDER BY jobs.name;";
               PreparedStatement pstatement = conn.prepareStatement(query);
               
               
               boolean hasresults = pstatement.execute();
              
               
                if( hasresults){
                   ResultSet resultset = pstatement.getResultSet();
                      while(resultset.next()){
                       String description = resultset.getString("name");
                       int id = resultset.getInt("id");
                       int user = resultset.getInt("userid");
                       
                       
                       jobsList.append("<input type=\"checkbox\" name=\"skills\" value=\"");
                       jobsList.append(id);
                       jobsList.append("\" id=\"skills_").append(id).append("\" ");
                       if(user !=0){
                           jobsList.append("checked  ");
                       }
                       
                       
                       jobsList.append(">\n");
                       
                       jobsList.append("<label for=\"skills_").append(id).append("\">");
                       jobsList.append(description);
                       jobsList.append("</label><br />\n\n");
                       
                       
                   }
                   
                }
        }
        catch(Exception e){
            e.printStackTrace();
        
        
        
        }
        return jobsList.toString();
    }
    
     public void setJobsList(int userid, String[] jobs){
         try{
               Connection conn = getConnection();
               
       
        
               PreparedStatement stmt = conn.prepareStatement("DELETE FROM applicants_to_jobs WHERE userid = ?");
               stmt.setInt(1, userid);
               stmt.execute();
               PreparedStatement pstatement = conn.prepareStatement("INSERT INTO applicants_to_jobs (userid, jobsid) VALUES(?,?)" );
                for (String job : jobs) {
                    int jobsid = Integer.parseInt(job);
                    pstatement.setInt(1, userid);
                    pstatement.setInt(2, jobsid);
                    pstatement.addBatch();
                }
                int[] r= pstatement.executeBatch();
                
        
        }
        catch(Exception e){
            e.printStackTrace();
        
        
        
        }
     }
    
}