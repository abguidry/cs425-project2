<%-- 
    Document   : applicant_skills
    Created on : Dec 4, 2019, 10:24:41 PM
    Author     : deara
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<jsp:useBean id="applicant" class="edu.jsu.mcis.cs425.project2.BeanApplicant" scope="session" />
<!DOCTYPE html>
<html>
   <head>
      <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
      <title>Select Skills</title>
   </head>
   <body>
      <form id="skillsform" name="skillsform" method="post" action="applicant_skills.jsp">
         <fieldset>
            <legend>Select Your Skills:</legend>
            <jsp:getProperty name="applicant" property="skillsList" />
            <input type="submit" value="Submit" />
         </fieldset>
      </form>
   </body>
</html>