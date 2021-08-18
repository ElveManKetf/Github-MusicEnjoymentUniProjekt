package login;

import java.io.IOException;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class Login
 */
@WebServlet("/Login")
public class Login extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Login() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		// Gebe Webseite aus
		out.print("<head>\r\n" + 
				"  <style>\r\n" + 
				"  form  { display: table;      }\r\n" + 
				"p     { display: table-row;  }\r\n" + 
				"label { display: table-cell; }\r\n" + 
				"input { display: table-cell; }\r\n" + 
				"  </style>\r\n" + 
				"</head>\r\n" + 
				"<h1><strong>CineYou</strong></h1>\r\n" + 
				"<form action=\"Login\" method=\"post\">\r\n" + 
				"<p><label for=\"a\">Email:</label> <input id=\"a\" name=\"email\" type=\"text\" /></p>\r\n" + 
				"<p><label for=\"d\">Passwort:</label><input id=\"d\" name=\"passwort\" type=\"password\" /></p>\r\n" + 
				"<br /> <input type=\"submit\" value=\"Login\">\r\n" + 
				"</form>");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		// Lese POST Parameter aus
		String email = request.getParameter("email");
		String passwort = request.getParameter("passwort");
		
		response.setContentType("text/html");
		
		// Versuche als Kunde bzw. Mitarbeiter einzuloggen
		if (this.login(email, passwort))
		{
			Cookie loginCookie = new Cookie("user",email);
			//setting cookie to expiry in 30 mins
			loginCookie.setMaxAge(30*60);
			response.addCookie(loginCookie);
			response.sendRedirect("HomeLoggedIn.jsp");
		}
		else if (this.loginMitarbeiter(email, passwort))
		{
			Cookie loginCookie = new Cookie("user",email);
			//setting cookie to expiry in 30 mins
			loginCookie.setMaxAge(30*60);
			response.addCookie(loginCookie);
			response.sendRedirect("/CineYou/mitarbeiter");
		}
		else
		{
			// Falls die Eingaben falsch sind, lade Seite neu
			doGet(request,response);
		}
	}
	
	
	private boolean loginMitarbeiter(String email, String passwort)
	{
		String sql = "SELECT * FROM Mitarbeiter WHERE email = ? AND passwort = ?";
		 
        try (Connection conn = this.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            pstmt.setString(2, passwort);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next())
            	return true;
            else
            	return false;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
	}
	
	private boolean login(String email, String passwort) {
        String sql = "SELECT * FROM Kunde WHERE email = ? AND passwort = ?";
 
        try (Connection conn = this.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            pstmt.setString(2, passwort);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next())
            	return true;
            else
            	return false;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

	// Verbinde zur Datenbank
	private Connection connect() {
        Connection conn = null;
        try {
        	Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection(database.Database.PATH);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } catch (Exception e) {
        	System.out.println(e.toString());
        }
        return conn;
    }
}
