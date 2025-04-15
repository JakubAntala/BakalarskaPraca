package ukf;

import java.io.*;
import java.sql.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

@WebServlet("/pridatEventServlet")
public class pridatEventServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private Connection con;

    public void init() throws ServletException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost/hotel_events", "root", "");
        } catch (Exception e) {
            throw new ServletException("Chyba pri pripájaní k databáze", e);
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (!RoleHelper.hasAccess(session, "staff", "admin")) {
            response.sendRedirect("mainServlet");
            return;
        }

        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        createHeader(out, request);
        createFooter(out, request);
        
        out.println("<!DOCTYPE html><html><head>");
        out.println("<meta charset='UTF-8'><title>Pridať Event</title>");
        out.println("<link href='https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css' rel='stylesheet'>");
        out.println("</head><body style='min-height: 100vh; display: flex; flex-direction: column; background-color: #f8f9fa;'>");
        out.println("<div class='container mt-5' style='max-width: 600px; background: white; padding: 30px; border-radius: 10px; box-shadow: 0 4px 10px rgba(0,0,0,0.1);'>");
        out.println("<h2>Pridať nový event</h2>");

        out.println("<form method='post' action='pridatEventServlet'>");

        out.println("<div class='mb-3'><label>Názov eventu:</label><input name='event_name' class='form-control' required></div>");
        out.println("<div class='mb-3'><label>Organizátor:</label><input name='organizer_company' class='form-control'></div>");
        out.println("<div class='mb-3'><label>Dátum konania:</label><input name='event_date' type='date' class='form-control'></div>");

       
        out.println("<div class='mb-3'><label>Hotel:</label>");
        out.println("<select name='hotel_id' class='form-select'>");
        try {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT id, name FROM hotel");
            while (rs.next()) {
                out.println("<option value='" + rs.getInt("id") + "'>" + rs.getString("name") + "</option>");
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            out.println("<option disabled>Chyba pri načítaní hotelov</option>");
        }
        out.println("</select></div>");

        out.println("<div class='mb-3'><label>Voľné miesta:</label>");
        out.println("<input name='available' type='number' class='form-control' min='0' max='1' step='1' required>");
        out.println("</div>");
        out.println("<div class='mb-3'><label>Popis:</label><textarea name='description' class='form-control'></textarea></div>");

        out.println("<button type='submit' class='btn btn-success'>Pridať</button>");
        out.println("</form></div></body></html>");
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (!RoleHelper.hasAccess(session, "staff", "admin")) {
            response.sendRedirect("mainServlet");
            return;
        }

        String eventName = request.getParameter("event_name");
        String company = request.getParameter("organizer_company");
        String eventDate = request.getParameter("event_date");
        String description = request.getParameter("description");
        int hotelId = Integer.parseInt(request.getParameter("hotel_id"));
        int available = Integer.parseInt(request.getParameter("available"));
        int userId = (int) session.getAttribute("ID");

        try {
            PreparedStatement ps = con.prepareStatement(
                "INSERT INTO event (event_name, event_date, organizer_company, hotel_id, available, description, created_at, updated_at, user_id) " +
                "VALUES (?, ?, ?, ?, ?, ?, NOW(), NOW(), ?)"
            );

            ps.setString(1, eventName);
            ps.setString(2, eventDate);
            ps.setString(3, company);
            ps.setInt(4, hotelId);
            ps.setInt(5, available);
            ps.setString(6, description);
            ps.setInt(7, userId);

            ps.executeUpdate();
            ps.close();

            response.sendRedirect("mainServlet");
        } catch (SQLException e) {
            response.setContentType("text/html;charset=UTF-8");
            response.getWriter().println("Chyba pri ukladaní eventu: " + e.getMessage());
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
