package ukf;

import java.io.IOException;
import java.sql.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

@WebServlet("/vymazEventServlet")
public class vymazEventServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private Connection con;

    public void init() throws ServletException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost/hotel_events", "root", "");
        } catch (Exception e) {
            throw new ServletException("Chyba pri pripojení k databáze", e);
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("ID") == null) {
            response.sendRedirect("loginServlet");
            return;
        }

        int currentUserId = (int) session.getAttribute("ID");
        String role = (String) session.getAttribute("role");

        int eventId = Integer.parseInt(request.getParameter("event_id"));

        try {
            PreparedStatement ps = con.prepareStatement("SELECT user_id FROM event WHERE id = ?");
            ps.setInt(1, eventId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                int creatorId = rs.getInt("user_id");

                if ("admin".equalsIgnoreCase(role) || currentUserId == creatorId) {

                    PreparedStatement delRes = con.prepareStatement("DELETE FROM event_reservation WHERE event_id = ?");
                    delRes.setInt(1, eventId);
                    delRes.executeUpdate();
                    delRes.close();

                    PreparedStatement delEvt = con.prepareStatement("DELETE FROM event WHERE id = ?");
                    delEvt.setInt(1, eventId);
                    delEvt.executeUpdate();
                    delEvt.close();

                    response.sendRedirect("mainServlet?deleted=ok");
                } else {
                    response.sendRedirect("mainServlet?error=nopermission");
                }
            } else {
                response.sendRedirect("mainServlet?error=notfound");
            }

            rs.close();
            ps.close();

        } catch (Exception e) {
            response.setContentType("text/html;charset=UTF-8");
            response.getWriter().println("<p style='color:red;'>Chyba pri mazaní eventu: " + e.getMessage() + "</p>");
        }
    }

    public void destroy() {
        try {
            if (con != null) con.close();
        } catch (Exception e) {}
    }
}
