package ukf;

import java.io.IOException;
import java.sql.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

@WebServlet("/rezervujEventServlet")
public class rezervujEventServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private Connection con;

    public void init() throws ServletException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost/hotel_events", "root", "");
        } catch (Exception e) {
            throw new ServletException("Nepodarilo sa pripojiť k databáze", e);
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("ID") == null) {
            response.sendRedirect("loginServlet");
            return;
        }

        int userId = (Integer) session.getAttribute("ID");
        int eventId = Integer.parseInt(request.getParameter("event_id"));

        try {
            PreparedStatement getEvent = con.prepareStatement("SELECT event_date FROM event WHERE id = ?");
            getEvent.setInt(1, eventId);
            ResultSet eventRs = getEvent.executeQuery();

            if (!eventRs.next()) {
                response.getWriter().println("Event neexistuje.");
                return;
            }

            Date eventDate = eventRs.getDate("event_date");
            eventRs.close();
            getEvent.close();

            PreparedStatement kontrola = con.prepareStatement(
                "SELECT id FROM event_reservation WHERE user_id = ? AND event_id = ?"
            );
            kontrola.setInt(1, userId);
            kontrola.setInt(2, eventId);
            ResultSet rs = kontrola.executeQuery();
            if (rs.next()) {
                response.sendRedirect("eventDetailServlet?eventId=" + eventId);
                return;
            }
            rs.close();
            kontrola.close();

            LocalTime start = LocalTime.of(10, 0);
            LocalTime end = LocalTime.of(18, 0);

            Timestamp startTimestamp = Timestamp.valueOf(eventDate.toLocalDate().atTime(start));
            Timestamp endTimestamp = Timestamp.valueOf(eventDate.toLocalDate().atTime(end));

            PreparedStatement ps = con.prepareStatement(
                "INSERT INTO event_reservation (event_id, user_id, start_time, end_time, date, created_at, updated_at) " +
                "VALUES (?, ?, ?, ?, ?, NOW(), NOW())"
            );

            ps.setInt(1, eventId);
            ps.setInt(2, userId);
            ps.setTimestamp(3, startTimestamp);
            ps.setTimestamp(4, endTimestamp);
            ps.setDate(5, eventDate);

            ps.executeUpdate();
            ps.close();

            response.sendRedirect("eventDetailServlet?eventId=" + eventId + "&rezervacia=ok");

        } catch (SQLException e) {
            response.setContentType("text/html;charset=UTF-8");
            response.getWriter().println("Chyba pri rezervácii eventu: " + e.getMessage());
        }
    }

    public void destroy() {
        try { if (con != null) con.close(); } catch (SQLException e) {}
    }
}
