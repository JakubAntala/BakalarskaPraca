package ukf;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

@WebServlet("/zmenitRoleServlet")
public class zmenitRoleServlet extends HttpServlet {
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
            throw new ServletException("Chyba DB pripojenia: " + e.getMessage());
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (!RoleHelper.hasAccess(session, "admin")) {
            response.sendRedirect("mainServlet");
            return;
        }

        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        createHeader(out, request);
        String success = request.getParameter("success");
        if ("1".equals(success)) {
            out.println("<div class='alert alert-success text-center mt-4' role='alert'>");
            out.println("Rola bola úspešne zmenená.");
            out.println("</div>");
        }
        out.println("<div class='container mt-5'>");
        out.println("<h3>Zmeniť rolu používateľom</h3>");
        try {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT id, first_name, last_name, email, role FROM user");

            out.println("<table class='table table-bordered mt-4'>");
            out.println("<thead><tr><th>Meno</th><th>Email</th><th>Rola</th><th>Akcia</th></tr></thead><tbody>");
            while (rs.next()) {
                int userId = rs.getInt("id");
                String meno = rs.getString("first_name") + " " + rs.getString("last_name");
                String email = rs.getString("email");
                String role = rs.getString("role");

                out.println("<tr>");
                out.println("<td>" + meno + "</td>");
                out.println("<td>" + email + "</td>");
                out.println("<td>");
                out.println("<form method='post' action='zmenitRoleServlet' class='d-flex'>");
                out.println("<input type='hidden' name='user_id' value='" + userId + "'>");
                out.println("<select name='new_role' class='form-select me-2'>");
                out.println("<option value='guest'" + (role.equals("guest") ? " selected" : "") + ">guest</option>");
                out.println("<option value='staff'" + (role.equals("staff") ? " selected" : "") + ">staff</option>");
                out.println("<option value='admin'" + (role.equals("admin") ? " selected" : "") + ">admin</option>");
                out.println("</select>");
                out.println("<button type='submit' class='btn btn-primary'>Zmeniť</button>");
                out.println("</form>");
                out.println("</td>");
                out.println("</tr>");
            }
            out.println("</tbody></table>");

        } catch (SQLException e) {
            out.println("<p class='text-danger'>Chyba pri načítaní používateľov: " + e.getMessage() + "</p>");
        }

        out.println("</div>");
        createFooter(out, request);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (!RoleHelper.hasAccess(session, "admin")) {
            response.sendRedirect("mainServlet");
            return;
        }

        int userId = Integer.parseInt(request.getParameter("user_id"));
        String newRole = request.getParameter("new_role");

        try {
            PreparedStatement ps = con.prepareStatement("UPDATE user SET role = ? WHERE id = ?");
            ps.setString(1, newRole);
            ps.setInt(2, userId);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            response.getWriter().println("Chyba pri zmene roly: " + e.getMessage());
        }

        response.sendRedirect("zmenitRoleServlet?success=1");
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
        out.println("<style>");
        out.println("html, body { height: 100%; margin: 0; padding: 0; display: flex; flex-direction: column; }");
        out.println(".container { flex: 1; padding-bottom: 60px; }");
        out.println("footer { height: 60px; }");
        out.println("</style>");
    }
}
