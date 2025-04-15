package ukf;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.mindrot.jbcrypt.BCrypt;
import java.io.PrintWriter;
import java.sql.*;

@WebServlet("/mainServlet")
public class mainServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    Connection con;
    String error = "";

    @Override
    public void init() throws ServletException {
        super.init();
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost/hotel_events", "root", "");
        } catch (Exception e) {
            error = e.getMessage();
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        HttpSession session = request.getSession(false);
        createHeader(out, request);
        createBody(out, request);
        createFooter(out, request);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        try {
            String operacia = request.getParameter("operacia");
            if (badConnection(out) || badOperation(operacia, out)) return;
            if (operacia.equals("login")) {
                if (!uspesneOverenie(out, request)) {
                    vypisNeopravnenyPristup(out);
                    return;
                }
            }

            int id = getLogedUser(request, out);
            if (id == 0) return;
            if (operacia.equals("logout")) { odhlas(out, request, response); return; }

           

            createHeader(out, request);
            createBody(out, request);
            createFooter(out, request);
        } catch (Exception e) {
            out.println(e);
        }
    }

    private boolean badConnection(PrintWriter out) {
        if (error.length() > 0) {
            out.println(error);
            return true;
        }
        return false;
    }

    private boolean badOperation(String operacia, PrintWriter out) {
        if (operacia == null) {
            vypisNeopravnenyPristup(out);
            return true;
        }
        return false;
    }

    private int getLogedUser(HttpServletRequest request, PrintWriter out) {
        HttpSession ses = request.getSession(false);
        if (ses == null || ses.getAttribute("ID") == null) {
            out.println("Neprihlaseny user");
            vypisNeopravnenyPristup(out);
            return 0;
        }
        return (Integer) ses.getAttribute("ID");
    }

    private void createHeader(PrintWriter out, HttpServletRequest request) {
        HttpSession ses = request.getSession(false);
        String meno = ses != null ? (String) ses.getAttribute("first_name") : null;
        String priezvisko = ses != null ? (String) ses.getAttribute("last_name") : null;
        String email = ses != null ? (String) ses.getAttribute("login") : null;

        out.println("<head>");
        out.println("<meta charset='UTF-8'>");
        out.println("<meta name='viewport' content='width=device-width, initial-scale=1'>");
        out.println("<title>Eventy</title>");
        out.println("<link href='https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css' rel='stylesheet'>");
        out.println("</head>");

        out.println("<nav style='background-color: #1a1a1a; color: white; padding: 15px; display: flex; align-items: center; justify-content: space-between;'>");

        out.println("<div style='display: flex; align-items: center;'>");
        out.println("<a class='navbar-brand mb-0 h1 me-3' href='mainServlet' style='text-decoration: none; color: white;'>Eventy</a>");
        out.println("<form action='mainServlet' method='get'>");
        out.println("<input type='text' name='query' placeholder='Hľadať eventy' style='padding: 5px; border-radius: 5px; border: none;'>");
        out.println("</form></div>");

        out.println("<a href='hotelServlet' style='color: white; text-decoration: none; font-size: 16px;'>Zobraziť hotely</a>");

        out.println("<div style='position: relative; display: inline-block;'>");

        if (ses != null && ses.getAttribute("ID") != null) {
            String inic = meno.substring(0, 1).toUpperCase() + priezvisko.substring(0, 1).toUpperCase();

            out.println("<button onclick='toggleMenu()' style='background: none; border: none; color: white; font-size: 16px; cursor: pointer;'>");
            out.println("<div style='display: flex; align-items: center; gap: 10px;'>");
            out.println("<div style='width: 40px; height: 40px; background-color: #555; border-radius: 50%; display: flex; align-items: center; justify-content: center; font-weight: bold;'>");
            out.println(inic);
            out.println("</div></div></button>");

            out.println("<div id='profileMenu' style='display: none; position: absolute; right: 0; background: white; color: black; box-shadow: 0 2px 5px rgba(0, 0, 0, 0.2); border-radius: 5px; padding: 10px; min-width: 200px;'>");
            out.println("<p style='font-weight: bold;'>" + meno + " " + priezvisko + "</p>");
            out.println("<p style='color: gray; font-size: 14px;'>" + email + "</p>");

            if (RoleHelper.hasAccess(ses, "admin", "staff")) {
                out.println("<a href='myEventServlet' style='display: block; padding: 5px 0;'>Moje eventy</a>");
                out.println("<a href='pridatEventServlet' style='display: block; padding: 5px 0;'>Pridať Event</a>");
            }
            if (RoleHelper.hasAccess(ses, "admin")) {
                out.println("<a href='zmenitRoleServlet' style='display: block; padding: 5px 0;'>Zmeniť role</a>");
            }

            out.println("<a href='upravProfilServlet' style='display: block; padding: 5px 0;'>Upraviť profil</a>");
            out.println("<a href='mojeRezervacieServlet' style='display: block; padding: 5px 0;'>Moje rezervácie</a>");
            out.println("<form action='mainServlet' method='post'>");
            out.println("<input type='hidden' name='operacia' value='logout'>");
            out.println("<button type='submit' style='background: none; border: none; color: red; padding: 5px 0;'>Odhlásiť sa</button>");
            out.println("</form>");
            out.println("</div>");
        } else {
            out.println("<a href='loginServlet' style='color: white; text-decoration: none; padding: 8px 16px; border: 1px solid white; border-radius: 5px;'>Prihlásiť sa</a>");
        }

        out.println("</div></nav>");
        out.println("<script>");
        out.println("function toggleMenu() {");
        out.println("  var menu = document.getElementById('profileMenu');");
        out.println("  menu.style.display = menu.style.display === 'block' ? 'none' : 'block';");
        out.println("}");
        out.println("</script>");
    }
	 
	  private void createBody(PrintWriter out, HttpServletRequest request) {
	        try {
	            Statement stmt = con.createStatement();
	            ResultSet rs = stmt.executeQuery("SELECT * FROM event");

	            out.println("<div class='container mt-5'>");
	            out.println("<div class='row justify-content-center'>");

	            while (rs.next()) {
	                int eventId = rs.getInt("id");

	                out.println("<div class='col-md-4 mb-4'>");

	                out.println("<a href='eventDetailServlet?eventId=" + eventId + "' class='text-decoration-none' style='color: inherit;'>");
	                out.println("<div class='card shadow-sm' style='border-radius: 12px; overflow: hidden; cursor: pointer;'>");

	                out.println("<div class='card-header text-white text-center fw-bold' style='background-color: #007bff;'>");
	                out.println(rs.getString("event_name"));
	                out.println("</div>");

	                out.println("<div class='card-body bg-white'>");
	                out.println("<p class='card-text text-muted'>" + rs.getString("organizer_company") + "</p>");
	                out.println("<p class='card-text'><strong>Dátum:</strong> " + rs.getString("event_date") + "</p>");

	                int volneMiesta = rs.getInt("available");
	                if (volneMiesta > 0) {
	                    out.println("<p class='card-text text-success'>Voľné miesta</p>");
	                } else {
	                    out.println("<p class='card-text text-danger'>Miesta sú plné</p>");
	                }

	                out.println("</div>"); 
	                out.println("</div>"); 
	                out.println("</a>"); 
	                out.println("</div>"); 
	            }

	            out.println("</div>"); 
	            out.println("</div>"); 

	            rs.close();
	            stmt.close();
	        } catch (Exception e) {
	            out.println("<p style='color: red;'>Chyba pri načítaní eventov: " + e.getMessage() + "</p>");
	        }
	    }

	 
	 private void createFooter(PrintWriter out, HttpServletRequest request) {
	        out.println("<footer class='footer py-3 bg-dark text-white text-center' style='position: fixed; bottom: 0; width: 100%;'>");
	        out.println("<p class='mb-0'>© 2024 Event Management</p>");
	        out.println("</footer>");

	       
	        out.println("<script src='https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js'></script>");

	        
	        out.println("<style>");
	        out.println("html, body { height: 100%; margin: 0; padding: 0; display: flex; flex-direction: column; }");
	        out.println(".container { flex: 1; padding-bottom: 60px; }"); 
	        out.println("footer { height: 60px; }"); 
	        out.println("</style>");
	    }

	 
	 private void odhlas(PrintWriter out, HttpServletRequest request, HttpServletResponse response) throws IOException {
		    HttpSession ses = request.getSession(false);
		    if (ses != null) {
		        ses.invalidate();
		    }
		    response.sendRedirect("loginServlet?logout=ok");
		}


	 


	 protected void vypisNeopravnenyPristup(PrintWriter out) {
	        out.println("Nie si prihlaseny.");
	    }
	 
	 private boolean uspesneOverenie(PrintWriter out, HttpServletRequest request) {
		    String login = request.getParameter("login");
		    String heslo = request.getParameter("pwd");

		    if (login == null || heslo == null) {
		        out.print("Treba login a heslo");
		        return false;
		    }

		    PreparedStatement pstmt = null;
		    ResultSet rs = null;

		    try {
		        String sql = "SELECT id, first_name, last_name, born, phone, email, role, password FROM user WHERE email = ?";
		        pstmt = con.prepareStatement(sql);
		        pstmt.setString(1, login);
		        rs = pstmt.executeQuery();

		        if (rs.next()) {
		            String hashedPassword = rs.getString("password");

		            if (BCrypt.checkpw(heslo, hashedPassword)) {
		                HttpSession session = request.getSession();
		                session.setAttribute("ID", rs.getInt("id"));
		                session.setAttribute("login", login);
		                session.setAttribute("first_name", rs.getString("first_name"));
		                session.setAttribute("last_name", rs.getString("last_name"));
		                session.setAttribute("born", rs.getDate("born"));
		                session.setAttribute("phone", rs.getString("phone"));
		                session.setAttribute("email", rs.getString("email"));
		                session.setAttribute("role", rs.getString("role"));

		                return true;
		            } else {
		                out.println("Nesprávne prihlasovacie údaje.");
		                return false;
		            }
		        } else {
		            out.println("Používateľ neexistuje.");
		            return false;
		        }
		    } catch (Exception e) {
		        out.print(e.getMessage());
		        return false;
		    } finally {
		        try {
		            if (rs != null) rs.close();
		            if (pstmt != null) pstmt.close();
		        } catch (Exception e) {
		            out.print(e.getMessage());
		        }
		    }
		}


}