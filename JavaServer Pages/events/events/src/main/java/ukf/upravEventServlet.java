package ukf;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

@WebServlet("/upravEventServlet")
public class upravEventServlet extends HttpServlet {
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
            throw new ServletException("Chyba DB: " + e.getMessage());
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (!RoleHelper.hasAccess(session, "staff", "admin")) {
            response.sendRedirect("mainServlet");
            return;
        }

        int eventId = Integer.parseInt(request.getParameter("event_id"));
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        createHeader(out, request);
        
        createFooter(out, request);

        try {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM event WHERE id = ?");
            ps.setInt(1, eventId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                out.println("<html><head><title>Upraviť event</title>");
                out.println("<link href='https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css' rel='stylesheet'>");
                out.println("</head><body style='margin: 0; padding: 0; background-color: #ffffff;'>");
                out.println("<div style='padding: 40px; max-width: 1400px; margin: 0 auto;'>");                
                out.println("<div style='padding: 40px; max-width: 1400px; margin: 0 auto;'>");     
                out.println("<h2>Upraviť event</h2>");

                out.println("<form method='post' action='upravEventServlet'>");
                out.println("<input type='hidden' name='event_id' value='" + eventId + "'>");

                out.println("<div class='mb-3'><label>Názov eventu:</label>");
                out.println("<input name='event_name' class='form-control' value='" + rs.getString("event_name") + "' required></div>");

                out.println("<div class='mb-3'><label>Organizátor:</label>");
                out.println("<input name='organizer_company' class='form-control' value='" + rs.getString("organizer_company") + "'></div>");

                out.println("<div class='mb-3'><label>Dátum:</label>");
                out.println("<input name='event_date' type='date' class='form-control' value='" + rs.getString("event_date") + "'></div>");

                out.println("<div class='mb-3'><label>Hotel:</label><select name='hotel_id' class='form-select'>");
                Statement stmt = con.createStatement();
                ResultSet hotely = stmt.executeQuery("SELECT * FROM hotel");
                int selectedHotel = rs.getInt("hotel_id");
                while (hotely.next()) {
                    int hotelId = hotely.getInt("id");
                    String selected = (hotelId == selectedHotel) ? "selected" : "";
                    out.println("<option value='" + hotelId + "' " + selected + ">" + hotely.getString("name") + "</option>");
                }
                out.println("</select></div>");

                out.println("<div class='mb-3'><label>Voľné miesta:</label>");
                out.println("<input name='available' type='number' class='form-control' min='0' max='1' step='1' value='" + rs.getInt("available") + "' required></div>");

                out.println("<div class='mb-3'><label>Popis:</label>");
                out.println("<textarea name='description' class='form-control'>" + rs.getString("description") + "</textarea></div>");

                out.println("<button type='submit' class='btn btn-primary'>Uložiť zmeny</button>");
                out.println("</form></div></body></html>");

            } else {
                out.println("Event neexistuje.");
            }

        } catch (SQLException e) {
            out.println("Chyba pri načítaní: " + e.getMessage());
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

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (!RoleHelper.hasAccess(session, "staff", "admin")) {
            response.sendRedirect("mainServlet");
            return;
        }

        try {
            int eventId = Integer.parseInt(request.getParameter("event_id"));
            String eventName = request.getParameter("event_name");
            String organizer = request.getParameter("organizer_company");
            String date = request.getParameter("event_date");
            int hotelId = Integer.parseInt(request.getParameter("hotel_id"));
            int available = Integer.parseInt(request.getParameter("available"));
            String description = request.getParameter("description");

            PreparedStatement ps = con.prepareStatement(
                "UPDATE event SET event_name=?, organizer_company=?, event_date=?, hotel_id=?, available=?, description=?, updated_at=NOW() WHERE id=?"
            );
            ps.setString(1, eventName);
            ps.setString(2, organizer);
            ps.setString(3, date);
            ps.setInt(4, hotelId);
            ps.setInt(5, available);
            ps.setString(6, description);
            ps.setInt(7, eventId);

            ps.executeUpdate();
            ps.close();

            response.sendRedirect("eventDetailServlet?eventId=" + eventId + "&updated=ok");
        } catch (SQLException e) {
            response.getWriter().println("Chyba pri ukladaní zmien: " + e.getMessage());
        }
    }
}
