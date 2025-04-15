package ukf;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

import org.mindrot.jbcrypt.BCrypt;

@WebServlet("/upravProfilServlet")
public class upravProfilServlet extends HttpServlet {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Connection con;

    public void init() throws ServletException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost/hotel_events", "root", "");
        } catch (Exception e) {
            throw new ServletException("Chyba pri pripojení k DB: " + e.getMessage());
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("ID") == null) {
            response.sendRedirect("loginServlet");
            return;
        }

        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        createHeader(out, request);
        out.println("<body style='margin:0; padding:0;'>");
        out.println("<div style='padding: 40px; max-width: 800px; margin: auto;'>");
        out.println("<h2>Upraviť profil</h2>");

        out.println("<form method='post' action='upravProfilServlet'>");
        out.println("<div class='mb-3'><label>Meno:</label><input name='first_name' class='form-control' value='" + session.getAttribute("first_name") + "' required></div>");
        out.println("<div class='mb-3'><label>Priezvisko:</label><input name='last_name' class='form-control' value='" + session.getAttribute("last_name") + "' required></div>");
        out.println("<div class='mb-3'><label>Email:</label><input name='email' type='email' class='form-control' value='" + session.getAttribute("email") + "' required></div>");
        out.println("<div class='mb-3'><label>Telefón:</label><input name='phone' class='form-control' value='" + session.getAttribute("phone") + "'></div>");
        out.println("<div class='mb-3'><label>Dátum narodenia:</label><input name='born' type='date' class='form-control' value='" + session.getAttribute("born") + "'></div>");
        out.println("<div class='mb-3'><label>Nové heslo:</label><input name='password1' type='password' class='form-control'></div>");
        out.println("<div class='mb-3'><label>Zopakujte heslo:</label><input name='password2' type='password' class='form-control'></div>");
        out.println("<button type='submit' class='btn btn-success'>Uložiť zmeny</button>");
        out.println("</form></div>");
        createFooter(out, request);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("ID") == null) {
            response.sendRedirect("loginServlet");
            return;
        }

        int userId = (Integer) session.getAttribute("ID");
        String firstName = request.getParameter("first_name");
        String lastName = request.getParameter("last_name");
        String email = request.getParameter("email");
        String phone = request.getParameter("phone");
        String born = request.getParameter("born");
        String pass1 = request.getParameter("password1");
        String pass2 = request.getParameter("password2");

        if (pass1 != null && !pass1.isEmpty() && !pass1.equals(pass2)) {
            response.getWriter().println("Heslá sa nezhodujú.");
            return;
        }

        try {
            String sql;
            PreparedStatement ps;

            if (pass1 != null && !pass1.isEmpty()) {
                sql = "UPDATE user SET first_name=?, last_name=?, email=?, phone=?, born=?, password=? WHERE id=?";
                ps = con.prepareStatement(sql);
                ps.setString(1, firstName);
                ps.setString(2, lastName);
                ps.setString(3, email);
                ps.setString(4, phone);
                ps.setString(5, born);
                ps.setString(6, BCrypt.hashpw(pass1, BCrypt.gensalt()));
                ps.setInt(7, userId);
            } else {
                sql = "UPDATE user SET first_name=?, last_name=?, email=?, phone=?, born=? WHERE id=?";
                ps = con.prepareStatement(sql);
                ps.setString(1, firstName);
                ps.setString(2, lastName);
                ps.setString(3, email);
                ps.setString(4, phone);
                ps.setString(5, born);
                ps.setInt(6, userId);
            }

            ps.executeUpdate();
            ps.close();

            session.setAttribute("first_name", firstName);
            session.setAttribute("last_name", lastName);
            session.setAttribute("email", email);
            session.setAttribute("phone", phone);
            session.setAttribute("born", born);

            response.sendRedirect("mainServlet?profil=aktualizovany");
        } catch (SQLException e) {
            response.getWriter().println("Chyba pri ukladaní profilu: " + e.getMessage());
        }
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
}
